package ru.spbau.group202.sharkova.cw2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    private static int n;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader()
                .getResource("mainscreen.fxml"));

        primaryStage.setTitle("Pairs");

        Controller controller = new Controller();
        controller.initializeController((GridPane) root.lookup("#boardGridPane"),
                (VBox) root.lookup("#menuVBox"), n);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Parameter: board size N, as in N x N board");
            System.exit(0);
        }
        n = Integer.parseInt(args[0]);
        launch(args);
    }
}
