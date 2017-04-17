/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.jsr352.massindexing.impl.steps.lucene;

import java.util.Collections;
import java.util.HashSet;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;

import org.hibernate.search.jsr352.massindexing.MassIndexingJobTestBase;
import org.hibernate.search.jsr352.massindexing.impl.JobContextData;
import org.hibernate.search.jsr352.massindexing.impl.util.PartitionBound;
import org.hibernate.search.jsr352.massindexing.test.entity.Company;

import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Unit test for item reader validation.
 *
 * @author Mincong Huang
 */
public class EntityReaderTest extends MassIndexingJobTestBase {

	private static final Company[] COMPANIES = new Company[] {
			new Company( "Red Hat" ),
			new Company( "Google" ),
			new Company( "Microsoft" )
	};

	@Mock
	private JobContext mockedJobContext;

	@Mock
	private StepContext mockedStepContext;

	@InjectMocks
	private EntityReader entityReader;

	@Before
	public void setUp() {
		persist( COMPANIES );

		String cacheable = String.valueOf( false );
		String entityName = Company.class.getName();
		String fetchSize = String.valueOf( 1000 );
		String maxResults = String.valueOf( Integer.MAX_VALUE );
		String partitionId = String.valueOf( 0 );
		entityReader = new EntityReader(
				cacheable,
				entityName,
				fetchSize,
				null,
				maxResults,
				partitionId
		);

		MockitoAnnotations.initMocks( this );
	}

	@Test
	public void canReadItems() throws Exception {
		// setup
		PartitionBound partitionBound = new PartitionBound( Company.class, null, null );
		Mockito.when( mockedJobContext.getTransientUserData() ).thenReturn( newMockedJobContextData( partitionBound ) );
		Mockito.doNothing().when( mockedStepContext ).setTransientUserData( Mockito.any() );

		entityReader.open( null );

		for ( Company expected : COMPANIES ) {
			Company actual = (Company) entityReader.readItem();
			assertThat( actual ).isEqualTo( expected );
		}
		// no more item
		assertThat( entityReader.readItem() ).isNull();
	}

	private JobContextData newMockedJobContextData(PartitionBound partitionBound) {
		JobContextData jobData = new JobContextData();
		jobData.setEntityManagerFactory( emf );
		jobData.setCustomQueryCriteria( new HashSet<>() );
		jobData.setEntityTypes( Company.class );
		jobData.setPartitionBounds( Collections.singletonList( partitionBound ) );
		return jobData;
	}

}
