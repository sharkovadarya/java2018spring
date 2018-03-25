package ru.spbau.group202.sharkova.hw2xo;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This class tests game logic methods
 * connected with making the next turn in computer hard mode
 * and determining the current game state.
 */
public class GameLogicTest {

    // utility method
    private void initializeGameLogic(GameLogic gameLogic, boolean isXsTurn, int moveCounter)
                                      throws NoSuchFieldException, IllegalAccessException {
        Field isXsTurnField = gameLogic.getClass().getDeclaredField("isXsTurn");
        isXsTurnField.setAccessible(true);
        isXsTurnField.set(gameLogic, isXsTurn);
        Field rowsField = gameLogic.getClass().getDeclaredField("rows");
        rowsField.setAccessible(true);
        rowsField.set(gameLogic, 3);
        Field colsField = gameLogic.getClass().getDeclaredField("cols");
        colsField.setAccessible(true);
        colsField.set(gameLogic, 3);
        Field moveCounterField = gameLogic.getClass().getDeclaredField("moveCounter");
        moveCounterField.setAccessible(true);
        moveCounterField.set(gameLogic, moveCounter);
    }

    @Test
    public void testCheckGameStateXsWinHorizontal()
                                       throws NoSuchFieldException, IllegalAccessException {
        String[][] state = new String[3][3];
        for (int i = 0; i < 3; i++) {
            state[0][i] = "X";
            for (int j = 1; j < 3; j++) {
                state[j][i] = "";
            }
        }
        state[1][0] = "O";
        state[1][1] = "O";

        GameLogic gameLogic = new GameLogic();
        initializeGameLogic(gameLogic, false, 5);

        assertEquals(Result.X_WIN, gameLogic.checkGameState(state, 3, 2, 0));
    }

    @Test
    public void testCheckGameStateOsWinVertical()
            throws NoSuchFieldException, IllegalAccessException {
        String[][] state = new String[3][3];
        for (int i = 0; i < 3; i++) {
            state[i][1] = "O";
            state[i][0] = "";
            state[i][2] = "";
        }
        state[1][0] = "X";
        state[2][0] = "X";
        state[2][2] = "X";

        GameLogic gameLogic = new GameLogic();
        initializeGameLogic(gameLogic, true, 6);

        assertEquals(Result.O_WIN, gameLogic.checkGameState(state, 3, 1, 0));
    }

    @Test
    public void testCheckGameStateXsWinDiagonal()
            throws NoSuchFieldException, IllegalAccessException {
        String[][] state = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; ++j) {
                state[i][j] = i == j ? "X" : "";
            }
        }
        state[1][2] = "O";
        state[0][1] = "O";

        GameLogic gameLogic = new GameLogic();
        initializeGameLogic(gameLogic, false, 5);

        assertEquals(Result.X_WIN, gameLogic.checkGameState(state, 3, 2, 2));
    }

    @Test
    public void testCheckGameStateDraw()
            throws NoSuchFieldException, IllegalAccessException {
        String[][] state = new String[3][3];
        state[0][0] = "X";
        state[0][1] = "X";
        state[0][2] = "O";
        state[1][0] = "O";
        state[1][1] = "X";
        state[1][2] = "X";
        state[2][0] = "X";
        state[2][1] = "O";
        state[2][2] = "O";


        GameLogic gameLogic = new GameLogic();
        initializeGameLogic(gameLogic, false, 9);

        assertEquals(Result.DRAW, gameLogic.checkGameState(state, 3, 0, 0));
    }

    // check game state after one move
    @Test
    public void testCheckGameStateOneMove()
            throws NoSuchFieldException, IllegalAccessException {
        String[][] state = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                state[i][j] = "";
            }
        }
        state[1][1] = "X";

        GameLogic gameLogic = new GameLogic();
        initializeGameLogic(gameLogic, false, 1);

        assertEquals(Result.IN_PROCESS, gameLogic.checkGameState(state, 3, 1, 1));
    }

    @Test
    public void testCheckGameStateInProcess()
            throws NoSuchFieldException, IllegalAccessException {
        String[][] state = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                state[i][j] = "";
            }
        }
        state[0][0] = "O";
        state[0][1] = "X";
        state[1][1] = "O";
        state[2][0] = "X";
        state[2][1] = "O";
        state[2][2] = "X";

        GameLogic gameLogic = new GameLogic();
        initializeGameLogic(gameLogic, true, 6);

        assertEquals(Result.IN_PROCESS, gameLogic.checkGameState(state, 3, 0, 0));
    }

    @Test
    public void testIsInBorders()
            throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        GameLogic gameLogic = new GameLogic();
        Method isInBordersMethod = gameLogic.getClass().getDeclaredMethod("isInBorders", int.class, int.class);
        isInBordersMethod.setAccessible(true);
        initializeGameLogic(gameLogic, true, 0);

        assertEquals(true, isInBordersMethod.invoke(gameLogic, 0, 0));
        assertEquals(true, isInBordersMethod.invoke(gameLogic, 2, 2));
        assertEquals(true, isInBordersMethod.invoke(gameLogic, 1, 2));
        assertEquals(true, isInBordersMethod.invoke(gameLogic, 0, 1));
        assertEquals(false, isInBordersMethod.invoke(gameLogic, 2, 3));
        assertEquals(false, isInBordersMethod.invoke(gameLogic, -1, 0));
        assertEquals(false, isInBordersMethod.invoke(gameLogic, 4, 2));
    }

    @Test
    public void testPickTurnFromOptions() throws NoSuchFieldException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException,
            InstantiationException {
        String[][] state = new String[3][3];
        state[0][0] = "";
        state[0][1] = "";
        state[0][2] = "O";
        state[1][0] = "O";
        state[1][1] = "X";
        state[1][2] = "X";
        state[2][0] = "";
        state[2][1] = "X";
        state[2][2] = "";

        GameLogic gameLogic = new GameLogic();
        Method pickTurnFromOptionsMethod = gameLogic.getClass()
                .getDeclaredMethod("pickTurnFromOptions", String[][].class, List.class);
        pickTurnFromOptionsMethod.setAccessible(true);
        initializeGameLogic(gameLogic, false, 5);

        Class[] classes = gameLogic.getClass().getDeclaredClasses();
        Class<?> intPairClass = Arrays.stream(classes).filter(c -> c.getSimpleName().equals("IntPair"))
                .findFirst().orElse(null);
        if (intPairClass == null) {
            fail();
        }

        Constructor<?> constructor = intPairClass.getConstructors()[0];
        constructor.setAccessible(true);

        for (int k = 0; k < 10; k++) {
            int number = (int) pickTurnFromOptionsMethod.invoke(gameLogic, state,
                    Arrays.asList(constructor.newInstance(gameLogic, 0, 0),
                            constructor.newInstance(gameLogic, 0, 1),
                            constructor.newInstance(gameLogic, 2, 0),
                            constructor.newInstance(gameLogic, 2, 2)));
            int i = number / 3;
            int j = number % 3;
            assertEquals("", state[i][j]);
        }
    }

    @Test
    public void testFindNearestEmptyCorner() throws NoSuchFieldException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        String[][] state = new String[3][3];
        state[0][0] = "";
        state[0][1] = "";
        state[0][2] = "O";
        state[1][0] = "";
        state[1][1] = "X";
        state[1][2] = "X";
        state[2][0] = "";
        state[2][1] = "";
        state[2][2] = "";

        GameLogic gameLogic = new GameLogic();
        Method findNearestEmptyCornerMethod = gameLogic.getClass()
                .getDeclaredMethod("findNearestEmptyCorner", String[][].class, int.class, int.class);
        findNearestEmptyCornerMethod.setAccessible(true);
        initializeGameLogic(gameLogic, false, 3);

        assertEquals(8, findNearestEmptyCornerMethod.invoke(gameLogic, state, 2, 1));

        state[1][0] = "O";
        int number = (int) findNearestEmptyCornerMethod.invoke(gameLogic, state, 0, 1);
        assertTrue(number == 0 || number == 6);

        state[0][1] = "X";
        assertEquals(0, findNearestEmptyCornerMethod.invoke(gameLogic, state, 1, 0));
    }

    @Test
    public void testMoveToFreeEdgeCell() throws NoSuchFieldException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        String[][] state = new String[3][3];
        state[0][0] = "";
        state[0][1] = "";
        state[0][2] = "O";
        state[1][0] = "";
        state[1][1] = "X";
        state[1][2] = "X";
        state[2][0] = "";
        state[2][1] = "";
        state[2][2] = "";

        GameLogic gameLogic = new GameLogic();
        Method moveToFreeEdgeCellMethod = gameLogic.getClass()
                .getDeclaredMethod("moveToFreeEdgeCell", String[][].class);
        moveToFreeEdgeCellMethod.setAccessible(true);
        initializeGameLogic(gameLogic, false, 3);

        for (int i = 0; i < 10; i++) {
            int number = (int) moveToFreeEdgeCellMethod.invoke(gameLogic, (Object) state);
            assertTrue(number == 1 || number == 3 || number == 7);
        }

        state[1][0] = "O";
        for (int i = 0; i < 10; i++) {
            int number = (int) moveToFreeEdgeCellMethod.invoke(gameLogic, (Object) state);
            assertTrue(number == 1 || number == 7);
        }

        state[0][1] = "X";
        for (int i = 0; i < 10; i++) {
            int number = (int) moveToFreeEdgeCellMethod.invoke(gameLogic, (Object) state);
            assertTrue(number == 7);
        }

        state[2][1] = "O";
        for (int i = 0; i < 10; i++) {
            int number = (int) moveToFreeEdgeCellMethod.invoke(gameLogic, (Object) state);
            assertTrue(number == -1);
        }
    }

    @Test
    public void testLookForWinOrBlock() throws NoSuchFieldException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        String[][] state = new String[3][3];
        state[0][0] = "";
        state[0][1] = "";
        state[0][2] = "O";
        state[1][0] = "";
        state[1][1] = "X";
        state[1][2] = "X";
        state[2][0] = "";
        state[2][1] = "";
        state[2][2] = "";

        GameLogic gameLogic = new GameLogic();
        Method lookForWinOrBlock = gameLogic.getClass()
                .getDeclaredMethod("lookForWinOrBlock", String[][].class, String.class);
        lookForWinOrBlock.setAccessible(true);
        initializeGameLogic(gameLogic, false, 3);

        assertEquals(3, lookForWinOrBlock.invoke(gameLogic, state, "X"));

        state[0][0] = "X";
        state[1][0] = "X";
        state[1][2] = "O";

        assertEquals(8, lookForWinOrBlock.invoke(gameLogic, state, "O"));

        state[0][0] = "O";
        state[0][1] = "X";
        state[2][1] = "O";
        state[2][2] = "X";

        assertEquals(-1, lookForWinOrBlock.invoke(gameLogic, state, "O"));
        assertEquals(-1, lookForWinOrBlock.invoke(gameLogic, state, "X"));
    }

    @Test
    public void testNextTurnComputerHardMode()
            throws NoSuchFieldException, IllegalAccessException {
        String[][] state = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                state[i][j] = "";
            }
        }

        state[1][1] = "X";

        GameLogic gameLogic = new GameLogic();
        initializeGameLogic(gameLogic, true, 1);

        for (int i = 0; i < 10; i++) {
            int turn = (int) gameLogic.nextTurnComputerHardMode(state, 1, 1);
            assertTrue(turn == 0 || turn == 2 || turn == 6 || turn == 8);
        }

        state[0][2] = "O";
        state[1][2] = "X";
        initializeGameLogic(gameLogic, true, 3);
        assertEquals(3, gameLogic.nextTurnComputerHardMode(state, 2, 1));

        state[1][0] = "O";
        state[0][1] = "X";
        initializeGameLogic(gameLogic, true, 5);
        assertEquals(7, gameLogic.nextTurnComputerHardMode(state, 1, 0));

        state[2][1] = "O";
        state[2][2] = "X";
        initializeGameLogic(gameLogic, true, 7);
        assertEquals(0, gameLogic.nextTurnComputerHardMode(state, 0, 0));
    }
}