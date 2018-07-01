package org.hppcoin.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.hppcoin.dao.impl.ContractDaoImpl;
import org.hppcoin.model.Contract;
import org.hppcoin.model.ContractType;
import org.hppcoin.util.ViewUtil;
import org.hppcoin.view.ContractView;

import com.jfoenix.controls.JFXProgressBar;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class RentedVPSController extends MenuControler implements Initializable {
	List<Contract> contractsList = null;
	List<Contract> contractsListPending = new ArrayList<Contract>();
	List<Contract> contractsListActive = new ArrayList<Contract>();
	List<Contract> contractsListPaused = new ArrayList<Contract>();
	List<Contract> contractsListSuspended = new ArrayList<Contract>();
	List<Contract> contractsListCompleted = new ArrayList<Contract>();
	List<ContractView> contractsPending = new ArrayList<ContractView>();
	List<ContractView> contractsActive = new ArrayList<ContractView>();
	List<ContractView> contractsSuspended = new ArrayList<ContractView>();
	List<ContractView> contractsPaused = new ArrayList<ContractView>();
	List<ContractView> contractsCompleted = new ArrayList<ContractView>();
	@FXML
	private TableView<ContractView> contractTablePending;

	@FXML
	private JFXProgressBar progress;

	@FXML
	private Slider cpuSlider;

	@FXML
	private Label cpuLbl;

	@FXML
	private TableView<ContractView> contractTableActive;

	@FXML
	private JFXProgressBar progressActive;

	@FXML
	private Slider cpuSliderActive;

	@FXML
	private Label cpuLblActive;

	@FXML
	private TableView<ContractView> contractTablePaused;

	@FXML
	private JFXProgressBar progressPaused;

	@FXML
	private Slider cpuSliderPaused;

	@FXML
	private Label cpuLblPaused;

	@FXML
	private TableView<ContractView> contractTableSuspended;

	@FXML
	private JFXProgressBar progressSuspended;

	@FXML
	private Slider cpuSliderSuspended;

	@FXML
	private Label cpuLblSuspended;

	@FXML
	private TableView<ContractView> contractTableCompleted;

	@FXML
	private JFXProgressBar progressCompleted;

	@FXML
	private Slider cpuSliderCompleted;

	@FXML
	private Label cpuLblCompleted;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		cpuSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				cpuLbl.setText(String.format("%.0f", newValue));

				filterPending();

			}
		});

		cpuSliderActive.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				cpuLblActive.setText(String.format("%.0f", newValue));

				filterActive();

			}
		});

		cpuSliderSuspended.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				cpuLblSuspended.setText(String.format("%.0f", newValue));

				filterSuspended();

			}
		});

		cpuSliderPaused.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				cpuLblPaused.setText(String.format("%.0f", newValue));

				filterPaused();

			}
		});

		cpuSliderCompleted.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				cpuLblCompleted.setText(String.format("%.0f", newValue));

				filterCompleted();

			}
		});
		prepareTableColumns(contractTablePending,false);
		prepareTableColumns(contractTableActive,true);
		prepareTableColumns(contractTablePaused,true);
		prepareTableColumns(contractTableSuspended,true);
		prepareTableColumns(contractTableCompleted,true);
		
		contractsList = new ContractDaoImpl().selectAll();
		if (contractsList != null && contractsList.size() > 0)
			for (Contract contract : contractsList) {
				switch (contract.getContractStatus()) {
				case PENDING:
					contractsListPending.add(contract);
					break;
				case PAUSED:
					contractsListPaused.add(contract);
					break;
				case SUSPENDED:
					contractsListSuspended.add(contract);
					break;
				case COMPLETE:
					contractsListCompleted.add(contract);
					break;

				case ACTIVE:
					contractsListActive.add(contract);
					break;

				default:
					break;
				}
			}
		contractsPending = ViewUtil.getContractsView(contractsListPending, progress, ContractType.BUY);
		Platform.runLater(() -> contractTablePending.getItems().addAll(contractsPending));

		contractsPaused = ViewUtil.getContractsView(contractsListPaused, progress, ContractType.BUY);
		Platform.runLater(() -> contractTablePaused.getItems().addAll(contractsPaused));

		contractsSuspended = ViewUtil.getContractsView(contractsListSuspended, progress, ContractType.BUY);
		Platform.runLater(() -> contractTableSuspended.getItems().addAll(contractsSuspended));

		contractsActive = ViewUtil.getContractsView(contractsListActive, progress, ContractType.BUY);
		Platform.runLater(() -> {

			contractTableActive.getItems().addAll(contractsActive);

		});

		contractsCompleted = ViewUtil.getContractsView(contractsListCompleted, progress, ContractType.BUY);
		Platform.runLater(() -> contractTableCompleted.getItems().addAll(contractsCompleted));
	}

	protected void filterPending() {
		ObservableList<ContractView> newlist = FXCollections.observableArrayList();
		int min = (int) cpuSlider.getValue();
		for (ContractView v : contractsPending)
			if (v.getCpu() >= min)
				newlist.add(v);
		Platform.runLater(() -> contractTablePending.setItems(newlist));
	}

	protected void filterPaused() {
		ObservableList<ContractView> newlist = FXCollections.observableArrayList();
		int min = (int) cpuSliderPaused.getValue();
		for (ContractView v : contractsPaused)
			if (v.getCpu() >= min)
				newlist.add(v);

		Platform.runLater(() -> contractTablePaused.setItems(newlist));
	}

	protected void filterActive() {
		ObservableList<ContractView> newlist = FXCollections.observableArrayList();
		int min = (int) cpuSliderActive.getValue();
		for (ContractView v : contractsActive)
			if (v.getCpu() >= min) {
				newlist.add(v);
			}

		Platform.runLater(() -> contractTableActive.setItems(newlist));
	}

	protected void filterCompleted() {
		ObservableList<ContractView> newlist = FXCollections.observableArrayList();
		int min = (int) cpuSliderCompleted.getValue();
		for (ContractView v : contractsCompleted)
			if (v.getCpu() >= min)
				newlist.add(v);

		Platform.runLater(() -> contractTableCompleted.setItems(newlist));
	}

	protected void filterSuspended() {
		ObservableList<ContractView> newlist = FXCollections.observableArrayList();
		int min = (int) cpuSliderSuspended.getValue();
		for (ContractView v : contractsSuspended)
			if (v.getCpu() >= min)
				newlist.add(v);
		Platform.runLater(() -> contractTableSuspended.setItems(newlist));
	}

	private void prepareTableColumns(TableView<ContractView> table,boolean manageButton) {
		// Contract Table
		// IP column
		TableColumn<ContractView, String> ipColumn = new TableColumn<>("IP");
		ipColumn.setMinWidth(250);
		ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));

		// manage column
				TableColumn<ContractView, String> manageColumn = new TableColumn<>("Manage");
				manageColumn.setMinWidth(100);
				manageColumn.setCellValueFactory(new PropertyValueFactory<>("manage"));
		// status column
		TableColumn<ContractView, String> statusColumn = new TableColumn<>("Contract Status");
		statusColumn.setMinWidth(100);
		statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
		// vCPUs column
		TableColumn<ContractView, String> vCPUsColumn = new TableColumn<>("vCPUs");
		vCPUsColumn.setMinWidth(100);
		vCPUsColumn.setCellValueFactory(new PropertyValueFactory<>("cpu"));
		// memory column
		TableColumn<ContractView, String> memoryColumn = new TableColumn<>("Memory");
		memoryColumn.setMinWidth(150);
		memoryColumn.setCellValueFactory(new PropertyValueFactory<>("memory"));
		// Storage column
		TableColumn<ContractView, String> storageColumn = new TableColumn<>("Storage");
		storageColumn.setMinWidth(150);
		storageColumn.setCellValueFactory(new PropertyValueFactory<>("storage"));
		// setupFees column
		TableColumn<ContractView, String> setupFees = new TableColumn<>("Setup Fees");
		setupFees.setMinWidth(70);
		setupFees.setCellValueFactory(new PropertyValueFactory<>("setupPrice"));

		// Cost per minute column
		TableColumn<ContractView, String> costPerMinute = new TableColumn<>("Hourly Price");
		costPerMinute.setMinWidth(130);
		costPerMinute.setCellValueFactory(new PropertyValueFactory<>("hourlyPrice"));

		// paymentInterval column
		TableColumn<ContractView, String> paymentInterval = new TableColumn<>("Payment Interval");
		paymentInterval.setMinWidth(140);
		paymentInterval.setCellValueFactory(new PropertyValueFactory<>("payementInterval"));
		table.getColumns().addAll(vCPUsColumn, memoryColumn, storageColumn, setupFees, costPerMinute, paymentInterval);
	if(manageButton)table.getColumns().add(manageColumn);
	}
}