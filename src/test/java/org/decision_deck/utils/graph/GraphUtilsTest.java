package org.decision_deck.utils.graph;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.Set;

import org.decision_deck.utils.relation.graph.Preorder;
import org.decision_deck.utils.relation.graph.mess.DiGraph;
import org.decision_deck.utils.relation.graph.mess.DiGraphImpl;
import org.decision_deck.utils.relation.graph.mess.GraphUtils;
import org.decision_deck.utils.relation.graph.mess.GraphUtilsFirst;
import org.decision_deck.utils.relation.graph.mess.PairN;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

@SuppressWarnings("deprecation")
public class GraphUtilsTest {
    @Test
    public void testDisagreements() throws Exception {
	assertEquals(ImmutableSet.of(), GraphUtils.getStrongDisagreements(getStraight(), getNice()));

	assertEquals(ImmutableSet.of(ImmutableSet.of("b", "c")),
		GraphUtils.getStrongDisagreements(getStraight(), getDisagreeing()));

	assertEquals(ImmutableSet.of(ImmutableSet.of("b", "c")),
		GraphUtils.getStrongDisagreements(getStraight(), getDisagreeingExtended()));

	assertEquals(ImmutableSet.of(ImmutableSet.of("b", "d")),
		GraphUtils.getStrongDisagreements(getStraight(), getDisagreeingStrongly()));

	assertEquals(ImmutableSet.of(ImmutableSet.of("b", "d"), ImmutableSet.of("c", "d")),
		GraphUtils.getStrongDisagreements(getStraight(), getDisagreeingStronglyExtended()));
    }

    private DiGraph<String> getStraight() {
	final DiGraphImpl<String> g = DiGraphImpl.create();
	g.addVertex("a");
	g.addVertex("b");
	g.addVertex("c");
	g.addVertex("d");
	g.addEdge("a", "b");
	g.addEdge("b", "c");
	g.addEdge("c", "d");
	g.addEdge("a", "c");
	g.addEdge("a", "d");
	g.addEdge("b", "d");
	return g;
    }

    private DiGraph<String> getNice() {
	final DiGraphImpl<String> g = DiGraphImpl.create();
	g.addVertex("a");
	g.addVertex("b");
	g.addVertex("c");
	g.addVertex("d");
	g.addEdge("a", "b");
	g.addEdge("b", "a");
	g.addEdge("a", "c");
	g.addEdge("b", "c");
	g.addEdge("c", "d");
	return g;
    }

    private DiGraph<String> getDisagreeingStronglyExtended() {
	final DiGraphImpl<String> g = DiGraphImpl.create();
	g.addVertex("a");
	g.addVertex("b");
	g.addVertex("c");
	g.addVertex("d");
	g.addEdge("a", "d");
	g.addEdge("d", "a");
	g.addEdge("a", "b");
	g.addEdge("d", "b");
	g.addEdge("b", "c");
	g.addEdge("d", "c");
	return g;
    }

    private DiGraph<String> getDisagreeingExtended() {
	final DiGraphImpl<String> g = DiGraphImpl.create();
	g.addVertex("a");
	g.addVertex("b");
	g.addVertex("c");
	g.addVertex("d");
	g.addEdge("a", "c");
	g.addEdge("c", "a");
	g.addEdge("a", "b");
	g.addEdge("c", "b");
	g.addEdge("b", "d");
	g.addEdge("a", "d");
	return g;
    }

    private DiGraph<String> getDisagreeing() {
	final DiGraphImpl<String> g = DiGraphImpl.create();
	g.addVertex("a");
	g.addVertex("b");
	g.addVertex("c");
	g.addVertex("d");
	g.addEdge("a", "c");
	g.addEdge("c", "a");
	g.addEdge("a", "b");
	g.addEdge("c", "b");
	g.addEdge("b", "d");
	return g;
    }

    private DiGraph<String> getDisagreeingStrongly() {
	final DiGraphImpl<String> g = DiGraphImpl.create();
	g.addVertex("a");
	g.addVertex("b");
	g.addVertex("c");
	g.addVertex("d");
	g.addEdge("a", "d");
	g.addEdge("d", "a");
	g.addEdge("a", "b");
	g.addEdge("d", "b");
	g.addEdge("b", "c");
	return g;
    }

    private static final Logger s_logger = LoggerFactory.getLogger(GraphUtilsTest.class);

    @Test
    public void testStrict() throws Exception {
	final Preorder<Object> preorder = new Preorder<Object>();
	final Object o1 = "o1"; // rank 1
	final Object o2 = "o2"; // rank 2
	final Object o3 = "o3"; // rank 4
	final Object o3bis = "o3bis"; // rank 4
	final Object o4 = "o4"; // rank 7

	preorder.put(o1, 1);
	preorder.put(o2, 2);
	preorder.put(o3, 3);
	preorder.put(o4, 4);
	preorder.put(o3bis, 3);

	final Set<PairN<Object, Object>> strict = GraphUtilsFirst.getStrictlyBetter(preorder);
	assertEquals(5, strict.size());
	final Iterator<PairN<Object, Object>> iterator = strict.iterator();
	assertEquals(new PairN<Object, Object>(o1, o2), iterator.next());
	assertEquals(new PairN<Object, Object>(o2, o3), iterator.next());
	assertEquals(new PairN<Object, Object>(o2, o3bis), iterator.next());
	assertEquals(new PairN<Object, Object>(o3, o4), iterator.next());
	assertEquals(new PairN<Object, Object>(o3bis, o4), iterator.next());
    }

    @Test
    public void testTransitiveClosure() throws Exception {
	final Preorder<Object> preorder = new Preorder<Object>();
	final Object o1 = "o1"; // rank 1
	final Object o2 = "o2"; // rank 2
	final Object o3 = "o3"; // rank 4
	final Object o3bis = "o3bis"; // rank 4
	final Object o4 = "o4"; // rank 7

	preorder.put(o2, 1);
	preorder.putAsLowest(o3);
	preorder.putAllAsLowest(ImmutableSet.of(o3, o3bis));
	preorder.put(o4, 3);
	preorder.putAsHighest(o1);
	s_logger.info("Preorder: {}.", preorder);

	// final Set<Pair<Object, Object>> strict = GraphUtils.getStrictlyBetter(preorder);
	// final Set<Pair<Object, Object>> extendedStrict = GraphUtils.getTransitiveClosure(strict);
	final Set<PairN<Object, Object>> extendedStrict = GraphUtilsFirst.getStrictlyBetterTransitiveClosure(preorder);

	assertEquals("too many: " + extendedStrict, 9, extendedStrict.size());

	final Builder<Object> builder = ImmutableSet.builder();
	builder.add(new PairN<Object, Object>(o1, o2));
	builder.add(new PairN<Object, Object>(o1, o3));
	builder.add(new PairN<Object, Object>(o1, o3bis));
	builder.add(new PairN<Object, Object>(o1, o4));
	builder.add(new PairN<Object, Object>(o2, o3));
	builder.add(new PairN<Object, Object>(o2, o3bis));
	builder.add(new PairN<Object, Object>(o2, o4));
	builder.add(new PairN<Object, Object>(o3, o4));
	builder.add(new PairN<Object, Object>(o3bis, o4));
	final ImmutableSet<Object> expected = builder.build();

	assertEquals(expected, extendedStrict);
    }
}
