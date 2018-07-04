package org.hppcoin.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.hppcoin.Main;
import org.hppcoin.controller.MyVPSController;
import org.hppcoin.dao.impl.NavigationDaoImpl;
import org.hppcoin.dao.impl.TransactionDaoImpl;
import org.hppcoin.model.Contract;
import org.hppcoin.model.ContractType;
import org.hppcoin.model.HPPTransaction;
import org.hppcoin.model.Navigation;
import org.hppcoin.model.TransactionType;
import org.hppcoin.model.VPS;
import org.hppcoin.model.XenServer;
import org.hppcoin.view.ContractView;
import org.hppcoin.view.TransactionView;
import org.hppcoin.view.VPSView;
import org.hppcoin.view.XenServerView;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXProgressBar;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputControl;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ViewUtil {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static SimpleDateFormat formatter = new SimpleDateFormat("dd MM YYY HH:mm:ss");
	private static NavigationDaoImpl navDao = new NavigationDaoImpl();

	public static ObservableList<XenServerView> getXenServerViewList(List<XenServer> xenServers,
			JFXProgressBar progess) {
		ObservableList<XenServerView> servers = FXCollections.observableArrayList();
		if (xenServers == null || xenServers.size() < 1)
			return servers;
		for (XenServer server : xenServers) {
			XenServerView serverView = new XenServerView();
			serverView.setCreationTime(formatter.format(new Date(server.getCreationTime())));
			serverView.setHost(server.getHostName());
			serverView.setLastAccessTime(formatter.format(new Date(server.getLastAccessTime())));
			serverView.setIp(server.getIp());
			serverView.setStatus(server.getStatus());
			Button manage = new Button();
			manage.setStyle("-fx-background-color:transparent;");
			manage.setStyle("-fx-cursor:hand;");
			FontAwesomeIcon icon = new FontAwesomeIcon();
			icon.setGlyphName("GEAR");
			icon.setFill(Color.rgb(50, 184, 224));
			icon.setSize("1.3em");
			manage.setGraphic(icon);
			manage.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							progess.setVisible(true);
							icon.setGlyphName("SPINNER");
							manage.setDisable(true);

						}
					});
					new Thread(new Runnable() {

						@Override
						public void run() {
							Navigation navigation = navDao.find();
							navigation.setCurrentXenServerIP(server.getIp());
							navDao.update(navigation);
							FXMLLoader fxmlLoader = new FXMLLoader(
									Main.class.getResource("xengui/XenServerDetail.fxml"));

							Parent root = null;
							try {
								root = (Parent) fxmlLoader.load();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Scene scene = new Scene(root);
							Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
							String css = Main.class.getResource("hppTheme.css").toExternalForm();
							scene.getStylesheets().add(css);
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									primaryStage.setScene(scene);
									primaryStage.show();
								}
							});
						}
					}).start();
				}
			});
			serverView.setManage(manage);
			servers.add(serverView);
		}
		return servers;
	}

	public static boolean isNull(TextInputControl field, int min) {
		if (field.getText() == null || field.getText().length() < min) {
			field.setStyle("-fx-border-color:red;");
			return true;
		}
		return false;
	}

	public static boolean isNull(TextInputControl field, int min, String contain) {
		if (field.getText() == null || field.getText().length() < min || !field.getText().contains(contain)) {
			field.setStyle("-fx-border-color:red;");
			return true;
		}
		return false;
	}

	public static boolean isNull(JFXComboBox<String> field, int min, String contain) {
		if (field.getValue() == null || field.getValue().length() < min || !field.getValue().contains(contain)) {
			field.setStyle("-fx-border-color:red;");
			return true;
		}
		return false;
	}

	public static boolean isNull(JFXComboBox<String> field, int min) {
		if (field.getValue() == null || field.getValue().length() < min) {
			field.setStyle("-fx-border-color:red;");
			return true;
		}
		return false;
	}

	public static ObservableList<VPSView> getVPSView(List<VPS> vpsList, JFXProgressBar progess, int type) {
		ObservableList<VPSView> servers = FXCollections.observableArrayList();
		if (vpsList == null || vpsList.size() < 1)
			return servers;
		for (VPS server : vpsList)
			if (type == 0) {
				VPSView serverView = new VPSView();
				serverView.setCreationTime(formatter.format(new Date(server.getCreationTime())));
				serverView.setLastAccessTime(formatter.format(new Date(server.getLastAccessTime())));
				serverView.setIp(server.getIp());
				serverView.setRentalStatus(server.getRentalSattus());
				serverView.setPowerStatus(server.getPowerStatus());
				serverView.setRentalStatus(server.getRentalSattus());
				serverView.setCpu(server.getCpu().getCores());
				serverView.setMemory(server.getMemory().getMemorySize());
				serverView.setStorage(server.getStorage().getStorageSize());
				serverView.setSetupPrice(server.getSetupPrice());
				serverView.setPayementInterval(MyVPSController.toStr(server.getPayementInterval()));
				serverView.setCostPerMinute(server.getCostPerMinute());
				if(server.getXenServer()!=null)			serverView.setXenHost(server.getXenServer().getHostName());
				else serverView.setXenHost("UNKOWN");
				Button manage = new Button();
				manage.setStyle("-fx-background-color:transparent;");
				manage.setStyle("-fx-cursor:hand;");
				FontAwesomeIcon icon = new FontAwesomeIcon();
				icon.setGlyphName("GEAR");
				icon.setFill(Color.rgb(50, 184, 224));
				icon.setSize("1.3em");
				manage.setGraphic(icon);
				manage.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								progess.setVisible(true);
								icon.setGlyphName("SPINNER");
								manage.setDisable(true);

							}
						});
						new Thread(new Runnable() {

							@Override
							public void run() {
								Navigation navigation = navDao.find();
								navigation.setCurrentVPSUID(server.getUuid());
								navDao.update(navigation);
								FXMLLoader fxmlLoader = new FXMLLoader(
										Main.class.getResource("vps/MyVPSDetailsView.fxml"));

								Parent root = null;
								try {
									root = (Parent) fxmlLoader.load();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Scene scene = new Scene(root);
								Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

								Platform.runLater(new Runnable() {

									@Override
									public void run() {
										primaryStage.setTitle("My VPS Detail");
										String css = Main.class.getResource("hppTheme.css").toExternalForm();
										scene.getStylesheets().add(css);
										primaryStage.setScene(scene);
										primaryStage.show();

									}
								});

							}
						}).start();
					}
				});
				serverView.setManage(manage);
				servers.add(serverView);
			} else if (type == 1) {
				VPSView serverView = new VPSView();
				serverView.setCreationTime(formatter.format(new Date(server.getCreationTime())));
				serverView.setLastAccessTime(formatter.format(new Date(server.getLastAccessTime())));
				serverView.setIp(server.getIp());
				serverView.setRentalStatus(server.getRentalSattus());
				serverView.setPowerStatus(server.getPowerStatus());
				serverView.setRentalStatus(server.getRentalSattus());
				serverView.setCpu(server.getCpu().getCores());
				serverView.setMemory(server.getMemory().getMemorySize());
				serverView.setStorage(server.getStorage().getStorageSize());
				serverView.setSetupPrice(server.getSetupPrice());
				serverView.setPayementInterval(MyVPSController.toStr(server.getPayementInterval()));
				serverView.setCostPerMinute(server.getCostPerMinute());
				if(server.getXenServer()!=null)			serverView.setXenHost(server.getXenServer().getHostName());
				else serverView.setXenHost("UNKOWN");
				Button manage = new Button();
				manage.setStyle("-fx-background-color:transparent;");
				manage.setStyle("-fx-cursor:hand;");
				FontAwesomeIcon icon = new FontAwesomeIcon();
				icon.setGlyphName("GEAR");
				icon.setFill(Color.rgb(50, 184, 224));
				icon.setSize("1.3em");
				manage.setGraphic(icon);
				manage.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								progess.setVisible(true);
								icon.setGlyphName("SPINNER");
								manage.setDisable(true);

							}
						});
						new Thread(new Runnable() {

							@Override
							public void run() {
								Navigation navigation = navDao.find();
								navigation.setCurrentVPSUID(server.getUuid());
								navDao.update(navigation);
								FXMLLoader fxmlLoader = new FXMLLoader(
										Main.class.getResource("vps/FindVPSDetailsView.fxml"));

								Parent root = null;
								try {
									root = (Parent) fxmlLoader.load();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Scene scene = new Scene(root);
								Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

								Platform.runLater(new Runnable() {

									@Override
									public void run() {
										primaryStage.setTitle("VPS Detail");
										String css = Main.class.getResource("hppTheme.css").toExternalForm();
										scene.getStylesheets().add(css);
										primaryStage.setScene(scene);
										primaryStage.show();

									}
								});

							}
						}).start();
					}
				});
				serverView.setManage(manage);
				servers.add(serverView);
			} else if (type == 2) {
				VPSView serverView = new VPSView();
				serverView.setCreationTime(formatter.format(new Date(server.getCreationTime())));
				serverView.setLastAccessTime(formatter.format(new Date(server.getLastAccessTime())));
				serverView.setIp(server.getIp());
				serverView.setRentalStatus(server.getRentalSattus());
				serverView.setPowerStatus(server.getPowerStatus());
				serverView.setRentalStatus(server.getRentalSattus());
				serverView.setCpu(server.getCpu().getCores());
				serverView.setMemory(server.getMemory().getMemorySize());
				serverView.setStorage(server.getStorage().getStorageSize());
				serverView.setSetupPrice(server.getSetupPrice());
				serverView.setPayementInterval(MyVPSController.toStr(server.getPayementInterval()));
				serverView.setCostPerMinute(server.getCostPerMinute());
				serverView.setXenHost(server.getXenServer().getHostName());
				Button manage = new Button();
				manage.setStyle("-fx-background-color:transparent;");
				manage.setStyle("-fx-cursor:hand;");
				FontAwesomeIcon icon = new FontAwesomeIcon();
				icon.setGlyphName("GEAR");
				icon.setFill(Color.rgb(50, 184, 224));
				icon.setSize("1.3em");
				manage.setGraphic(icon);
				manage.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								progess.setVisible(true);
								icon.setGlyphName("SPINNER");
								manage.setDisable(true);

							}
						});
						new Thread(new Runnable() {

							@Override
							public void run() {
								Navigation navigation = navDao.find();
								navigation.setCurrentVPSUID(server.getUuid());
								navDao.update(navigation);
								FXMLLoader fxmlLoader = new FXMLLoader(
										Main.class.getResource("vps/MyVPSDetailsView.fxml"));

								Parent root = null;
								try {
									root = (Parent) fxmlLoader.load();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Scene scene = new Scene(root);
								Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

								Platform.runLater(new Runnable() {

									@Override
									public void run() {
										primaryStage.setTitle("My VPS Detail");
										String css = Main.class.getResource("hppTheme.css").toExternalForm();
										scene.getStylesheets().add(css);
										primaryStage.setScene(scene);
										primaryStage.show();

									}
								});

							}
						}).start();
					}
				});
				serverView.setManage(manage);
				servers.add(serverView);
			}

		return servers;
	}

	public static List<TransactionView> getTransactionsView(List<HPPTransaction> transactions, JFXProgressBar progess) {
		List<TransactionView> txs = new ArrayList<TransactionView>();
		if (transactions == null || transactions.size() < 1)
			return txs;
		for (HPPTransaction tx : transactions) {
			TransactionView txView = new TransactionView();
			txView.setAmount(tx.getAmount());
			txView.setConfirmations(tx.getConfirmations());
			txView.setTxDate(new SimpleDateFormat("dd MMM yyy HH:mm:ss").format(new Date(tx.getTime())));
			FontAwesomeIcon type = new FontAwesomeIcon();
			type.setVisible(true);
			if (tx.getType().equals(TransactionType.RECEIVE)) {
				type.setGlyphName("ARROW_CIRCLE_DOWN");
				type.setFill(Color.rgb(21, 171, 156));
			}
			if (tx.getType().equals(TransactionType.SEND)) {
				type.setGlyphName("ARROW_CIRCLE_UP");
				type.setFill(Color.rgb(225, 93, 93));
			}
			type.setSize("1.3em");
			txView.setType(type);
			Button manage = new Button();
			manage.setStyle("-fx-background-color:transparent;");
			manage.setStyle("-fx-cursor:hand;");
			FontAwesomeIcon icon = new FontAwesomeIcon();
			icon.setGlyphName("ARROWS_ALT");
			icon.setFill(Color.rgb(50, 184, 224));
			icon.setSize("1.3em");
			manage.setGraphic(icon);
			manage.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					try {
						Main.hostServices.showDocument("http://explorer.hppcoin.com/tx/" + tx.getTxid());
					} catch (Exception ex) {
						LOGGER.severe(ex.getMessage());
					}

				}

			});
			txView.setExplore(manage);
			txs.add(txView);
		}
		return txs;
	}

	public static XenServerView getXenServerView(XenServer server, JFXProgressBar progess) {
		List<XenServer> servers = new ArrayList<XenServer>();
		servers.add(server);
		return getXenServerViewList(servers, progess).get(0);
	}

	public static List<ContractView> getContractsView(List<Contract> contracts, JFXProgressBar progess,
			ContractType type) {
		List<ContractView> contractsViews = new ArrayList<>();
		if (contracts == null || contracts.size() < 1)
			return contractsViews;
		for (Contract contract : contracts) {
			ContractView contractView = new ContractView();
			contractView.setEndDate(
					formatter.format(contract.getStartDate() + contract.getDurationHours() * 60L * 60L * 1000L));
			contractView.setHourlyPrice(contract.getCostPerMinute() * 60);
			switch (contract.getPayementInterval()) {
			case 1:
				contractView.setPayementInterval("Hourly");
				break;
			case 6:
			case 12:
				contractView.setPayementInterval(contract.getPayementInterval() + " Hours");
				break;
			case 24:
				contractView.setPayementInterval("Daily");
				break;
			case 168:
				contractView.setPayementInterval("Weekly");
				break;
			case 720:
				contractView.setPayementInterval("Monthly");
				break;
			}
			contractView.setStatus(contract.getContractStatus().toString());
			double amount = new TransactionDaoImpl().getReceivedAmount(contract.getRecievingAddress());
			 new TransactionDaoImpl().updateAll(contract);
			contractView.setPaidAmount(amount);
            contractView.setCpu(contract.getCores());
            contractView.setMemory(contract.getMemorySize());
            contractView.setStorage(contract.getStorageSize());
            contractView.setSshPort(contract.getSshPort());
            contractView.setPassword(contract.getPassword());
            contractView.setUsername(contract.getUsername());
            contractView.setIp(contract.getIp());
            contractView.setStartDate(formatter.format(new Date(contract.getStartDate())));
            contractView.setDurationHours(contract.getDurationHours());
            contractView.setEndDate(formatter.format(new Date(contract.getStartDate()+(contract.getDurationHours()*60L*60L*1000))));
			Button manage = new Button();
			manage.setStyle("-fx-background-color:transparent;");
			manage.setStyle("-fx-cursor:hand;");
			FontAwesomeIcon icon = new FontAwesomeIcon();
			icon.setGlyphName("GEAR");
			icon.setFill(Color.rgb(50, 184, 224));
			icon.setSize("1.3em");
			manage.setGraphic(icon);
			if (type == ContractType.SELL)
				manage.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								progess.setVisible(true);
								icon.setGlyphName("SPINNER");
								manage.setDisable(true);

							}
						});

						new Thread(new Runnable() {

							@Override
							public void run() {
								Navigation navigation = navDao.find();
								navigation.setCurrentContractID(contract.getId());
								navDao.update(navigation);
								FXMLLoader fxmlLoader = new FXMLLoader(
										Main.class.getResource("vps/ContractDetailsView.fxml"));

								Parent root = null;
								try {
									root = (Parent) fxmlLoader.load();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Scene scene = new Scene(root);
								Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
								Platform.runLater(new Runnable() {

									@Override
									public void run() {
										primaryStage.setScene(scene);
										String css = Main.class.getResource("hppTheme.css").toExternalForm();
										scene.getStylesheets().add(css);
										primaryStage.setTitle("Contract Detail");
										primaryStage.show();

									}
								});
							}
						}).start();

					}
				});
			else if (type == ContractType.BUY)
				manage.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						Platform.runLater(new Runnable() {

							@Override
							public void run() {
								progess.setVisible(true);
								icon.setGlyphName("SPINNER");
								manage.setDisable(true);

							}
						});

						new Thread(new Runnable() {

							@Override
							public void run() {
								Navigation navigation = navDao.find();
								navigation.setCurrentContractID(contract.getId());
								navDao.update(navigation);
								FXMLLoader fxmlLoader = new FXMLLoader(
										Main.class.getResource("vps/ContractDetailsViewBUY.fxml"));

								Parent root = null;
								try {
									root = (Parent) fxmlLoader.load();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Scene scene = new Scene(root);
								Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
								Platform.runLater(new Runnable() {

									@Override
									public void run() {
										primaryStage.setScene(scene);
										String css = Main.class.getResource("hppTheme.css").toExternalForm();
										scene.getStylesheets().add(css);
										primaryStage.setTitle("Contract Detail");
										primaryStage.show();

									}
								});
							}
						}).start();

					}
				});
			contractView.setManage(manage);
			contractsViews.add(contractView);
		}
		return contractsViews;
	}

}
