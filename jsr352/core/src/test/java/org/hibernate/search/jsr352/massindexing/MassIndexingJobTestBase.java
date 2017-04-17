/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.jsr352.massindexing;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.search.jsr352.logging.impl.Log;
import org.hibernate.search.util.logging.impl.LoggerFactory;

/**
 * @author Mincong Huang
 */
public abstract class MassIndexingJobTestBase {

	protected static final Log log = LoggerFactory.make( Log.class );

	protected static final String PERSISTENCE_UNIT_NAME = "h2";

	protected EntityManagerFactory emf;

	protected void persist(Object[] entities) {
		EntityManager em = null;
		try {
			emf = Persistence.createEntityManagerFactory( PERSISTENCE_UNIT_NAME );
			em = emf.createEntityManager();
			em.getTransaction().begin();
			for ( Object entity : entities ) {
				em.persist( entity );
			}
			em.getTransaction().commit();
		}
		finally {
			if ( em != null ) {
				em.close();
			}
		}
	}

}
