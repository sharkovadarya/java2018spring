package ru.spbau.group202.sharkova.hw2xo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Comparator;
import java.util.HashMap;

public class Statistics {

    // In this array, 0 is for winsProperty, 1 for lossesProperty, 2 for drawsProperty
    private HashMap<String, int[]> records = new HashMap<>();
    private TableView<TableRecord> table;

    public void setTable(TableView<TableRecord> table) {
        this.table = table;
    }

    public void recordWin(String winner, String loser) {
        int[] record = records.get(winner);
        if (record == null) {
            records.put(winner, new int[3]);
            record = records.get(winner);
        }
        record[0]++;
        records.put(winner, record);
        record = records.get(loser);
        if (record == null) {
            records.put(loser, new int[3]);
            record = records.get(loser);
        }
        record[1]++;
        records.put(loser, record);
    }

    public void recordDraw(String player1, String player2) {
        int[] record = records.get(player1);
        if (record == null) {
            records.put(player1, new int[3]);
            record = records.get(player1);
        }
        record[2]++;
        records.put(player1, record);
        record = records.get(player2);
        if (record == null) {
            records.put(player2, new int[3]);
            record = records.get(player2);
        }
        record[2]++;
        records.put(player2, record);
    }

    public void initializeTable() {
        ObservableList<TableRecord> data = FXCollections.observableArrayList();
        for (HashMap.Entry<String, int[]> entry : records.entrySet()) {
            data.add(new TableRecord(0, entry.getKey(), entry.getValue()[0],
                                        entry.getValue()[1], entry.getValue()[2]));

        }

        for (int i = 0; i < data.size(); i++) {
            data.get(i).setNumberProperty(Integer.toString(i + 1));
        }

        TableColumn number = new TableColumn("Number");
        number.setCellValueFactory(new PropertyValueFactory<>("numberProperty"));
        TableColumn player = new TableColumn("Player");
        player.setCellValueFactory(new PropertyValueFactory<>("playerProperty"));
        TableColumn wins = new TableColumn("Wins");
        wins.setCellValueFactory(new PropertyValueFactory<>("winsProperty"));
        TableColumn losses = new TableColumn("Losses");
        losses.setCellValueFactory(new PropertyValueFactory<>("lossesProperty"));
        TableColumn draws = new TableColumn("Draws");
        draws.setCellValueFactory(new PropertyValueFactory<>("drawsProperty"));

        data.sort(Comparator.comparing(TableRecord::getWinsProperty).reversed());

        table.setItems(data);
        table.getColumns().addAll(number, player, wins, losses, draws);

    }

    public class TableRecord {
        private StringProperty numberProperty;
        private StringProperty playerProperty;
        private StringProperty winsProperty;
        private StringProperty lossesProperty;
        private StringProperty drawsProperty;

        public TableRecord(int number, String playerName, int winsN, int lossesN, int drawsN) {
            this.numberProperty = new SimpleStringProperty(Integer.toString(number));
            this.playerProperty = new SimpleStringProperty(playerName);
            this.winsProperty = new SimpleStringProperty(Integer.toString(winsN));
            this.lossesProperty = new SimpleStringProperty(Integer.toString(lossesN));
            this.drawsProperty = new SimpleStringProperty(Integer.toString(drawsN));
        }

        public void setNumberProperty(String number) {
            this.numberProperty.set(number);
        }

        public String getNumberProperty() {
            return this.numberProperty.get();
        }

        public StringProperty numberProperty() {
            return this.numberProperty;
        }

        public void setPlayerProperty(String player) {
            this.playerProperty.set(player);
        }

        public String getPlayerProperty() {
            return this.playerProperty.get();
        }

        public StringProperty playerProperty() {
            return this.playerProperty;
        }

        public void setWinsProperty(String wins) {
            this.winsProperty.set(wins);
        }

        public String getWinsProperty() {
            return this.winsProperty.get();
        }

        public StringProperty winsProperty() {
            return this.winsProperty;
        }

        public void setLossesProperty(String losses) {
            this.lossesProperty.set(losses);
        }

        public String getLossesProperty() {
            return this.lossesProperty.get();
        }

        public StringProperty lossesProperty() {
            return this.lossesProperty;
        }

        public void setDrawsProperty(String draws) {
            this.drawsProperty.set(draws);
        }

        public String getDrawsProperty() {
            return this.drawsProperty.get();
        }

        public StringProperty drawsProperty() {
            return this.drawsProperty;
        }
    }
}
