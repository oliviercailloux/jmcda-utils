package org.decision_deck.utils.relation.graph;

import org.decision_deck.utils.relation.HomogeneousPair;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DirectedPseudograph;

public class RelationToDiGraphAdapter<V> extends DirectedPseudograph<V, HomogeneousPair<V>>
		implements DirectedGraph<V, HomogeneousPair<V>> {

	public RelationToDiGraphAdapter() {
		super(new EdgeFactory<V, HomogeneousPair<V>>() {
			@Override
			public HomogeneousPair<V> createEdge(V sourceVertex, V targetVertex) {
				return HomogeneousPair.createHomogeneous(sourceVertex, targetVertex);
			}
		});
	}

}
