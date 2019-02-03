package org.decision_deck.utils.relation;

import java.util.List;

import org.decision_deck.utils.Pair;

import com.google.common.base.Function;

public class PairToHomogeneous implements Function<Pair<List<Integer>, List<Integer>>, HomogeneousPair<List<Integer>>> {
	@Override
	public HomogeneousPair<List<Integer>> apply(Pair<List<Integer>, List<Integer>> input) {
		return new HomogeneousPair<List<Integer>>(input.getElt1(), input.getElt2());
	}
}