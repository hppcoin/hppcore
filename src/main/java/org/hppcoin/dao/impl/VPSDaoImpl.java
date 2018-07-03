/*
 * HPPCOIN License
 * 
 * Copyright (c) 2017-2018, HPPCOIN Developers.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the 
 * Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.hppcoin.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hppcoin.dao.VPSDao;
import org.hppcoin.model.Contract;
import org.hppcoin.model.HPPTransaction;
import org.hppcoin.model.Settings;
import org.hppcoin.model.VPS;
import org.hppcoin.model.VPSRentalStatus;

public class VPSDaoImpl implements VPSDao {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public VPS find(String vpsID) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		VPS vps = null;
		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			vps = em.find(VPS.class, vpsID);
			em.getTransaction().commit();
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}

		return vps;
	}
	
	public void update(VPS vps, Contract contract) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			em.merge(vps);
			List<Contract> contracts = vps.getContracts();
			if (contracts == null)
				contracts = new ArrayList<>();
			if (!contracts.contains(contract))
				contracts.add(contract);
			em.merge(contract);
			contract.setVps(vps);
			em.getTransaction().commit();
			em.close();
			emf.close();
			} 
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
	}

	public VPS save(VPS vps) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();

		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			em.persist(vps);
			em.getTransaction().commit();
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}

		return vps;
	}

	public List<VPS> selectAll() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		List<VPS> allVps = null;
		try {
			synchronized (Settings.monitor) {
			Query query = em.createNamedQuery("VPS.selectAll");
			em.getTransaction().begin();
			allVps = query.getResultList();
			em.getTransaction().commit();
			em.close();
			emf.close();
		}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
		return allVps;
	}

	public void updateVPS(List<VPS> vpsList) {
		if (vpsList == null || vpsList.size() == 0)
			return;
		

		if (vpsList.size() > 0)
			for (VPS vps : vpsList)
				try {
					synchronized (Settings.monitor) {
					EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
					EntityManager em = emf.createEntityManager();
					em.getTransaction().begin();
					em.merge(vps);
					em.flush();
					em.getTransaction().commit();
					em.close();
					emf.close();	}
				} catch (Exception e) {
					LOGGER.severe(e.getMessage());
				}

	}

	public List<VPS> selectMine() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		List<VPS> mine = null;
		try {
			synchronized (Settings.monitor) {
			Query query = em.createNamedQuery("VPS.selectMyVps");
			em.getTransaction().begin();
			mine = query.getResultList();
			em.getTransaction().commit();
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		}
		return mine;
	}

	public List<VPS> selectAllOthers() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		List<VPS> allVps = null;
		try {
			synchronized (Settings.monitor) {
			Query query = em.createNamedQuery("VPS.selectAllOthers");
			em.getTransaction().begin();
			allVps = query.getResultList();
			em.getTransaction().commit();
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
		return allVps;
	}

	public List<VPS> selectAllOthersAvailable() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		List<VPS> allVps = null;
		List<VPS> availableVps = new ArrayList<>();
		try {
			synchronized (Settings.monitor) {
			Query query = em.createNamedQuery("VPS.selectAllOthers");
			em.getTransaction().begin();
			allVps = query.getResultList();
			em.getTransaction().commit();
			if (allVps != null && allVps.size() > 0)
				for (VPS vps : allVps)
					if (vps.getRentalSattus().equals(VPSRentalStatus.AVAILABLE))
						availableVps.add(vps);
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
		return availableVps;
	}

	@Override
	public int update(VPS vps) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		try {
			synchronized (Settings.monitor) {
			em.getTransaction().begin();
			em.merge(vps);
			em.getTransaction().commit();
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
			return 0;
		}

		return 1;
		
	}

	public int purgeOtherVPS() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		List<VPS> allVps = null;
		int purged=0;
		try {
			synchronized (Settings.monitor) {
			Query query = em.createNamedQuery("VPS.selectAllOthers");
			em.getTransaction().begin();
			allVps = query.getResultList();
			
			if (allVps != null && allVps.size() > 0)
				for (VPS vps : allVps)
			if(vps.getRentalSattus().equals(VPSRentalStatus.AVAILABLE))	 {
				em.remove(vps);
				purged++;
			}
			em.getTransaction().commit();
			em.close();
			emf.close();	}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
		return purged;
	}

	@Override
	public boolean hasAvailable() {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		List<VPS> allVps = null;
		List<VPS> availableVps = null;
		try {
			synchronized (Settings.monitor) {
			Query query = em.createNamedQuery("VPS.selectMyVps");
			em.getTransaction().begin();
			allVps = query.getResultList();
			em.getTransaction().commit();
			if (allVps != null && allVps.size() > 0)
				for (VPS vps : allVps)
					if (vps.getRentalSattus().equals(VPSRentalStatus.AVAILABLE))
						return true;
			em.close();
			emf.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe(e.getMessage());
		}
		return false;
	}

}
