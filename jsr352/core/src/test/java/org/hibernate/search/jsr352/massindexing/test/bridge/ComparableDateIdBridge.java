/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.jsr352.massindexing.test.bridge;

import java.time.LocalDate;
import java.util.Locale;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;

import org.hibernate.search.bridge.LuceneOptions;
import org.hibernate.search.bridge.TwoWayFieldBridge;
import org.hibernate.search.jsr352.massindexing.test.embeddable.ComparableDateId;

/**
 * @author Mincong Huang
 */
public class ComparableDateIdBridge implements TwoWayFieldBridge {

	@Override
	public void set(String name, Object myDateIdObj, Document document, LuceneOptions luceneOptions) {
		ComparableDateId comparableDateId = (ComparableDateId) myDateIdObj;

		// cast int to string
		String year = String.format( Locale.ROOT, "%04d", comparableDateId.getYear() );
		String month = String.format( Locale.ROOT, "%02d", comparableDateId.getMonth() );
		String day = String.format( Locale.ROOT, "%02d", comparableDateId.getDay() );

		// store each property in a unique field
		luceneOptions.addFieldToDocument( name + ".year", year, document );
		luceneOptions.addFieldToDocument( name + ".month", month, document );
		luceneOptions.addFieldToDocument( name + ".day", day, document );

		// store the unique string representation in the named field
		luceneOptions.addFieldToDocument( name, objectToString( comparableDateId ), document );
	}

	@Override
	public Object get(String name, Document document) {
		IndexableField idxField;

		idxField = document.getField( name + ".year" );
		int y = Integer.valueOf( idxField.stringValue() );

		idxField = document.getField( name + ".month" );
		int m = Integer.valueOf( idxField.stringValue() );

		idxField = document.getField( name + ".day" );
		int d = Integer.valueOf( idxField.stringValue() );

		return new ComparableDateId( LocalDate.of( y, m, d ) );
	}

	@Override
	public String objectToString(Object myDateIdObj) {
		return String.valueOf( myDateIdObj );
	}

}
