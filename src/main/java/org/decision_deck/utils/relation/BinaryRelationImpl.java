package org.decision_deck.utils.relation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.decision_deck.utils.Pair;

import com.google.common.base.Objects;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Sets;

public class BinaryRelationImpl<F, T> implements BinaryRelation<F, T> {
    private final Set<Pair<F, T>> m_pairs = Sets.newLinkedHashSet();
    private final Set<F> m_from = Sets.newLinkedHashSet();
    private final Set<T> m_to = Sets.newLinkedHashSet();
    @Override
    public Iterator<Pair<F, T>> iterator() {
	return m_pairs.iterator();
    }

    @Override
    public Set<F> getFrom() {
	return m_from;
    }

    @Override
    public Set<T> getTo() {
	return m_to;
    }

    @Override
    public int getValueCount() {
	return m_from.size() * m_to.size();
    }

    @Override
    public boolean isEmpty() {
	return m_from.isEmpty() && m_to.isEmpty();
    }

    @Override
    public Set<Pair<F, T>> asPairs() {
	final Set<Pair<F, T>> pairs = m_pairs;
	final Set<F> from = m_from;
	final Set<T> to = m_to;
	return new ForwardingSet<Pair<F, T>>() {
	    @Override
	    protected Set<Pair<F, T>> delegate() {
		return pairs;
	    }

	    @Override
	    public boolean add(Pair<F, T> pair) {
		from.add(pair.getElt1());
		to.add(pair.getElt2());
		return delegate().add(pair);
	    }

	    @Override
	    public boolean addAll(Collection<? extends Pair<F, T>> collection) {
		return standardAddAll(collection);
	    }
	};
    }

    @Override
    public String toString() {
	return asPairs().toString();
    }

    @Override
    public boolean equals(Object obj) {
	if (!(obj instanceof BinaryRelation<?, ?>)) {
	    return false;
	}
	final BinaryRelation<?, ?> b2 = (BinaryRelation<?, ?>) obj;
	return b2.getFrom().equals(m_from) && b2.getTo().equals(m_to) && b2.asPairs().equals(m_pairs);
    }

    @Override
    public int hashCode() {
	return Objects.hashCode(m_from, m_to, m_pairs);
    }

    @Override
    public boolean contains(F from, T to) {
	checkNotNull(from);
	checkNotNull(to);
	return asPairs().contains(Pair.create(from, to));
    }

}
