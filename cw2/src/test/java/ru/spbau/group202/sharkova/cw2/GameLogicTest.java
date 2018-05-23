package ru.spbau.group202.sharkova.cw2;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import static org.junit.Assert.*;

public class GameLogicTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testBoard2x2() throws NoSuchFieldException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        GameLogic logic = new GameLogic(2);
        logic.generateNumbers();
        Field buttonNumbersField = logic.getClass().getDeclaredField("buttonNumbers");
        buttonNumbersField.setAccessible(true);
        HashMap<Integer, Integer> buttonNumbers = (HashMap<Integer, Integer>) buttonNumbersField.get(logic);
        Method assessTurnMethod = logic.getClass().getDeclaredMethod("assessTurn", int.class);
        assessTurnMethod.setAccessible(true);
        assertEquals(GameState.TURN, assessTurnMethod.invoke(logic, 0));
        int val0 = buttonNumbers.get(0);
        int val1 = buttonNumbers.get(1);
        int val2 = buttonNumbers.get(2);
        int val3 = buttonNumbers.get(3);

        if (val0 == val1) {
            assertEquals(GameState.IN_PROCESS_EQ, assessTurnMethod.invoke(logic, 1));
            assertEquals(GameState.TURN, assessTurnMethod.invoke(logic, 2));
            assertEquals(GameState.FINISHED, assessTurnMethod.invoke(logic, 3));
        } else if (val0 == val2) {
            assertEquals(GameState.IN_PROCESS_EQ, assessTurnMethod.invoke(logic, 2));
            assertEquals(GameState.TURN, assessTurnMethod.invoke(logic, 1));
            assertEquals(GameState.FINISHED, assessTurnMethod.invoke(logic, 3));
        } else if (val0 == val3) {
            assertEquals(GameState.IN_PROCESS_EQ, assessTurnMethod.invoke(logic, 3));
            assertEquals(GameState.TURN, assessTurnMethod.invoke(logic, 1));
            assertEquals(GameState.FINISHED, assessTurnMethod.invoke(logic, 2));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBoard4x4() throws NoSuchFieldException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        GameLogic logic = new GameLogic(4);
        Field buttonNumbersField = logic.getClass().getDeclaredField("buttonNumbers");
        buttonNumbersField.setAccessible(true);
        HashMap<Integer, Integer> buttonNumbers = new HashMap<>();
        buttonNumbers.put(0, 0);
        buttonNumbers.put(1, 1);
        buttonNumbers.put(2, 4);
        buttonNumbers.put(3, 7);
        buttonNumbers.put(4, 1);
        buttonNumbers.put(5, 3);
        buttonNumbers.put(6, 2);
        buttonNumbers.put(7, 6);
        buttonNumbers.put(8, 5);
        buttonNumbers.put(9, 4);
        buttonNumbers.put(10, 5);
        buttonNumbers.put(11, 3);
        buttonNumbers.put(12, 7);
        buttonNumbers.put(13, 0);
        buttonNumbers.put(14, 2);
        buttonNumbers.put(15, 6);
        buttonNumbersField.set(logic, buttonNumbers);

        Method assessTurnMethod = logic.getClass().getDeclaredMethod("assessTurn", int.class);
        assessTurnMethod.setAccessible(true);
        assertEquals(GameState.TURN, assessTurnMethod.invoke(logic, 0));
        assertEquals(GameState.IN_PROCESS_NEQ, assessTurnMethod.invoke(logic, 5));
        assertEquals(GameState.TURN, assessTurnMethod.invoke(logic, 0));
        assertEquals(GameState.IN_PROCESS_EQ, assessTurnMethod.invoke(logic, 13));
        assertEquals(GameState.TURN, assessTurnMethod.invoke(logic, 7));
        assertEquals(GameState.IN_PROCESS_EQ, assessTurnMethod.invoke(logic, 15));
        assertEquals(GameState.TURN, assessTurnMethod.invoke(logic, 12));
        assertEquals(GameState.IN_PROCESS_NEQ, assessTurnMethod.invoke(logic, 14));
        assertEquals(GameState.TURN, assessTurnMethod.invoke(logic, 12));
        assertEquals(GameState.IN_PROCESS_NEQ, assessTurnMethod.invoke(logic, 6));
        assertEquals(GameState.TURN, assessTurnMethod.invoke(logic, 14));
        assertEquals(GameState.IN_PROCESS_EQ, assessTurnMethod.invoke(logic, 6));
    }

}