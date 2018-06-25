package org.hppcoin.controller;

import static org.hppcoin.util.Display.maximize;
import static org.hppcoin.view.MyColors.*;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.hppcoin.Main;
import org.hppcoin.dao.impl.ContractDaoImpl;
import org.hppcoin.dao.impl.NavigationDaoImpl;
import org.hppcoin.model.Contract;
import org.hppcoin.model.ContractStatus;
import org.hppcoin.model.HPPTransaction;
import org.hppcoin.model.TransactionType;
import org.hppcoin.util.ViewUtil;
import org.hppcoin.view.TransactionView;

import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ContractDetailsBUYController extends MenuControler implements Initializable {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private Contract contract = null;
	@FXML
	private AnchorPane root;

	@FXML
	private Label unreachable;

	@FXML
	private JFXTextField ip;

	@FXML
	private JFXTextField username;

	@FXML
	private JFXTextField setupFees;

	@FXML
	private FontAwesomeIcon start;

	@FXML
	private FontAwesomeIcon pause;

	@FXML
	private JFXTextField hourlyPrice;

	@FXML
	private FontAwesomeIcon suspend;
	
	@FXML
	private FontAwesomeIcon terminal;

	@FXML
	private JFXTextField status;

	@FXML
	private JFXTextField recievingAddress;

	@FXML
	private Button suspendBtn;
	
	@FXML
	private Button terminalBtn;;

	@FXML
	private Button pauseBtn;

	@FXML
	private Button startBtn;

	@FXML
	private Label startTime;

	@FXML
	private Label contractStatus;

	@FXML
	private Label endTime;

	@FXML
	private Label recievedLbl;

	@FXML
	private Label recieved;

	@FXML
	private Label sent;

	@FXML
	private Label remaining;

	@FXML
	private Label cpu;

	@FXML
	private Label memory;

	@FXML
	private Label storage;

	@FXML
	private TableView<TransactionView> txTable;

	@FXML
	void pause(ActionEvent event) {
		contract.setContractStatus(ContractStatus.PAUSED);
		new ContractDaoImpl().update(contract);
		updateStatus();
	}

	@FXML
	void start(ActionEvent event) {
		contract.setContractStatus(ContractStatus.ACTIVE);
		new ContractDaoImpl().update(contract);
		updateStatus();
	}

	@FXML
	void suspend(ActionEvent event) {
		contract.setContractStatus(ContractStatus.SUSPENDED);
		new ContractDaoImpl().update(contract);
		updateStatus();
	}

	@FXML
	void openTerminal(ActionEvent event) {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Main.class.getResource("vps/Terminal.fxml"));

			AnchorPane rootLayout = (AnchorPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			String css = Main.class.getResource("hppTheme.css").toExternalForm();
			scene.getStylesheets().add(css);
			Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Platform.runLater(() -> {
				progress.setVisible(true);
				primaryStage.setScene(scene);
				primaryStage.setTitle("Xen Servers Admin Panel");
				maximize(primaryStage);
				primaryStage.show();
			});

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyy HH:mm");
		contract = new ContractDaoImpl().find(new NavigationDaoImpl().getCurrentContractID());
		updateStatus();
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				final Tooltip terminalTooltip = new Tooltip();
				terminalTooltip.setText("\nAccess the VPS via SSH\n" + "\nSend shell commands to VPS\n"

				);
				terminalBtn.setTooltip(terminalTooltip);

				final Tooltip suspendTooltip = new Tooltip();
				suspendTooltip.setText("\nSuspend the current contract\n" + "\nAnd halt all payments\n"

				);
				suspendBtn.setTooltip(suspendTooltip);

				final Tooltip pauseTooltip = new Tooltip();
				pauseTooltip.setText("\nPause the current contract\n" + "\nAnd temporarily halt payments\n"

				);
				pauseBtn.setTooltip(pauseTooltip);

				final Tooltip startTooltip = new Tooltip();
				startTooltip.setText("\nStart the current contract\n" + "\nAnd start paying renting fees\n"

				);
				startBtn.setTooltip(startTooltip);

				ip.setText(contract.getIp());
				username.setText(contract.getUsername());
				recievingAddress.setText(contract.getRecievingAddress());
				setupFees.setText(contract.getSetupPrice() + "");
				hourlyPrice.setText((contract.getCostPerMinute() * 60) + "");
				status.setText(contract.getContractStatus().toString());
				startTime.setText(dateFormat.format(new Date(contract.getStartDate())));
				endTime.setText(dateFormat
						.format(new Date(contract.getStartDate() + contract.getDurationHours() * 60L * 60L * 1000)));
				cpu.setText(contract.getCores() + " vCPUs");
				memory.setText(contract.getMemorySize() + " Bytes");
				storage.setText(contract.getStorageSize() + " Bytes");
				List<HPPTransaction> txs = contract.getTransactions();
				double remainingAmount = contract.getCostPerMinute() * 60 * contract.getDurationHours()
						+ contract.getSetupPrice();
				double sentAmount = 0;
				if (txs != null && txs.size() > 0)
					for (HPPTransaction tx : txs)
						if (tx.getType().equals(TransactionType.SEND))
							sentAmount += tx.getAmount();
				remainingAmount -= sentAmount;
				remaining.setText(remainingAmount + "");
				sent.setText(sentAmount + "");
				// Transactions Tables
				// type column
				TableColumn<TransactionView, FontAwesomeIcon> typeColumn = new TableColumn<>("Tx Type");
				typeColumn.setMinWidth(200);
				typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

				// txDate column
				TableColumn<TransactionView, String> txDateColumn = new TableColumn<>("Tx Date");
				txDateColumn.setMinWidth(200);
				txDateColumn.setCellValueFactory(new PropertyValueFactory<>("txDate"));

				// amount column
				TableColumn<TransactionView, Double> amountColumn = new TableColumn<>("Amount");
				amountColumn.setMinWidth(100);
				amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
				// confirmations column
				TableColumn<TransactionView, Integer> confirmationsColumn = new TableColumn<>("Confirmations");
				confirmationsColumn.setMinWidth(100);
				confirmationsColumn.setCellValueFactory(new PropertyValueFactory<>("confirmations"));

				// exploere column
				TableColumn<TransactionView, Button> exploereColumn = new TableColumn<>("Explorer");
				exploereColumn.setMinWidth(100);
				exploereColumn.setCellValueFactory(new PropertyValueFactory<>("explore"));
				txTable.getColumns().addAll(typeColumn, txDateColumn, amountColumn, confirmationsColumn,
						exploereColumn);
				if (txs != null && txs.size() > 0) {
					List<TransactionView> transactionViews = ViewUtil.getTransactionsView(txs, progress);
					txTable.getItems().addAll(transactionViews);
				}

			}
		});
	}

	private void updateStatus() {
		Platform.runLater(() -> {
			switch (contract.getContractStatus()) {
			case ACTIVE:
				contractStatus.setText("ACTIVE");
				terminalBtn.setDisable(false);
				terminal.setVisible(true);
				contractStatus.setTextFill(Color.rgb(50, 184, 224));
				startBtn.setDisable(true);
				start.setFill(GRAY);
				suspendBtn.setDisable(false);
				suspend.setFill(RED);
				pauseBtn.setDisable(false);
				pause.setFill(PAUSE);
				break;
			case PAUSED:
				contractStatus.setText("PAUSED");
				terminalBtn.setDisable(true);
				terminal.setVisible(false);
				contractStatus.setTextFill(GRAY);
				pauseBtn.setDisable(true);
				pause.setFill(GRAY);
				suspendBtn.setDisable(false);
				suspend.setFill(RED);
				startBtn.setDisable(false);
				start.setFill(BLUE);
				
				break;
			case SUSPENDED:
				contractStatus.setText("SUSPENDED");
				terminalBtn.setDisable(true);
				terminal.setVisible(false);
				contractStatus.setTextFill(Color.rgb(225, 10, 10));
				pauseBtn.setDisable(true);
				pause.setFill(GRAY);
				startBtn.setDisable(true);
				start.setFill(GRAY);
				suspendBtn.setDisable(true);
				suspend.setFill(GRAY);
				break;
			case COMPLETE:
				contractStatus.setText("COMPLETE");
				terminalBtn.setDisable(true);
				terminal.setVisible(false);
				contractStatus.setTextFill(Color.rgb(21, 171, 156));
				pauseBtn.setDisable(true);
				pause.setFill(GRAY);
				startBtn.setDisable(true);
				start.setFill(GRAY);
				suspendBtn.setDisable(true);
				suspend.setFill(GRAY);
				break;
			}
		});
	}

	@FXML
	JFXProgressBar progress;

}
