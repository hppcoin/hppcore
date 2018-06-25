package org.hppcoin.controller.ssh;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.hppcoin.controller.MenuControler;
import org.hppcoin.dao.impl.ContractDaoImpl;
import org.hppcoin.dao.impl.NavigationDaoImpl;
import org.hppcoin.dao.impl.VPSDaoImpl;
import org.hppcoin.model.Contract;
import org.hppcoin.model.VPS;

import com.jcabi.ssh.Shell;
import com.jcabi.ssh.SshByPassword;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class SSHController extends MenuControler implements Initializable {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	Contract contract = null;
	private Shell shell;
	@FXML
	public JFXTextArea consoleTxa;
	@FXML
	public JFXTextField commandTxf;
	@FXML
	public JFXButton executeBtn;

	@FXML
	public Label unreachable;

	@FXML
	public JFXProgressBar progress;

	@FXML
	private JFXTextField ip;

	@FXML
	private JFXTextField port;

	@FXML
	private JFXTextField password;

	@FXML
	private JFXPasswordField passwordHidden;

	@FXML
	private JFXCheckBox show;

	@FXML
	void showPassword(ActionEvent event) {
		JFXCheckBox check = (JFXCheckBox) event.getSource();
		if (check.isSelected())
			Platform.runLater(() -> {
				passwordHidden.setVisible(false);
				password.setVisible(true);
			});
		else
			Platform.runLater(() -> {
				passwordHidden.setVisible(true);
				password.setVisible(false);
			});
	}

	public void executeAction() {
		Platform.runLater(() -> {
			progress.setVisible(true);
			executeBtn.setDisable(true);
			commandTxf.setDisable(true);
			unreachable.setTextFill(Color.rgb(50, 184, 224));
			unreachable.setText("Connecting ... !");

		});
		new Thread(new Runnable() {

			@Override
			public void run() {
				String stdout = null;
				try {
					stdout = new Shell.Plain(shell).exec(commandTxf.getText());
					consoleTxa.appendText(stdout);
					System.out.println(stdout);
					Platform.runLater(() -> {
						progress.setVisible(false);
						executeBtn.setDisable(false);
						commandTxf.setDisable(false);
						unreachable.setTextFill(Color.rgb(50, 184, 224));
						unreachable.setText("ACTIVE");

					});
				} catch (IOException e) {
					// TODO Auto-generated catch block
					LOGGER.severe(e.getMessage());
					Platform.runLater(() -> {
						progress.setVisible(false);
						executeBtn.setDisable(false);
						commandTxf.setDisable(false);
						unreachable.setTextFill(Color.rgb(225, 10, 10));
						unreachable.setText("Command Error:try again!");

					});
				}
			}
		}).start();

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		contract = new ContractDaoImpl().find(new NavigationDaoImpl().getCurrentContractID());
		Platform.runLater(() -> {
			ip.setText(contract.getIp());
			password.setText(contract.getPassword());
			passwordHidden.setText(contract.getPassword());
			port.setText(String.valueOf(contract.getSshPort()));
			progress.setVisible(true);
			executeBtn.setDisable(true);
			commandTxf.setDisable(true);
			unreachable.setTextFill(Color.rgb(50, 184, 224));
			unreachable.setText("Connecting ... !");

		});
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					shell = new SshByPassword(contract.getIp(), contract.getSshPort(), contract.getUsername(), contract.getPassword());
					String stdout = new Shell.Plain(shell).exec("ls");
					System.out.println(stdout);
					Platform.runLater(() -> {
						progress.setVisible(false);
						executeBtn.setDisable(false);
						commandTxf.setDisable(false);
						unreachable.setText("ACTIVE");
						unreachable.setTextFill(Color.rgb(50, 184, 224));
					});
				} catch (Exception e) {
					Platform.runLater(() -> {
						progress.setVisible(false);
						executeBtn.setDisable(true);
						commandTxf.setDisable(true);
						unreachable.setTextFill(Color.rgb(225, 10, 10));
						unreachable.setText("VPS unreachable !");
					});
					LOGGER.severe(e.getMessage());
				}
			}
		}).start();

	}

}
