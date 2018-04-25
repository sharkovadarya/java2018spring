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
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class provides controls UI methods and interacts with game logic.
 */
public class Controller {

    private final static int rows = 3;
    private final static int cols = 3;
    private final static int winValue = 3;

    private GridPane pane;
    private VBox vBox;

    private GameMode mode = GameMode.HOT_SEAT;

    private Statistics statistics = new Statistics();
    private GameLogic logic = new GameLogic();

    // used for FXML loading
    public Controller() {}

    public void initializeController(GridPane gridPane, VBox v) {
        logic.setRows(rows);
        logic.setCols(cols);
        this.pane = gridPane;
        this.vBox = v;
    }

    /**
     * This method initializes UI elements of the window.
     */
    public void initializeScene() {
        initializeGridPane();
        initializeVBoxButtons();
        setTextAreaText("Default mode:\nHot Seat");
    }

    /**
     * This method initializes GridPane board buttons.
     */
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

    /**
     * This method initializes VBox side menu buttons.
     */
    private void initializeVBoxButtons() {
        Button hotSeatButton = (Button) vBox.getChildren().get(0);
        hotSeatButton.setOnAction(e -> {
            mode = GameMode.HOT_SEAT;
            restart();
        });

        Button easyBotButton = (Button) vBox.getChildren().get(1);
        easyBotButton.setOnAction(e -> {
            mode = GameMode.COMPUTER_EASY;
            restart();
        });

        Button hardBotButton = (Button) vBox.getChildren().get(2);
        hardBotButton.setOnAction(e -> {
            mode = GameMode.COMPUTER_HARD;
            restart();
        });

        Button statisticsButton = (Button) vBox.getChildren().get(3);
        statisticsButton.setOnAction(e -> showStatistics());
    }

    /**
     * This method creates a window displaying game session statistics.
     */
    private void showStatistics() {
        try {
            Parent root = FXMLLoader.load(getClass().getClassLoader()
                                  .getResource("statistics.fxml"));
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

    /**
     * This method restores default values of the game logic and UI.
     */
    private void restart() {
        logic.setXsTurn(true);
        logic.setMoveCounter(0);
        clearGridPane();
        setTextAreaText();
        setButtonsDisable(false);
    }

    /**
     * This method removes all text from GridPane board.
     */
    private void clearGridPane() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Button button = (Button) pane.getChildren().get(i * rows + j);
                button.setText("");
            }
        }
    }

    /**
     * This method fills the side text area with provided text.
     * @param strings lines to fill the text area with
     */
    private void setTextAreaText(String... strings) {
        TextArea textArea = (TextArea) vBox.getChildren()
                      .get(vBox.getChildren().size() - 1);
        textArea.setFont(Font.font("Verdana", 18));
        textArea.clear();
        for (String str : strings) {
            textArea.appendText(str + "\n");
        }
        textArea.appendText((logic.getXsTurn() ? "X" : "O") + "'s turn.");
    }

    /**
     * This method decides the action that happens on board button click.
     * The action is chosen based on the current game mode,
     * but it always represents making a turn or finishing the game.
     */
    private void onBoardButtonAction(Button b) {

        if (b.getText().isEmpty()) {
            b.setText(logic.getXsTurn() ? "X" : "O");
            logic.setXsTurn(!logic.getXsTurn());
            logic.setMoveCounter(logic.getMoveCounter() + 1);
            setTextAreaText();

            // form a state for GameLogic
            String[][] state = new String[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Button button = (Button) pane.getChildren()
                                            .get(i * rows + j);
                    state[i][j] = button.getText();
                }
            }

            Result res = logic.checkGameState(state, winValue,
                               Character.getNumericValue(b.getId().charAt(7)),
                               Character.getNumericValue(b.getId().charAt(6)));
            if (res != Result.IN_PROCESS) {
                TextArea textArea = (TextArea) vBox.getChildren()
                               .get(vBox.getChildren().size() - 1);
                textArea.setFont(Font.font("Verdana", 18));
                if (res == Result.X_WIN) {
                    textArea.setText("X wins.");

                    // record statistics
                    if (mode == GameMode.HOT_SEAT) {
                        statistics.recordWin("main player", "2nd HS mode player");
                    } else if (mode == GameMode.COMPUTER_EASY) {
                        statistics.recordWin("main player", "easy mode bot");
                    } else if (mode == GameMode.COMPUTER_HARD) {
                        statistics.recordWin("main player", "hard mode bot");
                    }
                } else if (res == Result.O_WIN) {
                    textArea.setText("O wins.");

                    // record statistics
                    if (mode == GameMode.HOT_SEAT) {
                        statistics.recordWin("2nd HS mode player", "main player");
                    } else if (mode == GameMode.COMPUTER_EASY) {
                        statistics.recordWin("easy mode bot", "main player");
                    } else if (mode == GameMode.COMPUTER_HARD) {
                        statistics.recordWin("hard mode bot", "main player" );
                    }
                } else {
                    textArea.setText("Draw.");

                    // record statistics
                    if (mode == GameMode.HOT_SEAT) {
                        statistics.recordDraw("2nd HS mode player", "main player");
                    } else if (mode == GameMode.COMPUTER_EASY) {
                        statistics.recordDraw("easy mode bot", "main player");
                    } else if (mode == GameMode.COMPUTER_HARD) {
                        statistics.recordDraw("hard mode bot", "main player" );
                    }
                }

                // disable the buttons if the game is over
                setButtonsDisable(true);
            } else if (!logic.getXsTurn() && mode != GameMode.HOT_SEAT) {
                if (mode == GameMode.COMPUTER_EASY) {
                    int r = makeRandomTurn();
                    Button button = (Button) pane.getChildren().get(r);
                    onBoardButtonAction(button);
                } else if (mode == GameMode.COMPUTER_HARD) {
                    int t = logic.nextTurnComputerHardMode(state,
                            Character.getNumericValue(b.getId().charAt(7)),
                            Character.getNumericValue(b.getId().charAt(6)));
                    if (t != -1) {
                        onBoardButtonAction((Button) pane.getChildren().get(t));
                    } else {
                        int r = makeRandomTurn();
                        Button button = (Button) pane.getChildren().get(r);
                        onBoardButtonAction(button);
                    }
                }
            }

        } else {
            TextArea textArea = (TextArea) vBox.getChildren()
                          .get(vBox.getChildren().size() - 1);
            textArea.setText("Can't interact\nwith filled cells.");
            textArea.setFont(Font.font("Verdana", 18));
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e -> setTextAreaText());
            delay.play();
        }
    }

    /**
     * This method chooses a random free cell for the next turn.
     */
    private int makeRandomTurn() {
        int r = ThreadLocalRandom.current().nextInt(0, 9);
        Button button = (Button) pane.getChildren().get(r);
        while (!button.getText().isEmpty()) {
            r = ThreadLocalRandom.current().nextInt(0, 9);
            button = (Button) pane.getChildren().get(r);
        }

        return r;
    }

    /**
     * This method disables/enables all board buttons.
     * @param disable value that the setDisable() method accepts
     */
    private void setButtonsDisable(boolean disable) {
        for (Node node : pane.getChildren()) {
            Button button = (Button) node;
            button.setDisable(disable);
        }
    }

    private enum GameMode {HOT_SEAT, COMPUTER_EASY, COMPUTER_HARD}

}
