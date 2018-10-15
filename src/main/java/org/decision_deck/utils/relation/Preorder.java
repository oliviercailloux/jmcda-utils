package org.decision_deck.utils.relation;

import static com.google.common.base.Preconditions.checkNotNull;

import org.decision_deck.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A reflexive and transitive binary relation. Not necessarily complete.
 * 
 * @param <E>
 *            the type of elements in this relation.
 * 
 * @author Olivier Cailloux
 */
public class Preorder<E> extends BinaryRelationForwarder<E, E> implements BinaryRelation<E, E> {

    /**
     * Owned by this object. A preorder: a transitive, reflexive relation.
     */
    private final BinaryRelation<E, E> m_relation;

    private final BinaryRelationRo<E, E> m_relationRo;

    public Preorder() {
	m_relation = new BinaryRelationImpl<E, E>();
	m_relationRo = new BinaryRelationRo<E, E>(m_relation);
    }

    /**
     * Adds (x, x), (y, y), (x, y) to this relation and all relations resulting from transitivity: w R x and y R z ⇒ w R
     * z is added.
     * 
     * @param x
     *            not <code>null</code>.
     * @param y
     *            not <code>null</code>.
     */
    public void addTransitive(E x, E y) {
	checkNotNull(x);
	checkNotNull(y);
	m_relation.asPairs().add(Pair.create(x, x));
	m_relation.asPairs().add(Pair.create(y, y));
	if (contains(x, y)) {
	    return;
	}
	for (E w : getFrom()) {
	    if (!contains(w, x)) {
		continue;
	    }
	    for (E z : getTo()) {
		if (!contains(y, z)) {
		    continue;
		}
		final Pair<E, E> pair = Pair.create(w, z);
		m_relation.asPairs().add(pair);
		s_logger.debug("Added pair {}.", pair);
	    }
	}
	assert (contains(x, y));
    }

    /**
     * Same effect as {@link #addTransitive(Object, Object)} and {@link #addTransitive(Object, Object)} inverted.
     * 
     * @param x
     *            not <code>null</code>.
     * @param y
     *            not <code>null</code>.
     */
    public void addEqTransitive(E x, E y) {
	addTransitive(x, y);
	addTransitive(y, x);
    }

    @Override
    protected BinaryRelation<E, E> delegate() {
	return m_relationRo;
    }

    static public <E> Preorder<E> create() {
        return new Preorder<E>();
    }

    @SuppressWarnings({ "unused", "all" })
    private static final Logger s_logger = LoggerFactory.getLogger(Preorder.class);

    static public <E> Preorder<E> copyOf(BinaryRelation<E, E> source) {
	final Preorder<E> pr = new Preorder<E>();
	for (Pair<E, E> pair : source.asPairs()) {
	    pr.addTransitive(pair.getElt1(), pair.getElt2());
	}
	return pr;
    }
}
