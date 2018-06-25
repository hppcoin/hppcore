package org.hppcoin.dao.impl;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hppcoin.model.Navigation;
import org.hppcoin.model.Settings;

public class NavigationDaoImpl {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private Navigation navigation = null;

	public NavigationDaoImpl() {
		super();
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();

		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			navigation = em.find(Navigation.class, 1L);
			if (navigation == null) {
				navigation = new Navigation();
				em.persist(navigation);
			}
			em.getTransaction().commit();
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());

		}
	}

	public String getCurrentXenServerIP() {
		return navigation.getCurrentXenServerIP();
	}

	public String getCurrentVPSUID() {
		return navigation.getCurrentVPSUID();
	}

	public String getCurrentContractID() {
		return navigation.getCurrentContractID();
	}

	public String getCurrentTransactionID() {

		return navigation.getCurrentContractID();
	}

	public int update(Navigation navigation2) {
		navigation = navigation2;
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();

		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			em.merge(navigation2);
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

	public Navigation find() {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		Navigation navigation = null;
		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			navigation = em.find(Navigation.class, 1L);
			if (navigation == null) {
				navigation = new Navigation();
				em.persist(navigation);
			}
			em.getTransaction().commit();
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			;
		}

		return navigation;
	}

}
