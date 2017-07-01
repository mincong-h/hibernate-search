/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.jsr352.massindexing.test.embeddable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Locale;

import javax.persistence.Embeddable;

/**
 * Primary key for {@link org.hibernate.search.jsr352.massindexing.test.entity.EntityWithComparableId}.
 *
 * @author Mincong Huang
 */
@Embeddable
public class ComparableDateId implements Serializable, Comparable<ComparableDateId> {

	private static final long serialVersionUID = -3941766084997859100L;

	private LocalDate d;

	public ComparableDateId() {

	}

	public ComparableDateId(LocalDate d) {
		this.d = d;
	}

	public int getYear() {
		return d.getYear();
	}

	public int getMonth() {
		return d.getMonthValue();
	}

	public int getDay() {
		return d.getDayOfMonth();
	}

	@Override
	public int hashCode() {
		return d.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if ( this == obj ) {
			return true;
		}
		if ( obj == null ) {
			return false;
		}
		if ( getClass() != obj.getClass() ) {
			return false;
		}
		ComparableDateId that = (ComparableDateId) obj;
		return this.d.equals( that.d );
	}

	@Override
	public String toString() {
		return String.format( Locale.ROOT, "%04d-%02d-%02d", getYear(), getMonth(), getDay() );
	}

	@Override
	public int compareTo(ComparableDateId that) {
		return this.d.compareTo( that.d );
	}

}
