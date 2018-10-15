package org.decision_deck.utils.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.google.common.base.Preconditions;

public class AbstractSetViewIterator<T> implements Iterator<T> {

    private final Iterator<T> m_delegate;
    private T m_current;
    private final AbstractSetView<T> m_delegateSet;

    /**
     * @param delegateIterator
     *            not <code>null</code>.
     * @param delegateSet
     *            not <code>null</code>.
     */
    public AbstractSetViewIterator(Iterator<T> delegateIterator, AbstractSetView<T> delegateSet) {
	Preconditions.checkNotNull(delegateIterator);
	Preconditions.checkNotNull(delegateSet);
	m_delegate = delegateIterator;
	m_delegateSet = delegateSet;
	m_current = null;
    }

    @Override
    public boolean hasNext() {
	return m_delegate.hasNext();
    }

    @Override
    public T next() {
	m_current = m_delegate.next();
	return m_current;
    }

    @Override
    public void remove() {
	if (m_current == null) {
	    throw new NoSuchElementException();
	}
	if (!m_delegateSet.contains(m_current)) {
	    throw new IllegalStateException("Current object does not exist.");
	}
	m_delegateSet.beforeRemove(m_current);
	/**
	 * NB must remove objects through the iterator, writing to the iterated object while iterating is not permitted.
	 */
	m_delegate.remove();
	m_delegateSet.afterRemove(m_current);
	m_current = null;
    }

}