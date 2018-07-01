package org.hppcoin.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.hppcoin.Main;
import org.hppcoin.crons.WalletListener;
import org.hppcoin.dao.TransactionDao;
import org.hppcoin.dao.impl.SettingsDaoImpl;
import org.hppcoin.dao.impl.TransactionDaoImpl;
import org.hppcoin.model.Balance;
import org.hppcoin.model.Settings;
import org.hppcoin.util.Sha256Digest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SetPasswordController implements Initializable {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	@FXML
	public TextField passwordTxf;
	@FXML
	public TextField confirmPasswordTxf;
	@FXML
	public TextField passwordHintTxf;
	@FXML
	public Label tooShort;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.info("SetPasswordController initialized");
		SettingsDaoImpl settingsDao = new SettingsDaoImpl();
		Settings settings = settingsDao.load();
		if (settings == null) {
			settings = new Settings();
			settings.setDefaultReceivingAddress(new WalletListener(false).getNewAddress());
			settingsDao.merge(settings);
		}

		TransactionDao txDao = new TransactionDaoImpl();
		Balance balance = txDao.loadBalance();
		if (balance == null)
			txDao.saveBalance(new Balance());

	}

	public void setPasswordAction(ActionEvent event) {
		SettingsDaoImpl settingsDao = new SettingsDaoImpl();
		Settings settings = settingsDao.load();
		if (settings == null)
			settings = new Settings();
		if (passwordTxf.getText() != null && passwordTxf.getText().length() > 7
				&& passwordTxf.getText().equals(confirmPasswordTxf.getText())) {
			settings.setPassword(Sha256Digest.sha256(passwordTxf.getText()));
			settings.setHint(passwordHintTxf.getText());
			settingsDao.merge(settings);
			try {
				// Load root layout from fxml file.
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(Main.class.getResource("login/Login.fxml"));

				AnchorPane rootLayout = (AnchorPane) loader.load();

				// Show the scene containing the root layout.
				Scene scene = new Scene(rootLayout);
				String css = Main.class.getResource("hppTheme.css").toExternalForm();
				scene.getStylesheets().add(css);
				Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				primaryStage.setResizable(false);
				primaryStage.setScene(scene);
				primaryStage.setTitle("Login");
				primaryStage.show();

			} catch (IOException e) {
				e.printStackTrace();
				LOGGER.severe(e.getMessage());
			}
		}
		if (passwordTxf.getText() == null || passwordTxf.getText().length() < 8) {
			tooShort.setText("Password minimum length 8 characters");
			return;
		}
		if (passwordHintTxf.getText() == null || passwordHintTxf.getText().length() < 1) {
			tooShort.setText("Password Hint is mandatory");
			return;
		}
		if (!passwordTxf.getText().equals(confirmPasswordTxf.getText())) {
			tooShort.setText("Password Does Not Match");
			return;
		}

	}

}
