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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hppcoin.dao.UserDao;
import org.hppcoin.model.User;
import org.hppcoin.util.Sha256Digest;

public class UserDaoImpl implements UserDao {

	@Override
	public User save(User user) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			em.persist(user);
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return user;
	}

	@Override
	public User selectBy(String password) {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		List<User> users = null;
		try {
			Query query = em.createNamedQuery("User.selectByPass");
			query.setParameter("pass1", Sha256Digest.sha256(password));
			em.getTransaction().begin();
			users = query.getResultList();
			em.getTransaction().commit();
			System.out.println("users.size = " + users.size());
			if (users != null && users.size() > 0)
				for (User u : users)
					if (u.getPassword().equals(Sha256Digest.sha256(password)))
						return u;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<User> selectAll() {

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hppcoin");
		EntityManager em = emf.createEntityManager();
		List<User> users = null;
		try {
			Query query = em.createNamedQuery("User.selectAll");
			em.getTransaction().begin();
			users = query.getResultList();
			em.getTransaction().commit();
			System.out.println("users.size = " + users.size());
			return users;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
