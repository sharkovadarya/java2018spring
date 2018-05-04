package ru.spbau.group202.sharkova.cw2;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameLogic {

    private boolean firstButtonPress;
    private int boardDimension;
    private int boardSize;
    private int takenBoardCells;
    private int prevTurn;

    private HashMap<Integer, Integer> buttonNumbers = new HashMap<>();

    public GameLogic(int n) {
        firstButtonPress = true;
        boardDimension = n;
        boardSize = n * n;
        takenBoardCells = 0;
    }

    public void generateNumbers() {
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < (boardDimension * boardDimension) / 2; i++) {
            Integer r = ThreadLocalRandom.current()
                    .nextInt(0, (boardDimension * boardDimension) / 2 + 1);
            numbers.add(r);
        }

        numbers.addAll(new ArrayList<>(numbers));
        Collections.shuffle(numbers);

        for (int i = 0; i < boardDimension; i++) {
            for (int j = 0; j < boardDimension; j++) {
                buttonNumbers.put(i * boardDimension + j, numbers.get(i * boardDimension + j));
            }
        }
    }

    public GameState assessTurn(int n) {
        if (firstButtonPress) {
            prevTurn = n;
            firstButtonPress = false;
            return GameState.TURN;
        }

        firstButtonPress = true;
        if (buttonNumbers.get(prevTurn).equals(buttonNumbers.get(n))) {
            takenBoardCells += 2;
            return takenBoardCells == boardSize ? GameState.FINISHED : GameState.IN_PROCESS_EQ;
        }

        return GameState.IN_PROCESS_NEQ;
    }

    public boolean isFinished() {
        return takenBoardCells == boardSize;
    }

    public int getNumber(int n) {
        return buttonNumbers.get(n);
    }

    public int getPrevious() {
        return prevTurn;
    }

}
