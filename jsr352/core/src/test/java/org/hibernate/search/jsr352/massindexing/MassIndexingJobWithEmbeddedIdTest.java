/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.jsr352.massindexing;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.criterion.Restrictions;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.jsr352.massindexing.test.embeddable.ComparableDateId;
import org.hibernate.search.jsr352.massindexing.test.entity.EntityWithComparableId;
import org.hibernate.search.jsr352.massindexing.test.entity.EntityWithNonComparableId;
import org.hibernate.search.jsr352.massindexing.test.embeddable.NonComparableDateId;
import org.hibernate.search.jsr352.test.util.JobTestUtil;
import org.hibernate.search.testsupport.TestForIssue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.hibernate.search.jsr352.test.util.JobTestUtil.findIndexedResults;

/**
 * Tests that mass indexing job can run with entity having
 * {@link javax.persistence.EmbeddedId} annotation.
 *
 * @author Mincong Huang
 */
@TestForIssue(jiraKey = "HSEARCH-2615")
public class MassIndexingJobWithEmbeddedIdTest {

	private static final LocalDate START = LocalDate.of( 2017, 6, 1 );

	private static final LocalDate END = LocalDate.of( 2017, 8, 1 );

	private static final JobOperator jobOperator = BatchRuntime.getJobOperator();

	private static final int JOB_TIMEOUT_MS = 5_000;

	private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory( "h2" );

	private FullTextEntityManager ftem;

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		emf.close();
	}

	@Before
	public void setUp() throws Exception {
		ftem = Search.getFullTextEntityManager( emf.createEntityManager() );
		ftem.getTransaction().begin();
		for ( LocalDate d = START; d.isBefore( END ); d = d.plusDays( 1 ) ) {
			ftem.persist( new EntityWithComparableId( d ) );
			ftem.persist( new EntityWithNonComparableId( d ) );
		}
		ftem.getTransaction().commit();

		assertThat( JobTestUtil.nbDocumentsInIndex( emf, EntityWithComparableId.class ) ).isEqualTo( 0 );
		assertThat( JobTestUtil.nbDocumentsInIndex( emf, EntityWithNonComparableId.class ) ).isEqualTo( 0 );
	}

	@After
	public void tearDown() throws Exception {
		ftem.getTransaction().begin();

		ftem.createQuery( "delete from EntityWithComparableId" ).executeUpdate();
		ftem.createQuery( "delete from EntityWithNonComparableId" ).executeUpdate();

		ftem.purgeAll( EntityWithComparableId.class );
		ftem.purgeAll( EntityWithNonComparableId.class );
		ftem.flushToIndexes();

		ftem.getTransaction().commit();
		ftem.close();
	}

	@Test
	public void canHandleEmbeddedId_whenComparable() throws Exception {
		Properties props = MassIndexingJob.parameters()
				.forEntities( EntityWithComparableId.class )
				.restrictedBy( Restrictions.ge(
						"comparableDateId",
						new ComparableDateId( LocalDate.of( 2017, 6, 20 ) )
				) )
				.build();

		startJobAndWait( MassIndexingJob.NAME, props );

		assertThat( findIndexedResults( emf, EntityWithComparableId.class, "value", "20170701" ) ).hasSize( 1 );
		int expectedDays = (int) ChronoUnit.DAYS.between( LocalDate.of( 2017, 6, 20 ), END );
		assertThat( JobTestUtil.nbDocumentsInIndex( emf, EntityWithComparableId.class ) ).isEqualTo( expectedDays );
	}

	@Test
	@Ignore
	public void cannotHandleEmbeddedId_whenNonComparable() throws Exception {
		Properties props = MassIndexingJob.parameters()
				.forEntities( EntityWithNonComparableId.class )
				.restrictedBy( Restrictions.ge(
						"nonComparableDateId",
						new NonComparableDateId( LocalDate.of( 2017, 6, 20 ) )
				) )
				.build();

		startJobAndWait( MassIndexingJob.NAME, props );

		assertThat( findIndexedResults( emf, EntityWithNonComparableId.class, "value", "20170701" ) ).hasSize( 0 );
		int err = (int) ChronoUnit.DAYS.between( LocalDate.of( 2017, 7, 1 ), LocalDate.of( 2017, 7, 20 ) );
		int expectedDays = (int) ChronoUnit.DAYS.between( LocalDate.of( 2017, 6, 20 ), END ) - err;
		assertThat( JobTestUtil.nbDocumentsInIndex( emf, EntityWithNonComparableId.class ) ).isEqualTo( expectedDays );
	}

	private void startJobAndWait(String jobName, Properties jobParams) throws InterruptedException {
		long execId = jobOperator.start( jobName, jobParams );
		JobExecution jobExec = jobOperator.getJobExecution( execId );
		jobExec = JobTestUtil.waitForTermination( jobOperator, jobExec, JOB_TIMEOUT_MS );
		assertThat( jobExec.getBatchStatus() ).isEqualTo( BatchStatus.COMPLETED );
	}

}
