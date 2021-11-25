package org.decision_deck.utils.collection;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractIterator<E> implements Iterator<E> {

	private final Iterator<E> m_delegate;
	private E m_current;

	/**
	 * @param delegateIterator not {@code null}.
	 */
	public AbstractIterator(Iterator<E> delegateIterator) {
		checkNotNull(delegateIterator);
		m_delegate = delegateIterator;
		m_current = null;
	}

	@Override
	public boolean hasNext() {
		return m_delegate.hasNext();
	}

	@Override
	public E next() {
		m_current = m_delegate.next();
		return m_current;
	}

	@Override
	public void remove() {
		if (m_current == null) {
			throw new NoSuchElementException();
		}
		m_delegate.remove();
		remove(m_current);
		m_current = null;
	}

	protected E getCurrent() {
		return m_current;
	}

	/**
	 * Called after removal of an element through the delegate iterator.
	 * 
	 * @param e an element returned by the underlying iterator. May be
	 *          {@code null} if the underlying iterator accepts these elements.
	 */
	protected abstract void remove(E e);

	public Iterator<E> getDelegate() {
		return m_delegate;
	}

}