package org.decision_deck.utils.relation.graph.mess;

import java.util.HashSet;
import java.util.Set;

import org.decision_deck.utils.relation.graph.Edge;

/**
 * Constructs the transitive closure of the input graph.
 * 
 * @author Vinayak R. Borkar
 * @since May 5, 2007
 */
public class TransitiveClosureNew {
	// ~ Static fields/initializers ---------------------------------------------

	/**
	 * Singleton instance.
	 */
	public static final TransitiveClosureNew INSTANCE = new TransitiveClosureNew();

	// ~ Constructors -----------------------------------------------------------

	private TransitiveClosureNew() {
		/**
		 * Private Constructor.
		 */
	}

	// ~ Methods ----------------------------------------------------------------

	/**
	 * Computes the transitive closure of the given graph. TOD currently, the graph
	 * should have no loop.
	 * 
	 * @param       <V> the vertex type.
	 * 
	 * @param graph - Graph to compute transitive closure for.
	 */
	public <V> void closeSimpleDirectedGraph(DiGraph<V> graph) {
		Set<V> vertexSet = graph.vertexSet();

		Set<V> newEdgeTargets = new HashSet<V>();

		// At every iteration of the outer loop, we add a path of length 1
		// between nodes that originally had a path of length 2. In the worst
		// case, we need to make floor(log |V|) + 1 iterations. We stop earlier
		// if there is no change to the output graph.

		int bound = computeBinaryLog(vertexSet.size());
		boolean done = false;
		for (int i = 0; !done && (i < bound); ++i) {
			done = true;
			for (V v1 : vertexSet) {
				newEdgeTargets.clear();

				for (Edge<V> v1OutEdge : graph.outgoingEdgesOf(v1)) {
					V v2 = v1OutEdge.getTarget();
					for (Edge<V> v2OutEdge : graph.outgoingEdgesOf(v2)) {
						V v3 = v2OutEdge.getTarget();

						if (v1.equals(v3)) {
							// Its a simple graph, so no self loops.
							continue;
						}

						if (graph.containsEdge(v1, v3)) {
							// There is already an edge from v1 ---> v3, skip;
							continue;
						}

						newEdgeTargets.add(v3);
						done = false;
					}
				}

				for (V v3 : newEdgeTargets) {
					graph.addEdge(v1, v3);
				}
			}
		}
	}

	/**
	 * Computes floor(log_2(n)) + 1
	 * 
	 * @param n n
	 * @return floor(log_2(n)) + 1
	 */
	private int computeBinaryLog(int n) {
		assert n >= 0;

		int result = 0;
		int n2 = n;
		while (n2 > 0) {
			n2 >>= 1;
			++result;
		}

		return result;
	}
}