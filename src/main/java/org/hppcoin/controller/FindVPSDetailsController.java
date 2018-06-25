package org.hppcoin.controller;

import static org.hppcoin.util.Display.maximize;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.hppcoin.Main;
import org.hppcoin.crons.WalletListener;
import org.hppcoin.dao.VPSDao;
import org.hppcoin.dao.impl.ContractDaoImpl;
import org.hppcoin.dao.impl.NavigationDaoImpl;
import org.hppcoin.dao.impl.VPSDaoImpl;
import org.hppcoin.model.Contract;
import org.hppcoin.model.ContractType;
import org.hppcoin.model.VPS;
import org.hppcoin.model.VPSRentalStatus;
import org.hppcoin.util.ViewUtil;
import org.hppcoin.view.ContractView;

import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class FindVPSDetailsController extends MenuControler implements Initializable {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private VPS vps=null;
	private VPSDao vpsDao=null;
	@FXML
	private AnchorPane root;

	@FXML
	private Label unreachable;

	@FXML
	private JFXProgressBar progress;

	@FXML
	private Label vcpus;

	@FXML
	private Label ram;

	@FXML
	private Label hd;

	@FXML
	private Label setupFees;
	
	@FXML
	private JFXTextField duration;

	@FXML
	private Label durationError;
	
	@FXML
	private Label hourlyCost;

	@FXML
	private Label paymentInterval;

	@FXML
	private Label maxDelayedPayments;

	@FXML
	private Label totalSuspended;

	@FXML
	private Label totalCompletedtotal;

	@FXML
	private TableView<ContractView> contractTable;

	@FXML
	void rentVPS(ActionEvent event) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int durationHours=0;
						try {
						durationHours=Integer.parseInt(duration.getText());
						}catch (Exception e) {
					Platform.runLater(()->	durationError.setText("duration is an integer number"));				
						return;
						}
				double balance=new WalletListener(false).getBalance();
				if(balance<(durationHours*60*vps.getCostPerMinute()+vps.getSetupPrice())) {
					Platform.runLater(()->					durationError.setText("Insufficient funds"));	
					return;
				}
				if(durationHours<vps.getPayementInterval()) {
					Platform.runLater(()->				durationError.setText("Minimum Duration "+vps.getPayementInterval()+" Hours"));	
					return;
				}
				Contract newContract=new Contract(true,ContractType.BUY);
				newContract.setDurationHours(durationHours);
				 newContract.setVps(vps);
				 new ContractDaoImpl().save(newContract);
				 
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
						Platform.runLater(()->{
						primaryStage.setScene(scene);
						primaryStage.setTitle("Rented VPS List");
						maximize(primaryStage);
						primaryStage.show();}
								);
						

					} catch (IOException e) {
						e.printStackTrace();
						LOGGER.severe(e.getMessage());
					}
				
			}
		}).start();
   
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyy HH:mm");
		vpsDao= new VPSDaoImpl();
		 vps =vpsDao.find(new NavigationDaoImpl().getCurrentVPSUID());
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				maxDelayedPayments.setText(String.valueOf(vps.getPayDelay()));
				setupFees.setText(String.valueOf(vps.getSetupPrice()));
				hourlyCost.setText(String.valueOf(vps.getCostPerMinute() * 60));
				// Payment Interval
				paymentInterval.setText(MyVPSController.toStr(vps.getPayementInterval()));
				vcpus.setText(String.valueOf(vps.getCpu().getCores()) + " vCPUs");
				ram.setText(String.valueOf(vps.getMemory().getMemorySize()) + " Bytes");
				hd.setText(String.valueOf(vps.getStorage().getStorageSize()) + " Bytes");
				int totalSuspendedContracts = 0;
				int totalCompletedContracts = 0;
				List<Contract> contracts = vps.getContracts();
				totalSuspended.setText(String.valueOf(totalSuspendedContracts));
				totalCompletedtotal.setText(String.valueOf(totalCompletedContracts));
				List<ContractView> contactViews = ViewUtil.getContractsView(contracts, progress, ContractType.BUY);
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

			}
		});

	}

}
