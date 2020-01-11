package de.hsduesseldorf.medien.securesystems.editor.controller;

import static javafx.scene.control.Alert.AlertType.*;

import de.hsduesseldorf.medien.securesystems.editor.app.MainApp;
import de.hsduesseldorf.medien.securesystems.editor.exception.InvalidChecksum;
import de.hsduesseldorf.medien.securesystems.editor.model.Document;
import de.hsduesseldorf.medien.securesystems.editor.service.encryptor.DocumentEncryptor;
import de.hsduesseldorf.medien.securesystems.editor.service.encryptor.DocumentEncryptorFactory;
import java.io.File;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.crypto.BadPaddingException;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The main controller handles all the events from the editors ui. */
public class EditorController implements Initializable {

  static final Logger LOG = LoggerFactory.getLogger(EditorController.class);

  MainApp mainApp;
  OptionsDialogController optionsDialogController;
  Document currentDocument;
  boolean abort;

  @FXML MenuItem menuFileSave;

  @FXML MenuItem menuFileSaveAs;

  @FXML MenuItem menuFileOpen;

  @FXML MenuItem menuFileQuit;

  @FXML TextArea text;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    menuFileOpen.setOnAction(evt -> open());
    menuFileSave.setOnAction(evt -> save());
    menuFileSaveAs.setOnAction(evt -> saveAs());
    menuFileQuit.setOnAction(evt -> quit());
  }

  public void setOptionsDialogController(OptionsDialogController optionsDialogController) {
    this.optionsDialogController = optionsDialogController;
  }

  public void setMainApp(MainApp mainApp) {
    this.mainApp = mainApp;
  }

  boolean save() {

    try {
      LOG.debug("Save..");
      if (currentDocument == null) {
        return this.saveAs();
      }

      optionsDialogController.setSelectedCipherName(currentDocument.getCipherName());
      mainApp.getOptionsDialog().showAndWait();

      byte[] data = text.getText().getBytes();
      Document document =
          new Document(
              new Date(),
              optionsDialogController.getSelectedCipherName(),
              data.length,
              data,
              currentDocument.getFile());

      if (document.getCipherName() != null) {
        DocumentEncryptor encryptor =
            DocumentEncryptorFactory.getInstance(
                optionsDialogController.getSelectedCipherName().name(),
                optionsDialogController.getPassword().toCharArray());
        document = encryptor.encrypt(document);
      }

      try {
        JAXB.marshal(document, currentDocument.getFile());
      } catch (DataBindingException e) {
        e.printStackTrace();
        return false;
      }
      return true;
    } catch (GeneralSecurityException e) {
      alertBox("Crypto error!", "An crypto error occured", ERROR);
      e.printStackTrace();
    } finally {
      optionsDialogController.setPassword(null);
    }
    return false;
  }

  boolean saveAs() {
    try {
      LOG.debug("Save as..");
      this.abort = false;
      Stage optionsDialog = mainApp.getOptionsDialog();

      optionsDialog.setOnCloseRequest(
          evt -> {
            this.abort = true;
          });

      optionsDialog.showAndWait();

      if (abort) return false;

      FileChooser fileChooser = new FileChooser();
      if (currentDocument != null) {
        fileChooser.setInitialDirectory(currentDocument.getFile().getParentFile());
        fileChooser.setInitialFileName(currentDocument.getFile().getName());
      } else {
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
      }
      fileChooser.setTitle("Save file");
      fileChooser
          .getExtensionFilters()
          .addAll(
              new FileChooser.ExtensionFilter("XML Files", "*.xml"),
              new FileChooser.ExtensionFilter("All Files", "*"));
      File file = fileChooser.showSaveDialog(new Stage());
      if (file == null) return false;

      byte[] data = text.getText().getBytes();
      Document document =
          new Document(
              new Date(), optionsDialogController.getSelectedCipherName(), data.length, data, file);

      if (document.getCipherName() != null) {
        DocumentEncryptor encryptor =
            DocumentEncryptorFactory.getInstance(
                optionsDialogController.getSelectedCipherName().name(),
                optionsDialogController.getPassword().toCharArray());
        document = encryptor.encrypt(document);
      }
      try {
        JAXB.marshal(document, file);
        currentDocument = document;
        currentDocument.setFile(file);
      } catch (DataBindingException e) {
        alertBox("Document error", "Could not save Document properly!", ERROR);
        e.printStackTrace();
        return false;
      }
      return true;
    } catch (GeneralSecurityException e) {
      alertBox("Crypto error!", "An crypto error occured", ERROR);
      e.printStackTrace();
    } finally {
      optionsDialogController.setPassword(null);
    }
    return false;
  }

  boolean open() {
    LOG.debug("Open..");
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Load file");
    fileChooser
        .getExtensionFilters()
        .addAll(
            new FileChooser.ExtensionFilter("XML Files", "*.xml"),
            new FileChooser.ExtensionFilter("All Files", "*"));
    fileChooser.setInitialDirectory(
        currentDocument != null
            ? currentDocument.getFile().getParentFile()
            : new File(System.getProperty("user.home")));
    File file = fileChooser.showOpenDialog(new Stage());
    if (file == null) return false;
    try {
      Document document = JAXB.unmarshal(file, Document.class);
      if (document.getCipherName() != null) {
        optionsDialogController.disableComboBox(true);
        optionsDialogController.setPassword(null);
        optionsDialogController.setSelectedCipherName(document.getCipherName());
        this.abort = false;
        Stage optionsDialog = mainApp.getOptionsDialog();
        optionsDialog.setOnCloseRequest(
            evt -> {
              this.abort = true;
            });
        optionsDialog.showAndWait();
        if (abort) return false;
        optionsDialogController.disableComboBox(false);
        String passwort = optionsDialogController.getPassword();
        LOG.debug("password:" + passwort);
        DocumentEncryptor encryptor =
            DocumentEncryptorFactory.getInstance(
                document.getCipherName().name(), passwort.toCharArray());
        document = encryptor.decrypt(document);
      }
      currentDocument = document;
      byte[] data = document.getPayload();
      text.setText(new String(data));
      optionsDialogController.setSelectedCipherName(document.getCipherName());
    } catch (InvalidChecksum e) {
      alertBox(
          "Corrupted checksum",
          "Document might be manipulated. The checksum is corrupted",
          WARNING);
      return false;
    } catch (DataBindingException e) {
      alertBox("Document error", "Could not save Document properly!", ERROR);
      e.printStackTrace();
      return false;
    } catch (BadPaddingException e) {
      alertBox("Bad Password", "Please use the right password", INFORMATION);
      e.printStackTrace();
      return false;

    } catch (GeneralSecurityException e) {
      alertBox("Crypto error!", "An crypto error occured", ERROR);
      e.printStackTrace();
    }
    currentDocument.setFile(file);
    return true;
  }

  void quit() {
    LOG.debug("Quit..");
    System.exit(0);
  }

  void alertBox(String title, String content, Alert.AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
  }
}
