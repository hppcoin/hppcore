package org.hppcoin.controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

import org.controlsfx.control.WorldMapView;
import org.controlsfx.glyphfont.FontAwesome;
import org.hppcoin.Main;
import org.hppcoin.crons.WalletListener;
import org.hppcoin.dao.LMNodeDao;
import org.hppcoin.dao.SettingsDao;
import org.hppcoin.dao.impl.LMNodeDaoImpl;
import org.hppcoin.dao.impl.SettingsDaoImpl;
import org.hppcoin.dao.impl.TransactionDaoImpl;
import org.hppcoin.model.Balance;
import org.hppcoin.model.HPPTransaction;
import org.hppcoin.model.LMNode;
import org.hppcoin.model.Settings;
import org.hppcoin.model.TransactionType;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class WalletController extends MenuControler implements Initializable {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	@FXML
	private WorldMapView map;
	private FontAwesome fontAwesome = new FontAwesome();
	@FXML
	private JFXTextField newAddress;
	@FXML
	private ImageView qrcode;
	@FXML
	private AnchorPane root;
	@FXML
	JFXTextField payTo;
	@FXML
	JFXTextField amount;
	@FXML
	private Hyperlink txidSend;
	@FXML
	private JFXButton sendButton;

	@FXML
	private FontAwesomeIcon check;

	@FXML
	private Label balanceSend;

	@FXML
	private Label errorAddress;
	@FXML
	private Label errorAmount;

	@FXML
	private Label available;

	@FXML
	private Label pending;

	@FXML
	private Label total;

	@FXML
	private FontAwesomeIcon f1;

	@FXML
	private Label d1;

	@FXML
	private Label am1;

	@FXML
	private Label ad1;

	@FXML
	private FontAwesomeIcon f2;

	@FXML
	private Label d2;

	@FXML
	private Label am2;

	@FXML
	private Label ad2;

	@FXML
	private FontAwesomeIcon f3;

	@FXML
	private Label d3;

	@FXML
	private Label am3;

	@FXML
	private Label ad3;

	@FXML
	private FontAwesomeIcon f4;

	@FXML
	private Label d4;

	@FXML
	private Label am4;

	@FXML
	private Label ad4;

	@FXML
	private FontAwesomeIcon f5;

	@FXML
	private Label d5;

	@FXML
	private Label am5;

	@FXML
	private Label ad5;

	@FXML
	private FontAwesomeIcon f6;

	@FXML
	private Label d6;

	@FXML
	private Label am6;

	@FXML
	private Label ad6;

	@FXML
	private Tab overviewTab;

	@FXML
	private Tab sendTab;

	@FXML
	private Tab receiveTab;

	@FXML
	private Tab transactionsTab;

	@FXML
	private Tab lmNodesTab;

	@FXML
	private Tab lmnMapTab;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		updateOverviewUI();
	}

	@FXML
	public void updateTransactionsUI() {

	}

	@FXML
	public void updateSendUI() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// Init Balance
				sendButton.getStyleClass().add("buttonStyleHpp");
				Balance balance = getBalance();
				balanceSend.setText("Balance: " + balance.getAvailable() + " HPP");

			}
		});
	}

	@FXML
	public void updateLMNodeTableUI() {

	}

	@FXML
	public void updateLMNodeUI() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					LMNodeDao lmnodeDao = new LMNodeDaoImpl();
					final LMNode current = lmnodeDao.getWinnerGeo();

					final List<LMNode> nodes = lmnodeDao.getAll();
					BooleanProperty showColorsProperty = new SimpleBooleanProperty(this, "showColors", true);
					List<WorldMapView.Location> locations = new ArrayList<>();
					Platform.runLater(() -> map.getLocations().clear());
					if (nodes != null && nodes.size() > 0)
						for (LMNode node : nodes)
							locations.add(new WorldMapView.Location(node.getCountryIso(), node.getLatitude(),
									node.getLongitude()));
					Platform.runLater(() -> map.getLocations().addAll(locations));
					Platform.runLater(() -> map.setShowLocations(true));
					final Tooltip tooltip = new Tooltip();
					Platform.runLater(() -> map.setCountryViewFactory(country -> {
						WorldMapView.CountryView view = new WorldMapView.CountryView(country);
						if (showColorsProperty.get()) {
							view.getStyleClass().add("country" + getRank(country.toString(), nodes));
						}
						return view;
					}));

					Platform.runLater(() -> map.setLocationViewFactory(location -> {
						if (location.getLatitude() == current.getLatitude()
								&& location.getLongitude() == current.getLongitude()) {
							final org.controlsfx.glyphfont.Glyph glyph = fontAwesome.create(FontAwesome.Glyph.STAR);
							glyph.setFontSize(32);
							glyph.setStyle("-fx-text-fill: yellow; -fx-stroke: orange;");
							glyph.setEffect(new DropShadow());
							glyph.setTranslateX(-8);
							glyph.setTranslateY(-8);
							glyph.setOnMouseClicked(evt -> {
								Alert alert = new Alert(Alert.AlertType.INFORMATION);
								alert.setTitle("Next LMN Payee");
								alert.setContentText("Payee :" + current.getAddress() + "\nIP :" + current.getIp()
										+ "\nCountry :" + current.getCountry());
								alert.show();
							});
							glyph.setOnMouseEntered(evt -> tooltip.setText(location.getName()));
							Tooltip.install(glyph, tooltip);
							return glyph;
						} else {
							Circle circle = new Circle();
							circle.getStyleClass().add("location");
							circle.setRadius(4);
							circle.setTranslateX(-4);
							circle.setTranslateY(-4);
							circle.setOnMouseEntered(evt -> tooltip.setText(location.getName()));
							Tooltip.install(circle, tooltip);
							return circle;
						}
					}));
				} catch (Exception e) {
					LOGGER.severe(e.getMessage());
				}
			}

		}).start();
	}

	@FXML
	public void send() {
		new Thread(() -> {
			double hppAmount = 0;
			Platform.runLater(() -> {
				sendButton.getStyleClass().removeAll("buttonStyleHpp, focus");
				sendButton.getStyleClass().add("buttonStyleHpp");
				errorAddress.setText("");
				errorAmount.setText("");
				check.setVisible(false);
				txidSend.setText("");
			});

			try {
				hppAmount = Double.parseDouble(amount.getText());
				if (hppAmount > getBalance().getAvailable()) {
					Platform.runLater(() -> errorAmount.setText("Insufficient funds"));
					return;
				}
			} catch (NumberFormatException e) {
				Platform.runLater(() -> errorAmount.setText("invalide amount"));
				return;
			}
			try {
				String txid = new WalletListener(false).sendToAddress(payTo.getText(), hppAmount);
				Platform.runLater(() -> {
					payTo.setText("");
					amount.setText("");
					check.setVisible(true);
					txidSend.setText("Txid:" + txid);
					txidSend.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							try {
								Main.hostServices.showDocument("http://explorer.hppcoin.com/tx/" + txid);
							} catch (Exception ex) {
								LOGGER.severe(ex.getMessage());
							}
						}
					});
					balanceSend.setText("Balance: " + new WalletListener(false).getBalance() + " HPP");
				});

			} catch (Exception e) {
				String msg = e.getMessage();
				if (msg != null && msg.contains("Invalid hppcoin address"))
					Platform.runLater(() -> errorAddress.setText("Invalid hppcoin address"));
				if (msg != null && msg.contains("Insufficient funds"))
					Platform.runLater(() -> errorAmount.setText("Insufficient funds"));
				if (msg != null && msg.contains("Invalid amount for send"))
					Platform.runLater(() -> errorAmount.setText("Invalid amount"));

			}
		}).start();

	}

	@FXML
	public void updateReceiveUI() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				generateCrcode(new SettingsDaoImpl().load().getDefaultReceivingAddress());
			}
		});

	}

	@FXML
	public void updateOverviewUI() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				clearUI();
				// Init Balance

				Balance balance = getBalance();
				Platform.runLater(() -> {
					available.setText(balance.getAvailable() + " HPP");
					pending.setText(balance.getPending() + " HPP");
					total.setText((balance.getAvailable() + balance.getPending()) + " HPP");

					// init recent txs
					Label[] amounts = { am1, am2, am3, am4, am5, am6 };
					Label[] dates = { d1, d2, d3, d4, d5, d6 };
					Label[] addresses = { ad1, ad2, ad3, ad4, ad5, ad6 };
					FontAwesomeIcon[] fonts = { f1, f2, f3, f4, f5, f6 };
					SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyy HH:mm");
					List<HPPTransaction> txs = getRecenet();

					int i = 0;
					if (txs != null && txs.size() > 0)
						for (HPPTransaction tx : txs) {

							dates[i].setText(formatter.format(tx.getTime()));
							amounts[i].setText(String.valueOf(tx.getAmount()) + " HPP");
							addresses[i].setText(tx.getAddress());
							if (tx.getType().toString().equals(TransactionType.RECEIVE.toString())) {
								fonts[i].setFill(Color.rgb(21, 171, 156));
								fonts[i].setGlyphName("ARROW_CIRCLE_DOWN");
							}
							if (tx.getType().toString().equals(TransactionType.SEND.toString())) {
								fonts[i].setFill(Color.rgb(225, 93, 93));
								fonts[i].setGlyphName("ARROW_CIRCLE_UP");
							}
							if (tx.getType().toString().equals(TransactionType.GENERATE.toString())) {
								fonts[i].setFill(Color.rgb(50, 184, 224));
								fonts[i].setGlyphName("ANCHOR");
							}
							fonts[i].setVisible(true);

							if (i == 5)
								break;
							i++;
						}
				});
			}
		}).start();
	}

	private void clearUI() {
		Platform.runLater(()->{
		available.setText("");
		pending.setText("");
		total.setText("");
		check.setVisible(false);
		txidSend.setText("");
		Label[] amounts = { am1, am2, am3, am4, am5, am6 };
		Label[] dates = { d1, d2, d3, d4, d5, d6 };
		Label[] addresses = { ad1, ad2, ad3, ad4, ad5, ad6 };
		FontAwesomeIcon[] fonts = { f1, f2, f3, f4, f5, f6 };
		
		for (int i = 0; i < 6; i++) {
			dates[i].setText("");
			amounts[i].setText("");
			addresses[i].setText("");
			fonts[i].setVisible(false);
		}});
		
	}

	private Balance getBalance() {
		Balance balance = new TransactionDaoImpl().loadBalance();
		return balance;
	}

	private List<HPPTransaction> getRecenet() {
		List<HPPTransaction> transactions = new TransactionDaoImpl().selectAll();
		if (transactions != null && transactions.size() > 1)
			Collections.sort(transactions, (o1, o2) -> {

				return new Long(o2.getTime()).compareTo(new Long(o1.getTime()));
			});

		return transactions;
	}

	public void generateNew() {
		try {
			SettingsDao settingsDao = new SettingsDaoImpl();
			String address = new WalletListener(false).getNewAddress();
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					generateCrcode(address);

				}
			});

			Settings settings = settingsDao.load();
			settings.setDefaultReceivingAddress(address);
			settingsDao.merge(settings);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
	}

	private void generateCrcode(String address) {
		newAddress.setText(address);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		int width = 360;
		int height = 360;
		BufferedImage bufferedImage = null;
		try {
			BitMatrix byteMatrix = qrCodeWriter.encode(address, BarcodeFormat.QR_CODE, width, height);
			bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			bufferedImage.createGraphics();

			Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
			graphics.setColor(java.awt.Color.WHITE);
			graphics.fillRect(0, 0, width, height);
			graphics.setColor(java.awt.Color.BLACK);

			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					if (byteMatrix.get(i, j)) {
						graphics.fillRect(i, j, 1, 1);
					}
				}
			}
		} catch (WriterException ex) {
			LOGGER.severe(ex.getMessage());
		}

		qrcode.setImage(SwingFXUtils.toFXImage(bufferedImage, null));

	}

	private Map<String, Integer> count(List<LMNode> nodes) {
		Map<String, Integer> country_number = new HashMap<String, Integer>();

		if (nodes != null && nodes.size() > 0)
			for (LMNode node : nodes) {
				if (country_number.keySet() == null || (!country_number.keySet().contains(node.getCountryIso())))
					country_number.put(node.getCountryIso(), new Integer(0));
				int num = country_number.get(node.getCountryIso());
				country_number.put(node.getCountryIso(), new Integer(++num));
			}
		return country_number;
	}

	private List<List<String>> sortCountriesByLMN(List<LMNode> nodes) {
		List<List<String>> sub = new ArrayList<List<String>>(8);
		Map<String, Integer> country_number = count(nodes);
		Set<String> countries = country_number.keySet();
		if (countries == null || countries.size() < 1)
			return sub;
		Collection<Integer> values = country_number.values();
		Set<Integer> svalues = new HashSet<>();
		svalues.addAll(values);
		List<Integer> sortedValues = new ArrayList<Integer>();
		sortedValues.addAll(svalues);
		Comparator<Integer> comp = (Integer n1, Integer n2) -> n2.compareTo(n1);
		Collections.sort(sortedValues, comp);

		int max = 8;
		if (sortedValues.size() < 8)
			max = sortedValues.size();
		for (int i = 0; i < 8; i++)
			sub.add(i, new ArrayList<>());
		for (int i = 0; i < max; i++) {
			for (String country : countries)
				if (country_number.get(country) == sortedValues.get(i))
					sub.get(i).add(country);
		}
		return sub;
	}

	private int getRank(String countryIso, List<LMNode> nodes) {
		int rank = 8;
		List<List<String>> sub = sortCountriesByLMN(nodes);
		for (int i = 0; i < 7; i++)
			if (sub.get(i).contains(countryIso))
				rank = i + 1;
		return rank;
	}

}
