package de.hsduesseldorf.medien.securesystems.editor.app;

import de.hsduesseldorf.medien.securesystems.editor.controller.EditorController;
import de.hsduesseldorf.medien.securesystems.editor.controller.OptionsDialogController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/** main class where the application is started initially */
public class MainApp extends Application {

  // stages
  private Stage primaryStage;
  private Stage optionsDialog;

  // controller
  private OptionsDialogController optionsDialogController;
  private EditorController editorController;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    this.primaryStage.setTitle("CryptoTeXt");
    initRootLayout();
    initOptionsDialog();
    initDependencies();
  }

  public void initRootLayout() {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("/views/editor.fxml"));
      AnchorPane element = loader.load();
      this.editorController = loader.getController();
      Scene root = new Scene(element);
      this.primaryStage.setScene(root);
      this.primaryStage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void initOptionsDialog() {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(getClass().getResource("/views/optionsDialog.fxml"));
      VBox element = (VBox) loader.load();
      this.optionsDialogController = loader.getController();
      this.optionsDialog = new Stage();
      this.optionsDialog.initModality(Modality.APPLICATION_MODAL);
      this.optionsDialog.setScene(new Scene(element));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void initDependencies() {
    this.editorController.setOptionsDialogController(this.optionsDialogController);
    this.editorController.setMainApp(this);
    this.optionsDialogController.setMainApp(this);
  }

  public Stage getPrimaryStage() {
    return primaryStage;
  }

  public Stage getOptionsDialog() {
    return optionsDialog;
  }
}
