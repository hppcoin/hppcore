package org.hppcoin.crons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.hppcoin.model.LMNode;
import org.hppcoin.model.TransactionType;
import org.hppcoin.util.HttpRetreiver;
import org.hppcoin.util.IPUtil;

import wf.bitcoin.javabitcoindrpcclient.BitcoinJSONRPCClient;

public class WalletListener extends BitcoinJSONRPCClient {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public WalletListener(boolean testNet) {
		super(testNet);

	}

public	List<org.hppcoin.model.HPPTransaction> listRecentTransactions() {
		List<org.hppcoin.model.HPPTransaction> txs = new ArrayList<>();

		List<Transaction> recentTxs = super.listTransactions();
		if (recentTxs != null && recentTxs.size() > 0)
			for (Transaction recentTx : recentTxs) {
				org.hppcoin.model.HPPTransaction tx = new org.hppcoin.model.HPPTransaction();
				tx.setTxid(recentTx.txId());
				tx.setAddress(recentTx.address());
				tx.setAmount(recentTx.amount());
				TransactionType type = TransactionType.SEND;
				if (recentTx.category().equals("receive"))
					type = TransactionType.RECEIVE;
				if (recentTx.category().equals("send"))
					type = TransactionType.SEND;
				if (recentTx.category().equals("generate"))
					type = TransactionType.GENERATE;
				tx.setConfirmations(recentTx.confirmations());
				tx.setType(type);
				tx.setTime(recentTx.time().getTime());
				txs.add(tx);
			}
		return txs;
	}

	public List<LMNode> LMNodes() {
		List<LMNode> nodes = new ArrayList<>();
		Map<String, Object> map = listLMNodes();
		if (map != null && map.keySet() != null)
			for (String key : map.keySet()) {
				String parts[] = ((String) map.get(key)).split(" ");
				LMNode node = new LMNode(parts[0], parts[1], parts[2]);
				for (String p : parts)
					if (p != null && p.contains(":"))
						node.setIp(p.split(":")[0]);
				nodes.add(node);

			}
		getGeo(nodes);
		return nodes;
	}

	private void getGeo(List<LMNode> nodes) {
		if (nodes == null || nodes.size() < 1)
			return;

		int cores = Runtime.getRuntime().availableProcessors();
		if (cores < 32)
			cores = 32;
		final int iterationsPerCore = nodes.size() / cores;
		LOGGER.info("Updating GEO data using " + cores + " cores " + iterationsPerCore + " iterations per core");
		final int CORES = cores;
		Thread t[] = new Thread[CORES];
		for (int i = 0; i < cores - 1; i++) {
			final int it = i;
			t[i] = new Thread(new Runnable() {

				@Override
				public void run() {
					for (int j = 0; j < iterationsPerCore; j++) {
						LMNode node = nodes.get(it * iterationsPerCore + j);

						getGeo(node);
					}
				}
			});

		}
		// final iteration with more work to do
		t[CORES - 1] = new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = (CORES - 1) * iterationsPerCore; i < nodes.size(); i++) {
					LMNode node = nodes.get(i);
					getGeo(node);
				}
			}

		});
		for (int i = 0; i < CORES; i++)
			t[i].start();
		for (int i = 0; i < CORES; i++)
			try {
				t[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				LOGGER.severe(e.getMessage());
			}

	}

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		List<LMNode> nodes = new WalletListener(false).LMNodes();
		for (LMNode node : nodes)
			if (node.getCountry().equals("Uknown"))
				System.out.println(node);
		System.out.println("time in sec =" + (System.currentTimeMillis() - start) / 1000);

	}

	public static void getGeo(LMNode node) {
		HttpRetreiver client = new HttpRetreiver();
		if (node.getIp() != null && (node.getIp().length() > 5)) {
			try {
				String ipData = client.get("http://denver.hppcoin.org:17777/json/" + node.getIp());
				node = IPUtil.setNode(ipData, node);
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
			}
			if (node.getCountryIso() == null || node.getCountryIso().length() < 2)
				try {
					String ipData = client.get("http://nice.hppcoin.org:17777/json/" + node.getIp());
					node = IPUtil.setNode(ipData, node);
				} catch (Exception e) {
					LOGGER.severe(e.getMessage());
				}
			if (node.getCountryIso() == null || node.getCountryIso().length() < 2)
				try {
					String ipData = client.get("http://italy.hppcoin.org:17777/json/" + node.getIp());
					node = IPUtil.setNode(ipData, node);
				} catch (Exception e) {
					LOGGER.severe(e.getMessage());
				}
		}
		if (node.getCountryIso() == null || node.getCountryIso().length() < 2) {
			node.setCountry("Uknown");
			node.setCountryIso("Uknown");
		}

	}
}
