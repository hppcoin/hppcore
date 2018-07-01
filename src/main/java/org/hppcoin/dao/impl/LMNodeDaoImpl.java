package org.hppcoin.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hppcoin.crons.WalletListener;
import org.hppcoin.dao.LMNodeDao;
import org.hppcoin.model.LMNode;
import org.hppcoin.model.Settings;

public class LMNodeDaoImpl implements LMNodeDao {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	public LMNode getWinner() {
		LMNode node = new LMNode();
		try {
			WalletListener wallet = new WalletListener(false);

			Map<String, Object> current = wallet.current();
			node.setAddress((String) current.get("payee"));
			node.setBlockNumber((Long) current.get("height"));
			node.setIp(((String) current.get("IP:port")).split(":")[0]);

		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		if (node.getAddress() == null)
			node.setAddress(" ");
		if (node.getAddress() == null)
			node.setAddress(" ");
		if (node.getIp() == null)
			node.setIp(" ");
		return node;
	}

	@Override
	public List<LMNode> getAll() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		List<LMNode> nodes = null;
		try {
			synchronized (Settings.monitor) {
			Query query = em.createNamedQuery("LMNode.selectAll");
			em.getTransaction().begin();
			nodes = query.getResultList();
			em.getTransaction().commit();
			em.close();
			emf.close();
			} 
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return nodes;
	}

	@Override
	public LMNode save(LMNode node) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		List<LMNode> nodes = null;
		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			em.persist(node);
			em.getTransaction().commit();
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return node;
	}

	public void update(List<LMNode> nodes) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		try {
		
			Query query = em.createNamedQuery("LMNode.deleteAll");
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			query.executeUpdate();
			em.getTransaction().commit();
			em.close();
			emf.close();
			} 
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		for (LMNode node : nodes) {
			try {
				save(node);
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
			}
			try {
				TimeUnit.MILLISECONDS.sleep(1);
			} catch (InterruptedException e) {
				LOGGER.severe(e.getMessage());
			}
		}

	}

	@Override
	public LMNode getWinnerGeo() {
		LMNode node = getWinner();
		WalletListener.getGeo(node);
		return node;
	}

}
