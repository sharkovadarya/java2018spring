package ru.spbau.group202.sharkova.ftp.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import org.jetbrains.annotations.NotNull;
import ru.spbau.group202.sharkova.ftp.client.Client;
import ru.spbau.group202.sharkova.ftp.client.FileEntry;
import ru.spbau.group202.sharkova.ftp.utils.exceptions.NotADirectoryException;
import ru.spbau.group202.sharkova.ftp.utils.exceptions.ftp.FTPException;

import java.util.List;

/**
 * Represents a custom treeView item corresponding to a file.
 */
public class FilesTreeViewItem extends TreeItem<TreeViewFile> {
    private boolean isLeaf;

    @NotNull
    private Client client;

    private boolean firstRequest = true;

    public FilesTreeViewItem(@NotNull final TreeViewFile file,
                             boolean isDir, @NotNull Client client) {
        super(file);
        this.client = client;
        isLeaf = !isDir;
    }

    /**
     * This method gets current treeView node children.
     * @return list of current node children
     */
    @Override
    public ObservableList<TreeItem<TreeViewFile>> getChildren() {
        if (firstRequest) {
            try {
                super.getChildren().setAll(buildChildren(this));
                firstRequest = false;
            } catch (FTPException|NotADirectoryException e) {
                setExpanded(false);
            }
        }
        return super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        return isLeaf;
    }

    @NotNull
    private ObservableList<TreeItem<TreeViewFile>> buildChildren(
            @NotNull TreeItem<TreeViewFile> treeItem)
            throws FTPException, NotADirectoryException {
        TreeViewFile file = treeItem.getValue();

        ObservableList<TreeItem<TreeViewFile>> children = FXCollections.observableArrayList();

        if (treeItem.isLeaf() || file == null) {
            return children;
        }

        List<FileEntry> files = client.list(file.getAbsolutePath());

        for (FileEntry entry : files) {
            TreeViewFile childFile =
                    new TreeViewFile(file.toPath().resolve(entry.getFilename()).toString());
            children.add(new FilesTreeViewItem(childFile, entry.isDirectory(), client));
        }
        return children;
    }
}