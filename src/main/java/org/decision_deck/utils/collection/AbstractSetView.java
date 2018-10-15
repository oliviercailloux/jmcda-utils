package org.decision_deck.utils.collection;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Preconditions;

/**
 * A set view that wraps an other set. When the user calls {#remove(Object)} on this view, if the underlying set
 * contains the object, this view calls first {@link #beforeRemove(Object)}, then {@link #justRemove(Object)}, then
 * {@link #afterRemove(Object)}. Extenders of this view may override the methods to do appropriate job before or after
 * removal. When an iterator is asked on this view, and the iterator remove method is used, this view first calls
 * {@link #beforeRemove(Object)}, then removes the object using the iterator method, then calls
 * {@link #afterRemove(Object)}.
 * 
 * @author Olivier Cailloux
 * 
 * @param <T>
 *            the type of the objects in the set view and the underlying set.
 */
public abstract class AbstractSetView<T> extends AbstractSet<T> implements Set<T> {
    private final Set<T> m_delegateSet;

    /**
     * @param delegateSet
     *            not <code>null</code>.
     */
    public AbstractSetView(Set<T> delegateSet) {
	Preconditions.checkNotNull(delegateSet);
	m_delegateSet = delegateSet;
    }

    @Override
    public boolean add(T e) {
	return m_delegateSet.add(e);
    }

    @Override
    public boolean remove(Object o) {
	if (!contains(o)) {
	    return false;
	}
	beforeRemove(o);
	justRemove(o);
	afterRemove(o);
	return true;
    }

    /**
     * @param object
     *            has just been removed per {@link #justRemove(Object)} or per the underlying set iterator remove
     *            method.
     */
    protected void afterRemove(@SuppressWarnings("unused") Object object) {
	/** May be overridden to add behavior. */
    }

    /**
     * @param object
     *            exists according to {@link #contains(Object)}.
     */
    protected void beforeRemove(@SuppressWarnings("unused") Object object) {
	/** May be overridden to add behavior. */
    }

    /**
     * Must remove the object from the set. The default implementation calls {@link Set#remove(Object)} on the
     * underlying set and ensures it returns <code>true</code>. For consistency, this method must have the same effect
     * as the underlying set iterator remove method.
     * 
     * @param object
     *            exists according to {@link #contains(Object)}.
     */
    protected void justRemove(Object object) {
	final boolean removed = m_delegateSet.remove(object);
	if (!removed) {
	    throw new IllegalStateException("Object has not been correctly removed: " + object + ".");
	}
    }

    @Override
    public boolean contains(Object c) {
	return m_delegateSet.contains(c);
    }

    @Override
    public Iterator<T> iterator() {
	return new AbstractSetViewIterator<T>(m_delegateSet.iterator(), this);
    }

    @Override
    public int size() {
	return m_delegateSet.size();
    }

}
