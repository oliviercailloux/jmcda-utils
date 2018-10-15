package org.decision_deck.utils.relation.graph.mess;

import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;

import org.decision_deck.utils.relation.graph.Preorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

@SuppressWarnings("deprecation")
public class GraphUtilsFirst {
    /**
     * The result does contain the transitive part. TODO remove this method.
     * 
     * @param <E1>
     *            a type
     * @param preorder
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public <E1> Set<PairN<E1, E1>> getStrictlyBetterTransitiveClosure(Preorder<E1> preorder) {
	final Set<PairN<E1, E1>> pairs = Sets.newLinkedHashSet();
	final NavigableSet<Integer> ranks = preorder.getRanks();
	for (Integer betterRank : ranks.headSet(ranks.last())) {
	    final Set<E1> betterElements = preorder.get(betterRank.intValue());
	    for (E1 betterElement : betterElements) {
		for (Integer worstRank : ranks.tailSet(betterRank, false)) {
		    final Set<E1> worstElements = preorder.get(worstRank.intValue());
		    for (E1 worstElement : worstElements) {
			pairs.add(new PairN<E1, E1>(betterElement, worstElement));
		    }
		}
	    }
	}
	return pairs;
    }

    static public <E> Set<PairN<E, E>> getTransitiveClosure_broken(Set<PairN<E, E>> relation) {
	final Set<PairN<E, E>> pairs = Sets.newLinkedHashSet();
	for (PairN<E, E> start : relation) {
	    pairs.add(start);
	    final E middle = start.getElt2();
	    for (PairN<E, E> end : relation) {
		final E middleEnd = end.getElt1();
		if (middle.equals(middleEnd)) {
		    pairs.add(new PairN<E, E>(start.getElt1(), end.getElt2()));
		}
	    }
	}
	return pairs;
    }

    /**
     * The result does not contain the transitive part.
     * 
     * @param <E1>
     *            a type
     * @param preorder
     *            not <code>null</code>.
     * @return not <code>null</code>.
     */
    static public <E1> Set<PairN<E1, E1>> getStrictlyBetter(Preorder<E1> preorder) {
	final Set<PairN<E1, E1>> pairs = Sets.newLinkedHashSet();
	final NavigableSet<Integer> ranks = preorder.getRanks();
	for (Integer betterRank : ranks.headSet(ranks.last())) {
	    final Set<E1> betterElements = preorder.get(betterRank.intValue());
	    for (E1 betterElement : betterElements) {
		final Integer worstRank = ranks.higher(betterRank);
		final Set<E1> worstElements = preorder.get(worstRank.intValue());
		for (E1 worstElement : worstElements) {
		    pairs.add(new PairN<E1, E1>(betterElement, worstElement));
		}
	    }
	}
	return pairs;
    }

    static public <E1, E2> Set<PairN<E1, E2>> getSymetricPart(Set<PairN<E1, E2>> relation) {
	final HashSet<PairN<E1, E2>> symetric = Sets.newHashSet();
	for (PairN<E1, E2> pair1 : relation) {
	    for (PairN<E1, E2> pair2 : relation) {
		if (pair1.getElt1().equals(pair2.getElt2()) && pair1.getElt2().equals(pair2.getElt1())) {
		    symetric.add(pair1);
		    symetric.add(pair2);
		}
	    }
	}
	return symetric;
    }

    private static final Logger s_logger = LoggerFactory.getLogger(GraphUtilsFirst.class);

    static public <E1, E2> boolean isAsymetric(Set<PairN<E1, E2>> relation) {
        for (PairN<E1, E2> pair1 : relation) {
            for (PairN<E1, E2> pair2 : relation) {
        	if (pair1.getElt1().equals(pair2.getElt2()) && pair1.getElt2().equals(pair2.getElt1())) {
        	    s_logger.warn("Asymetric: {}, {}.", pair1, pair2);
        	    return false;
        	}
            }
        }
        return true;
    }
    
}
