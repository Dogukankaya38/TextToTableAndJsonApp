package dogukan.texttotableandjsonapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.*;

public class TextToTableAndJsonApp extends Application {

    private TableView<TableRowData> tableView;
    private TextArea jsonTextArea;

    @Override
    public void start(Stage primaryStage) {
        // Load and set the icon
        primaryStage.getIcons().add(new Image("file:icon.png"));

        TextArea textArea = new TextArea();
        Button convertButton = new Button("Convert to Table and JSON");
        Button copyButton = new Button("Copy JSON to Clipboard");

        tableView = new TableView<>();
        jsonTextArea = new TextArea();
        jsonTextArea.setEditable(false);

        convertButton.setOnAction(event -> {
            String text = textArea.getText();
            List<TableRowData> tableData = convertTextToTableData(text);
            displayTableData(tableData);
            String jsonOutput = convertTableDataToJson(tableData);
            jsonTextArea.setText(jsonOutput);
        });

        copyButton.setOnAction(event -> {
            String jsonOutput = jsonTextArea.getText();
            copyToClipboard(jsonOutput);
        });

        VBox vbox = new VBox(10, textArea, convertButton, tableView, new Label("JSON Output:"), jsonTextArea, copyButton);
        Scene scene = new Scene(vbox, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Text to Table and JSON Converter");
        primaryStage.show();
    }

    private List<TableRowData> convertTextToTableData(String text) {
        List<TableRowData> tableData = new ArrayList<>();
        String[] lines = Arrays.stream(text.split("\n")).filter(s -> !s.isEmpty()).toArray(String[]::new);

        if (lines.length > 0) {
            String[] headers = lines[0].split("\t");

            for (int i = 1; i < lines.length; i++) {
                String[] values = lines[i].split("\t");
                TableRowData rowData = new TableRowData();
                for (int j = 0; j < headers.length; j++) {
                    if (values.length > j) {
                        rowData.addColumnData(headers[j], values[j]);
                    }
                    else {
                        rowData.addColumnData(headers[j], "");
                    }
                }
                tableData.add(rowData);
            }
        }

        return tableData;
    }

    private void displayTableData(List<TableRowData> tableData) {
        tableView.getColumns().clear();
        tableView.getItems().clear();

        if (!tableData.isEmpty()) {
            TableRowData firstRow = tableData.get(0);
            for (String header : firstRow.getHeaders()) {
                TableColumn<TableRowData, String> column = new TableColumn<>(header);
                column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getColumnData(header)));
                tableView.getColumns().add(column);
            }

            ObservableList<TableRowData> data = FXCollections.observableArrayList(tableData);
            tableView.setItems(data);
        }
    }

    private String convertTableDataToJson(List<TableRowData> tableData) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        List<Map<String, Object>> jsonList = new ArrayList<>();

        if (!tableData.isEmpty()) {
            List<String> headers = tableData.get(0).getHeaders();

            for (TableRowData rowData : tableData) {
                Map<String, Object> sortedDataMap = new LinkedHashMap<>();

                for (String header : headers) {
                    sortedDataMap.put(header, rowData.getDataMap().getOrDefault(header, ""));
                }

                jsonList.add(sortedDataMap);
            }
        }

        try {
            return objectMapper.writeValueAsString(jsonList);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }




    private void copyToClipboard(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
