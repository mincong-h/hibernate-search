/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.jsr352.massindexing.impl.util;


import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for {@link javax.persistence.Embeddable} types.
 *
 * @author Mincong Huang
 */
// TODO can it extend annotation Embeddable directly?
public class EmbeddableComparator<T extends Serializable> implements Comparator<T> {

	@Override
	public int compare(T o1, T o2) {
		return 0;
	}

}
