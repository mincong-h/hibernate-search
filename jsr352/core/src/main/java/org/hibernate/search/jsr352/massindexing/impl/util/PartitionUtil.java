/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.jsr352.massindexing.impl.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Comparator for {@link javax.persistence.Embeddable} types.
 *
 * @author Mincong Huang
 */

public final class PartitionUtil {

	public static <T> GenericComparator comparatorOf(List<T> list, Class<T> embeddableType) {
		Field[] fields = embeddableType.getDeclaredFields();
		List<Comparator<T>> comparators = new ArrayList<>( fields.length );

		for ( Field field : fields ) {
			// TODO how to implement field comparator?
		}
		return new GenericComparator<>(embeddableType, comparators);
	}

	private static class GenericComparator<T> implements Comparator<T> {

		private Class<T> classType;

		private List<Comparator<T>> comparators;

		GenericComparator(Class<T> classType, List<Comparator<T>> comparators) {
			this.classType = classType;
			this.comparators = comparators;
		}

		@Override
		public int compare(T o1, T o2) {
			for ( Comparator<T> c : comparators ) {
				int result = c.compare( o1, o2 );
				if (result != 0) {
					return result;
				}
			}
			return 0;
		}

	}

}
