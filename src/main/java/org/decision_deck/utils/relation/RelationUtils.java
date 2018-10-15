package org.decision_deck.utils.relation;

import java.util.Random;
import java.util.Set;

import org.decision_deck.utils.Pair;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class RelationUtils {

    public static <E> int getNbIncomparable(Preorder<E> r) {
	final Set<E> from = r.getFrom();
	final Set<E> to = r.getTo();
	int incomp = 0;
	for (E f : from) {
	    for (E t : to) {
		if (!r.contains(f, t) && !r.contains(t, f)) {
		    ++incomp;
		}
	    }
	}
	assert (incomp % 2 == 0);
	return incomp / 2;
    }

    public static <E> void completeRandomly(Preorder<E> p, long seed) {
	final Random r = new Random(seed);
	while (true) {
	    final Set<Pair<E, E>> i = getIncomp(p);
	    if (i.isEmpty()) {
		break;
	    }
	    final Pair<E, E> randomIncomp = Iterables.get(i, r.nextInt(i.size()));
	    final int order = r.nextInt(3);
	    switch (order) {
	    case 0:
		p.addEqTransitive(randomIncomp.getElt1(), randomIncomp.getElt2());
		break;
	    case 1:
		p.addTransitive(randomIncomp.getElt1(), randomIncomp.getElt2());
		break;
	    case 2:
		p.addTransitive(randomIncomp.getElt2(), randomIncomp.getElt1());
		break;
	    default:
		throw new IllegalStateException();
	    }
	}
    }

    public static <E> Set<Pair<E, E>> getIncomp(Preorder<E> p) {
	final Set<Pair<E, E>> incomp = Sets.newLinkedHashSet();
	for (E f : p.getFrom()) {
	    for (E t : p.getTo()) {
		if (!p.contains(f, t) && !p.contains(t, f) && !incomp.contains(Pair.create(t, f))) {
		    incomp.add(Pair.create(f, t));
		}
	    }
	}
	return incomp;
    }

}
