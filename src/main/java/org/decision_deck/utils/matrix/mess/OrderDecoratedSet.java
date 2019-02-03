package org.decision_deck.utils.matrix.mess;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.decision_deck.utils.collection.AbstractSetView;
import org.decision_deck.utils.collection.CollectionUtils;
import org.decision_deck.utils.collection.ForwardingSetChangeableDelegate;
import org.decision_deck.utils.collection.extensional_order.ExtensionalComparator;
import org.decision_deck.utils.collection.extensional_order.ExtentionalTotalOrder;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ForwardingSet;

/**
 * 
 * Decorates an other Set and adds an external ordering capability. This object
 * is in one of three possible states.
 * <ul>
 * <li>uses delegate order;</li>
 * <li>uses a general comparator;</li>
 * <li>uses a {@link ExtensionalComparator} or a collection defining the order,
 * in which case this object is said to be in <em>subset mode</em>. In this
 * mode, supplementary caution is required as adding an object to this set
 * requires its order to be defined.</li>
 * </ul>
 * 
 * @author Olivier Cailloux
 * 
 * @param <E> the type of elements stored in this set and in the delegated set.
 */
public class OrderDecoratedSet<E> extends ForwardingSet<E> implements Set<E> {
	/**
	 * Creates a new decorator for the given set. The default is to use the order
	 * given by the delegate. Therefore the returned set has the same iteration
	 * order as the given decorated set, to begin with.
	 * 
	 * @param          <E> the type of elements used.
	 * @param delegate not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public <E> OrderDecoratedSet<E> create(Set<E> delegate) {
		return new OrderDecoratedSet<E>(delegate);
	}

	/**
	 * The decorated set, not <code>null</code>.
	 */
	private final Set<E> m_delegate;
	/**
	 * May be <code>null</code>. If not <code>null</code>, the general comparator to
	 * use to order, defining the ordered set.
	 */
	private Comparator<? super E> m_comparator;
	/**
	 * May be <code>null</code>. If not <code>null</code>, the subset comparator to
	 * use to order, defining the ordered set.
	 */
	private ExtensionalComparator<E> m_subsetComparator;
	/**
	 * Is <code>null</code> iff no general comparator is defined. If not
	 * <code>null</code>, defines the ordering shown by this object.
	 */
	private NavigableSet<E> m_orderedSetGeneral;
	/**
	 * Is <code>null</code> iff no subset comparator is defined. If not
	 * <code>null</code>, defines the ordering shown by this object.
	 */
	private ExtentionalTotalOrder<E> m_orderedSetFromSubsetComparator;

	/**
	 * Creates a new decorator for the given set. The default is to use the order
	 * given by the delegate. Therefore the returned set has the same iteration
	 * order as the given decorated set, to begin with.
	 * 
	 * @param delegate not <code>null</code>.
	 */
	public OrderDecoratedSet(Set<E> delegate) {
		checkNotNull(delegate);
		m_delegate = delegate;
		m_comparator = null;
		m_subsetComparator = null;
		m_orderedSetGeneral = null;
		m_orderedSetFromSubsetComparator = null;
		m_view = ForwardingSetChangeableDelegate.create(delegate);
	}

	/**
	 * <p>
	 * Adds the specified element to this set if it is not already present. More
	 * formally, adds the specified element <tt>e</tt> to this set if the set
	 * contains no element <tt>e2</tt> such that
	 * <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>. If this set
	 * already contains the element, the call leaves the set unchanged and returns
	 * <tt>false</tt>.
	 * </p>
	 * <p>
	 * This set implementation adds a supplementary restriction to adding an object:
	 * its order must be defined. Ordering information must be given with any new
	 * element added as this object does not know otherwise how to treat them
	 * compared to the other elements. Please use rather {@link #addAsBest(Object)}
	 * or {@link #addAfter(Object, Object)} if order of the object to add is not
	 * known already. This restriction applies only if this object is in subset
	 * ordering mode (see {@link OrderDecoratedSet}), it does not apply if it uses
	 * the order of the delegate or a general comparator.
	 * </p>
	 * 
	 * @throws UnsupportedOperationException if the given element has no known
	 *                                       order.
	 */
	@Override
	public boolean add(E element) {
		return delegate().add(element);
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		return standardAddAll(collection);
	}

	@SuppressWarnings("deprecation")
	private void setOrderedSet() {
		assert (m_comparator == null || m_subsetComparator == null);
		if (m_comparator != null) {
			m_orderedSetGeneral = new TreeSet<E>(m_comparator);
			m_orderedSetGeneral.addAll(m_delegate);
			m_orderedSetFromSubsetComparator = null;
		} else if (m_subsetComparator != null) {
			final ExtensionalComparator<E> subsetComparator = m_subsetComparator;
			m_orderedSetFromSubsetComparator = CollectionUtils.newNavigableSet(subsetComparator);
			m_orderedSetFromSubsetComparator.addAll(m_delegate);
			m_orderedSetGeneral = null;
		} else {
			m_orderedSetGeneral = null;
			m_orderedSetFromSubsetComparator = null;
		}
		assert ((m_orderedSetGeneral == null) == (m_comparator == null));
		assert ((m_orderedSetFromSubsetComparator == null) == (m_subsetComparator == null));
	}

	/**
	 * Switches this object to delegate mode. Any previously set ordering
	 * information is lost.
	 */
	public void setOrderByDelegate() {
		m_comparator = null;
		m_subsetComparator = null;
		setOrderedSet();
		setViewDelegate();
	}

	/**
	 * Switches this object to comparator mode. Any previously set ordering
	 * information is lost.
	 * 
	 * @param comparator not <code>null</code>, must be a general comparator (i.e.
	 *                   not a {@link ExtensionalComparator}).
	 */
	public void setComparator(Comparator<E> comparator) {
		m_comparator = comparator;
		m_subsetComparator = null;
		setOrderedSet();
		setViewDelegate();
	}

	/**
	 * @param subsetComparator not <code>null</code>.
	 */
	public void setSubsetComparator(ExtensionalComparator<E> subsetComparator) {
		checkNotNull(subsetComparator);
		m_comparator = null;
		m_subsetComparator = subsetComparator;
		setOrderedSet();
		setViewDelegate();
	}

	@SuppressWarnings("deprecation")
	public void setOrder(Collection<E> order) {
		m_comparator = null;
		m_subsetComparator = ExtensionalComparator.create(order);
	}

	@Override
	public String toString() {
		final ToStringHelper helper = Objects.toStringHelper(this);
		helper.add("Mode", getModeString());
		helper.add("Contents", delegate().toString());
		return helper.toString();
	}

	private String getModeString() {
		assert (m_comparator == null || m_subsetComparator == null);
		assert ((m_orderedSetGeneral == null) == (m_comparator == null));
		assert ((m_orderedSetFromSubsetComparator == null) == (m_subsetComparator == null));
		if (m_orderedSetGeneral != null) {
			return "general";
		} else if (m_orderedSetFromSubsetComparator != null) {
			return "subset";
		} else {
			return "delegate";
		}
	}

	@Override
	protected Set<E> delegate() {
		return m_view;
	}

	/**
	 * Retrieves a writable view to the decorated set underlying this object. The
	 * user of this object is reponsible for ensuring the order of all objects in
	 * the decorated set is known to this object, otherwise the behavior of this
	 * object is not determined any more. Thus it is not advised to add elements to
	 * the decorated set through this method, rather add elements through this
	 * object's #add or similar methods which will ensure ordering information is
	 * consistent. This method may be useful however to remove informations from the
	 * underlying decorated set without modifying the ordering informations, which
	 * is especially useful in case the ordering information is shared.
	 * 
	 * @return not <code>null</code>.
	 */
	public Set<E> getDecoratedSet() {
		return m_delegate;
	}

	private void setViewDelegate() {
		assert (m_comparator == null || m_subsetComparator == null);
		assert ((m_orderedSetGeneral == null) == (m_comparator == null));
		assert ((m_orderedSetFromSubsetComparator == null) == (m_subsetComparator == null));
		if (m_orderedSetGeneral != null) {
			m_view.setDelegate(getView(m_orderedSetGeneral, m_delegate));
		} else if (m_orderedSetFromSubsetComparator != null) {
			m_view.setDelegate(getView(m_orderedSetFromSubsetComparator, m_delegate));
		} else {
			m_view.setDelegate(m_delegate);
		}
	}

	/**
	 * The facade this object finally shows. It delegates either to this object's
	 * delegate, that is, the decorated set, or to the navigable set if defined.
	 * This view is a writeable view thus it manages the sync between this object
	 * delegate and the ordered set, in case it is used.
	 */
	private final ForwardingSetChangeableDelegate<E> m_view;

	protected <T> Set<T> getView(final Set<T> orderedSet, final Set<T> setToUpdate) {
		final AbstractSetView<T> orderedView = new AbstractSetView<T>(orderedSet) {
			@Override
			public boolean add(T e) {
				setToUpdate.add(e);
				return super.add(e);
			}

			@Override
			protected void beforeRemove(Object object) {
				setToUpdate.remove(object);
			}
		};

		return orderedView;
	}

	public void addAsBest(E e) {
		m_orderedSetFromSubsetComparator.addAsHighest(e);
	}

	public void addAfter(E previous, E toAdd) {
		m_orderedSetFromSubsetComparator.addAfter(previous, toAdd);
	}

	public void addAsWorst(E e) {
		m_orderedSetFromSubsetComparator.addAsLowest(e);
	}
}
