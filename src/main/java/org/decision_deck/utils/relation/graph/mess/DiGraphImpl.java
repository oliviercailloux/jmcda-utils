package org.decision_deck.utils.relation.graph.mess;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.decision_deck.utils.relation.graph.Edge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 * TODO replace the Edge by Edge, which can't have {@code null} elements,
 * and has source and target.
 * 
 * @author Olivier Cailloux
 * 
 * @param <V> the vertex type.
 */
public class DiGraphImpl<V> implements DiGraph<V> {

	private final DefaultDirectedGraph<V, Edge<V>> m_graph;

	static public <V> DiGraphImpl<V> create() {
		return new DiGraphImpl<V>(new DefaultDirectedGraph<V, Edge<V>>(new GraphUtils.SimpleEdgeFactory<V>()));
	}

	static public <V> DiGraphImpl<V> copyOf(DiGraph<V> source) {
		final DiGraphImpl<V> g = new DiGraphImpl<V>(
				new DefaultDirectedGraph<V, Edge<V>>(new GraphUtils.SimpleEdgeFactory<V>()));
		GraphUtils.copyToBetter(source, g);
		return g;
	}

	/**
	 * Creates a new object delegating to the given graph.
	 * 
	 * @param graph not {@code null}, must be empty.
	 */
	public DiGraphImpl(DefaultDirectedGraph<V, Edge<V>> graph) {
		checkNotNull(graph);
		checkArgument(graph.vertexSet().isEmpty());
		m_graph = graph;
	}

	@Override
	public Edge<V> addEdge(V sourceVertex, V targetVertex) {
		return m_graph.addEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean addVertex(V v) {
		return m_graph.addVertex(v);
	}

	@Override
	public boolean containsEdge(V sourceVertex, V targetVertex) {
		return m_graph.containsEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean containsVertex(V v) {
		return m_graph.containsVertex(v);
	}

	@Override
	public Set<Edge<V>> edgeSet() {
		return m_graph.edgeSet();
	}

	@Override
	public Set<Edge<V>> edgesOf(V vertex) {
		return m_graph.edgesOf(vertex);
	}

	@Override
	public Edge<V> removeEdge(V sourceVertex, V targetVertex) {
		return m_graph.removeEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean removeVertex(V v) {
		return m_graph.removeVertex(v);
	}

	@Override
	public Set<V> vertexSet() {
		return m_graph.vertexSet();
	}

	@Override
	public Set<Edge<V>> incomingEdgesOf(V vertex) {
		return m_graph.incomingEdgesOf(vertex);
	}

	@Override
	public Set<Edge<V>> outgoingEdgesOf(V vertex) {
		return m_graph.outgoingEdgesOf(vertex);
	}

	@Override
	public DirectedGraph<V, Edge<V>> getGraph() {
		return m_graph;
	}

}
