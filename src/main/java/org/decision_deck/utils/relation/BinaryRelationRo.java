package org.decision_deck.utils.relation;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.decision_deck.utils.Pair;

import com.google.common.collect.Iterators;

public class BinaryRelationRo<F, T> extends BinaryRelationForwarder<F, T> implements BinaryRelation<F, T> {

    private final BinaryRelation<F, T> m_delegate;

    public BinaryRelationRo(BinaryRelation<F, T> delegate) {
	m_delegate = delegate;
    }

    @Override
    protected BinaryRelation<F, T> delegate() {
	return m_delegate;
    }

    @Override
    public Iterator<Pair<F, T>> iterator() {
	return Iterators.unmodifiableIterator(m_delegate.iterator());
    }

    @Override
    public Set<F> getFrom() {
	return Collections.unmodifiableSet(m_delegate.getFrom());
    }

    @Override
    public Set<T> getTo() {
	return Collections.unmodifiableSet(m_delegate.getTo());
    }

    @Override
    public Set<Pair<F, T>> asPairs() {
	return Collections.unmodifiableSet(m_delegate.asPairs());
    }

    @Override
    public String toString() {
	return "Ro-" + toStringOriginal();
    }

}
