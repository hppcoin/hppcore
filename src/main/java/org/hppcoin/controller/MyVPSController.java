package org.hppcoin.controller;

import static org.hppcoin.util.ViewUtil.isNull;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.hppcoin.crons.WalletListener;
import org.hppcoin.dao.impl.VPSDaoImpl;
import org.hppcoin.dao.impl.XenServerDaoImpl;
import org.hppcoin.model.CPU;
import org.hppcoin.model.Memory;
import org.hppcoin.model.Storage;
import org.hppcoin.model.VPS;
import org.hppcoin.model.VPSAccesType;
import org.hppcoin.model.VPSRentalStatus;
import org.hppcoin.model.XenServer;
import org.hppcoin.service.XenServerService;
import org.hppcoin.util.ViewUtil;
import org.hppcoin.view.VPSView;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import com.xensource.xenapi.VM;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class MyVPSController extends MenuControler implements Initializable {
	private final Map<String, String> vmParams = new HashMap<String, String>();
	ObservableList<VPSView> vpss = FXCollections.observableArrayList();
	@FXML
	private AnchorPane root;

	@FXML
	private JFXTextField ip;

	@FXML
	private JFXTextField username;

	@FXML
	private JFXComboBox<String> vms;

	@FXML
	private JFXComboBox<String> servers;

	@FXML
	private Button okButton;

	@FXML
	private FontAwesomeIcon ok;

	@FXML
	private JFXPasswordField password;

	@FXML
	private JFXProgressBar vmProgress;

	@FXML
	private JFXComboBox<String> paymentInterval;

	@FXML
	private JFXTextField hourlyPrice;

	@FXML
	private JFXTextField setupFees;

	@FXML
	private TableView<VPSView> vpsTable;

	@FXML
	private JFXTextField ip1;

	@FXML
	private JFXTextField username1;

	@FXML
	private JFXTextField password1;

	@FXML
	private JFXComboBox<String> paymentInterval1;

	@FXML
	private JFXTextField hourlyPrice1;

	@FXML
	private JFXTextField setupFees1;

	@FXML
	private JFXComboBox<String> powerState;

	@FXML
	private JFXComboBox<String> rentingStatus;

	@FXML
	private Slider cpuSlider;

	@FXML
	private Label cpuLbl;

	@FXML
	private JFXComboBox<String> powerState1;

	@FXML
	private JFXComboBox<String> rentingStatus1;

	@FXML
	private Slider cpuSlider1;

	@FXML
	private Label cpuLbl1;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// sliders
				cpuSlider.valueProperty().addListener(new ChangeListener<Number>() {

					@Override
					public void changed(ObservableValue<? extends Number> observable, Number oldValue,
							Number newValue) {
						cpuLbl.setText(String.format("%.0f", newValue));

						filter();

					}
				});
				// power state
				ObservableList<String> powerStates = FXCollections.observableArrayList();
				powerStates.addAll(" ", "HALTED", "PAUSED", "RUNNING", "SUSPENDED");

				// Renting Status
				ObservableList<String> rentalStates = FXCollections.observableArrayList();
				rentalStates.addAll(" ", "AVAILABLE", "RENTED", "MAINTENANCE");
				rentingStatus.setItems(rentalStates);
				rentingStatus1.setItems(rentalStates);
				// Xen Servers List

				// Payment Interval
				ObservableList<String> paymentIntervals = FXCollections.observableArrayList();
				paymentIntervals.addAll("Hourly", "6 Hours", "12 Hours", "Daily", "Weekly", "Monthly");

				List<XenServer> myServers = new XenServerDaoImpl().selectAll();

				if (myServers != null && myServers.size() > 0) {
					final ObservableList<String> myServersO = FXCollections.observableArrayList();
					for (XenServer s : myServers)
						myServersO.add(s.getIp());
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							powerState.setItems(powerStates);
							powerState1.setItems(powerStates);
							paymentInterval.setItems(paymentIntervals);
							paymentInterval1.setItems(paymentIntervals);
							servers.setItems(myServersO);

						}
					});

				}
			}
		}).start();

		// VMs List
		servers.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				vmProgress.setVisible(true);
				new Thread(new Runnable() {

					@Override
					public void run() {
						XenServer xenServer = new XenServerDaoImpl().find(servers.getValue());

						try {
							XenServerService service = new XenServerService(xenServer);
							List<VM> xenVms = service.getALLVM();

							for (VM vm : xenVms)
								vmParams.put(vm.getNameLabel(service.connection), vm.getUuid(service.connection));
							ObservableList<String> labels = FXCollections.observableArrayList();
							labels.addAll(vmParams.keySet());
							Platform.runLater(new Runnable() {
								@Override
								public void run() {
									vms.setItems(labels);
									vmProgress.setVisible(false);
								}
							});

						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				}).start();
				;

			}
		});
		// VPS Table
		// xenServer column
		TableColumn<VPSView, String> xenServerColumn = new TableColumn<>("Xen Hostname");
		xenServerColumn.setMinWidth(100);
		xenServerColumn.setCellValueFactory(new PropertyValueFactory<>("xenHost"));

		// username column
		TableColumn<VPSView, String> ipColumn = new TableColumn<>("IP");
		ipColumn.setMinWidth(250);
		ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));

		// status column
		TableColumn<VPSView, String> statusColumn = new TableColumn<>("Renting Status");
		statusColumn.setMinWidth(100);
		statusColumn.setCellValueFactory(new PropertyValueFactory<>("rentalStatus"));
		// vCPUs column
		TableColumn<VPSView, String> vCPUsColumn = new TableColumn<>("vCPUs");
		vCPUsColumn.setMinWidth(100);
		vCPUsColumn.setCellValueFactory(new PropertyValueFactory<>("cpu"));
		// memory column
		TableColumn<VPSView, String> memoryColumn = new TableColumn<>("Memory");
		memoryColumn.setMinWidth(150);
		memoryColumn.setCellValueFactory(new PropertyValueFactory<>("memory"));
		// Storage column
		TableColumn<VPSView, String> storageColumn = new TableColumn<>("Storage");
		storageColumn.setMinWidth(150);
		storageColumn.setCellValueFactory(new PropertyValueFactory<>("storage"));

		// manage column
		TableColumn<VPSView, String> manageColumn = new TableColumn<>("Manage");
		manageColumn.setMinWidth(100);
		manageColumn.setCellValueFactory(new PropertyValueFactory<>("manage"));

		vpsTable.getColumns().addAll(xenServerColumn, ipColumn, vCPUsColumn, memoryColumn, storageColumn, statusColumn,
				manageColumn);

		List<VPS> vpsList = new VPSDaoImpl().selectMine();
		vpss = ViewUtil.getVPSView(vpsList, vmProgress, 0);
		vpsTable.setItems(vpss);

	}

	protected void filter() {
		ObservableList<VPSView> newlist = FXCollections.observableArrayList();
		int min = (int) cpuSlider.getValue();
		for (VPSView v : vpss)
			if (v.getCpu() >= min)
				newlist.add(v);
		ObservableList<VPSView> newlist2 = FXCollections.observableArrayList();
		String power = powerState.getValue();
		String renting = rentingStatus.getValue();

		if (power != null && power.length() > 2) {
			for (VPSView v : newlist)
				if (v.getPowerStatus().toString().equals(power))
					newlist2.add(v);
		} else
			newlist2.addAll(newlist);
		ObservableList<VPSView> newlist3 = FXCollections.observableArrayList();
		if (renting != null && renting.length() > 2) {
			for (VPSView v : newlist2)
				if (v.getRentalStatus().toString().equals(renting))
					newlist3.add(v);
		}

		else
			newlist3.addAll(newlist2);
		vpsTable.setItems(newlist3);
	}

	@FXML
	void createVPS(ActionEvent event) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				double horlyPriceVal = 0, setupFeesValue = 0;
				if (isNull(ip, 5, ".") | isNull(username, 1) | isNull(password, 1) | isNull(servers, 5) | isNull(vms, 4)
						| isNull(paymentInterval, 2) | isNull(setupFees, 1) | isNull(hourlyPrice, 1))
					return;
				try {
					horlyPriceVal = Double.parseDouble(hourlyPrice.getText());
					if(horlyPriceVal<=0){
						hourlyPrice.setText("");
						isNull(setupFees, 1);
						return;
					}
				} catch (Exception e) {
					hourlyPrice.setText("");
					isNull(hourlyPrice, 1);
					return;
				}
				try {
					setupFeesValue = Double.parseDouble(setupFees.getText());
					if(setupFeesValue<=0){
						setupFees.setText("");
						isNull(setupFees, 1);
						return;
					}
				} catch (Exception e) {
					setupFees.setText("");
					isNull(setupFees, 1);
					return;
				}
				String serverIP = servers.getValue();
				String vmuid = vmParams.get(vms.getValue());

				XenServer xenServer = new XenServerDaoImpl().find(serverIP);
				VM vm = null;
				try {
					XenServerService service = new XenServerService(xenServer);
					vm = service.getVM(vmuid);
					VPS vps = new VPS();
					vps.setMine((byte) 1);
					vps.setUuid(vmuid);
					vps.setXenServer(xenServer);
					vps.setRentalSattus(VPSRentalStatus.AVAILABLE);
					vps.setCpu(new CPU(service.getCores(vm)));
					vps.setPowerStatus(service.getPowerState(vm));
					vps.setCreationTime(new Date().getTime());
					vps.setLastAccessTime(new Date().getTime());
					vps.setMemory(new Memory(service.getMemory(vm)));
					vps.setPayementInterval(toInt(paymentInterval.getValue()));
					vps.setRecievingAddress(new WalletListener(false).getNewAddress());
					vps.setStorage(new Storage(service.getStorage(vm)));
					vps.setCostPerMinute(horlyPriceVal / 60.0);
					vps.setSetupPrice(setupFeesValue);
					vps.setIp(ip.getText());
					vps.setUser(username.getText());
					vps.setPassword(password.getText());
					vps.setXenType(true);
					vps.setType(VPSAccesType.PASSWORD);
					new VPSDaoImpl().save(vps);
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

				} catch (Exception e) {
					vms.setValue("");
					isNull(vms, 4);
					e.printStackTrace();

				}
			}

		}).start();
	}

	@FXML
	void filterPowerState(ActionEvent event) {
		filter();
	}

	@FXML
	void filterVPSStatus(ActionEvent event) {
		filter();
	}

	public static int toInt(String value) {
		int hours = 1;
		switch (value) {
		case "Hourly":
			hours = 1;
			break;
		case "6 Hours":
			hours = 6;
			break;
		case "12 Hours":
			hours = 12;
			break;
		case "Daily":
			hours = 24;
			break;
		case "Weekly":
			hours = 24 * 7;
			break;
		case "Monthly":
			hours = 720;
			break;
		default:
			break;
		}
		return hours;
	}

	public static String toStr(int value) {
		String hours = "Hourly";
		switch (value) {
		case 1:
			hours = "Hourly";
			break;
		case 6:
			hours = "6 Hours";
			break;
		case 12:
			hours = "12 Hours";
			break;
		case 24:
			hours = "Daily";
			break;
		case 168:
			hours = "Weekly";
			break;
		case 720:
			hours = "Monthly";
			break;
		default:
			break;
		}
		return hours;
	}
}