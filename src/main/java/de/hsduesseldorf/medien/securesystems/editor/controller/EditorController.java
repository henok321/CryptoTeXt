package de.hsduesseldorf.medien.securesystems.editor.controller;

import de.hsduesseldorf.medien.securesystems.editor.app.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

import java.net.URL;
import java.util.ResourceBundle;

public class EditorController implements Initializable, MainAppSettable {

    MainApp mainApp;

    @FXML
    MenuItem menuFileSave;

    @FXML
    MenuItem menuFileSaveAs;

    @FXML
    MenuItem menuFileOpen;

    @FXML
    MenuItem menuFileQuit;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuFileOpen.setOnAction(e -> open());
        menuFileSave.setOnAction(e -> save());
        menuFileSaveAs.setOnAction(e -> saveAs());
        menuFileQuit.setOnAction(e -> quit());
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    void save() {
        mainApp.getOptionsDialog().show();
    }

    void saveAs() {

    }

    void open() {

    }

    void quit() {
    }
}
