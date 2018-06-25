package org.hppcoin.controller;

import static org.hppcoin.util.Display.maximize;

import java.io.IOException;
import java.util.logging.Logger;

import org.hppcoin.Main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MenuControler {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	@FXML
	private HBox dashboardMenu;

	@FXML
	private HBox myVPSMenu;

	@FXML
	private HBox rentedVPSMenu;

	@FXML
	private HBox findVPSMenu;

	@FXML
	private HBox walletMenu;

	@FXML
	private HBox globalStatsMenu;

	@FXML
	private HBox NotificationsMenu;

	@FXML
	private HBox settingsMenu;

	@FXML
	private HBox xenServerMenu;

	@FXML
	public void showXenServers(MouseEvent event) {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("xengui/XenServers.fxml"));

			AnchorPane rootLayout = (AnchorPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			String css = Main.class.getResource("hppTheme.css").toExternalForm();
			scene.getStylesheets().add(css);
			Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			primaryStage.setScene(scene);
			primaryStage.setTitle("Xen Servers Admin Panel");
			maximize(primaryStage);
			primaryStage.show();

		} catch (IOException e) {
			e.getCause().printStackTrace();
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
	}

	@FXML
	public void showDashboard(MouseEvent event) {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("dashboard/dashboard.fxml"));

			AnchorPane rootLayout = (AnchorPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			String css = Main.class.getResource("hppTheme.css").toExternalForm();
			scene.getStylesheets().add(css);
			Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			primaryStage.setResizable(true);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Dashboard");
			maximize(primaryStage);
			primaryStage.show();

		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	@FXML
	public void showFindVPS(MouseEvent event) {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("vps/findVPS.fxml"));

			AnchorPane rootLayout = (AnchorPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			String css = Main.class.getResource("hppTheme.css").toExternalForm();
			scene.getStylesheets().add(css);
			Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			primaryStage.setScene(scene);
			primaryStage.setTitle("Find VPS");
			maximize(primaryStage);
			primaryStage.show();

		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	@FXML
	public void showGlobalStats(MouseEvent event) {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("statistics/globalStatistics.fxml"));

			AnchorPane rootLayout = (AnchorPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			String css = Main.class.getResource("hppTheme.css").toExternalForm();
			scene.getStylesheets().add(css);
			Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			primaryStage.setScene(scene);
			primaryStage.setTitle("Global Statistics");
			maximize(primaryStage);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
	}

	@FXML
	public void showMyVPS(MouseEvent event) {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("vps/MyVPS.fxml"));

			AnchorPane rootLayout = (AnchorPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			String css = Main.class.getResource("hppTheme.css").toExternalForm();
			scene.getStylesheets().add(css);
			Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			primaryStage.setScene(scene);
			primaryStage.setTitle("My VPS List");
			maximize(primaryStage);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
	}

	@FXML
	public void showNotifications(MouseEvent event) {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("notifications/notifications.fxml"));

			AnchorPane rootLayout = (AnchorPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			String css = Main.class.getResource("hppTheme.css").toExternalForm();
			scene.getStylesheets().add(css);
			Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			primaryStage.setScene(scene);
			primaryStage.setTitle("Notifications");
			maximize(primaryStage);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
	}

	@FXML
	public void showRentedVPS(MouseEvent event) {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("vps/rentedVPS.fxml"));

			AnchorPane rootLayout = (AnchorPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			String css = Main.class.getResource("hppTheme.css").toExternalForm();
			scene.getStylesheets().add(css);
			Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			primaryStage.setScene(scene);
			primaryStage.setTitle("Rented VPS List");
			maximize(primaryStage);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
	}

	@FXML
	public void showSettings(MouseEvent event) {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("settings/settings.fxml"));

			AnchorPane rootLayout = (AnchorPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			String css = Main.class.getResource("hppTheme.css").toExternalForm();
			scene.getStylesheets().add(css);
			Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			primaryStage.setScene(scene);
			primaryStage.setTitle("Settings");
			maximize(primaryStage);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
	}

	@FXML
	public void showWallet(MouseEvent event) {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("wallet/wallet.fxml"));

			AnchorPane rootLayout = (AnchorPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			String css = Main.class.getResource("hppTheme.css").toExternalForm();
			scene.getStylesheets().add(css);
			Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			primaryStage.setScene(scene);
			primaryStage.setTitle("Wallet");
			maximize(primaryStage);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
	}
}
