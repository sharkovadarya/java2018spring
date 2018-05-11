package ru.spbau.group202.sharkova.ftp.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * Application GUI client main class.
 */
public class GuiClient extends Application {

    private String rootDirectory;

    @Override
    @SuppressWarnings("unchecked")
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader()
                .getResource("gui/mainscreen.fxml"));
        stage.setTitle("FTP");
        stage.setMinWidth(100);
        stage.setMinHeight(200);

        getRootDirectory();

        Controller controller = new Controller();
        controller.initializeController((TreeView<TreeViewFile>) root.lookup("#filesTreeView"),
                (TextArea) root.lookup("#textArea"), rootDirectory);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void getRootDirectory() {
        TextInputDialog dialog = new TextInputDialog("/");
        dialog.setTitle("Choose root directory");
        dialog.setContentText("Input root directory:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            this.rootDirectory = result.get();
        } else {
            new Alert(Alert.AlertType.ERROR,
                    "No root directory provided; closing application").showAndWait();
            Platform.exit();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
