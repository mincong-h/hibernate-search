/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.jsr352.massindexing.impl.steps.beforechunk;

import javax.batch.api.AbstractBatchlet;
import javax.batch.api.BatchProperty;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.jsr352.logging.impl.Log;
import org.hibernate.search.jsr352.massindexing.MassIndexingJobParameters;
import org.hibernate.search.jsr352.massindexing.impl.JobContextData;
import org.hibernate.search.jsr352.massindexing.impl.util.SerializationUtil;
import org.hibernate.search.util.logging.impl.LoggerFactory;

import static org.hibernate.search.jsr352.massindexing.MassIndexingJobParameters.OPTIMIZE_AFTER_PURGE;
import static org.hibernate.search.jsr352.massindexing.MassIndexingJobParameters.PURGE_ALL_ON_START;

/**
 * Enhancements before the chunk step {@code produceLuceneDoc} (lucene document production)
 *
 * @author Mincong Huang
 */
public class BeforeChunkBatchlet extends AbstractBatchlet {

	private static final Log log = LoggerFactory.make( Log.class );

	@Inject
	private JobContext jobContext;

	@Inject
	@BatchProperty(name = MassIndexingJobParameters.PURGE_ALL_ON_START)
	private String serializedPurgeAllOnStart;

	@Inject
	@BatchProperty(name = MassIndexingJobParameters.OPTIMIZE_AFTER_PURGE)
	private String serializedOptimizeAfterPurge;

	@Inject
	@BatchProperty(name = MassIndexingJobParameters.TENANT_ID)
	private String tenantId;

	private FullTextEntityManager ftem;

	@Override
	public String process() throws Exception {
		boolean purgeAllOnStart = SerializationUtil.parseBooleanParameter( PURGE_ALL_ON_START, serializedPurgeAllOnStart );
		boolean optimizeAfterPurge = SerializationUtil.parseBooleanParameter( OPTIMIZE_AFTER_PURGE, serializedOptimizeAfterPurge );

		if ( purgeAllOnStart ) {
			JobContextData jobData = (JobContextData) jobContext.getTransientUserData();
			EntityManagerFactory emf = jobData.getEntityManagerFactory();
			// FIXME Multi-tenancy is not handled
			ftem = Search.getFullTextEntityManager( emf.createEntityManager() );
			jobData.getEntityTypes().forEach( ftem::purgeAll );

			if ( optimizeAfterPurge ) {
				log.startOptimization();
				ftem.getSearchFactory().optimize();
			}
		}
		return null;
	}

	@Override
	public void stop() throws Exception {
		ftem.close();
	}
}
