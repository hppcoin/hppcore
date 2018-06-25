package org.hppcoin;

import java.io.IOException;
import java.util.logging.Logger;

import org.hppcoin.dao.impl.ContractDaoImpl;
import org.hppcoin.dao.impl.SettingsDaoImpl;
import org.hppcoin.model.Settings;
import org.hppcoin.util.HppLogger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private Stage primaryStage;
	private AnchorPane rootLayout;
	private StackPane stackPane;
	public static HostServices hostServices = null;
	

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		hostServices = this.getHostServices();
		this.primaryStage.setTitle("HPPCOIN Core V1.1.0");
		primaryStage.setOnCloseRequest(e -> closeHPP());
		initRootLayout();
		Platform.setImplicitExit(false);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				event.consume();
				closeHPP();
			}
		});

	}

	private void closeHPP() {

		int doNotShowAgain = new SettingsDaoImpl().doNotShowAgain();
		int active = new ContractDaoImpl().countActive();
		if (doNotShowAgain != 0 && active != 0) {
			JFXDialogLayout dialogContent = new JFXDialogLayout();
			dialogContent.setHeading(new Text("Close HPP Core"));
			VBox vBox = new VBox();

			vBox.getChildren().add(new Label(
					"Attention : You have an active ! VPS Closing HPP Core will suspend all payements and may cause data loose"));
			vBox.getChildren().add(new Label("Close and suspend all active VPS ?"));
			vBox.getChildren().add(new Label(" "));
			HBox hBox = new HBox();
			JFXCheckBox checkbox = new JFXCheckBox("Do not Show this message again");
			JFXButton cancel = new JFXButton("Cancel");
			cancel.getStyleClass().add("cancel");

			JFXButton close = new JFXButton("Close HPP Core");
			close.getStyleClass().add("close");
			hBox.getChildren().add(cancel);
			hBox.getChildren().add(new Label(" "));
			hBox.getChildren().add(close);
			vBox.getChildren().add(hBox);
			vBox.getChildren().add(new Label(" "));
			vBox.getChildren().add(checkbox);
			dialogContent.setBody(vBox);
			stackPane = new StackPane();
			Pane rootPane = ((Pane) primaryStage.getScene().getRoot());
			for (Node node : rootPane.getChildren()) {
				if (!(node instanceof StackPane))
					node.setDisable(true);
			}
			rootPane.getChildren().add(stackPane);
			JFXDialog dialog = new JFXDialog(stackPane, dialogContent, JFXDialog.DialogTransition.BOTTOM);
			cancel.setOnAction(e -> {
				for (Node node : rootPane.getChildren()) {
					if (!(node instanceof StackPane))
						node.setDisable(false);
				}
				dialog.close();
			});
			close.setOnAction(e -> {
				suspendAllVPS();
				System.exit(0);
			});
			dialog.show();
		} else
			System.exit(0);
	}

	private void suspendAllVPS() {

	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {

		SettingsDaoImpl settingsDao = new SettingsDaoImpl();
		Settings settings = settingsDao.load();
		if (settings == null || settings.getPassword() == null) {
			if (settings == null)
				settingsDao.merge(new Settings());
			try {
				// Load root layout from fxml file.
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(Main.class.getResource("login/SetPassword.fxml"));
				rootLayout = (AnchorPane) loader.load();

				// Show the scene containing the root layout.
				Scene scene = new Scene(rootLayout);
				String css = Main.class.getResource("hppTheme.css").toExternalForm();
				scene.getStylesheets().add(css);
				primaryStage.setScene(scene);
				primaryStage.setResizable(false);
				primaryStage.show();
			} catch (IOException e) {
				LOGGER.severe(e.getMessage());
			}

		} else
			try {
				// Load root layout from fxml file.
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(Main.class.getResource("login/Login.fxml"));
				rootLayout = (AnchorPane) loader.load();

				// Show the scene containing the root layout.
				Scene scene = new Scene(rootLayout);
				String css = Main.class.getResource("hppTheme.css").toExternalForm();
				scene.getStylesheets().add(css);
				primaryStage.setScene(scene);
				primaryStage.setResizable(false);
				primaryStage.show();
			} catch (IOException e) {
				LOGGER.severe(e.getMessage());
			}
	}

	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		try {
			HppLogger.setup();
			System.out.println("Logger setup");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.severe(e.getMessage());
		}
		LOGGER.severe("App main started");
		launch(args);
	}

}
