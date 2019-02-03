package org.decision_deck.utils.collection.extensional_order;

import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.TreeSet;

import org.decision_deck.utils.collection.AbstractIterator;

import com.google.common.collect.ForwardingNavigableSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * <p>
 * A <a href=http://en.wikipedia.org/wiki/Total_order>total order</a>. This can
 * also be considered as a sorted set whose elements order is defined by
 * extension, hence the class name.
 * </p>
 * <p>
 * This structure has an order defined on a subset S of the set T of elements
 * accepted by the type E. This is in contrast to the general Java collection
 * framework where it seems implied that the subset S must equal the set T. This
 * requirement is not explicitly mentioned in the JDK collections javadoc, but
 * it seems to be implicit because of the some method contracts such as
 * {@link #headSet}, which does not define what to do if an element in T but
 * outside S is given. This class generally throws
 * {@link IllegalArgumentException} when such an element is given and its order
 * must be known to proceed.
 * </p>
 * <p>
 * This class conceptually relates to Apache Commons' ListOrderedSet and to an
 * <a href="https://gist.github.com/1331347">InsertionOrderedSet</a>
 * implementation found on the web.
 * </p>
 *
 * @author Olivier Cailloux
 *
 * @param <E> the type of elements this set deals with.
 */
public class ExtentionalTotalOrder<E> extends ForwardingNavigableSet<E> implements NavigableSet<E> {
	private class Decorated extends AbstractIterator<E> implements Iterator<E> {

		public Decorated(Iterator<E> delegate) {
			super(delegate);
		}

		@SuppressWarnings("synthetic-access")
		@Override
		protected void remove(E e) {
			m_comparator.remove(e);
		}
	}

	private class DecoratedStandardDescendingSet extends StandardDescendingSet {
		public DecoratedStandardDescendingSet() {
			ExtentionalTotalOrder.this.super();
		}

		@Override
		public Iterator<E> iterator() {
			return new Decorated(delegate().descendingIterator());
		}
	}

	static public <E> ExtentionalTotalOrder<E> create() {
		@SuppressWarnings("deprecation")
		/**
		 * ExtensionalComparator.create() should be package visible and not deprecated.
		 */
		final ExtensionalComparator<E> comparator = ExtensionalComparator.create();
		final TreeSet<E> delegate = new TreeSet<E>(comparator);
		return new ExtentionalTotalOrder<E>(delegate, comparator);
	}

	/**
	 * Creates a new empty set using the given comparator.
	 * 
	 * @param            <E> the type of the elements in the created set.
	 * 
	 * @param comparator not <code>null</code>. One of the compared element may be
	 *                   <code>null</code> element (which counts as a normal element
	 *                   for this object).
	 * @return a new object.
	 */
	@Deprecated
	static public <E> ExtentionalTotalOrder<E> create(ExtensionalComparator<E> comparator) {
		final TreeSet<E> delegate = new TreeSet<E>(comparator);
		return new ExtentionalTotalOrder<E>(delegate, comparator);
	}

	/**
	 * Creates a new object initialized with the given collection, in iteration
	 * order. The content of the collection is used to define the universe of
	 * objects initially contained in the returned set, and the order of iteration
	 * on the given collection is used to define the order on that set of objects.
	 * 
	 * @param       <E> the type of the elements in the created set.
	 * 
	 * @param order not <code>null</code>. May be empty (in which case this
	 *              comparator will not be able to compare any objects until
	 *              elements are added). One of the collection element may be
	 *              <code>null</code> element (which counts as a normal element for
	 *              this object). The collection may not contain duplicate elements.
	 *              It is recommended to use a {@link SortedSet} when possible to
	 *              ensure a correct iteration order and no duplicate.
	 * @return a new object.
	 */
	static public <E> ExtentionalTotalOrder<E> create(Iterable<E> order) {
		// final ExtensionalComparator<E> comparator =
		// ExtensionalComparator.create(order);
		// final TreeSet<E> delegate = new TreeSet<E>(comparator);
		// Iterables.addAll(delegate, order);
		// return new ExtentionalTotalOrder<E>(delegate, comparator);
		final ExtentionalTotalOrder<E> total = ExtentionalTotalOrder.create();
		for (E e : order) {
			total.addAsHighest(e);
		}
		return total;
	}

	private final ExtensionalComparator<E> m_comparator;

	private final NavigableSet<E> m_delegate;

	private ExtentionalTotalOrder(NavigableSet<E> delegate, ExtensionalComparator<E> comparator) {
		m_delegate = delegate;
		m_comparator = comparator;
	}

	/**
	 * <p>
	 * Throws an {@link UnsupportedOperationException}. This class requires the
	 * ordering to be defined by extention and this method does not allow to specify
	 * how the added element should be compared to other elements already contained
	 * in this set.
	 * </p>
	 * <p>
	 * An alternative would be, on the model of {@link Deque}, to add as highest by
	 * default. However, the Set add method contract requires to do nothing if the
	 * object to add is already in the set. This would make the method behavior
	 * counter intuitive. Furthermore, it is difficult to see why someone would like
	 * to use this object's add method as if it was a normal collection, not
	 * considering ordering.
	 * </p>
	 * 
	 * @param e unused.
	 * @return nothing.
	 * @throws UnsupportedOperationException always.
	 * @see #addAsHighest
	 * @see #addAfter
	 */
	@Override
	public boolean add(E e) {
		throw new UnsupportedOperationException("Missing ordering information.");
	}

	public void addAfter(E previous, E toAdd) {
		m_comparator.addAfter(previous, toAdd);
		m_delegate.add(toAdd);
	}

	/**
	 * <p>
	 * Throws an {@link UnsupportedOperationException}. This class requires the
	 * ordering to be defined by extention and this method does not allow to specify
	 * how the added elements should be compared to other elements already contained
	 * in this set.
	 * </p>
	 * 
	 * @param c unused.
	 * @return nothing.
	 * @throws UnsupportedOperationException always.
	 * @see #add
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException("Missing ordering information.");
	}

	public void addAsHighest(E e) {
		m_comparator.addAsHighest(e);
		m_delegate.add(e);
	}

	public void addAsLowest(E e) {
		m_comparator.addAsLowest(e);
		m_delegate.add(e);
	}

	@Override
	public void clear() {
		m_delegate.clear();
		m_comparator.clear();
	}

	@Override
	public Comparator<? super E> comparator() {
		/**
		 * Let's wrap our comparator to make sure its internal state is not modifiable
		 * through the returned reference.
		 */
		return Ordering.from(m_comparator);
	}

	@Override
	public boolean contains(Object o) {
		return m_comparator.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return standardContainsAll(c);
	}

	@Override
	public Iterator<E> descendingIterator() {
		return new Decorated(m_delegate.descendingIterator());
	}

	@Override
	public NavigableSet<E> descendingSet() {
		return new DecoratedStandardDescendingSet();
	}

	@Override
	public NavigableSet<E> headSet(E toElement) {
		final SortedSet<E> headSet = m_delegate.headSet(toElement);
		return Sets.unmodifiableNavigableSet((NavigableSet<E>) headSet);
	}

	@Override
	public NavigableSet<E> headSet(E toElement, boolean inclusive) {
		return Sets.unmodifiableNavigableSet(m_delegate.headSet(toElement, inclusive));
	}

	@Override
	public Iterator<E> iterator() {
		return new Decorated(m_delegate.iterator());
	}

	@Override
	public E pollFirst() {
		return standardPollFirst();
	}

	@Override
	public E pollLast() {
		return standardPollLast();
	}

	@Override
	public boolean remove(Object o) {
		final boolean removed = m_delegate.remove(o);
		final Integer removedInt = m_comparator.remove(o);
		final boolean removedAgain = removedInt != null;
		assert removed == removedAgain;
		return removed;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return standardRemoveAll(c);
	}

	public void replace(E oldElement, E newElement) {
		m_delegate.remove(oldElement);
		m_comparator.replaceElement(oldElement, newElement);
		m_delegate.add(newElement);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return standardRetainAll(c);
	}

	@Override
	public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
		return Sets.unmodifiableNavigableSet(m_delegate.subSet(fromElement, fromInclusive, toElement, toInclusive));
	}

	@Override
	public NavigableSet<E> subSet(E fromElement, E toElement) {
		final SortedSet<E> subSet = m_delegate.subSet(fromElement, toElement);
		return Sets.unmodifiableNavigableSet((NavigableSet<E>) subSet);
	}

	@Override
	public NavigableSet<E> tailSet(E fromElement) {
		return (NavigableSet<E>) standardTailSet(fromElement);
	}

	@Override
	public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
		return Sets.unmodifiableNavigableSet(m_delegate.tailSet(fromElement, inclusive));
	}

	@Override
	protected NavigableSet<E> delegate() {
		return m_delegate;
	}

}
