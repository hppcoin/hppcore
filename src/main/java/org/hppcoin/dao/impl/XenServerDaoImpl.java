package org.hppcoin.dao.impl;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hppcoin.dao.XenServerDao;
import org.hppcoin.model.Settings;
import org.hppcoin.model.XenServer;

public class XenServerDaoImpl implements XenServerDao {

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public XenServer find(String IP) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		XenServer xenServer = null;
		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			xenServer = em.find(XenServer.class, IP);
			em.getTransaction().commit();
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}

		return xenServer;
	}

	public int save(XenServer xenServer) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();

		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			em.persist(xenServer);
			em.getTransaction().commit();
			em.close();
			emf.close();
			} 
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			return 0;
		}

		return 1;
	}

	public List<XenServer> selectAll() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		List<XenServer> allXenServer = null;
		try {
			Query query = em.createNamedQuery("XenServer.selectAll");
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			allXenServer = query.getResultList();
			em.getTransaction().commit();
			em.close();
			emf.close();
			} 
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return allXenServer;
	}

	@Override
	public void updateDefault(String ip) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		List<XenServer> allXenServer = null;
		try {
			Query query = em.createNamedQuery("XenServer.selectAll");
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			allXenServer = query.getResultList();
			if (allXenServer != null && allXenServer.size() > 0)
				for (XenServer s : allXenServer)
					if ((!s.getIp().equals(ip) && s.isDefaultServer()))
						s.setDefaultServer(false);
			em.getTransaction().commit();
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}

	}

	@Override
	public int update(XenServer server) {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();

		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			em.merge(server);
			em.getTransaction().commit();
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			return 0;
		}

		return 1;
	}

}
