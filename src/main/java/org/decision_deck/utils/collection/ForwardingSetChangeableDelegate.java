package org.decision_deck.utils.collection;

import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingSet;

public class ForwardingSetChangeableDelegate<T> extends ForwardingSet<T> implements Set<T> {
	private Set<T> m_delegateSet;

	/**
	 * @param delegate not {@code null}.
	 */
	public void setDelegate(Set<T> delegate) {
		Preconditions.checkNotNull(delegate);
		m_delegateSet = delegate;
	}

	/**
	 * @param delegate not {@code null}.
	 */
	public ForwardingSetChangeableDelegate(Set<T> delegate) {
		Preconditions.checkNotNull(delegate);
		m_delegateSet = delegate;
	}

	@Override
	protected Set<T> delegate() {
		return m_delegateSet;
	}

	/**
	 * @param delegate not {@code null}.
	 * @return a new object.
	 */
	static public <T> ForwardingSetChangeableDelegate<T> create(Set<T> delegate) {
		return new ForwardingSetChangeableDelegate<T>(delegate);
	}
}