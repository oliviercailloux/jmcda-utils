package org.decision_deck.utils.relation.graph;

import java.util.Comparator;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Ordering;

/**
 * <p>
 * An edge in a directed graph. Can also be viewed as a directed pair. None of
 * the elements of the pair may be <code>null</code>. Note that this object
 * represents an ordered pair, or a 2-tuple, which is different than an
 * unordered pair or <em>couple</em> in French.
 * </p>
 * <p>
 * Objects of this type are immutable <b>iff</b> the underlying objects are
 * immutable. This object is designed for use with immutable underlying objects,
 * and methods using this class may assume it is immutable.
 * </p>
 *
 * @author Olivier Cailloux
 *
 * @param <V> the vertex type.
 */
public class Edge<V> {

	static public class GetSource<V> implements Function<Edge<? extends V>, V> {
		@Override
		public V apply(Edge<? extends V> input) {
			return input.getSource();
		}

	}

	static public class GetTarget<V> implements Function<Edge<? extends V>, V> {
		@Override
		public V apply(Edge<? extends V> input) {
			return input.getTarget();
		}

	}

	private final V m_elt1;

	private final V m_elt2;

	private static final char RIGHT_ANGLE_BRACKET = '\u27E9';

	private static final char LEFT_ANGLE_BRACKET = '\u27E8';

	static public <V> Edge<V> create(V source, V target) {
		return new Edge<V>(source, target);
	}

	static public <V> Ordering<Edge<? extends V>> getLexicographicOrdering(Comparator<V> source, Comparator<V> target) {
		final GetSource<V> getSource = new GetSource<V>();
		final Ordering<Edge<? extends V>> first = Ordering.from(source).onResultOf(getSource);
		final Ordering<Edge<? extends V>> second = Ordering.from(target).onResultOf(new GetTarget<V>());
		final Ordering<Edge<? extends V>> compound = first.compound(second);
		return compound;
	}

	public Edge(V source, V target) {
		m_elt1 = source;
		m_elt2 = target;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Edge<?> other = (Edge<?>) obj;
		if (m_elt1 == null) {
			if (other.m_elt1 != null) {
				return false;
			}
		} else if (!m_elt1.equals(other.m_elt1)) {
			return false;
		}
		if (m_elt2 == null) {
			if (other.m_elt2 != null) {
				return false;
			}
		} else if (!m_elt2.equals(other.m_elt2)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the first element of the pair. May be <code>null</code>.
	 */
	public V getSource() {
		return m_elt1;
	}

	/**
	 * @return the second element of the pair. May be <code>null</code>.
	 */
	public V getTarget() {
		return m_elt2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_elt1 == null) ? 0 : m_elt1.hashCode());
		result = prime * result + ((m_elt2 == null) ? 0 : m_elt2.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return LEFT_ANGLE_BRACKET + m_elt1.toString() + ", " + m_elt2 + RIGHT_ANGLE_BRACKET;

		/** This would fail on <code>null</code> elements. */
		// return getToStringFunction(Functions.toStringFunction(),
		// Functions.toStringFunction()).apply(this);
	}

	/**
	 * <p>
	 * Retrieves a function which, given an edge, gives its string form in the form
	 * of the transformation of the first element using the given function, a comma
	 * to separate them, and the transformation of the second element, surrounded by
	 * less than and greater than signs (to indicate a tuple). No <code>null</code>
	 * pairs are accepted by the function, but the elements themselves may be
	 * <code>null</code> iff the given transformation functions accept those.
	 * </p>
	 * <p>
	 * This provides an easy way to get short debug strings. E.g. to get a string
	 * representing the contents of a set of pairs of alternatives <em>s</em>, use
	 * <code>Joiner.on(", ").join(Iterables.transform(s, Edge.getToStringFunction(Alternative.getIdFct(), Alternative.getIdFct())))</code>
	 * .
	 * </p>
	 *
	 * @param                <V> the vertex type.
	 * @param sourceToString a function which transforms the first element of a pair
	 *                       to a string.
	 * @param targetToString a function which transforms the second element of a
	 *                       pair to a string.
	 *
	 * @return not <code>null</code>.
	 */
	static public <V> Function<Edge<V>, String> getToStringFunction(final Function<V, String> sourceToString,
			final Function<V, String> targetToString) {
		return new Function<Edge<V>, String>() {
			@Override
			public String apply(Edge<V> input) {
				final Function<Edge<? extends V>, V> fctElt1 = new Edge.GetSource<V>();
				final Function<Edge<? extends V>, V> fctElt2 = new Edge.GetTarget<V>();
				final Function<Edge<? extends V>, String> str1 = Functions.compose(sourceToString, fctElt1);
				final Function<Edge<? extends V>, String> str2 = Functions.compose(targetToString, fctElt2);
				/** Uses tuple notation, not set ('{' and '}'), as this is an ordered pair. */
				return LEFT_ANGLE_BRACKET + str1.apply(input) + ", " + str2.apply(input) + RIGHT_ANGLE_BRACKET;
			}
		};
	}
}
