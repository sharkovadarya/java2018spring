package ru.spbau.group202.sharkova.hw2xo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader()
                              .getResource("mainscreen.fxml"));

        primaryStage.setTitle("XO");
        primaryStage.setMinHeight(200);
        primaryStage.setMinWidth(200);


        Controller controller = new Controller();
        controller.initializeController((GridPane) root.lookup("#boardGridPane"),
                                                (VBox) root.lookup("#menuVBox"));
        controller.initializeScene();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
