package ru.spbau.group202.sharkova.ftp.gui;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Wrapper class for TreeView file.
 * Is used for its overridden toString() method.
 */
public class TreeViewFile extends File {
    private boolean isRoot;

    TreeViewFile(@NotNull String string, boolean isRoot) {
        super(string);
        this.isRoot = isRoot;
    }

    TreeViewFile(@NotNull String string) {
        super(string);
    }

    @Override
    public String toString() {
        return isRoot ? super.toString() : getName();
    }
}
