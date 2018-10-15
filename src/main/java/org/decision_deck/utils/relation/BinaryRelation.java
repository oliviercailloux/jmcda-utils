package org.decision_deck.utils.relation;

import java.util.Set;

import org.decision_deck.utils.Pair;

import com.google.common.collect.SetMultimap;

/**
 * <p>
 * A binary relation, in the mathematical sense, with a from set and a to set. An instance represents the relation that
 * holds between pairs. A pair, considered to be ordered, consist in an element of the from set and an element of the to
 * set. For each possible pair, the object indicates whether the relation holds.
 * </p>
 * <p>
 * The from and the to set may include elements that are in relation to no other objects.
 * </p>
 * <p>
 * A particular case, quite usual, is when the from set equals the to set.
 * </p>
 * <p>
 * TODO change iterable into collection?
 * </p>
 * <p>
 * Note that this is conceptually similar to {@link SetMultimap}, with added emphasis on symetry between from and to.
 * E.g. there is no equivalent of {@link #getTo()} in the SetMultimap.
 * </p>
 * 
 * @author Olivier Cailloux
 * 
 * @param <F>
 *            the from elements.
 * @param <T>
 *            the to elements.
 * 
 */
public interface BinaryRelation<F, T> extends Iterable<Pair<F, T>> {

    /**
     * <p>
     * Returns a view of the from set. The returned set is backed by the relation, thus changes to one are reflected to
     * the other.
     * </p>
     * <p>
     * The set contains at least all objects that are in relation with at least one object in the to set, but may be
     * larger than that.
     * </p>
     * <p>
     * If the returned set is empty, then this relation is necessarily empty. The converse does not necessarily hold.
     * </p>
     * 
     * @return not <code>null</code>.
     */
    public Set<F> getFrom();

    /**
     * <p>
     * Returns a view of the to set. The returned set is backed by the relation, thus changes to one are reflected to
     * the other.
     * </p>
     * <p>
     * The set contains at least all objects that are in relation with at least one object in the from set, but may be
     * larger than that.
     * </p>
     * <p>
     * If the returned set is empty, then this relation is necessarily empty. The converse does not necessarily hold.
     * </p>
     * 
     * @return not <code>null</code>.
     */
    public Set<T> getTo();

    /**
     * <p>
     * Returns the number of pairs that this relation considers, including the pairs that are not related. This is the
     * number of elements in the from set multiplied by the number of elements in the to set. This is necessarily
     * greater than or equal to the number of related pairs in this relation.
     * </p>
     * <p>
     * To get the number of related pairs in this relation, use {@link #asPairs()} and {@link Set#size()}.
     * </p>
     * 
     * @return the count of values.
     */
    public int getValueCount();

    /**
     * <p>
     * Checks whether this relation has an empty from and to set.
     * </p>
     * <p>
     * If this method returns <code>true</code>, this implies that this relation contains no related pair, but the
     * converse does not necessarily hold. To check whether this relation has no related pairs, use {@link #asPairs()}
     * and {@link Set#isEmpty()}.
     * </p>
     * 
     * @return <code>true</code> iff the relation has empty from and to sets.
     */
    public boolean isEmpty();

    /**
     * <p>
     * A binary relation equals an other one iff they contain equal from set, to set, and related pairs.
     * </p>
     * <p>
     * For this test, equality rather than identity of the elements from the from and the to set is used.
     * </p>
     * 
     */
    @Override
    public boolean equals(Object obj);

    /**
     * <p>
     * Retrieves the pairs that this object considers related. The set is backed by this object.
     * </p>
     * <p>
     * Adding a new pair to the set also adds the first element of the pair to the from set and the second element of
     * the pair to the to set. However, removing a pair does <em>not</em> modify the from and to set. Therefore, adding
     * a new element then removing it are not exact inverses from the point of view of this object. Add and remove
     * operations are indeed inverse operations from the point of view of the returned set.
     * </p>
     * 
     * @return not <code>null</code>.
     */
    public Set<Pair<F, T>> asPairs();

    /**
     * Indicates whether the given pair is related according to this relation. This is necessarily <code>false</code> if
     * the from element does not belong to the from set, or the to element does not belong to the to set.
     * 
     * @param from
     *            not <code>null</code>.
     * @param to
     *            not <code>null</code>.
     * @return whether the given <code>from</code> element is in relation with the <code>to</code> element.
     */
    public boolean contains(F from, T to);
}
