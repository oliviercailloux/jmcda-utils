package org.decision_deck.utils;

import java.util.Comparator;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Ordering;

/**
 * <P>
 * A simple pair object, holding two other objects, of possibly different types.
 * Elements of the pair may <em>not</em> be <code>null</code>. Note that this
 * object represents an ordered pair, or a 2-tuple, which is different than an
 * unordered pair or couple in French.
 * </P>
 * <P>
 * Objects of this type are immutable <b>iff</b> the underlying elements are
 * immutable.
 * </P>
 * <p>
 * TODO move into relation package.
 * </p>
 *
 * @author Olivier Cailloux
 *
 * @param <T1>
 *            the type of the first element of the pair.
 * @param <T2>
 *            the type of the second element of the pair.
 */
public class Pair<T1, T2> {
	static public class GetElt1<T> implements Function<Pair<? extends T, ?>, T> {
		@Override
		public T apply(Pair<? extends T, ?> input) {
			return input.getElt1();
		}

	}

	static public class GetElt2<T> implements Function<Pair<?, ? extends T>, T> {
		@Override
		public T apply(Pair<?, ? extends T> input) {
			return input.getElt2();
		}

	}

	private static final char LEFT_ANGLE_BRACKET = '\u27E8';

	private static final char RIGHT_ANGLE_BRACKET = '\u27E9';

	static public <Type1, Type2> Pair<Type1, Type2> create(Type1 elt1, Type2 elt2) {
		return new Pair<Type1, Type2>(elt1, elt2);
	}

	static public <Type1, Type2> Ordering<Pair<? extends Type1, ? extends Type2>> getLexicographicOrdering(
			final Comparator<Type1> c1, final Comparator<Type2> c2) {
		final Ordering<Pair<? extends Type1, ?>> first = Ordering.from(c1).onResultOf(new GetElt1<Type1>());
		final Ordering<Pair<?, ? extends Type2>> second = Ordering.from(c2).onResultOf(new GetElt2<Type2>());
		final Ordering<Pair<? extends Type1, ? extends Type2>> compound = first
				.<Pair<? extends Type1, ? extends Type2>> compound(second);
		return compound;
	}

	/**
	 * <p>
	 * Retrieves a function which, given a pair, gives its string form in the
	 * form of the transformation of the first element using the given function,
	 * a comma to separate them, and the transformation of the second element,
	 * surrounded by angle brackets (to indicate a tuple). No <code>null</code>
	 * pairs are accepted by the function.
	 * </p>
	 * <p>
	 * This provides an easy way to get short debug strings. E.g. to get a
	 * string representing the contents of a set of pairs of alternatives
	 * <em>s</em>, use
	 * <code>Joiner.on(", ").join(Iterables.transform(s, Pair.getToStringFunction(Alternative.getIdFct(), Alternative.getIdFct())))</code>
	 * .
	 * </p>
	 *
	 * @param <F1>
	 *            the type of the first element of the pair to transform.
	 * @param <F2>
	 *            the type of the second element of the pair to transform.
	 * @param toString1
	 *            a function which transforms the first element of a pair to a
	 *            string.
	 * @param toString2
	 *            a function which transforms the second element of a pair to a
	 *            string.
	 *
	 * @return not <code>null</code>.
	 */
	static public <F1, F2> Function<Pair<F1, F2>, String> getToStringFunction(
			final Function<? super F1, String> toString1, final Function<? super F2, String> toString2) {
		return new Function<Pair<F1, F2>, String>() {
			@Override
			public String apply(Pair<F1, F2> input) {
				final Function<Pair<? extends F1, ?>, F1> fctElt1 = new Pair.GetElt1<F1>();
				final Function<Pair<?, ? extends F2>, F2> fctElt2 = new Pair.GetElt2<F2>();
				final Function<Pair<? extends F1, ?>, String> str1 = Functions.compose(toString1, fctElt1);
				final Function<Pair<?, ? extends F2>, String> str2 = Functions.compose(toString2, fctElt2);
				return LEFT_ANGLE_BRACKET + str1.apply(input) + ", " + str2.apply(input) + RIGHT_ANGLE_BRACKET;
			}
		};
	}

	private final T1 m_elt1;

	private final T2 m_elt2;

	public Pair(T1 elt1, T2 elt2) {
		/** Check not null! */
		m_elt1 = elt1;
		m_elt2 = elt2;
	}

	/**
	 * Two pairs are equal iff their first elements are equal and their second
	 * elements are equal. Equality rather than identity is used to test
	 * elements equality.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Pair<?, ?> other = (Pair<?, ?>) obj;
		if (!m_elt1.equals(other.getElt1())) {
			return false;
		}
		if (!m_elt2.equals(other.getElt2())) {
			return false;
		}
		return true;
	}

	/**
	 * Retrieves the first element of the pair.
	 *
	 * @return not <code>null</code>.
	 */
	public T1 getElt1() {
		return m_elt1;
	}

	/**
	 * Retrieves the second element of the pair.
	 *
	 * @return not <code>null</code>.
	 */
	public T2 getElt2() {
		return m_elt2;
	}

	@Override
	public int hashCode() {
		final int prime = 71;
		int result = 1;
		result = prime * result + ((m_elt1 == null) ? 0 : m_elt1.hashCode());
		result = prime * result + ((m_elt2 == null) ? 0 : m_elt2.hashCode());
		return result;
	}

	@Override
	public String toString() {
		final Function<Pair<T1, T2>, String> toStringFunction = getToStringFunction(Functions.toStringFunction(),
				Functions.toStringFunction());
		return toStringFunction.apply(this);
	}
}
