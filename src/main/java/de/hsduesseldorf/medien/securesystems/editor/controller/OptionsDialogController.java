package de.hsduesseldorf.medien.securesystems.editor.controller;

import de.hsduesseldorf.medien.securesystems.editor.app.MainApp;
import de.hsduesseldorf.medien.securesystems.editor.model.CipherName;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;

/** This controller handles all events on the options dialog */
public class OptionsDialogController implements Initializable {

  MainApp mainApp;
  CipherName selectedCipherName;

  @FXML ComboBox<CipherName> chipherSelection;

  @FXML PasswordField password;

  @FXML Button submitOptions;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    submitOptions.setOnAction(evt -> submit());
    chipherSelection.getItems().setAll(CipherName.values());
  }

  public void submit() {
    this.selectedCipherName = chipherSelection.getValue();
    mainApp.getOptionsDialog().hide();
  }

  public void setMainApp(MainApp mainApp) {
    this.mainApp = mainApp;
  }

  public CipherName getSelectedCipherName() {
    return chipherSelection.getValue();
  }

  public void setSelectedCipherName(CipherName selectedCipherName) {
    chipherSelection.setValue(selectedCipherName);
  }

  public String getPassword() {
    return this.password.getText();
  }

  public void setPassword(String passwordString) {
    password.setText(passwordString);
  }

  public void disableComboBox(boolean disabled) {
    chipherSelection.setDisable(disabled);
  }
}
