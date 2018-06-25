package org.hppcoin.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

public class DashboardController extends MenuControler implements Initializable {
	@FXML
	private AnchorPane root;

	@FXML
	private Tab overviewTab;

	@FXML
	private Tab sendTab;

	@FXML
	private Tab receiveTab;

	@FXML
	private Tab transactionsTab;

	@FXML
	private PieChart vpsChart;

	@FXML
	private PieChart contractChart;

	@FXML
	private LineChart<?, ?> lineChart;

	@FXML
	private CategoryAxis days;

	@FXML
	private NumberAxis amount;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<PieChart.Data> vpsData = FXCollections.observableArrayList(new PieChart.Data("Rented", 15),
				new PieChart.Data("Iddle", 8), new PieChart.Data("Suspended", 4));
		ObservableList<PieChart.Data> contractData = FXCollections.observableArrayList(new PieChart.Data("Active", 25),
				new PieChart.Data("Suspended", 13), new PieChart.Data("Renewal", 13));

		vpsChart.setData(vpsData);
		contractChart.setData(contractData);
		XYChart.Series serires = new XYChart.Series();
		serires.getData().add(new XYChart.Data<>("1", 546.67));
		serires.getData().add(new XYChart.Data<>("2", 847.67));
		serires.getData().add(new XYChart.Data<>("3", 1020.67));
		serires.getData().add(new XYChart.Data<>("4", 1546.67));
		serires.getData().add(new XYChart.Data<>("5", 5046.67));
		serires.getData().add(new XYChart.Data<>("6", 900.67));
		lineChart.getData().addAll(serires);

	}

}