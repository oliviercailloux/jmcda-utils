package org.decision_deck.utils.relation.graph.mess;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Writer;
import java.util.NoSuchElementException;
import java.util.Set;

import org.decision_deck.utils.Pair;
import org.decision_deck.utils.PredicateUtils;
import org.decision_deck.utils.matrix.ForwardingSparseMatrixRead;
import org.decision_deck.utils.matrix.Matrixes;
import org.decision_deck.utils.matrix.SparseMatrixD;
import org.decision_deck.utils.matrix.SparseMatrixDRead;
import org.decision_deck.utils.matrix.SparseMatrixFuzzy;
import org.decision_deck.utils.matrix.SparseMatrixFuzzyRead;
import org.decision_deck.utils.matrix.ValidatingDecoratedMatrix;
import org.decision_deck.utils.matrix.mess.IMatrixBinary;
import org.decision_deck.utils.relation.BinaryRelation;
import org.decision_deck.utils.relation.BinaryRelationImpl;
import org.decision_deck.utils.relation.graph.Edge;
import org.decision_deck.utils.relation.graph.Preorder;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.TransitiveClosure;
import org.jgrapht.ext.IntegerNameProvider;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.SimpleDirectedGraph;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.CharSink;

/**
 * This is a real mess. Stay away.
 *
 * @author Olivier Cailloux
 *
 */
public class GraphUtils {
	/**
	 * Caution. The table view is not implemented correctly.
	 *
	 * @author Olivier Cailloux
	 *
	 * @param <R>
	 *            the row type.
	 * @param <C>
	 *            the column type.
	 */
	static public class BinaryMatrixNew<R, C> extends ForwardingSparseMatrixRead<R, C>
			implements IMatrixBinary<R, C>, SparseMatrixDRead<R, C> {

		/**
		 * Creates a new matrix ensuring every element it contains is between
		 * zero and one.
		 *
		 */
		public BinaryMatrixNew() {
			super(Matrixes.<R, C> newValidating(PredicateUtils.inBetween(0d, 1d)));
		}

		/**
		 * Creates a new matrix decorating the given matrix by ensuring every
		 * element it contains is zero or one.
		 *
		 * @param delegate
		 *            not <code>null</code>, must be empty.
		 */
		public BinaryMatrixNew(SparseMatrixD<R, C> delegate) {
			super(new ValidatingDecoratedMatrix<R, C>(delegate,
					Predicates.in(Sets.newHashSet(Double.valueOf(0d), Double.valueOf(1d)))));
		}

		@Override
		public boolean getBooleanValue(R row, C column) {
			final Double dbl = delegate().getEntry(row, column);
			if (dbl == null) {
				checkArgument(getRows().contains(row) && getColumns().contains(column));
				return false;
			}
			if (dbl.doubleValue() == 1d) {
				return true;
			}
			if (dbl.doubleValue() == 0d) {
				return false;
			}
			throw new IllegalArgumentException("Unexpected value at position " + row + ", " + column + ".");
		}

		@Override
		public Double getEntry(R row, C column) {
			return getBooleanValue(row, column) ? Double.valueOf(1d) : Double.valueOf(0d);
		}

		@Override
		public void put(R row, C column, boolean value) {
			delegate().put(row, column, value ? 1d : 0d);
		}

		@Override
		protected SparseMatrixD<R, C> delegate() {
			return (SparseMatrixD<R, C>) super.delegate();
		}
	}

	static public class SimpleEdgeFactory<V> implements EdgeFactory<V, Edge<V>> {
		@Override
		public Edge<V> createEdge(V sourceVertex, V targetVertex) {
			checkNotNull(sourceVertex);
			checkNotNull(targetVertex);
			return new Edge<V>(sourceVertex, targetVertex);
		}
	}

	static public <V> void addClique(DiGraph<V> target, Set<V> vertices) {
		for (V v1 : vertices) {
			target.addVertex(v1);
			for (V v2 : vertices) {
				target.addVertex(v2);
				target.addEdge(v1, v2);
				target.addEdge(v2, v1);
			}
		}
	}

	static public <V, E> void addClique(DirectedGraph<V, E> target, Set<V> vertices) {
		for (V v1 : vertices) {
			target.addVertex(v1);
			for (V v2 : vertices) {
				target.addVertex(v2);
				target.addEdge(v1, v2);
			}
		}
	}

	static public <V> void addEdges(DiGraph<V> target, Set<V> from, Set<V> to) {
		for (V v1 : from) {
			target.addVertex(v1);
			for (V v2 : to) {
				target.addVertex(v2);
				target.addEdge(v1, v2);
			}
		}
	}

	static public <V, E> void addEdges(DirectedGraph<V, E> target, Set<V> from, Set<V> to) {
		for (V v1 : from) {
			target.addVertex(v1);
			for (V v2 : to) {
				target.addVertex(v2);
				target.addEdge(v1, v2);
			}
		}
	}

	/**
	 * Adds the specified source and target vertices to the graph, if not
	 * already included, and creates a new edge and adds it to the specified
	 * graph similarly to the {@link DiGraph#addEdge} method.
	 *
	 * @param <V>
	 *            the vertex type.
	 *
	 * @param g
	 *            not <code>null</code>.
	 * @param sourceVertex
	 *            not <code>null</code>.
	 * @param targetVertex
	 *            not <code>null</code>.
	 *
	 * @return The newly created edge if added to the graph, otherwise
	 *         <code>null</code>.
	 */
	public static <V> Edge<V> addEdgeWithVertices(DiGraph<V> g, V sourceVertex, V targetVertex) {
		checkNotNull(g);
		checkNotNull(sourceVertex);
		checkNotNull(targetVertex);
		g.addVertex(sourceVertex);
		g.addVertex(targetVertex);

		return g.addEdge(sourceVertex, targetVertex);
	}

	static public <V, E> void addLoops(DefaultDirectedGraph<V, E> target) {
		for (V vertex : target.vertexSet()) {
			target.addEdge(vertex, vertex);
		}
	}

	static public <V> void computeTransitiveClosure(DiGraph<V> orig) {
		TransitiveClosureNew.INSTANCE.closeSimpleDirectedGraph(orig);
	}

	static public <V> void computeTransitiveReduct(BinaryRelation<V, V> relation) {
		assert (getTransitiveClosure(relation).asPairs().equals(relation.asPairs()));
		// final Set<V> cycles = new CycleDetector<V, E>(g).findCycles();
		// checkArgument(cycles.isEmpty(), cycles);
		// final Set<V> vertices = ImmutableSet.copyOf(g.vertexSet());
		final SetView<V> vertices = Sets.union(relation.getFrom(), relation.getTo());
		for (V v1 : vertices) {
			for (V v2 : Sets.difference(vertices, ImmutableSet.of(v1))) {
				for (V v3 : Sets.difference(vertices, ImmutableSet.of(v1, v2))) {
					if (relation.contains(v1, v2) && relation.contains(v2, v3) && relation.contains(v1, v3)) {
						// final boolean hasRev1 = g.containsEdge(v2, v1);
						// final boolean hasRev2 = g.containsEdge(v3, v2);
						// final boolean hasRev3 = g.containsEdge(v3, v1);
						// if (hasRev1 != hasRev2 || hasRev1 != hasRev3) {
						// throw new IllegalArgumentException("Found cycle in "
						// + v1 + v2 + v3 + ".");
						// }
						// if (!hasRev1) {
						final boolean removed = relation.asPairs().remove(Pair.create(v1, v3));
						assert (removed);
						// }
					}
				}
			}
		}
		// final SimpleDirectedGraph<V, Edge<V>> g =
		// GraphUtils.getSimpleDiGraph(relation);
		// transitiveReduct(g);
		// final BinaryRelation<V, V> reducedRelation =
		// GraphUtils.getBinaryRelation(g);
	}

	static public <V, E> void copyTo(Graph<V, E> source, Graph<V, E> target) {
		for (V vertex : source.vertexSet()) {
			target.addVertex(vertex);
		}
		for (E edge : source.edgeSet()) {
			final boolean added = target.addEdge(source.getEdgeSource(edge), source.getEdgeTarget(edge), edge);
			if (!added) {
				throw new IllegalArgumentException("Target graph does not support addition of (some) source edges.");
			}
		}
	}

	static public <V> void copyToBetter(DiGraph<V> source, DiGraph<V> target) {
		for (V vertex : source.vertexSet()) {
			target.addVertex(vertex);
		}
		for (Edge<V> edge : source.edgeSet()) {
			final Edge<V> added = target.addEdge(edge.getSource(), edge.getTarget());
			if (added == null) {
				throw new IllegalArgumentException("Problem copying " + edge + ".");
			}
		}
	}

	static public <V> void export(DiGraph<V> g, CharSink dest) throws IOException {
		final DOTExporterTemp<V, Edge<V>> exporter = new DOTExporterTemp<V, Edge<V>>(new IntegerNameProvider<V>(),
				new StringNameProvider<V>(), null);
		try (Writer writer = dest.openBufferedStream()) {
			exporter.export(writer, g.getGraph());
		}
	}

	static public <V> BinaryRelation<V, V> getBinaryRelation(DirectedGraph<V, Edge<V>> source) {
		final BinaryRelation<V, V> relation = new BinaryRelationImpl<V, V>();
		final Set<V> vertexSet = source.vertexSet();
		for (V v : vertexSet) {
			final Set<Edge<V>> edges = source.outgoingEdgesOf(v);
			for (Edge<V> edge : edges) {
				final Pair<V, V> pair = Pair.create(edge.getSource(), edge.getTarget());
				relation.asPairs().add(pair);
			}
		}
		return relation;
	}

	static public <V> DefaultDirectedGraph<V, Edge<V>> getDefaultDiGraph(BinaryRelation<V, V> source) {
		final SimpleEdgeFactory<V> factory = new SimpleEdgeFactory<V>();
		final DefaultDirectedGraph<V, Edge<V>> g = new DefaultDirectedGraph<V, Edge<V>>(factory);
		final Set<Pair<V, V>> pairs = source.asPairs();
		for (Pair<V, V> pair : pairs) {
			g.addVertex(pair.getElt1());
			g.addVertex(pair.getElt2());
			g.addEdge(pair.getElt1(), pair.getElt2());
		}

		return g;
	}

	static public <V> DefaultDirectedGraph<V, Edge<V>> getDiGraph(IMatrixBinary<V, V> source) {
		final SimpleEdgeFactory<V> factory = new SimpleEdgeFactory<V>();
		final DefaultDirectedGraph<V, Edge<V>> g = new DefaultDirectedGraph<V, Edge<V>>(factory);
		for (V v1 : source.getRows()) {
			for (V v2 : source.getColumns()) {
				if (source.getBooleanValue(v1, v2)) {
					g.addEdge(v1, v2);
				}
			}
		}

		return g;
	}

	/**
	 *
	 * @param <V>
	 *            the type of the elements.
	 * @param preorder
	 *            not <code>null</code>.
	 * @return not <code>null</code>, a graph representing the given preorder.
	 */
	static public <V> DefaultDirectedGraph<V, Edge<V>> getDiGraph(Preorder<V> preorder) {
		// final SparseMatrixFuzzy<E, E> res = MatrixUtils.newZeroToOne();
		// final Set<E> elements = p1.getElements();
		// for (E e1 : elements) {
		// for (E e2 : elements) {
		// if ((p1.compare(e1, e2) >= 0)) {
		// res.put(e1, e2, 1d);
		// }
		// }
		// }
		// return getDiGraph(res);
		final SimpleEdgeFactory<V> factory = new SimpleEdgeFactory<V>();
		final DefaultDirectedGraph<V, Edge<V>> g = new DefaultDirectedGraph<V, Edge<V>>(factory);
		final PeekingIterator<Set<V>> iterator = Iterators.peekingIterator(preorder.asListOfSets().iterator());
		while (iterator.hasNext()) {
			final Set<V> clique = iterator.next();
			addClique(g, clique);
			Set<V> next;
			try {
				next = iterator.peek();
			} catch (NoSuchElementException exc) {
				continue;
			}
			addEdges(g, clique, next);
		}
		return g;
	}

	static public <V> DefaultDirectedWeightedGraph<V, Edge<V>> getDiGraph(SparseMatrixFuzzyRead<V, V> source) {
		final SimpleEdgeFactory<V> factory = new SimpleEdgeFactory<V>();
		final DefaultDirectedWeightedGraph<V, Edge<V>> g = new DefaultDirectedWeightedGraph<V, Edge<V>>(factory);
		for (V v1 : source.getRows()) {
			for (V v2 : source.getColumns()) {
				final Double entry = source.getEntry(v1, v2);
				if (entry != null) {
					// Graphs.addEdgeWithVertices(g, v1, v2,
					// entry.doubleValue());
					Graphs.addEdgeWithVertices(g, v1, v2);
				}
			}
		}

		return g;
	}

	/**
	 * TODO param should be binary as the values are not used.
	 *
	 * @param <V>
	 *            vertex type.
	 * @param source
	 *            not <code>null</code>.
	 * @return not <code>null</code>.
	 */
	static public <V> DiGraph<V> getDiGraphBetter(SparseMatrixFuzzyRead<V, V> source) {
		final DiGraphImpl<V> g = DiGraphImpl.create();
		for (V v1 : source.getRows()) {
			for (V v2 : source.getColumns()) {
				final Double entry = source.getEntry(v1, v2);
				if (entry == null) {
					continue;
				}
				if (entry.doubleValue() <= 0.0001d) {
					continue;
				}
				if (entry.doubleValue() >= 0.9999d) {
					addEdgeWithVertices(g, v1, v2);
				} else {
					throw new IllegalArgumentException("Value " + entry + ".");
				}
			}
		}

		return g;
	}

	/**
	 *
	 * @param <V>
	 *            the type of the elements.
	 * @param preorder
	 *            not <code>null</code>.
	 * @return not <code>null</code>, a graph representing the given preorder,
	 *         with none of the edges that can be deduced from the transitivity
	 *         property.
	 */
	static public <V> DiGraph<V> getDiGraphBetterWithTransitiveReduct(Preorder<V> preorder) {
		final DiGraphImpl<V> g = DiGraphImpl.create();
		final PeekingIterator<Set<V>> iterator = Iterators.peekingIterator(preorder.asListOfSets().iterator());
		while (iterator.hasNext()) {
			final Set<V> clique = iterator.next();
			addClique(g, clique);
			Set<V> next;
			try {
				next = iterator.peek();
			} catch (NoSuchElementException exc) {
				continue;
			}
			addEdges(g, clique, next);
		}
		return g;
	}

	/**
	 *
	 * @param <V>
	 *            the type of the elements.
	 * @param preorder
	 *            not <code>null</code>.
	 * @return not <code>null</code>, a graph representing the given preorder.
	 */
	static public <V> DiGraph<V> getDiGraphWithTransitiveEdgesButNoLoops(Preorder<V> preorder) {
		final DiGraphImpl<V> g = DiGraphImpl.create();
		final PeekingIterator<Set<V>> iterator = Iterators.peekingIterator(preorder.asListOfSets().iterator());
		while (iterator.hasNext()) {
			final Set<V> clique = iterator.next();
			addClique(g, clique);
			Set<V> next;
			try {
				next = iterator.peek();
			} catch (NoSuchElementException exc) {
				continue;
			}
			addEdges(g, clique, next);
		}
		computeTransitiveClosure(g);
		return g;
	}

	/**
	 * Retrieves the set of ordered pairs that are connected differently in the
	 * two given graphs. (make it more precise!)
	 *
	 * @param <V>
	 *            the vertex type.
	 * @param g1
	 *            not <code>null</code>.
	 * @param g2
	 *            not <code>null</code>.
	 * @return not <code>null</code>, may be empty. A set of immutable sets of
	 *         size two.
	 */
	static public <V> Set<Edge<V>> getDisagreements(DiGraph<V> g1, DiGraph<V> g2) {
		final SetView<V> vertices = Sets.union(g1.vertexSet(), g2.vertexSet());
		// final FloydWarshallShortestPaths<V, Edge<V>> shortest1 = new
		// FloydWarshallShortestPaths<V, Edge<V>>(
		// g1.getGraph());
		// final FloydWarshallShortestPaths<V, Edge<V>> shortest2 = new
		// FloydWarshallShortestPaths<V, Edge<V>>(
		// g2.getGraph());
		// final StrongConnectivityInspector<V, Edge<V>> inspector1 = new
		// StrongConnectivityInspector<V, Edge<V,
		// V>>(g1.getGraph());
		// final StrongConnectivityInspector<V, Edge<V>> inspector2 = new
		// StrongConnectivityInspector<V, Edge<V,
		// V>>(g2.getGraph());
		final Set<Edge<V>> disagreements = Sets.newLinkedHashSet();
		for (V source : vertices) {
			for (V target : vertices) {
				// final boolean ab = shortest1.getShortestPath(source, target)
				// != null;
				// final boolean ba = shortest1.getShortestPath(target, source)
				// != null;
				final boolean ab = g1.containsEdge(source, target);
				final boolean ab2 = g2.containsEdge(source, target);

				if (ab != ab2) {
					disagreements.add(Edge.create(source, target));
				}
			}
		}
		return disagreements;
	}

	/**
	 * Computes a relation so that the row element is greater or equal to the
	 * column element iff both preorders say so. This is not necessarily a
	 * complete relation. The result is transitive. TODO this should rather be a
	 * graph...
	 *
	 * @param <E>
	 *            the type of the elements.
	 * @param p1
	 *            not <code>null</code>.
	 * @param p2
	 *            not <code>null</code>.
	 * @return not <code>null</code>, a matrix which contains as rows and as
	 *         columns the elements in the given preorders.
	 */
	static public <E> SparseMatrixFuzzyRead<E, E> getIntersection(Preorder<E> p1, Preorder<E> p2) {
		final SparseMatrixFuzzy<E, E> res = Matrixes.newSparseFuzzy();
		checkArgument(p1.asSet().equals(p2.asSet()));
		final Set<E> elements = p1.asSet();
		for (E e1 : elements) {
			for (E e2 : elements) {
				final boolean geq1 = p1.compare(e1, e2) >= 0;
				final boolean geq2 = p2.compare(e1, e2) >= 0;
				if (geq1 && geq2) {
					res.put(e1, e2, 1d);
				}
			}
		}
		return res;
	}

	static public <V> SimpleDirectedGraph<V, Edge<V>> getSimpleDiGraph(BinaryRelation<V, V> source) {
		final SimpleEdgeFactory<V> factory = new SimpleEdgeFactory<V>();
		final SimpleDirectedGraph<V, Edge<V>> g = new SimpleDirectedGraph<V, Edge<V>>(factory);
		final Set<Pair<V, V>> pairs = source.asPairs();
		for (Pair<V, V> pair : pairs) {
			g.addVertex(pair.getElt1());
			g.addVertex(pair.getElt2());
			g.addEdge(pair.getElt1(), pair.getElt2());
		}

		return g;
	}

	/**
	 * Retrieves the set of unordered pairs that are connected differently in
	 * the two given graphs. Only the pairs which are such that g1 has a path
	 * from source to target and g2 has a path from target to source, or
	 * conversely, are reported. Thus this method does not consider it a
	 * disagreement if the first graph has no links between some a and b and the
	 * second graph has, for example. Both graphs do not have to contain the
	 * same vertices, and they do not have to be transitive or anything similar.
	 *
	 * @param <V>
	 *            the vertex type.
	 * @param g1
	 *            not <code>null</code>.
	 * @param g2
	 *            not <code>null</code>.
	 * @return not <code>null</code>, may be empty. A set of immutable sets of
	 *         size two.
	 */
	static public <V> Set<Set<V>> getStrongDisagreements(DiGraph<V> g1, DiGraph<V> g2) {
		final SetView<V> vertices = Sets.union(g1.vertexSet(), g2.vertexSet());
		// final FloydWarshallShortestPaths<V, Edge<V>> shortest1 = new
		// FloydWarshallShortestPaths<V, Edge<V>>(
		// g1.getGraph());
		// final FloydWarshallShortestPaths<V, Edge<V>> shortest2 = new
		// FloydWarshallShortestPaths<V, Edge<V>>(
		// g2.getGraph());
		// final StrongConnectivityInspector<V, Edge<V>> inspector1 = new
		// StrongConnectivityInspector<V, Edge<V,
		// V>>(g1.getGraph());
		// final StrongConnectivityInspector<V, Edge<V>> inspector2 = new
		// StrongConnectivityInspector<V, Edge<V,
		// V>>(g2.getGraph());
		final Set<Set<V>> disagreements = Sets.newLinkedHashSet();
		for (V source : vertices) {
			for (V target : vertices) {
				// final boolean ab = shortest1.getShortestPath(source, target)
				// != null;
				// final boolean ba = shortest1.getShortestPath(target, source)
				// != null;
				final boolean ab = g1.containsEdge(source, target);
				final boolean ba = g1.containsEdge(target, source);
				if (ab == ba) {
					/**
					 * a and b considered equivalent by the first graph, thus no
					 * disagreement is possible here.
					 */
					continue;
				}
				// final boolean ab2 = shortest2.getShortestPath(source, target)
				// != null;
				// final boolean ba2 = shortest2.getShortestPath(target, source)
				// != null;
				final boolean ab2 = g2.containsEdge(source, target);
				final boolean ba2 = g2.containsEdge(target, source);
				if (ab2 == ba2) {
					continue;
				}
				/** Just to make it clear. */
				assert (ab != ba);
				assert (ab2 != ba2);
				assert ((ab == ab2 && ba == ba2) || (ab != ab2 && ba != ba2));

				if (ab != ab2) {
					disagreements.add(ImmutableSet.of(source, target));
				}
			}
		}
		return disagreements;
	}

	static public <V> BinaryRelation<V, V> getTransitiveClosure(BinaryRelation<V, V> relation) {
		final SimpleDirectedGraph<V, Edge<V>> g = GraphUtils.getSimpleDiGraph(relation);
		TransitiveClosure.INSTANCE.closeSimpleDirectedGraph(g);
		final BinaryRelation<V, V> transitiveRelation = GraphUtils.getBinaryRelation(g);
		return transitiveRelation;
	}

	static public <V> boolean isTransitive(DiGraph<V> g) {
		final DiGraphImpl<V> g2 = DiGraphImpl.copyOf(g);
		computeTransitiveClosure(g2);
		return g2.edgeSet().size() == g.edgeSet().size();
	}

	static public <E> void removeLoops(BinaryRelation<E, E> r) {
		for (E e : Sets.union(r.getFrom(), r.getTo())) {
			final boolean removed = r.asPairs().remove(Pair.create(e, e));
			checkArgument(removed);
		}
	}

	static public <V> void removeLoops(DiGraph<V> g) {
		for (V v : g.vertexSet()) {
			g.removeEdge(v, v);
		}
	}

	static public <V, E> void removeLoops(Graph<V, E> g) {
		for (V v : g.vertexSet()) {
			g.removeEdge(v, v);
		}
	}

	static public <V, E> void transitiveReduct(DirectedGraph<V, E> g) {
		final Set<V> cycles = new CycleDetector<V, E>(g).findCycles();
		checkArgument(cycles.isEmpty(), cycles);
		// final Set<V> vertices = ImmutableSet.copyOf(g.vertexSet());
		for (V v1 : g.vertexSet()) {
			for (V v2 : Sets.difference(g.vertexSet(), ImmutableSet.of(v1))) {
				for (V v3 : Sets.difference(g.vertexSet(), ImmutableSet.of(v1, v2))) {
					if (g.containsEdge(v1, v2) && g.containsEdge(v2, v3) && g.containsEdge(v1, v3)) {
						// final boolean hasRev1 = g.containsEdge(v2, v1);
						// final boolean hasRev2 = g.containsEdge(v3, v2);
						// final boolean hasRev3 = g.containsEdge(v3, v1);
						// if (hasRev1 != hasRev2 || hasRev1 != hasRev3) {
						// throw new IllegalArgumentException("Found cycle in "
						// + v1 + v2 + v3 + ".");
						// }
						// if (!hasRev1) {
						g.removeEdge(v1, v3);
						// }
					}
				}
			}
		}
	}
}
