package org.decision_deck.utils.relation.graph.mess;

import java.util.Comparator;

import org.decision_deck.utils.Pair;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Ordering;

/**
 * <P>
 * A simple pair object, holding two other objects, of possibly different types.
 * One or both elements of the pair may be <code>null</code>. Note that this
 * object represents an ordered pair, or a 2-tuple, which is different than an
 * unordered pair or <em>couple</em> in French.
 * </P>
 * <P>
 * Objects of this type are immutable <b>iff</b> the underlying objects are
 * immutable.
 * </P>
 * <p>
 * The N letter in the class name indicates that this object accepts
 * <code>null</code> elements.
 * </p>
 *
 * @author Olivier Cailloux
 *
 * @param <T1> the type of the first element of the pair.
 * @param <T2> the type of the second element of the pair.
 * @deprecated rather use {@link Pair}, which does not accept null elements.
 */
@Deprecated
public class PairN<T1, T2> {
	static public class GetElt1<T> implements Function<PairN<? extends T, ?>, T> {
		@Override
		public T apply(PairN<? extends T, ?> input) {
			return input.getElt1();
		}

	}

	static public class GetElt2<T> implements Function<PairN<?, ? extends T>, T> {
		@Override
		public T apply(PairN<?, ? extends T> input) {
			return input.getElt2();
		}

	}

	private static final char LEFT_ANGLE_BRACKET = '\u27E8';

	private static final char RIGHT_ANGLE_BRACKET = '\u27E9';

	static public <Type1, Type2> PairN<Type1, Type2> create(Type1 elt1, Type2 elt2) {
		return new PairN<Type1, Type2>(elt1, elt2);
	}

	static public <Type1, Type2> Ordering<PairN<? extends Type1, ? extends Type2>> getLexicographicOrdering(
			final Comparator<Type1> c1, final Comparator<Type2> c2) {
		final GetElt1<Type1> getElt1 = new GetElt1<Type1>();
		final Ordering<PairN<? extends Type1, ?>> first = Ordering.from(c1).onResultOf(getElt1);
		final Ordering<PairN<?, ? extends Type2>> second = Ordering.from(c2).onResultOf(new GetElt2<Type2>());
		final Ordering<PairN<? extends Type1, ? extends Type2>> compound = first
				.<PairN<? extends Type1, ? extends Type2>>compound(second);
		return compound;
	}

	/**
	 * <p>
	 * Retrieves a function which, given a pair, gives its string form in the form
	 * of the transformation of the first element using the given function, a comma
	 * to separate them, and the transformation of the second element, surrounded by
	 * less than and greater than signs (to indicate a tuple). No <code>null</code>
	 * pairs are accepted by the function, but the elements themselves may be
	 * <code>null</code> iff the given transformation functions accept those.
	 * </p>
	 * <p>
	 * This provides an easy way to get short debug strings. E.g. to get a string
	 * representing the contents of a set of pairs of alternatives <em>s</em>, use
	 * <code>Joiner.on(", ").join(Iterables.transform(s, Pair.getToStringFunction(Alternative.getIdFct(), Alternative.getIdFct())))</code>
	 * .
	 * </p>
	 * 
	 * @param           <F1> the type of the first element of the pair to transform.
	 * @param           <F2> the type of the second element of the pair to
	 *                  transform.
	 * @param toString1 a function which transforms the first element of a pair to a
	 *                  string.
	 * @param toString2 a function which transforms the second element of a pair to
	 *                  a string.
	 * 
	 * @return not <code>null</code>.
	 */
	static public <F1, F2> Function<PairN<F1, F2>, String> getToStringFunction(final Function<F1, String> toString1,
			final Function<F2, String> toString2) {
		return new Function<PairN<F1, F2>, String>() {
			@Override
			public String apply(PairN<F1, F2> input) {
				final Function<PairN<? extends F1, ?>, F1> fctElt1 = new PairN.GetElt1<F1>();
				final Function<PairN<?, ? extends F2>, F2> fctElt2 = new PairN.GetElt2<F2>();
				final Function<PairN<? extends F1, ?>, String> str1 = Functions.compose(toString1, fctElt1);
				final Function<PairN<?, ? extends F2>, String> str2 = Functions.compose(toString2, fctElt2);
				/**
				 * Uses tuple notation, not set ('{' and '}'), as this is an ordered pair.
				 */
				return LEFT_ANGLE_BRACKET + str1.apply(input) + ", " + str2.apply(input) + RIGHT_ANGLE_BRACKET;
			}
		};
	}

	private final T1 m_elt1;

	private final T2 m_elt2;

	public PairN(final T1 elt1, final T2 elt2) {
		m_elt1 = elt1;
		m_elt2 = elt2;
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
		final PairN<?, ?> other = (PairN<?, ?>) obj;
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
	public T1 getElt1() {
		return m_elt1;
	}

	/**
	 * @return the second element of the pair. May be <code>null</code>.
	 */
	public T2 getElt2() {
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
}
