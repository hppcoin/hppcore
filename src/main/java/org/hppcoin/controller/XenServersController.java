package org.hppcoin.controller;

import static org.hppcoin.util.ViewUtil.isNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import org.apache.xmlrpc.XmlRpcException;
import org.hppcoin.dao.XenServerDao;
import org.hppcoin.dao.impl.XenServerDaoImpl;
import org.hppcoin.model.XenServer;
import org.hppcoin.service.XenServerService;
import org.hppcoin.util.ViewUtil;
import org.hppcoin.view.XenServerView;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import com.xensource.xenapi.Types.XenAPIException;
import com.xensource.xenapi.VM;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class XenServersController extends MenuControler implements Initializable {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// ip column
		TableColumn<XenServerView, String> ipColumn = new TableColumn<>("IP");
		ipColumn.setMinWidth(200);
		ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));

		// Hostname column
		TableColumn<XenServerView, String> hostNameColumn = new TableColumn<>("Host Name");
		hostNameColumn.setMinWidth(210);
		hostNameColumn.setCellValueFactory(new PropertyValueFactory<>("host"));

		// status column
		TableColumn<XenServerView, String> statusColumn = new TableColumn<>("Status");
		statusColumn.setMinWidth(100);
		statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
		// lastAccessTime column
		TableColumn<XenServerView, String> lastAccessColumn = new TableColumn<>("Last Access");
		lastAccessColumn.setMinWidth(250);
		lastAccessColumn.setCellValueFactory(new PropertyValueFactory<>("lastAccessTime"));
		// manage column
		TableColumn<XenServerView, String> manageColumn = new TableColumn<>("Manage");
		manageColumn.setMinWidth(100);
		manageColumn.setCellValueFactory(new PropertyValueFactory<>("manage"));

		xenTable.getColumns().addAll(ipColumn, hostNameColumn, statusColumn, lastAccessColumn, manageColumn);

		List<XenServer> xenServers = new XenServerDaoImpl().selectAll();
		ObservableList<XenServerView> servers = ViewUtil.getXenServerViewList(xenServers, xenProgress);
		xenTable.setItems(servers);
	}

	@FXML
	private TableView<XenServerView> xenTable;

	@FXML
	private FontAwesomeIcon ok;

	@FXML
	private FontAwesomeIcon ko;

	@FXML
	private JFXProgressBar xenProgress;
	@FXML
	private JFXTextField ip;

	@FXML
	private JFXTextField username;

	@FXML
	private JFXPasswordField password;

	@FXML
	private JFXTextField hostName;

	@FXML
	private JFXCheckBox defaultServer;

	@FXML
	void addXenServer(ActionEvent event) {
		if (isNull(ip, 5, ".") || isNull(username, 1) || isNull(password, 1))
			return;
		new Thread(new Runnable() {

			@Override
			public void run() {
				XenServer server = new XenServer();
				server.setIp(ip.getText());
				server.setUsername(username.getText());
				server.setPassword(password.getText());
				server.setHostName(hostName.getText());
				server.setCreationTime(new Date().getTime());
				server.setDefaultServer(defaultServer.isSelected());
				XenServerDao xenServerDao = new XenServerDaoImpl();
				xenServerDao.save(server);
				if (defaultServer.isSelected())
					xenServerDao.updateDefault(server.getIp());

				try {
					xenProgress.setVisible(true);
					XenServerService service = new XenServerService(server);
					List<VM> vms = service.getALLVM();
					server.setLastAccessTime(new Date().getTime());
					server.setStatus("ON");
					xenServerDao.update(server);
					XenServerView serverView = ViewUtil.getXenServerView(server, xenProgress);
					serverView.setTotalVMs(vms.size());
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							xenTable.getItems().add(serverView);
							xenProgress.setVisible(false);
							ok.setVisible(true);
						}
					});

				} catch (XenAPIException | MalformedURLException | XmlRpcException e) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							xenProgress.setVisible(false);
							ko.setVisible(true);
						}
					});
					server.setStatus("Unreachable");
					xenServerDao.update(server);
					LOGGER.severe(e.getMessage());
					e.printStackTrace();
				}

			}
		}).start();

	}

}
