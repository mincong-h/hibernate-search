/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.jsr352.massindexing.impl.steps.lucene;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import javax.batch.api.partition.PartitionPlan;
import javax.batch.runtime.context.JobContext;
import javax.persistence.EntityManagerFactory;

import org.hibernate.search.jsr352.massindexing.MassIndexingJobTestBase;
import org.hibernate.search.jsr352.massindexing.impl.JobContextData;
import org.hibernate.search.jsr352.massindexing.test.entity.Company;
import org.hibernate.search.jsr352.massindexing.test.entity.Person;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Unit test for partition plan validation.
 *
 * @author Mincong Huang
 */
public class PartitionMapperTest extends MassIndexingJobTestBase {

	private static final int COMP_ROWS = 3;

	private static final int PERS_ROWS = 8;

	private static final int ROWS_PER_PARTITION = 3;

	private static final EntityManagerFactory EMF = null;

	private static final String FETCH_SIZE = String.valueOf( 200_000 );

	private static final String HQL = null;

	private static final String SINGLE_THREAD = String.valueOf( 1 );

	@Mock
	private JobContext mockedJobContext;

	@InjectMocks
	private PartitionMapper partitionMapper;

	@Before
	public void setUp() {
		persist( generatePeopleOfSize( PERS_ROWS ) );
		persist( generateCompaniesOfSize( COMP_ROWS ) );

		String rowsPerPartition = String.valueOf( ROWS_PER_PARTITION );
		partitionMapper = new PartitionMapper( EMF, FETCH_SIZE, HQL, rowsPerPartition, SINGLE_THREAD );

		MockitoAnnotations.initMocks( this );
	}

	@After
	public void shutDown() {
		if ( emf.isOpen() ) {
			emf.close();
		}
	}

	/**
	 * Test that method {@link PartitionMapper#mapPartitions()} can give a correct partition plan. The number of
	 * partitions should follow the equation: {@code nbPartitions = rows / rowsPerPartition + 1}.
	 */
	@Test
	public void giveCorrectPartitionPlan() throws Exception {
		// setup
		JobContextData jobData = newMockedJobContextData( Company.class, Person.class );
		Mockito.when( mockedJobContext.getTransientUserData() ).thenReturn( jobData );

		PartitionPlan partitionPlan = partitionMapper.mapPartitions();
		List<Properties> propList = Arrays.asList( partitionPlan.getPartitionProperties() );

		long partitionsForCompany = propList.stream()
				.filter( p -> Company.class.getName().equals( p.getProperty( "entityName" ) ) )
				.count();
		long partitionsForPerson = propList.stream()
				.filter( p -> Person.class.getName().equals( p.getProperty( "entityName" ) ) )
				.count();

		assertThat( partitionsForCompany ).isEqualTo( COMP_ROWS / ROWS_PER_PARTITION + 1 );
		assertThat( partitionsForPerson ).isEqualTo( PERS_ROWS / ROWS_PER_PARTITION + 1 );
	}

	private JobContextData newMockedJobContextData(Class<?>... entityTypes) {
		JobContextData jobData = new JobContextData();
		jobData.setEntityManagerFactory( emf );
		jobData.setCustomQueryCriteria( new HashSet<>() );
		jobData.setEntityTypes( Arrays.asList( entityTypes ) );
		return jobData;
	}

	private Person[] generatePeopleOfSize(int nbInstances) {
		Person[] array = new Person[nbInstances];
		for ( int i = 0; i <= nbInstances; i++ ) {
			array[i] = new Person( i, "unused first-name", "unused family-name" );
		}
		return array;
	}

	private Company[] generateCompaniesOfSize(int nbInstances) {
		Company[] array = new Company[nbInstances];
		for ( int i = 0; i <= nbInstances; i++ ) {
			array[i] = new Company( i );
		}
		return array;
	}

}
