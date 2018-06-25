package org.hppcoin.controller;

import static org.hppcoin.util.ViewUtil.isNull;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.hppcoin.dao.XenServerDao;
import org.hppcoin.dao.impl.NavigationDaoImpl;
import org.hppcoin.dao.impl.XenServerDaoImpl;
import org.hppcoin.model.VPS;
import org.hppcoin.model.XenServer;
import org.hppcoin.service.XenServerService;
import org.hppcoin.util.ViewUtil;
import org.hppcoin.view.MyColors;
import org.hppcoin.view.VPSView;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class XenServerDetailController extends MenuControler implements Initializable {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	XenServerDao xenServerDao =null;
	private XenServerService xenService = null;
	private XenServer server = null;
	@FXML
	private AnchorPane root;
	private StackPane stackPane;
	@FXML
	private TableView<VPSView> vpsTable;
	@FXML
	Button rebootButton;

	@FXML
	Button shutdownButton;

	@FXML
	Button okButton;

	@FXML
	private JFXProgressBar progress;

	@FXML
	private JFXTextField ip;

	@FXML
	private JFXButton modifyXenServer;

	@FXML
	private JFXTextField username;

	@FXML
	private JFXTextField hostName;

	@FXML
	private JFXCheckBox defaultServer;

	@FXML
	private FontAwesomeIcon shutdown;

	@FXML
	private FontAwesomeIcon ok;

	@FXML
	private FontAwesomeIcon reboot;

	@FXML
	private JFXPasswordField password;

	@FXML
	private Label creationTime;

	@FXML
	private Label unreachable;

	@FXML
	private Label lastAccessTime;

	@FXML
	private Label totalVMs;

	@FXML
	private Label totalVPS;

	@FXML
	void modifyXenServer(ActionEvent event) {
		
		if (isNull(ip, 5, ".") || isNull(username, 1) || isNull(password, 1))
			return;
		server.setIp(ip.getText());
		server.setUsername(username.getText());
		server.setPassword(password.getText());
		server.setHostName(hostName.getText());
		server.setDefaultServer(defaultServer.isSelected());
		xenServerDao.update(server);
		if (defaultServer.isSelected())
			xenServerDao.updateDefault(server.getIp());
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ok.setVisible(true);
				okButton.setGraphic(ok);
				final Tooltip okTooltip = new Tooltip();
				okTooltip.setText("Modified successefully");
				okButton.setTooltip(okTooltip);
			}
		});

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		xenServerDao= new XenServerDaoImpl();
		 server = xenServerDao.find(new NavigationDaoImpl().getCurrentXenServerIP());
		 
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyy HH:mm");
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							progress.setVisible(true);
							unreachable.setText("Synchronizing ...");
							unreachable.setTextFill(MyColors.BLUE);
							modifyXenServer.setDisable(true);
							ip.setDisable(true);
							defaultServer.setDisable(true);
							hostName.setDisable(true);
							username.setDisable(true);
							password.setDisable(true);
							rebootButton.setDisable(true);
							shutdownButton.setDisable(true);
							reboot.setFill(Color.rgb(204, 221, 238));
							shutdown.setFill(Color.rgb(204, 221, 238));
							creationTime.setText(dateFormat.format(server.getCreationTime()));
							lastAccessTime.setText(dateFormat.format(server.getLastAccessTime()));
							ip.setText(server.getIp());
							username.setText(server.getUsername());
							password.setText(server.getPassword());
							hostName.setText(server.getHostName());
							defaultServer.setSelected(server.isDefaultServer());
							
						}
					});
					
					xenService = new XenServerService(server);
					int totalVmsValue = xenService.getALLVM().size();
					List<VPS> vpss = server.getVpsList();
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							final Tooltip RebootTooltip = new Tooltip();
							RebootTooltip.setText("\nReboot XenServer \n" + server.getHostName() + "\n");
							rebootButton.setGraphic(reboot);
							rebootButton.setTooltip(RebootTooltip);
							shutdownButton.setGraphic(shutdown);
							final Tooltip shutdownTooltip = new Tooltip();
							shutdownTooltip.setText("Shut down XenServer \n" + server.getHostName() + "\n");
							shutdownButton.setTooltip(shutdownTooltip);
							if (vpss != null && vpss.size() > 0) {
								totalVPS.setText(String.valueOf(vpss.size()));
								//VPS Table
								//xenServer column
								TableColumn<VPSView, String> xenServerColumn=new TableColumn<>("Xen Hostname");
								xenServerColumn.setMinWidth(100);
								xenServerColumn.setCellValueFactory(new PropertyValueFactory<>("xenHost"));
								
								//username column
								TableColumn<VPSView, String> ipColumn=new TableColumn<>("IP");
								ipColumn.setMinWidth(250);
								ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));
								
								//status column
								TableColumn<VPSView, String> statusColumn=new TableColumn<>("Renting Status");
								statusColumn.setMinWidth(100);
								statusColumn.setCellValueFactory(new PropertyValueFactory<>("rentalStatus"));
								//vCPUs column
								TableColumn<VPSView, String> vCPUsColumn=new TableColumn<>("vCPUs");
								vCPUsColumn.setMinWidth(100);
								vCPUsColumn.setCellValueFactory(new PropertyValueFactory<>("cpu"));
								//memory column
								TableColumn<VPSView, String> memoryColumn=new TableColumn<>("Memory");
								memoryColumn.setMinWidth(150);
								memoryColumn.setCellValueFactory(new PropertyValueFactory<>("memory"));
								//Storage column
								TableColumn<VPSView, String> storageColumn=new TableColumn<>("Storage");
								storageColumn.setMinWidth(150);
								storageColumn.setCellValueFactory(new PropertyValueFactory<>("storage"));
										
								//manage column
								TableColumn<VPSView, String> manageColumn=new TableColumn<>("Manage");
								manageColumn.setMinWidth(100);
								manageColumn.setCellValueFactory(new PropertyValueFactory<>("manage"));
								
								vpsTable.getColumns().addAll(xenServerColumn,ipColumn,vCPUsColumn,memoryColumn,storageColumn,statusColumn,manageColumn);
								vpsTable.setItems(ViewUtil.getVPSView(vpss, progress,0));
							} else {
								totalVPS.setText("0");
								vpsTable.setVisible(false);
							}
							totalVMs.setText(String.valueOf(totalVmsValue));
							server.setLastAccessTime(new Date().getTime());
							lastAccessTime.setText(dateFormat.format(new Date()));

							modifyXenServer.setDisable(false);
							ip.setDisable(false);
							defaultServer.setDisable(false);
							hostName.setDisable(false);
							username.setDisable(false);
							password.setDisable(false);
							rebootButton.setDisable(false);
							shutdownButton.setDisable(false);
							reboot.setFill(Color.rgb(50, 184, 224));
							shutdown.setFill(Color.rgb(225, 10, 10));
							progress.setVisible(false);
							unreachable.setText("ACTIVE");
							unreachable.setTextFill(MyColors.BLUE);
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							vpsTable.setVisible(false);
							unreachable.setText("Server Unreachable ! please check Server credentials");
							unreachable.setTextFill(MyColors.RED);
							LOGGER.severe(e.getMessage());
							reboot.setFill(MyColors.GRAY);
							shutdown.setFill(MyColors.GRAY);
							rebootButton.setDisable(true);
							shutdownButton.setDisable(true);
							
							modifyXenServer.setDisable(false);
							ip.setDisable(false);
							defaultServer.setDisable(false);
							hostName.setDisable(false);
							username.setDisable(false);
							password.setDisable(false);
							progress.setVisible(false);
						}
					});

				}
			}
		}).start();

	}

	@FXML
	private void reboot(ActionEvent event) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				JFXDialogLayout dialogContent = new JFXDialogLayout();
				dialogContent.setHeading(new Text("Reboot XenServer " + server.getHostName()));
				VBox vBox = new VBox();

				vBox.getChildren().add(new Label("Caution! : Rebooting XenServer wil halt all active VMs"));
				vBox.getChildren().add(new Label("Reboot and halt all active VMs ?"));
				vBox.getChildren().add(new Label(" "));
				HBox hBox = new HBox();

				JFXButton cancel = new JFXButton("Cancel");
				cancel.getStyleClass().add("cancel");

				JFXButton close = new JFXButton("Reboot");
				close.getStyleClass().add("close");
				hBox.getChildren().add(cancel);
				hBox.getChildren().add(new Label(" "));
				hBox.getChildren().add(close);
				vBox.getChildren().add(hBox);
				vBox.getChildren().add(new Label(" "));
				dialogContent.setBody(vBox);
				stackPane = new StackPane();
				Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				Pane rootPane = ((Pane) primaryStage.getScene().getRoot());
				for (Node node : rootPane.getChildren()) {
					if (!(node instanceof StackPane))
						node.setDisable(true);
				}
				rootPane.getChildren().add(stackPane);

				JFXDialog dialog = new JFXDialog(stackPane, dialogContent, JFXDialog.DialogTransition.BOTTOM);
				cancel.setOnAction(e -> {
					for (Node node : rootPane.getChildren()) {
						if (!(node instanceof StackPane))
							node.setDisable(false);
					}
					dialog.close();
				});

				close.setOnAction(e -> {

					new Thread(new Runnable() {

						@Override
						public void run() {
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									progress.setVisible(true);

								}
							});
							try {
								xenService.reboot();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									progress.setVisible(false);
									for (Node node : rootPane.getChildren()) {
										if (!(node instanceof StackPane))
											node.setDisable(false);
									}
									dialog.close();
								}
							});
						}
					}).start();
				});
				dialog.show();
			}
		});

	}

	@FXML
	private void shutdown(ActionEvent event) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				JFXDialogLayout dialogContent = new JFXDialogLayout();
				dialogContent.setHeading(new Text("Shutdown XenServer " + server.getHostName()));
				VBox vBox = new VBox();

				vBox.getChildren()
						.add(new Label("Caution! : Shutting down XenServer wil shut down all active VMs and VPS"));
				vBox.getChildren().add(new Label("Sht down and halt all active VMs and VPS ?"));
				vBox.getChildren().add(new Label(" "));
				HBox hBox = new HBox();

				JFXButton cancel = new JFXButton("Cancel");
				cancel.getStyleClass().add("cancel");

				JFXButton close = new JFXButton("Shutdown");
				close.getStyleClass().add("close");
				hBox.getChildren().add(cancel);
				hBox.getChildren().add(new Label(" "));
				hBox.getChildren().add(close);
				vBox.getChildren().add(hBox);
				vBox.getChildren().add(new Label(" "));
				dialogContent.setBody(vBox);
				stackPane = new StackPane();
				Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
				Pane rootPane = ((Pane) primaryStage.getScene().getRoot());
				for (Node node : rootPane.getChildren()) {
					if (!(node instanceof StackPane))
						node.setDisable(true);
				}
				rootPane.getChildren().add(stackPane);

				JFXDialog dialog = new JFXDialog(stackPane, dialogContent, JFXDialog.DialogTransition.BOTTOM);
				cancel.setOnAction(e -> {
					for (Node node : rootPane.getChildren()) {
						if (!(node instanceof StackPane))
							node.setDisable(false);
					}
					dialog.close();
				});
				close.setOnAction(e -> {
					new Thread(new Runnable() {

						@Override
						public void run() {
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									progress.setVisible(true);

								}
							});
							try {
								xenService.shutDown();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									progress.setVisible(false);
									for (Node node : rootPane.getChildren()) {
										if (!(node instanceof StackPane))
											node.setDisable(false);
									}
									dialog.close();
								}
							});
						}
					}).start();

				});
				dialog.show();
			}
		});

	}

}
