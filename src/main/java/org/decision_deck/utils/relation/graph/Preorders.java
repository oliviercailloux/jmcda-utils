package org.decision_deck.utils.relation.graph;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class Preorders {
	static public enum ComparisonState {
		BETTER, EQUIVALENT, INCOMPARABLE, WORST
	}

	static private class FctGetIntersectionComparisonTo<E> implements Function<E, ComparisonState> {
		private final E m_e;
		private final Preorder<E> m_p1;
		private final Preorder<E> m_p2;

		public FctGetIntersectionComparisonTo(E e, Preorder<E> p1, Preorder<E> p2) {
			m_p1 = p1;
			m_p2 = p2;
			m_e = e;
		}

		@Override
		public ComparisonState apply(E input) {
			return getIntersection(m_e, input, m_p1, m_p2);
		}
	}

	static public ComparisonState asComparisonState(int comparisonResult) {
		if (comparisonResult < 0) {
			return ComparisonState.WORST;
		}
		if (comparisonResult > 0) {
			return ComparisonState.BETTER;
		}
		return ComparisonState.EQUIVALENT;
	}

	static public <E> ComparisonState getIntersection(E e1, E e2, Preorder<E> p1, Preorder<E> p2) {
		final int c1 = p1.compare(e1, e2);
		final int c2 = p2.compare(e1, e2);
		if (c1 == 0) {
			return asComparisonState(c2);
		}
		if (c2 == 0) {
			return asComparisonState(c1);
		}
		if (c1 > 0 && c2 > 0) {
			return ComparisonState.BETTER;
		}
		if (c1 < 0 && c2 < 0) {
			return ComparisonState.WORST;
		}
		assert (c1 < 0 && c2 > 0 || (c1 > 0 && c2 < 0));
		return ComparisonState.INCOMPARABLE;
	}

	/**
	 * Retrieves the intersection of two preorders. If two elements compare in a
	 * strongly conflicting way according to the two given preorders, that is, if
	 * one preorder gives an element strictly better than an other one and the other
	 * preorder considers the latter as strictly better than the former, the
	 * resulting preorder is not complete and this method returns <code>null</code>.
	 * If the given preorders do not contain the same elements, the method returns
	 * necessarily <code>null</code> as the resulting intersection is not complete.
	 * 
	 * @param p1 not <code>null</code>.
	 * @param p2 not <code>null</code>.
	 * @return <code>null</code> iff the intersection does not result in a complete
	 *         preorder.
	 */
	static public <E> Preorder<E> getIntersection(Preorder<E> p1, Preorder<E> p2) {
		checkNotNull(p1);
		checkNotNull(p2);
		if (!p1.asSet().equals(p2.asSet())) {
			return null;
		}
		final Set<E> elements = p1.asSet();

		final Preorder<E> inter = new Preorder<E>();
		if (elements.isEmpty()) {
			return inter;
		}

		final E first = elements.iterator().next();
		inter.putAsHighest(first);
		// treated.add(first);
		for (final E e : Sets.difference(elements, ImmutableSet.of(first))) {
			final Function<E, ComparisonState> getIntersectionComparison = new FctGetIntersectionComparisonTo<E>(e, p1,
					p2);
			int rank = 1;
			ComparisonState lastComparison;
			for (;; ++rank) {
				final Set<E> rankCompared = inter.get(rank);
				final Collection<ComparisonState> comparisons = Collections2.transform(rankCompared,
						getIntersectionComparison);
				assert (!comparisons.isEmpty());
				lastComparison = comparisons.iterator().next();
				// final ComparisonState allComparisons =
				// CollectionVariousUtils.getOmnipresentElement(comparisons);
				// if (allComparisons == null || allComparisons ==
				// ComparisonState.INCOMPARABLE) {
				// return null;
				// }
				if (Iterables.any(comparisons, Predicates.or(Predicates.not(Predicates.equalTo(lastComparison)),
						Predicates.equalTo(ComparisonState.INCOMPARABLE)))) {
					return null;
				}
				if (lastComparison == ComparisonState.BETTER) {
					break;
				}
				if (lastComparison == ComparisonState.EQUIVALENT) {
					break;
				}
				assert (lastComparison == ComparisonState.WORST);
				if (rank == inter.getRanksCount()) {
					break;
				}
			}
			assert (1 <= rank && rank <= inter.getRanksCount());
			for (int rankDesc = rank + 1; rankDesc <= inter.getRanksCount(); ++rankDesc) {
				final Set<E> rankCompared = inter.get(rankDesc);
				final Collection<ComparisonState> comparisons = Collections2.transform(rankCompared,
						getIntersectionComparison);
				assert (!comparisons.isEmpty());
				if (Iterables.any(comparisons, Predicates.not(Predicates.equalTo(ComparisonState.BETTER)))) {
					return null;
				}
			}

			switch (lastComparison) {
			case BETTER:
				inter.insertAsNewRank(e, rank);
				break;
			case EQUIVALENT:
				inter.put(e, rank);
				break;
			case WORST:
				assert (rank == inter.getRanksCount());
				inter.putAsLowest(e);
				break;
			case INCOMPARABLE:
			default:
				throw new IllegalStateException();
			}
		}
		return inter;
	}
}
