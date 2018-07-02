/*
 * HPPCOIN License
 * 
 * Copyright (c) 2017-2018, HPPCOIN Developers.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the 
 * Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.hppcoin.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.hppcoin.Main;
import org.hppcoin.crons.VPSCron;
import org.hppcoin.crons.WalletCron;
import org.hppcoin.dao.impl.SettingsDaoImpl;
import org.hppcoin.model.Settings;
import org.hppcoin.util.OsCheck;
import org.hppcoin.util.Sha256Digest;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginController implements Initializable {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		LOGGER.info("LoginController initialized");
		WalletCron.updateTransactionsAndBalance();
		WalletCron.updateLMNList();
		VPSCron.purgeOtherVPS();
		WalletCron.continuePayingActiveContracts();
		WalletCron.suspendAllImpaidContracts();
		List<String> peers = new ArrayList<>();
		try {
			new Thread(() -> {
				OsCheck.OSType ostype = OsCheck.getOperatingSystemType();
				try {
					WalletCron.createConfigFileIfNotExist(ostype);
					switch (ostype) {
					case Windows:
						Runtime.getRuntime().exec("hppcoind.exe -daemon");
						break;
					case MacOS:
						Runtime.getRuntime().exec("./hppcoind -daemon");
						break;
					case Linux:
						Runtime.getRuntime().exec("./hppcoind -daemon");
						break;
					case Other:
						break;
					}

				} catch (IOException e) {
				}
			}).start();
			String path = LoginController.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			LOGGER.info("Path :" + path);
			File jarFile = new File(path);
			String jarDir = jarFile.getParentFile().getPath();
			File file = new File(jarDir + File.separator + "peers");
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = br.readLine()) != null)
				peers.add(line);
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
			LOGGER.severe(e.getMessage());
		}
		// start agents
		String id = String.valueOf(new Random().nextLong());
		for (String peer : peers) {
			try {
				LOGGER.info("Start Launching agent in Platform :" + peer);
				// jade.MicroBoot.main(new String[] { "-host", peer, "-container", "-agents",
				// "sellerAgent" + id + ":org.hppcoin.mas.SellerAgent;"+"buyerAgent" + id +
				// ":org.hppcoin.mas.BuyerAgent;"+"sendContractRequestAgent" + id +
				// ":org.hppcoin.mas.SendContractRequestAgent" });
				jade.MicroBoot.main(new String[] { "-host", peer, "-container", "-agents",
						"buyerAgent" + id + ":org.hppcoin.mas.BuyerAgent;" + "sellerAgent" + id
								+ ":org.hppcoin.mas.SellerAgent;" + "receiveCredentialsAgent" + id
								+ ":org.hppcoin.mas.ReceiveCredentialsAgent;" + "receiveRequestAgent" + id
								+ ":org.hppcoin.mas.ReceiveRequestAgent;" + "receiveRequestResponseAgent" + id
								+ ":org.hppcoin.mas.ReceiveRequestResponseAgent;" + "sendContractRequestAgent" + id
								+ ":org.hppcoin.mas.SendContractRequestAgent;" + "setupFeesVerificationAgent" + id
								+ ":org.hppcoin.mas.SetupFeesVerificationAgent" });
				LOGGER.info("Done! Launching agent in Platform :" + peer);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.severe(e.getMessage());
			}
		}
	}

	// for database you should just create the db file and deploy it using jfx
	// deploy plugin Or look at it as installer' task (additional files)
	public Button loginBtn;
	public Button passwordHintBtn;
	public Button resetPasswordBtn;
	public TextField loginTxf;
	public TextField passwordTxf;
	public Label passwordHintLbl;
	@FXML
	public Label error;

	public LoginController() {
		super();

	}

	public void closeAction(MouseEvent event) {
		System.exit(0);
	}

	public void loginAction(ActionEvent event) {
		String url = "jdbc:sqlite:hppcoin.db";

		try (Connection conn = DriverManager.getConnection(url)) {
			if (conn != null) {
				DatabaseMetaData meta = conn.getMetaData();
				LOGGER.info("The driver name is " + meta.getDriverName());
			}

		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}
		loginBtn.setText("Wait please");
		LOGGER.severe("Wait please");
		loginBtn.setDisable(true);

		Settings settings = new SettingsDaoImpl().load();

		if (null != settings && settings.getPassword().equals(Sha256Digest.sha256(passwordTxf.getText())))
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
				primaryStage.show();

			} catch (IOException e) {
				LOGGER.severe(e.getMessage());
			}
		else {
			loginBtn.setText("Login");
			loginBtn.setDisable(false);
			error.setText("Wrong password");
		}
	}

	public void passwordHintAction() {
		String hint = "Password Hint : ";
		passwordHintLbl.setText(hint + new SettingsDaoImpl().load().getHint());
	}

	public void resetPasswordAction(ActionEvent event) {
		passwordHintLbl.setText("");
		error.setText("");
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("login/SetPassword.fxml"));

			AnchorPane rootLayout = (AnchorPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			String css = Main.class.getResource("hppTheme.css").toExternalForm();
			scene.getStylesheets().add(css);
			Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Set Password");
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
	}

}
