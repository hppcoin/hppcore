package org.hppcoin.controller;

import static org.hppcoin.util.ViewUtil.isNull;
import static org.hppcoin.view.MyColors.BLUE;
import static org.hppcoin.view.MyColors.GRAY;
import static org.hppcoin.view.MyColors.GREEN;
import static org.hppcoin.view.MyColors.RED;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.hppcoin.dao.VPSDao;
import org.hppcoin.dao.impl.NavigationDaoImpl;
import org.hppcoin.dao.impl.VPSDaoImpl;
import org.hppcoin.model.Contract;
import org.hppcoin.model.ContractStatus;
import org.hppcoin.model.ContractType;
import org.hppcoin.model.PowerStatus;
import org.hppcoin.model.VPS;
import org.hppcoin.service.XenServerService;
import org.hppcoin.util.ViewUtil;
import org.hppcoin.view.ContractView;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class MyVPSDetailController extends MenuControler implements Initializable {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private XenServerService service = null;
	private VPS vps = null;
	private VPSDao vpsDao = null;
	@FXML
	private AnchorPane root;

	@FXML
	private Label unreachable;

	@FXML
	private JFXTextField ip;

	@FXML
	private JFXProgressBar progress;

	@FXML
	private JFXTextField username;

	@FXML
	private JFXTextField setupFees;

	@FXML
	private FontAwesomeIcon shutdownIcn;

	@FXML
	private FontAwesomeIcon rebootIcn;

	@FXML
	private JFXPasswordField password;

	@FXML
	private Button okButton;
	
	@FXML
	private FontAwesomeIcon ok;
	
	@FXML
	private JFXTextField hourlyPrice;

	@FXML
	private FontAwesomeIcon startIcn;

	@FXML
	private FontAwesomeIcon deleteIcn;

	@FXML
	private JFXComboBox<String> paymentInterval;

	@FXML
	private Button startBtn;

	@FXML
	private Button rebootBtn;

	@FXML
	private Button shutdownBtn;

	@FXML
	private Button deleteBtn;

	@FXML
	private JFXTextField sshPort;

	@FXML
	private JFXTextField payDelay;

	@FXML
	private Label creationTime;
	
	@FXML
	private Label thevpsStatus;

	@FXML
	private Label totalSuspended;

	@FXML
	private Label totalComplete;

	@FXML
	private Label lastAccessTime;

	@FXML
	private Label totalVMs;

	@FXML
	private Label totalVPS;
	
	@FXML
	private JFXButton modifyBtn;

	@FXML
	private Label vcpus;

	@FXML
	private Label ram;

	@FXML
	private Label hd;

	@FXML
	private Label totalVPS1;

	@FXML
	private Label totalIncome;

	@FXML
	private TableView<ContractView> contractTable;

	@FXML
	void delete(ActionEvent event) {
		  Platform.runLater(()-> {thevpsStatus.setText("Deleting VM ...");progress.setVisible(true);});
		new Thread(() -> {
			if (service == null)
				try {
					service = new XenServerService(vps.getXenServer());
				} catch (Exception e) {
					LOGGER.severe(e.getMessage());
					e.printStackTrace();
				}
			int r = service.deleteVM(vps.getUuid());
			if (r == 1) {
				vps.setPowerStatus(PowerStatus.DELETED);
				vpsDao.update(vps);
				updateStatus();
			}
			else updateStatus();
			  Platform.runLater(()->progress.setVisible(false));
		}).start();
	}

	@FXML
	void reboot(ActionEvent event) {
		
		  Platform.runLater(()-> {thevpsStatus.setText("Rebooting ...");progress.setVisible(true);});
		new Thread(() -> {
			if (service == null)
				try {
					service = new XenServerService(vps.getXenServer());
				} catch (Exception e) {
					LOGGER.severe(e.getMessage());
					e.printStackTrace();
				}
			int r = service.rebootVM(vps.getUuid());
			if (r == 1) {
				vps.setPowerStatus(PowerStatus.RUNNING);
				vpsDao.update(vps);
				updateStatus();
			}
			else updateStatus();
			  Platform.runLater(()->progress.setVisible(false));
		}).start();
	}

	@FXML
	void shutdown(ActionEvent event) {
		  Platform.runLater(()-> {thevpsStatus.setText("Halting ...");progress.setVisible(true);});
		new Thread(() -> {
			if (service == null)
				try {
					service = new XenServerService(vps.getXenServer());
				} catch (Exception e) {
					LOGGER.severe(e.getMessage());
					e.printStackTrace();
				}
			int r = service.shutDownVM(vps.getUuid());
			if (r == 1) {
				vps.setPowerStatus(PowerStatus.HALTED);
				vpsDao.update(vps);
				updateStatus();
			}else updateStatus();
			  Platform.runLater(()->progress.setVisible(false));
		}).start();
	}

	@FXML
	void start(ActionEvent event) {
		  Platform.runLater(()-> {thevpsStatus.setText("Srtarting ...");progress.setVisible(true);});
		new Thread(() -> {
			if (service == null)
				try {
					service = new XenServerService(vps.getXenServer());
				} catch (Exception e) {
					LOGGER.severe(e.getMessage());
					e.printStackTrace();
				}
			int r = service.startVM(vps.getUuid());
			if (r == 1) {
				vps.setPowerStatus(PowerStatus.RUNNING);
				vpsDao.update(vps);
				updateStatus();
			}else updateStatus();
			 Platform.runLater(()->progress.setVisible(false));
		}).start();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
        
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyy HH:mm");
		vpsDao = new VPSDaoImpl();
		vps = vpsDao.find(new NavigationDaoImpl().getCurrentVPSUID());
		new Thread(() -> {
			try {   Platform.runLater(()->{
				progress.setVisible(true);
				thevpsStatus.setTextFill(Color.rgb(21, 171, 156));
				startBtn.setDisable(true);
				startIcn.setFill(GRAY);
				shutdownBtn.setDisable(true);
				shutdownIcn.setFill(GRAY);
				rebootBtn.setDisable(true);
				rebootIcn.setFill(GRAY);
				deleteBtn.setDisable(true);
				deleteIcn.setFill(GRAY);
			});
				service = new XenServerService(vps.getXenServer());
				vps.setPowerStatus(service.getPowerState(service.getVM(vps.getUuid())));
				 updateStatus();
				 Platform.runLater(()->progress.setVisible(false));
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}
		}).start();
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ip.setText(vps.getIp());
				username.setText(vps.getUser());
				password.setText(vps.getPassword());
				sshPort.setText(String.valueOf(vps.getSshPort()));
				payDelay.setText(String.valueOf(vps.getPayDelay()));
				setupFees.setText(String.valueOf(vps.getSetupPrice()));
				hourlyPrice.setText(String.valueOf(vps.getCostPerMinute() * 60));
				// Payment Interval
				ObservableList<String> paymentIntervals = FXCollections.observableArrayList();
				paymentIntervals.addAll("Hourly", "6 Hours", "12 Hours", "Daily", "Weekly", "Monthly");
				paymentInterval.setItems(paymentIntervals);
				paymentInterval.setValue(MyVPSController.toStr(vps.getPayementInterval()));
				creationTime.setText(dateFormat.format(new Date(vps.getCreationTime())));
				lastAccessTime.setText(dateFormat.format(new Date(vps.getLastAccessTime())));
				vcpus.setText(String.valueOf(vps.getCpu().getCores()) + " vCPUs");
				ram.setText(String.valueOf(vps.getMemory().getMemorySize()) + " Bytes");
				hd.setText(String.valueOf(vps.getStorage().getStorageSize()) + " Bytes");
				int totalSuspendedContracts = 0;
				int totalCompletedContracts = 0;
				List<Contract> contracts = vps.getContracts();
				if (contracts != null && contracts.size() > 0)
					for (Contract c : contracts) {
						if (c.getContractStatus().equals(ContractStatus.SUSPENDED))
							totalSuspendedContracts++;
						if (c.getContractStatus().equals(ContractStatus.COMPLETE))
							totalCompletedContracts++;
					}
				totalSuspended.setText(String.valueOf(totalSuspendedContracts));
				totalComplete.setText(String.valueOf(totalCompletedContracts));
				List<ContractView> contactViews = ViewUtil.getContractsView(contracts, progress, ContractType.SELL);
				// Contract Table
				// End date column
				TableColumn<ContractView, String> endDateColumn = new TableColumn<>("End date");
				endDateColumn.setMinWidth(200);
				endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));

				// PaidAmount column
				TableColumn<ContractView, String> paidAmountColumn = new TableColumn<>("Paid Amount");
				paidAmountColumn.setMinWidth(200);
				paidAmountColumn.setCellValueFactory(new PropertyValueFactory<>("paidAmount"));

				// Hourly Price column
				TableColumn<ContractView, String> hourlyPriceColumn = new TableColumn<>("Hourly Price");
				hourlyPriceColumn.setMinWidth(100);
				hourlyPriceColumn.setCellValueFactory(new PropertyValueFactory<>("hourlyPrice"));
				// Payment interval column
				TableColumn<ContractView, String> payementIntervalColumn = new TableColumn<>("Payment Interval");
				payementIntervalColumn.setMinWidth(100);
				payementIntervalColumn.setCellValueFactory(new PropertyValueFactory<>("payementInterval"));
				// memory column
				TableColumn<ContractView, String> statusColumn = new TableColumn<>("Status");
				statusColumn.setMinWidth(150);
				statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

				// manage column
				TableColumn<ContractView, String> manageColumn = new TableColumn<>("Manage");
				manageColumn.setMinWidth(100);
				manageColumn.setCellValueFactory(new PropertyValueFactory<>("manage"));

				contractTable.getColumns().addAll(endDateColumn, paidAmountColumn, hourlyPriceColumn,
						payementIntervalColumn, statusColumn, manageColumn);
				if (contactViews != null && contactViews.size() > 0)
					contractTable.getItems().addAll(contactViews);

			
				final Tooltip haltTooltip = new Tooltip();
				haltTooltip.setText("\nShutdown this VPS\n" + "\nAnd Terminate All\nActive Contract");
				shutdownBtn.setTooltip(haltTooltip);
				
				final Tooltip rebootTooltip = new Tooltip();
				rebootTooltip.setText("\nReboot this VPS\n");
				rebootBtn.setTooltip(rebootTooltip);
				
				final Tooltip deleteTooltip = new Tooltip();
				deleteTooltip.setText("\nDelete this VPS\n" + "\nAnd Terminate All\nActive Contract");
				deleteBtn.setTooltip(deleteTooltip);
				
				final Tooltip startTooltip = new Tooltip();
				startTooltip.setText("\nStart this VPS\n");
				startBtn.setTooltip(startTooltip);
				
			}
		});

	}
	@FXML
	void modifyVPS(ActionEvent event) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				double horlyPriceVal = 0, setupFeesValue = 0;
				int sshPortValue=22,maxPayDelayValue=3;
				if (isNull(ip, 5, ".") | isNull(username, 1) | isNull(password, 1) 
						| isNull(paymentInterval, 2) | isNull(setupFees, 1) | isNull(hourlyPrice, 1))
					return;
				try {
					horlyPriceVal = Double.parseDouble(hourlyPrice.getText());
					vps.setCostPerMinute(horlyPriceVal/60.0);
				} catch (Exception e) {
					hourlyPrice.setText("");
					isNull(hourlyPrice, 1);
					return;
				}
				try {
					setupFeesValue = Double.parseDouble(setupFees.getText());
					vps.setSetupPrice(setupFeesValue);
				} catch (Exception e) {
					setupFees.setText("");
					isNull(setupFees, 1);
					return;
				}
				try {
					sshPortValue = Integer.parseInt(sshPort.getText());
					vps.setSshPort(sshPortValue);
				} catch (Exception e) {
					sshPort.setText("22");
					isNull(sshPort, 10);
					return;
				}
				try {
					maxPayDelayValue = Integer.parseInt(payDelay.getText());
					vps.setPayDelay(maxPayDelayValue);
				} catch (Exception e) {
					payDelay.setText("");
					isNull(payDelay, 1);
					return;
				}
				
				vps.setIp(ip.getText());
				vps.setUser(username.getText());
				vps.setPassword(password.getText());
				vps.setPayementInterval(MyVPSController.toInt(paymentInterval.getValue()));
				
					
					vpsDao.update(vps);
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							ok.setVisible(true);
							okButton.setGraphic(ok);
							final Tooltip okTooltip = new Tooltip();
							okTooltip.setText("Added successefully");
							okButton.setTooltip(okTooltip);

						}
					});

				
			}

		}).start();
	}

	private void updateStatus() {
		Platform.runLater(() -> {
			switch (vps.getPowerStatus()) {
			case RUNNING:
				thevpsStatus.setText("RUNNING");
				
				thevpsStatus.setTextFill(BLUE);
				startBtn.setDisable(true);
				startIcn.setFill(GRAY);
				shutdownBtn.setDisable(false);
				shutdownIcn.setFill(RED);
				rebootBtn.setDisable(false);
				rebootIcn.setFill(GREEN);
				deleteBtn.setDisable(false);
				deleteIcn.setFill(RED);
				break;
			case PAUSED:
				thevpsStatus.setText("PAUSED");
				thevpsStatus.setTextFill(GRAY);
				shutdownBtn.setDisable(false);
				shutdownIcn.setFill(RED);
				startBtn.setDisable(false);
				startIcn.setFill(BLUE);
				rebootBtn.setDisable(true);
				rebootIcn.setFill(GRAY);
				deleteBtn.setDisable(false);
				deleteIcn.setFill(RED);
				
				break;
			case HALTED:
				thevpsStatus.setText("HALTED");
				thevpsStatus.setTextFill(RED);
				startBtn.setDisable(false);
				startIcn.setFill(BLUE);
				shutdownBtn.setDisable(true);
				shutdownIcn.setFill(GRAY);
				rebootBtn.setDisable(true);
				rebootIcn.setFill(GRAY);
				deleteBtn.setDisable(false);
				deleteIcn.setFill(RED);
				
				break;
			case DELETED:
				thevpsStatus.setText("DELETED");
				thevpsStatus.setTextFill(RED);
				startBtn.setDisable(true);
				startIcn.setFill(GRAY);
				shutdownBtn.setDisable(true);
				shutdownIcn.setFill(GRAY);
				rebootBtn.setDisable(true);
				rebootIcn.setFill(GRAY);
				deleteBtn.setDisable(true);
				deleteIcn.setFill(GRAY);
				modifyBtn.setDisable(true);
				modifyBtn.setTextFill(GRAY);
				break;
			}
		});
	}
}
