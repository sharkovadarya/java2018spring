package ru.spbau.group202.sharkova.hw2xo;

import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class Controller {

    private int rows;
    private int cols;
    private GridPane pane;
    private VBox vBox;
    private int winValue;

    private boolean isXsTurn = true;
    private int moveCounter = 0;
    private GameMode mode = GameMode.HOT_SEAT;

    private Statistics statistics = new Statistics();

    // used for FXML loading
    public Controller() {}

    public Controller(int r, int c, int winV, GridPane gridPane, VBox v) {
        this.rows = r;
        this.cols = c;
        this.winValue = winV;
        this.pane = gridPane;
        this.vBox = v;
    }

    public void initializeScene() {
        initializeGridPane();
        initializeVBoxButtons();
        setTextAreaText("Default mode:\nHot Seat");
    }

    private void initializeGridPane() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Button button = (Button) pane.getChildren().get(i * rows + j);
                button.setStyle("-fx-font-size: 100px; ");
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                button.setOnAction(e -> onBoardButtonAction(button));
            }
        }
    }

    private void initializeVBoxButtons() {
        Button newGameButton = (Button) vBox.getChildren().get(0);
        newGameButton.setOnAction(e -> showNewGameDialog());

        Button statisticsButton = (Button) vBox.getChildren().get(1);
        statisticsButton.setOnAction(e -> showStatistics());
    }

    private void showStatistics() {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("statistics.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Statistics");
            statistics.setTable((TableView) root.lookup("#statisticsTable"));
            statistics.initializeTable();
            stage.setResizable(false);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Failed to load statistics");
            alert.setHeaderText("An error has occured.");
            alert.show();
        }
    }

    private void showNewGameDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Choose new game type");
        alert.setHeaderText("New Game Type");

        ButtonType buttonTypeHS = new ButtonType("Hot Seat\nMode");
        ButtonType buttonTypeCME = new ButtonType("Computer\nMode:\nEasy");
        ButtonType buttonTypeCMH = new ButtonType("Computer\nMode:\nHard");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeHS, buttonTypeCME, buttonTypeCMH, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent()) {
            alert.close();
            return;
        }

        if (result.get() == buttonTypeHS) {
            mode = GameMode.HOT_SEAT;
            restart();
        } else if (result.get() == buttonTypeCME) {
            mode = GameMode.COMPUTER_EASY;
            restart();
        } else if (result.get() == buttonTypeCMH) {
            mode = GameMode.COMPUTER_HARD;
            restart();
        } else if (result.get() == buttonTypeCancel){
            alert.close();
        }
    }

    private void restart() {
        isXsTurn = true;
        moveCounter = 0;
        clearGridPane();
        setTextAreaText();
        setButtonsDisable(false);
    }

    private void clearGridPane() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Button button = (Button) pane.getChildren().get(i * rows + j);
                button.setText("");
            }
        }
    }

    private void setTextAreaText(String... args) {
        TextArea textArea = (TextArea) vBox.getChildren().get(vBox.getChildren().size() - 1);
        textArea.setFont(Font.font("Verdana", 18));
        textArea.clear();
        for (String arg : args) {
            textArea.appendText(arg + "\n");
        }
        textArea.appendText((isXsTurn ? "X" : "O") + "'s turn.");
    }

    private void onBoardButtonAction(Button b) {

        if (b.getText().isEmpty()) {
            b.setText(isXsTurn ? "X" : "O");
            isXsTurn = !isXsTurn;
            moveCounter++;
            setTextAreaText();

            String[][] state = new String[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Button button = (Button) pane.getChildren().get(i * rows + j);
                    state[i][j] = button.getText();
                }
            }

            Result res = checkGameState(state, winValue, Character.getNumericValue(b.getId().charAt(7)),
                                                  Character.getNumericValue(b.getId().charAt(6)));
            if (res != Result.IN_PROCESS) {
                TextArea textArea = (TextArea) vBox.getChildren().get(vBox.getChildren().size() - 1);
                textArea.setFont(Font.font("Verdana", 18));
                if (res == Result.X_WIN) {
                    textArea.setText("X wins.");
                    if (mode == GameMode.HOT_SEAT) {
                        statistics.recordWin("main player", "second HS mode player");
                    } else if (mode == GameMode.COMPUTER_EASY) {
                        statistics.recordWin("main player", "computer easy mode bot");
                    } else if (mode == GameMode.COMPUTER_HARD) {
                        statistics.recordWin("main player", "computer hard mode bot");
                    }
                } else if (res == Result.O_WIN) {
                    textArea.setText("O wins.");
                    if (mode == GameMode.HOT_SEAT) {
                        statistics.recordWin("second HS mode player", "main player");
                    } else if (mode == GameMode.COMPUTER_EASY) {
                        statistics.recordWin("computer easy mode bot", "main player");
                    } else if (mode == GameMode.COMPUTER_HARD) {
                        statistics.recordWin("computer hard mode bot", "main player" );
                    }
                } else {
                    textArea.setText("Draw.");
                    if (mode == GameMode.HOT_SEAT) {
                        statistics.recordDraw("second HS mode player", "main player");
                    } else if (mode == GameMode.COMPUTER_EASY) {
                        statistics.recordDraw("computer easy mode bot", "main player");
                    } else if (mode == GameMode.COMPUTER_HARD) {
                        statistics.recordDraw("computer hard mode bot", "main player" );
                    }
                }
                setButtonsDisable(true);
            } else if (!isXsTurn && mode != GameMode.HOT_SEAT) {
                if (mode == GameMode.COMPUTER_EASY) {
                    int r = makeRandomTurn();
                    Button button = (Button) pane.getChildren().get(r);
                    onBoardButtonAction(button);
                } else if (mode == GameMode.COMPUTER_HARD) {
                    int t = nextTurnComputerHardMode(state,
                            Character.getNumericValue(b.getId().charAt(7)),
                            Character.getNumericValue(b.getId().charAt(6)));
                    if (t != -1) {
                        onBoardButtonAction((Button) pane.getChildren().get(t));
                    } else {
                        // TODO workaround! ugly code!
                        int r = makeRandomTurn();
                        Button button = (Button) pane.getChildren().get(r);
                        onBoardButtonAction(button);
                    }
                }
            }

        } else {
            TextArea textArea = (TextArea) vBox.getChildren().get(vBox.getChildren().size() - 1);
            textArea.setText("Can't interact\nwith filled cells.");
            textArea.setFont(Font.font("Verdana", 18));
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e -> setTextAreaText());
            delay.play();
        }
    }

    private int makeRandomTurn() {
        int r = ThreadLocalRandom.current().nextInt(0, 9);
        Button button = (Button) pane.getChildren().get(r);
        while (!button.getText().isEmpty()) {
            r = ThreadLocalRandom.current().nextInt(0, 9);
            button = (Button) pane.getChildren().get(r);
        }

        return r;
    }

    private int nextTurnComputerHardMode(String[][] state, int lastX, int lastY) {
        int result = lookForWinOrBlock(state, "O");
        if (result != -1) {
            return result;
        }

        result = lookForWinOrBlock(state, "X");
        if (result != -1) {
            return result;
        }

        /*for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (state[i][j].isEmpty()) {
                    state[i][j] = "X";
                    isXsTurn = false;
                    Result res = checkGameState(state, 3, j, i);
                    if (res == Result.X_WIN) {
                        return i * rows + j; // block the winning move
                    }
                    state[i][j] = "";
                }
            }
        }*/

        // last move was a center move
        if (lastX == rows / 2 && lastY == cols / 2) {
            /*
             * If this were a game of tic tac toe with a win value of more than three,
             * this would not work, we would need to check every cell in the diagonal
             * direction; here, however, blocking the  corners would be enough
             */

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
        /*if (lastX == cols / 2) {
            if (state[0][cols / 2].isEmpty()) {
                return cols / 2;
            } else if (state[rows - 1][cols / 2].isEmpty()) {
                return (rows - 1) * rows + cols / 2;
            }
        } else if (lastY == rows / 2) {
            if (lastX == 0 && state[rows / 2][0].isEmpty()) {
                return (rows / 2) * rows;
            } else if (lastX == cols - 1 && state[rows - 1][cols - 1].isEmpty()) {
                return (rows - 1) * rows + cols - 1;
            }
        }*/

        //return -1;
    }

    private int lookForWinOrBlock(String[][] state, String v) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (state[i][j].isEmpty()) {
                    state[i][j] = v;
                    isXsTurn = v.equals("O");
                    Result res = checkGameState(state, 3, j, i);
                    if (v.equals("O") && res == Result.O_WIN || v.equals("X") && res == Result.X_WIN) {
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

    private int moveToFreeEdgeCell(String[][] state) {
        List<IntPair> options = Arrays.asList(new IntPair(rows / 2, 0), new IntPair(rows / 2, cols - 1),
                                              new IntPair(0, cols / 2), new IntPair(rows - 1, cols / 2));
        return pickTurnFromOptions(state, options);
    }

    private int findNearestEmptyCorner(String[][] state, int x, int y) {
        if (y == rows / 2) {
            if (x == 0) {
                List<IntPair> options = Arrays.asList(new IntPair(0, 0), new IntPair(rows - 1, 0));
                return pickTurnFromOptions(state, options);
            }
            // else x == cols - 1
            List<IntPair> options = Arrays.asList(new IntPair(0, cols - 1), new IntPair(rows - 1, cols - 1));
            return pickTurnFromOptions(state, options);
        } else if (x == cols / 2) {
            if (y == 0) {
                List<IntPair> options = Arrays.asList(new IntPair(0, 0), new IntPair(0, cols - 1));
                return pickTurnFromOptions(state, options);
            }
            // else y == rows - 1
            List<IntPair> options = Arrays.asList(new IntPair(rows - 1, 0), new IntPair(rows - 1, cols - 1));
            return pickTurnFromOptions(state, options);
        }

        return -1;
    }

    private int pickTurnFromOptions(String[][] state, List<IntPair> options) {
        Collections.shuffle(options);
        for (IntPair p : options) {
            if (state[p.x][p.y].isEmpty()) {
                return p.x * rows + p.y;
            }
        }

        return -1;
    }

    private Result checkGameState(String[][] state, int winValue, int lastX, int lastY) {

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
                if (state[lastY + j * direction.y][lastX + j * direction.x].equals(lastMark)) {
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
                if (state[lastY + (-1) * j * direction.y][lastX + (-1) * j * direction.x].equals(lastMark)) {
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

    private boolean isInBorders(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }

    private void setButtonsDisable(boolean disable) {
        for (Node node : pane.getChildren()) {
            Button button = (Button) node;
            button.setDisable(disable);
        }
    }

    private enum Result {X_WIN, O_WIN, DRAW, IN_PROCESS}
    private enum GameMode {HOT_SEAT, COMPUTER_EASY, COMPUTER_HARD}

    private class IntPair {
        int x;
        int y;

        public IntPair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

}
