package org.decision_deck.utils.relation;

import java.util.Set;

/**
 * @param <F> the type used for the “from” elements.
 * @param <T> the type used for the “to” elements.
 * 
 */
public interface BinaryRelationTentative<F, T> {
	/**
	 * @return a view of the domain of this relation: all the elements x such that
	 *         for some y, (x, y) is in the relation.
	 */
	public Set<F> getFromSet();

	/**
	 * @return a view of the range of this relation: all the elements y such that
	 *         for some x, (x, y) is in the relation.
	 */
	public Set<T> getTo();

	/**
	 * @return the number of pairs that this relation contains.
	 */
	public int size();

	/**
	 * @return {@code true} iff the relation has empty from and to sets.
	 */
	public boolean isEmpty();

	/**
	 * A binary relation equals an other one iff they have equal from and to sets
	 * and for each (x, y) contained in one, (x, y) is contained in the other one.
	 */
	@Override
	public boolean equals(Object obj);

	/**
	 * @return whether the given {@code from} element is in relation with the
	 *         {@code to} element.
	 */
	public boolean contains(F from, T to);

	/**
	 * Optional operation.
	 */
	public boolean add(F from, T to);
}