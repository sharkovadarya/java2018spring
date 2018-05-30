package ru.spbau.group202.sharkova.ftp.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import org.jetbrains.annotations.NotNull;
import ru.spbau.group202.sharkova.ftp.utils.Protocol;
import ru.spbau.group202.sharkova.ftp.client.Client;
import ru.spbau.group202.sharkova.ftp.utils.exceptions.UnableToSaveFileException;
import ru.spbau.group202.sharkova.ftp.utils.exceptions.ftp.FTPConnectionException;
import ru.spbau.group202.sharkova.ftp.utils.exceptions.ftp.FTPException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * FXML controller class.
 */
public class Controller {

    @FXML
    private TreeView<TreeViewFile> filesTreeView;
    @FXML
    private TextArea textArea;
    private Client client;

    /**
     * Initializes the used fields.
     * @param filesTreeView layout TreeView object
     * @param textArea layout TextArea object
     * @param rootDirectory TreeView root directory string
     */
    public void initializeController(@NotNull TreeView<TreeViewFile> filesTreeView,
                                     @NotNull TextArea textArea, @NotNull String rootDirectory) {
        this.textArea = textArea;

        try {
            initializeClient();
            this.filesTreeView = filesTreeView;
            this.filesTreeView.setRoot(new FilesTreeViewItem(new TreeViewFile(rootDirectory, true), true, client));
            setTextAreaContent("Double click", "on file", "to download it", "(get request)",
                    "\nDouble click", "on directory", "to list files", "(list request)");
        } catch (FTPConnectionException e) {
            setTextAreaContent("Couldn't connect", "to the server.\n", "If the server", "is not running,", "start it.");
        }
    }

    @FXML
    private void onMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            get();
        }
    }

    @FXML
    private void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
           get();
        }
    }

    private void get() {
        TreeItem<TreeViewFile> item = filesTreeView.getSelectionModel().getSelectedItem();
        if (item == null || !item.isLeaf()) {
            return;
        }

        File toSave = item.getValue();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(toSave.getName());
        File saved = fileChooser.showSaveDialog(null);

        if (saved == null) {
            setTextAreaContent("No download", "directory specified.");
            return;
        }

        try {
            if (client == null) {
                initializeClient();
            }
            byte[] content = client.get(toSave.getAbsolutePath());
            Files.write(saved.toPath(), content);
        } catch (FTPException e) {
            setTextAreaContent("Couldn't",  "get files", "from server.");
        } catch (UnableToSaveFileException e) {
            setTextAreaContent("Couldn't", "download file", e.getMessage());
        } catch (IOException e) {
            setTextAreaContent("Couldn't", "download file", "from server.");
        }
    }

    private void initializeClient() throws FTPConnectionException {
        client = new Client(Protocol.HOST, Protocol.PORT);
        client.connect();
    }

    private void setTextAreaContent(String... args) {
        textArea.setFont(Font.font("Verdana", 18));
        textArea.clear();
        for (String str : args) {
            textArea.appendText(str + "\n");
        }
    }
}
