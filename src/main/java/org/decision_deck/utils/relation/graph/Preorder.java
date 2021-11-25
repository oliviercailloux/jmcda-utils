package org.decision_deck.utils.relation.graph;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;

import org.decision_deck.utils.collection.extensional_order.ExtentionalTotalOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;

/**
 * <p>
 * Objects of this class represent a complete preorder, or weak order, that is,
 * a relation on a set of objects called elements, which can be viewed as an
 * ordered set of classes of equivalence. A class of equivalence is a set of
 * elements that are considered equal by the preorder. All objects in the
 * greatest class of equivalence are given the rank number one, all elements in
 * the second greatest class of equivalence have the rank two, and so on. As of
 * vocabulary, in this class the greatest rank is considered to be the rank one,
 * thus containing the greatest elements in terms of this preorder relation.
 * </p>
 * <p>
 * This object guarantees that any element it contains has only one rank: no two
 * equal elements (in terms of {@link #equals}) may occupy two different ranks
 * at the same time.
 * </p>
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 * This comparator guarantees {@code (x.equals(y))} <em>implies</em>
 * {@code (compare(x, y)==0)} instead of
 * {@code (compare(x, y)==0) == (x.equals(y))}.
 * </p>
 * <p>
 * This class does not implement {@link Set} because its equals method considers
 * that ordering matters, contrary to the Set interface. However, a method is
 * provided to obtain a set view. The class does not implement {@link List}
 * because the list indexes and preorder ranks correspond to different
 * referencing mechanisms. However, a method is provided to obtain a list view.
 * </p>
 * <p>
 * This could be made to implement writable Collection.
 * </p>
 *
 * @author Olivier Cailloux
 *
 * @param <E> the type of elements on which the relation is defined.
 */
public class Preorder<E> extends AbstractCollection<E> implements Comparator<E>, Collection<E> {
	private static final Ordering<Integer> s_compareByRank = Ordering.<Integer>natural().reverse();

	private static final Logger s_logger = LoggerFactory.getLogger(Preorder.class);

	/**
	 * All ranks are set, no {@code null} values, no empty set.
	 */
	private final List<Set<E>> m_byRanks = Lists.newLinkedList();

	/**
	 * Values are from 1 for the rank with greatest elements.
	 */
	private final Map<E, Integer> m_ranks = new HashMap<E, Integer>();

	/**
	 * Creates an empty preorder. Its number of ranks is zero.
	 */
	public Preorder() {
		/** Public constructor. */
	}

	/**
	 * Creates a preorder containing the given elements and reflecting the order as
	 * determined by the given comparator. If the given comparator is consistent
	 * with equals, then the resulting preorder is a total order and its number of
	 * ranks is the number of elements given. Otherwise, the number of ranks is less
	 * than or equal to the number of elements.
	 *
	 * @param elements   not {@code null}.
	 * @param comparator not {@code null}.
	 */
	public Preorder(Set<E> elements, Comparator<E> comparator) {
		checkNotNull(elements);
		checkNotNull(comparator);
		final List<E> ordered = Ordering.from(comparator).sortedCopy(elements);
		if (ordered.isEmpty()) {
			return;
		}
		E previous = ordered.get(0);
		putAsHighest(previous);
		for (E e : Iterables.skip(ordered, 1)) {
			final int compare = comparator.compare(previous, e);
			assert (compare <= 0);
			if (compare == 0) {
				put(e, 1);
			} else {
				putAsHighest(e);
			}
			previous = e;
		}
	}

	/**
	 * <p>
	 * Retrieves an unmodifiable view of the contents of this preorder, as a
	 * collection of equivalence classes. The collection iteration order is the
	 * ascending order defined by this preorder, with the lowest elements (with
	 * lowest ranks, thus ranks having a bigger number) coming first.
	 * </p>
	 * <p>
	 * The return type is a list rather than a set because the order matters in this
	 * collection, and the list equality definition takes the order into account.
	 * All sets the returned list contains are non empty, and all these sets are
	 * different.
	 * </p>
	 * <p>
	 * <b>Warning</b>: do not mix indexes used as references to sets of elements in
	 * the returned list and rank numbers used as references to sets of elements in
	 * this preorder. These two reference systems are different. The returned list
	 * is indexed from zero, as mandated by the {@link List} interface. Index zero
	 * of the returned list corresponds to the set containing the lowest elements.
	 * On the contrary, in this preorder, the lowest elements belong to the rank
	 * with the biggest rank number, and the highest elements belong to rank one.
	 *
	 * @return not {@code null}, no empty sets.
	 */
	public List<Set<E>> asListOfSets() {
		return Collections.unmodifiableList(m_byRanks);
	}

	/**
	 * <p>
	 * Retrieves a view of the elements in this object. Iteration order reflects the
	 * preorder relation: elements in a lower equivalence class are iterated before
	 * elements in a higher equivalence class. Iteration order on the elements of a
	 * given equivalence class is undefined.
	 * </p>
	 * <p>
	 * If this preorder is a total order, the returned set has the same iteration
	 * order as the one returned by {@link #getTotalOrder()}.
	 * </p>
	 * <p>
	 * The view is currently read-only, but should be made writable in the future.
	 * </p>
	 *
	 * @return not {@code null}.
	 */
	public Set<E> asSet() {
		return new AbstractSet<E>() {

			@Override
			public Iterator<E> iterator() {
				return Preorder.this.iterator();
			}

			@Override
			public int size() {
				return Preorder.this.size();
			}
		};
	}

	@Override
	public int compare(E element1, E element2) {
		checkArgument(m_ranks.containsKey(element1), "Unknown " + element1);
		checkArgument(m_ranks.containsKey(element2), "Unknown " + element2);
		return s_compareByRank.compare(m_ranks.get(element1), m_ranks.get(element2));
	}

	/**
	 * A preorder equals an other object iff it is a preorder and they contain the
	 * same elements in the same ranks.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Preorder) {
			Preorder<?> p2 = (Preorder<?>) obj;
			return m_byRanks.equals(p2.m_byRanks);
		}
		return false;
	}

	/**
	 * @param rank between 1 and the number of ranks, inclusive. 1 is the best rank.
	 * @return an unmodifiable view of the objects stored at the given rank.
	 */
	public Set<E> get(int rank) {
		checkArgument(rank >= 1);
		checkArgument(rank <= getRanksCount(),
				"Given rank: " + rank + ", too high, expected rank ≤ " + getRanksCount() + ".");
		return Collections.unmodifiableSet(m_byRanks.get(rank - 1));
	}

	/**
	 * @param element not {@code null}.
	 * @return the rank of the given element, or {@code null} iff the given
	 *         element is not contained in this object.
	 */
	public Integer getRank(E element) {
		checkNotNull(element);
		return m_ranks.get(element);
	}

	/**
	 * Retrieves the set of integers between one and the number of ranks. This is
	 * the set of occupied ranks. The returned set considers rank one to be the
	 * lowest rank, although rank one contains the elements this preorder considers
	 * greatest. This is conformant to the natural integer ordering.
	 *
	 * @return not {@code null}, empty iff this object is empty.
	 */
	public NavigableSet<Integer> getRanks() {
		final Range<Integer> range = Range.closed(Integer.valueOf(1), Integer.valueOf(m_byRanks.size()));
		ContiguousSet<Integer> ranks = ContiguousSet.create(range, DiscreteDomain.integers());
		for (Integer rank : ranks) {
			assert (!m_byRanks.get(rank.intValue() - 1).isEmpty());
		}
		return Sets.newTreeSet(ranks);
	}

	/**
	 * Retrieves the number of occupied ranks in this object. This is at most the
	 * number of elements in this preorder.
	 *
	 * @return zero iff this object is empty.
	 */
	public int getRanksCount() {
		return m_byRanks.size();
	}

	/**
	 * Retrieves a copy of this preorder content as a total order, if this preorder
	 * is a total order (i.e., if there is exactly one element per rank). The object
	 * ranked first is set as highest, or best. This corresponds to the last element
	 * in iteration order of the returned set, as the returned set is ordered from
	 * lowest to highest.
	 *
	 * @return {@code null} iff this preorder is not a total order.
	 */
	public NavigableSet<E> getTotalOrder() {
		final ExtentionalTotalOrder<E> order = ExtentionalTotalOrder.create();
		final List<Set<E>> byRanks = m_byRanks;
		for (Set<E> elements : byRanks) {
			if (elements.size() != 1) {
				return null;
			}
			final E element = elements.iterator().next();
			order.addAsLowest(element);
		}
		return order;
	}

	@Override
	public int hashCode() {
		return m_byRanks.hashCode();
	}

	/**
	 * <p>
	 * Inserts a new rank containing only the given element.
	 * <p>
	 * After this method returns, the given element is strictly greater than all
	 * elements that existed in this preorder, prior to this call, at a rank equal
	 * to or less than the given rank; and strictly less than all elements that
	 * existed in this preorder, prior to this call, at a rank greater than the
	 * given rank. All elements that existed, prior to this call, at a rank equal or
	 * lower than the given rank end up one rank lower.
	 * </p>
	 * <p>
	 * The given rank must be between one and the number of ranks plus one. If it is
	 * one, this call is equivalent to {@link #putAsHighest}. If it is the number of
	 * ranks plus one, this call is equivalent to {@link #putAsLowest} <em>if the
	 * element does not already exist</em>.
	 * </p>
	 * <p>
	 * If the given element exists at a rank lower than the given rank, it is moved.
	 * This implies that calling this method does not necessarily increase the
	 * number of ranks: the number of ranks increase iff, prior to this call, the
	 * given element was not contained in this preorder or was contained and was not
	 * alone in its rank.
	 * </p>
	 * <p>
	 * If the given element already exists at the given rank and is the only element
	 * at this rank, nothing changes and this method returns {@code false}.
	 * </p>
	 * <p>
	 * <b>Warning:</b> When using this method with an element which already exists
	 * in this preorder, proceed with caution. If the element exists at a rank
	 * greater than the given rank, it is an error and an exception is thrown.
	 * Indeed the behavior of the method is impossible to define in an intuitive way
	 * in such a case.
	 * </p>
	 *
	 * @param content may not already exist at a better rank than the given one.
	 * @param rank    between one and the number of ranks plus one.
	 * @return {@code true} iff this method call changed the state of this
	 *         preorder, {@code false} iff the given element already existed at
	 *         the given rank and was alone in its equivalence class.
	 */
	public boolean insertAsNewRank(E content, int rank) {
		checkArgument(rank >= 1);
		checkArgument(rank <= getRanksCount() + 1);
		final Integer existingRank = getRank(content);
		checkArgument(existingRank == null || existingRank.intValue() >= rank);
		if (existingRank != null) {
			final int currentRank = existingRank.intValue();
			if (currentRank == rank && m_byRanks.get(currentRank - 1).size() == 1) {
				return false;
			}
			assert (currentRank <= rank);
			remove(content);
		}

		for (int rankToMove = rank - 1; rankToMove < m_byRanks.size(); ++rankToMove) {
			final Set<E> elementsToMove = m_byRanks.get(rankToMove);
			for (E e : elementsToMove) {
				assert (m_ranks.get(e).intValue() == rankToMove + 1);
				m_ranks.put(e, Integer.valueOf(rankToMove + 2));
			}
		}
		m_byRanks.add(rank - 1, Sets.<E>newLinkedHashSet());
		put(content, rank);

		return true;
	}

	/**
	 * Returns an iterator over the elements in this preorder. Iteration order
	 * matches the one from the preorder.
	 *
	 * @return not {@code null}.
	 */
	@Override
	public Iterator<E> iterator() {
		final Iterable<E> concat = Iterables.concat(asListOfSets());
		return concat.iterator();
	}

	/**
	 * Lowers the given element by one rank. If the given element has rank
	 * <em>r</em>, after the method returns it has rank <em>r+1</em>.
	 *
	 * @param element must exist in this object, may not be the only element at the
	 *                worst rank otherwize it is impossible to lower it.
	 */
	public void lower(E element) {
		checkNotNull(element);
		final Integer oldRank = m_ranks.get(element);
		if (oldRank == null) {
			throw new IllegalStateException("Unknown object.");
		}
		final int oldR = oldRank.intValue();
		checkState(oldR != m_byRanks.size() || getLowests().size() > 1, "Can't lower the only object at lowest rank.");
		put(element, oldR + 1);
	}

	/**
	 * Adds an element to the set of elements at a given rank. If the element
	 * already existed and was at the given rank, nothing happens. If the element
	 * already existed and was at a different rank, the existing element is removed
	 * from that rank and put at the given rank instead. It is admitted to put any
	 * element at a rank number equals to the number of ranks this object contains
	 * plus one, which is equivalent to ask to put it as lowest, <em>except</em> if
	 * the element is already the unique worst one (with no ex-æquos), in which case
	 * this method will throw an exception.
	 *
	 * @param element not {@code null}.
	 * @param rank    between 1 and the number of ranks + 1, inclusive. 1 is the
	 *                best, or highest, rank.
	 *
	 * @return {@code true} iff the element has been added or moved, thus was
	 *         not already in the set of elements at that rank.
	 */
	public boolean put(E element, int rank) {
		/**
		 * NB implementation must accept possible empty sets in the internal rank
		 * structure.
		 */
		checkNotNull(element);
		checkArgument(rank >= 1);
		checkArgument(rank <= getRanksCount() + 1);
		final Integer oldRank = m_ranks.get(element);
		if (oldRank != null) {
			final int oldR = oldRank.intValue();
			if (oldR == rank) {
				return false;
			}
			checkArgument(oldR != getRanksCount() || get(getRanksCount()).size() != 1,
					"Asking to put the unique element at rank " + oldR + " into new rank " + rank
							+ ", this is impossible.");
			final boolean wasThere = m_byRanks.get(oldR - 1).remove(element);
			assert (wasThere);
			if (m_byRanks.get(oldR - 1).isEmpty()) {
				m_byRanks.remove(oldR - 1);
			}
		}
		final boolean added = getOrInitRank(rank).add(element);
		m_ranks.put(element, Integer.valueOf(rank));
		s_logger.debug("Added element {} at rank " + rank + ".", element);
		return added;
	}

	/**
	 * Puts all the elements in the given rank, as per calling repeatedly the method
	 * {@link #put(Object, int)}. Elements already existing at an other rank are
	 * moved.
	 *
	 * @param elements not {@code null}, but may be empty.
	 * @param rank     the rank where to put the objects.
	 * @return {@code true} iff the call changed the ranking, thus
	 *         {@code false} iff every elements were already in the given rank.
	 */
	public boolean put(Set<E> elements, int rank) {
		checkNotNull(elements);
		checkArgument(rank >= 1);
		checkArgument(rank <= getRanksCount() + 1);
		boolean changed = false;
		for (E element : elements) {
			final boolean put = put(element, rank);
			changed = changed || put;
		}
		return changed;
	}

	public boolean putAllAsHighest(Set<E> elements) {
		if (getRanksCount() >= 1 && get(1).equals(elements)) {
			return false;
		}
		for (Entry<E, Integer> entry : m_ranks.entrySet()) {
			final int oldR = entry.getValue().intValue();
			entry.setValue(Integer.valueOf(oldR + 1));
		}
		m_byRanks.add(0, Sets.<E>newLinkedHashSet());
		/**
		 * NB at this stage an invariant is broken, as the new set is empty. But the put
		 * method does not mind. Also note that the method will move the element if it
		 * exists.
		 */
		for (E element : elements) {
			final boolean changed = put(element, 1);
			assert (changed);
		}
		return true;
	}

	public boolean putAllAsLowest(Set<E> elements) {
		final Set<E> worsts = getLowests();
		if (worsts.equals(elements)) {
			return false;
		}
		final int newRank;
		if (worsts.size() == 1 && elements.contains(Iterables.getOnlyElement(worsts))) {
			newRank = getRanksCount();
		} else {
			newRank = getRanksCount() + 1;
		}
		boolean changed = false;
		for (E element : elements) {
			final boolean thisChanged = put(element, newRank);
			changed = changed || thisChanged;
		}
		assert (changed);
		return true;
	}

	/**
	 * Adds an element so that it is greater than every other elements. The given
	 * element has rank one after this method returns. Any elements that this
	 * preorder contained prior to this call and different than the given element is
	 * moved one rank below. If the given element was already in this preorder, it
	 * is moved. If the given element already was the highest element (which implies
	 * that it is alone in its equivalence class), this method call does not change
	 * this preorder content.
	 *
	 * @param element not {@code null}.
	 * @return {@code true} iff any element has changed position or the given
	 *         element has been added, or equivalently, {@code false} iff the
	 *         element was already the unique best one.
	 */
	public boolean putAsHighest(E element) {
		checkNotNull(element);
		if (m_ranks.get(element) != null && m_ranks.get(element).intValue() == 1) {
			return false;
		}
		for (Entry<E, Integer> entry : m_ranks.entrySet()) {
			final int oldR = entry.getValue().intValue();
			entry.setValue(Integer.valueOf(oldR + 1));
		}
		m_byRanks.add(0, Sets.<E>newLinkedHashSet());
		/**
		 * NB at this stage an invariant is broken, as the new set is empty. But the put
		 * method does not mind. Also note that the method will move the element if it
		 * exists.
		 */
		final boolean changed = put(element, 1);
		assert (changed);
		return true;
	}

	/**
	 * Adds the given element and makes it the lowest element, or moves it to the
	 * lowest position if it is already present in this preorder. This increases the
	 * ranks count by one, except if the element already was the unique (with no
	 * ex-æquo) lowest one.
	 *
	 * @param element not {@code null}.
	 * @return {@code true} iff this object changed as a result of this call.
	 */
	public boolean putAsLowest(E element) {
		if (m_ranks.get(element) != null && m_ranks.get(element).intValue() == getRanksCount()) {
			return false;
		}
		final boolean changed = put(element, getRanksCount() + 1);
		assert (changed);
		return true;
	}

	/**
	 * Raises the given element by one rank. The given element ends up being greater
	 * than it was previously: if it was at rank 3, it ends up at rank 2.
	 *
	 * @param element must exist in this object, must not be the unique highest
	 *                element.
	 */
	public void raise(E element) {
		checkNotNull(element);
		final Integer oldRank = m_ranks.get(element);
		if (oldRank == null) {
			throw new IllegalStateException("Unknown object.");
		}
		final int oldR = oldRank.intValue();
		if (oldR == 1) {
			if (get(1).size() == 1) {
				throw new IllegalArgumentException("Can't raise unique best element.");
			}
			putAsHighest(element);
		} else {
			put(element, oldR - 1);
		}
	}

	/**
	 * Removes the given element from this object, if it is present.
	 *
	 * @param element not {@code null}.
	 * @return {@code true} iff this preorder contained the given element prior
	 *         to this call.
	 */
	@Override
	public boolean remove(Object element) {
		checkNotNull(element);
		final Integer previous = m_ranks.remove(element);
		if (previous == null) {
			return false;
		}
		final int oldR = previous.intValue();
		final boolean wasThere = m_byRanks.get(oldR - 1).remove(previous);
		assert (wasThere);
		if (m_byRanks.get(oldR - 1).isEmpty()) {
			m_byRanks.remove(oldR - 1);
			for (int rankToMove = oldR; rankToMove < m_byRanks.size(); ++rankToMove) {
				final Set<E> elementsToMove = m_byRanks.get(rankToMove);
				for (E e : elementsToMove) {
					assert (m_ranks.get(e).intValue() == rankToMove + 1);
					m_ranks.put(e, Integer.valueOf(rankToMove));
				}
			}
			// final Iterable<E> elementsToMove =
			// Iterables.concat(m_byRanks.subList(oldR, m_byRanks.size()));
			// for (E e : elementsToMove) {
			// m_ranks.put(e, Integer.valueOf(m_ranks.get(e).intValue() - 1));
			// }
		}
		return true;
	}

	@Override
	public int size() {
		return m_ranks.size();
	}

	@Override
	public String toString() {
		final ToStringHelper helper = MoreObjects.toStringHelper(this);
		helper.addValue(asListOfSets());
		return helper.toString();
	}

	private Set<E> getLowests() {
		final Set<E> worsts = getRanksCount() > 0 ? get(getRanksCount()) : Collections.<E>emptySet();
		return worsts;
	}

	private Set<E> getOrInitRank(int rank) {
		assert (rank <= getRanksCount() + 1) : "Rank too low: " + rank + ", lowest is " + getRanksCount() + ".";
		if (rank == getRanksCount() + 1) {
			m_byRanks.add(Sets.<E>newLinkedHashSet());
		}
		return m_byRanks.get(rank - 1);
	}

}
