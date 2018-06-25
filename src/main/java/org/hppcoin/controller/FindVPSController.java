package org.hppcoin.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.hppcoin.dao.impl.VPSDaoImpl;
import org.hppcoin.util.ViewUtil;
import org.hppcoin.view.VPSView;

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
import javafx.scene.layout.AnchorPane;

public class FindVPSController extends MenuControler implements Initializable {
	ObservableList<VPSView> vpss = FXCollections.observableArrayList();
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	@FXML
	private AnchorPane root;

	@FXML
	private Slider cpuSlider;

	@FXML
	private Slider memorySlider;

	@FXML
	private Label cpuLbl;

	@FXML
	private Label memoryLbl;

	@FXML
	private Label hdLbl;

	@FXML
	private Slider hdSlider;

	@FXML
	private JFXProgressBar progress;

	@FXML
	private TableView<VPSView> vpsTable;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		vpss = ViewUtil.getVPSView(new VPSDaoImpl().selectAllOthersAvailable(), progress, 1);

		cpuSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				cpuLbl.setText(String.format("%.0f vCPUs", newValue));
				filter();
			}
		});
		memorySlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				memoryLbl.setText(String.format("%.0f MB", newValue));
				filter();
			}
		});
		hdSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				hdLbl.setText(String.format("%.0f GB", newValue));
				filter();
			}
		});

		// VPS Table

		// os column
		TableColumn<VPSView, String> osColumn = new TableColumn<>("Operating System");
		osColumn.setMinWidth(140);
		osColumn.setCellValueFactory(new PropertyValueFactory<>("os"));
		// vCPUs column
		TableColumn<VPSView, String> vCPUsColumn = new TableColumn<>("vCPUs");
		vCPUsColumn.setMinWidth(60);
		vCPUsColumn.setCellValueFactory(new PropertyValueFactory<>("cpu"));
		// memory column
		TableColumn<VPSView, String> memoryColumn = new TableColumn<>("Memory");
		memoryColumn.setMinWidth(100);
		memoryColumn.setCellValueFactory(new PropertyValueFactory<>("memory"));
		// Storage column
		TableColumn<VPSView, String> storageColumn = new TableColumn<>("Storage");
		storageColumn.setMinWidth(100);
		storageColumn.setCellValueFactory(new PropertyValueFactory<>("storage"));

		// setupFees column
		TableColumn<VPSView, String> setupFees = new TableColumn<>("Setup Fees");
		setupFees.setMinWidth(70);
		setupFees.setCellValueFactory(new PropertyValueFactory<>("setupPrice"));

		// Cost per minute column
		TableColumn<VPSView, String> costPerMinute = new TableColumn<>("Cost per minute");
		costPerMinute.setMinWidth(130);
		costPerMinute.setCellValueFactory(new PropertyValueFactory<>("costPerMinute"));

		// paymentInterval column
		TableColumn<VPSView, String> paymentInterval = new TableColumn<>("Payment Interval");
		paymentInterval.setMinWidth(140);
		paymentInterval.setCellValueFactory(new PropertyValueFactory<>("payementInterval"));

		// manage column
		TableColumn<VPSView, String> manageColumn = new TableColumn<>("Detail");
		manageColumn.setMinWidth(100);
		manageColumn.setCellValueFactory(new PropertyValueFactory<>("manage"));

		vpsTable.getColumns().addAll(vCPUsColumn, memoryColumn, storageColumn, setupFees, costPerMinute,
				paymentInterval, osColumn, manageColumn);
		Platform.runLater(() -> {
			vpsTable.getItems().addAll(vpss);
			progress.setVisible(false);
		});

	}

	protected void filter() {
		ObservableList<VPSView> newlist = FXCollections.observableArrayList();
		int minCpu = (int) cpuSlider.getValue();
		for (VPSView v : vpss)
			if (v.getCpu() >= minCpu)
				newlist.add(v);
		ObservableList<VPSView> newlist2 = FXCollections.observableArrayList();
		int minMemory = (int) memorySlider.getValue();
		int minHd = (int) hdSlider.getValue();

		if (minMemory > 128) {
			// Memory in MB
			for (VPSView v : newlist)
				if (v.getMemory() > minMemory * 1000L * 1000L)
					newlist2.add(v);
		} else
			newlist2.addAll(newlist);
		ObservableList<VPSView> newlist3 = FXCollections.observableArrayList();
		if (minHd > 5) {
			// Storage in GB
			for (VPSView v : newlist2)
				if (v.getStorage() > minHd * 1000L * 1000L * 1000L)
					newlist3.add(v);
		}

		else
			newlist3.addAll(newlist2);
		Platform.runLater(() -> {
			vpsTable.setItems(newlist3);
		});
	}

}