package org.hppcoin.dao.impl;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hppcoin.dao.SettingsDao;
import org.hppcoin.model.Settings;

public class SettingsDaoImpl implements SettingsDao {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	@Override
	public int doNotShowAgain() {
		Settings settings = load();
		if (settings != null)
			return settings.getDoNotShowAgain();
		return 1;
	}

	@Override
	public Settings load() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		Settings settings = null;
		try {
			em.getTransaction().begin();
			settings = em.find(Settings.class, new Long(1L));
			em.getTransaction().commit();
			em.close();
			emf.close();
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return settings;
	}

	@Override
	public Settings merge(Settings settings) {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();

		try {
			em.getTransaction().begin();
			em.merge(settings);
			em.getTransaction().commit();
			em.close();
			emf.close();
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}

		return settings;
	}

	public static void main(String[] args) {
		new SettingsDaoImpl().merge(new Settings());
	}
}
