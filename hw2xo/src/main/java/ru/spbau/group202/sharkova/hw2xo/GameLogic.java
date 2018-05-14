package ru.spbau.group202.sharkova.hw2xo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for game logic:
 * making the next turn in hard mode,
 * checking the game state.
 */
public class GameLogic {

    private int rows;
    private int cols;
    private boolean isXsTurn = true;
    private int moveCounter;

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public void setXsTurn(boolean isXsTurn) {
        this.isXsTurn = isXsTurn;
    }

    public void setMoveCounter(int moveCounter) {
        this.moveCounter = moveCounter;
    }

    public boolean getXsTurn() {
        return isXsTurn;
    }

    public int getMoveCounter() {
        return moveCounter;
    }

    /**
     * This method determines the game state based on the board state and last move.
     * @param state current board state
     * @param winValue how many cells in a row need to be filled in order to win
     * @param lastX last turn x coordinates
     * @param lastY last turn y coordinates
     * @result current game state
     */
    public Result checkGameState(String[][] state, int winValue,
                                                   int lastX, int lastY) {

        IntPair[] directions = {new IntPair(1, -1),
                new IntPair(1, 1),
                new IntPair(1, 0),
                new IntPair(0 ,1)};

        for (IntPair direction : directions) {
            int cnt = 1;
            int j = 1;
            String lastMark = isXsTurn ? "O" : "X";
            while (cnt < winValue &&
                    isInBorders(lastX + j * direction.x, lastY + j * direction.y)) {
                if (state[lastY + j * direction.y][lastX + j * direction.x]
                                                         .equals(lastMark)) {
                    cnt++;
                    j++;
                } else {
                    break;
                }
            }

            if (cnt == winValue) {
                return (lastMark.equals("X") ? Result.X_WIN : Result.O_WIN);
            }

            j = 1;
            while (cnt < winValue && isInBorders(lastX + (-1) * j * direction.x,
                    lastY + (-1) * j * direction.y)) {
                if (state[lastY + (-1) * j * direction.y][lastX + (-1) * j * direction.x]
                        .equals(lastMark)) {
                    cnt++;
                    j++;
                } else {
                    break;
                }
            }

            if (cnt == winValue) {
                return (lastMark.equals("X") ? Result.X_WIN : Result.O_WIN);
            }
        }

        if (moveCounter == rows * cols) {
            return Result.DRAW;
        }

        return Result.IN_PROCESS;
    }

    /**
     * This method makes next turn for the second player
     * in the computer hard mode based on current board state
     * and the first player's last move.
     * @param state current board state
     * @param lastX first player's last move X coordinate
     * @param lastY first player's last move Y coordinate
     * @return number of the cell for the next turn or -1 if no option found
     */
    public int nextTurnComputerHardMode(String[][] state, int lastX, int lastY) {
        int result = lookForWinOrBlock(state, "O");
        if (result != -1) {
            return result;
        }

        result = lookForWinOrBlock(state, "X");
        if (result != -1) {
            return result;
        }

        // last move was a center move
        if (lastX == rows / 2 && lastY == cols / 2) {

            List<IntPair> options = Arrays.asList(new IntPair(0, 0), new IntPair(0, cols - 1),
                    new IntPair(rows - 1, 0), new IntPair(rows - 1, cols - 1));
            Collections.shuffle(options);
            for (IntPair p : options) {
                if (state[p.x][p.y].isEmpty()) {
                    return p.x * rows + p.y;
                }
            }
        }

        // last move was a corner move
        if (lastX == 0 && (lastY == 0 || lastY == (rows - 1))
                || lastX == (cols - 1) && (lastY == 0 || lastY == (rows - 1))) {
            // if the center cell if free
            if (state[rows / 2][cols / 2].isEmpty()) {
                return (rows / 2) * rows + cols / 2;
            } else {
                return moveToFreeEdgeCell(state);
            }
        }

        // last move was an edge move -> try a nearby corner
        return findNearestEmptyCorner(state, lastX, lastY);
    }

    /**
     * This method checks possibilities of winning at the current turn.
     * @param state current board state
     * @param v X or O
     * @return number of the cell for the next turn or -1 if not found
     */
    private int lookForWinOrBlock(String[][] state, String v) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (state[i][j].isEmpty()) {
                    state[i][j] = v;
                    isXsTurn = v.equals("O");
                    Result res = checkGameState(state, 3, j, i);
                    if (v.equals("O") && res == Result.O_WIN ||
                        v.equals("X") && res == Result.X_WIN) {
                        if (v.equals("O")) {
                            isXsTurn = false;
                        }
                        return i * rows + j;
                    }
                    state[i][j] = "";
                    isXsTurn = false; // restore the value
                }
            }
        }

        return -1;
    }

    /**
     * This method looks for a free edge (not center or corner) cell.
     * @param state current board state
     * @return number of the cell for the next turn or -1 if not found
     */
    private int moveToFreeEdgeCell(String[][] state) {
        List<IntPair> options = Arrays.asList(new IntPair(rows / 2, 0),
                                              new IntPair(rows / 2, cols - 1),
                new IntPair(0, cols / 2), new IntPair(rows - 1, cols / 2));
        return pickTurnFromOptions(state, options);
    }

    /**
     * This method looks for a nearest empty corner for a given cell.
     * @param state current board state
     * @param x x coordinate of the given cell
     * @param y y coordinate of the given cell
     * @return number of the cell for the next turn or -1 if not found
     */
    private int findNearestEmptyCorner(String[][] state, int x, int y) {
        if (y == rows / 2) {
            if (x == 0) {
                List<IntPair> options = Arrays.asList(new IntPair(0, 0),
                                              new IntPair(rows - 1, 0));
                return pickTurnFromOptions(state, options);
            }
            // else x == cols - 1
            List<IntPair> options = Arrays.asList(new IntPair(0, cols - 1),
                                          new IntPair(rows - 1, cols - 1));
            return pickTurnFromOptions(state, options);
        } else if (x == cols / 2) {
            if (y == 0) {
                List<IntPair> options = Arrays.asList(new IntPair(0, 0),
                                              new IntPair(0, cols - 1));
                return pickTurnFromOptions(state, options);
            }
            // else y == rows - 1
            List<IntPair> options = Arrays.asList(new IntPair(rows - 1, 0),
                                          new IntPair(rows - 1, cols - 1));
            return pickTurnFromOptions(state, options);
        }

        return -1;
    }

    /**
     * Utility method to find a random empty cell from the given list.
     * @param state current board state
     * @param options list of options to pick from
     * @return number of an empty cell from the options list or -1 if not found
     */
    private int pickTurnFromOptions(String[][] state, List<IntPair> options) {
        Collections.shuffle(options);
        for (IntPair p : options) {
            if (state[p.x][p.y].isEmpty()) {
                return p.x * rows + p.y;
            }
        }

        return -1;
    }

    /**
     * This method checks whether the cell with given coordinates
     * is in the board borders.
     * @param x cell x coordinate
     * @param y cell y coordinate
     */
    private boolean isInBorders(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }

    /**
     * This private class represents a pair of two integers.
     */
    private class IntPair {
        int x;
        int y;

        public IntPair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
