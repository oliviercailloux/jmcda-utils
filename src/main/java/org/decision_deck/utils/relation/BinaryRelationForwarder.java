package org.decision_deck.utils.relation;

import java.util.Iterator;
import java.util.Set;

import org.decision_deck.utils.Pair;

public abstract class BinaryRelationForwarder<F, T> implements BinaryRelation<F, T> {

    @Override
    public boolean equals(Object obj) {
	return delegate().equals(obj);
    }

    @Override
    public int hashCode() {
	return delegate().hashCode();
    }

    protected String toStringOriginal() {
	return delegate().toString();
    }

    abstract protected BinaryRelation<F, T> delegate();

    @Override
    public Iterator<Pair<F, T>> iterator() {
	return delegate().iterator();
    }

    @Override
    public Set<F> getFrom() {
	return delegate().getFrom();
    }

    @Override
    public Set<T> getTo() {
	return delegate().getTo();
    }

    @Override
    public int getValueCount() {
	return delegate().getValueCount();
    }

    @Override
    public boolean isEmpty() {
	return delegate().isEmpty();
    }

    @Override
    public Set<Pair<F, T>> asPairs() {
	return delegate().asPairs();
    }

    @Override
    public boolean contains(F from, T to) {
	return delegate().contains(from, to);
    }

    @Override
    public String toString() {
        return "Forwarder-" + delegate().toString();
    }

}
