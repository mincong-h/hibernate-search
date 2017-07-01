/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.jsr352.massindexing.test.entity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.jsr352.massindexing.test.bridge.ComparableDateIdBridge;
import org.hibernate.search.jsr352.massindexing.test.embeddable.ComparableDateId;

/**
 * @author Mincong Huang
 */
@Entity
@Indexed
public class EntityWithComparableId {

	@EmbeddedId
	@DocumentId
	@FieldBridge(impl = ComparableDateIdBridge.class)
	private ComparableDateId comparableDateId;

	@Field
	private String value;

	public EntityWithComparableId() {
	}

	public EntityWithComparableId(LocalDate d) {
		this.comparableDateId = new ComparableDateId( d );
		this.value = DateTimeFormatter.ofPattern( "yyyyMMdd", Locale.ROOT ).format( d );
	}

	public ComparableDateId getComparableDateId() {
		return comparableDateId;
	}

	public void setComparableDateId(ComparableDateId comparableDateId) {
		this.comparableDateId = comparableDateId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "EntityWithComparableId [comparableDateId=" + comparableDateId + ", value=" + value + "]";
	}

}
