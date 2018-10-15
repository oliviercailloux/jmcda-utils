package org.decision_deck.utils.relation;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Iterator;

import org.decision_deck.utils.Pair;

import com.google.common.collect.AbstractIterator;

/**
 * An extension of a pair, usable in case the pair is homogeneous. Adds iteration functionality over the two elements
 * constituting the pair.
 * 
 * @author Olivier Cailloux
 * 
 * @param <E>
 *            the type of the elements constituting the pair.
 */
public final class HomogeneousPair<E> extends Pair<E, E> implements Iterable<E> {
    private static class HPairIterator<E> extends AbstractIterator<E> {
	private HomogeneousPair<E> m_hPair;
	/**
	 * -1 for left member, +1 for right member, 0 for end of data.
	 */
	private int m_current;

	public HPairIterator(HomogeneousPair<E> hPair) {
	    checkArgument(hPair != null);
	    m_hPair = hPair;
	    m_current = -1;
	}

	@Override
	protected E computeNext() {
	    if (m_current == -1) {
		m_current = 1;
		return m_hPair.getElt1();
	    }
	    if (m_current == 1) {
		m_current = 0;
		return m_hPair.getElt2();
	    }
	    assert (m_current == 0);
	    return endOfData();
	}
    }

    @Override
    public String toString() {
	return "Homogeneous pair-" + super.toString();
    }

    public HomogeneousPair(E elt1, E elt2) {
	super(elt1, elt2);
    }

    @Override
    public Iterator<E> iterator() {
	return new HPairIterator<E>(this);
    }

    static public <E> HomogeneousPair<E> createHomogeneous(E elt1, E elt2) {
	return new HomogeneousPair<E>(elt1, elt2);
    }
}
