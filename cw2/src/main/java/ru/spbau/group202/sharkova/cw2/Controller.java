package ru.spbau.group202.sharkova.cw2;

import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class Controller {

    private int numberOfButtons;

    private GridPane pane;
    private VBox vBox;

    private GameLogic logic;

    private boolean firstButtonClick = true;

    public void initializeController(GridPane gridPane, VBox v, int numberOfButtons) {
        this.pane = gridPane;
        this.vBox = v;
        this.numberOfButtons = numberOfButtons;
        if (numberOfButtons % 2 == 0) {
            resetGame();
            initializeGridPane();
            logic = new GameLogic(numberOfButtons);
            logic.generateNumbers();
        } else {
            setTextArea("Could not\ngenerate\nthe board.\nThe number\n must be even");
        }
    }

    private void resetGridPane() {
        pane.getChildren().clear();
    }

    private void initializeGridPane() {
        for (int i = 0; i < numberOfButtons; i++) {
            for (int j = 0; j < numberOfButtons; j++) {
                Button button = new Button();
                button.setId("" + (i * numberOfButtons + j));
                button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                button.setMinSize(0, 0);
                button.setOnAction(e -> onBoardButtonAction(button));
                GridPane.setHgrow(button, Priority.ALWAYS);
                GridPane.setVgrow(button, Priority.ALWAYS);
                pane.add(button, j, i);
            }
        }
    }

    private void onBoardButtonAction(Button b) {
        if (firstButtonClick) {
            firstButtonClick = false;
            for (int i = 0; i < numberOfButtons; i++) {
                for (int j = 0; j < numberOfButtons; j++) {
                    Button button = (Button) pane.getChildren().get(i * numberOfButtons + j);
                    button.setStyle(String.format("-fx-font-size: %dpx;", (int)(0.2 * b.getWidth())));
                }
            }
        }

        int id = Integer.parseInt(b.getId());
        GameState state = logic.assessTurn(id);
        b.setText("" + logic.getNumber(id));
        switch (state) {
            case IN_PROCESS_NEQ:
                pane.setDisable(true);
                PauseTransition pause = new PauseTransition(Duration.seconds(0.25));
                pause.setOnFinished(e -> {
                    pane.setDisable(false);
                    setTextArea("Numbers\ndon't match");
                    Button prev = (Button) pane.getChildren().get(logic.getPrevious());
                    prev.setText("");
                    b.setText("");
                    b.setDisable(false);
                    prev.setDisable(false);
                });
                pause.play();
                break;
            case IN_PROCESS_EQ:
                pane.setDisable(true);
                pause = new PauseTransition(Duration.seconds(0.25));
                pause.setOnFinished(e -> {
                    pane.setDisable(false);
                    Button prev = (Button) pane.getChildren().get(logic.getPrevious());
                    prev.setDisable(true);
                    b.setDisable(true);
                    setTextArea(logic.isFinished() ?
                            "Congratulations!\nYou've finished\nthe game" : "Numbers\nmatched");
                });
                pause.play();
                break;
            case FINISHED:
                pane.setDisable(true);
                setTextArea("Congratulations!\nYou've finished\nthe game");
                break;
            case TURN:
                pane.setDisable(true);
                pause = new PauseTransition(Duration.seconds(0.25));
                pause.setOnFinished(e -> {
                    pane.setDisable(false);
                    b.setDisable(true);
                    setTextArea("Select\nthe next\nbutton");
                });
                pause.play();
                break;
        }
    }

    private void setTextArea(String s) {
        TextArea textArea = (TextArea) vBox.getChildren()
                .get(vBox.getChildren().size() - 1);
        textArea.setFont(Font.font("Verdana", 18));
        textArea.clear();
        textArea.appendText(s + "\n");
    }

    private void resetGame() {
        resetGridPane();
        firstButtonClick = true;
    }
}
