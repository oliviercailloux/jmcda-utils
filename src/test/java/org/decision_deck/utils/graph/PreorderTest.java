package org.decision_deck.utils.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.decision_deck.utils.relation.graph.Edge;
import org.decision_deck.utils.relation.graph.Preorder;
import org.decision_deck.utils.relation.graph.Preorders;
import org.decision_deck.utils.relation.graph.mess.DiGraph;
import org.decision_deck.utils.relation.graph.mess.GraphUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class PreorderTest {

    @Test
    public void testPreorderIntersect() {
	final Preorder<String> toY = Preorders.getIntersection(getPreorderStar(), getPreorderDownArrow());
	assertEquals(getPreorderY(), toY);

	final Preorder<String> fromY = Preorders.getIntersection(getPreorderY(), getPreorderDownArrow());
	assertEquals(getPreorderY().asSet().size(), 5);
	assertEquals(getPreorderY(), fromY);

	final Preorder<String> diff = Preorders.getIntersection(getPreorderY(), getPreorderDownArrowDifferent());
	assertNull(diff);

	final Preorder<String> id = Preorders.getIntersection(getPreorderStraight(), getPreorderStraight());
	assertEquals(getPreorderStraight(), id);
    }

    public Preorder<String> getPreorderY() {
	final Preorder<String> p = new Preorder<String>();
	p.putAllAsHighest(ImmutableSet.of("a", "b"));
	p.putAllAsLowest(ImmutableSet.of("c"));
	p.putAllAsLowest(ImmutableSet.of("d"));
	p.putAllAsLowest(ImmutableSet.of("e"));
	return p;
    }

    public Preorder<String> getPreorderStraight() {
	final Preorder<String> p = new Preorder<String>();
	p.putAsLowest("L");
	p.putAsLowest("M");
	p.putAsLowest("G");
	p.putAsLowest("T");
	p.putAsLowest("D");
	p.putAsLowest("A");
	p.putAsLowest("J");
	p.putAsLowest("I");
	p.putAsLowest("R");
	p.putAsLowest("S");

	p.putAsLowest("B");
	p.putAsLowest("H");
	p.putAsLowest("K");
	p.putAsLowest("E");
	p.putAsLowest("O");
	p.putAsLowest("P");
	p.putAsLowest("N");
	p.putAsLowest("Q");
	p.putAsLowest("C");
	p.putAsLowest("F");

	return p;
    }

    @Test
    public void testPreorder() throws Exception {
	final Preorder<Object> preorder = new Preorder<Object>();
	final Object o1 = "o1";
	final Object o2 = "o2";
	final Object o3 = "o3";
	final Object o3bis = "o3bis";
	final Object o4 = "o4";

	preorder.put(o2, 1);
	preorder.putAsLowest(o3);
	assertEquals(ImmutableSet.of(o2), preorder.get(1));
	assertEquals(ImmutableSet.of(o3), preorder.get(2));

	preorder.putAllAsLowest(ImmutableSet.of(o3, o3bis));
	assertEquals(ImmutableSet.of(o2), preorder.get(1));
	assertEquals(ImmutableSet.of(o3, o3bis), preorder.get(2));

	preorder.put(o4, 3);
	preorder.putAsHighest(o1);
	assertEquals(ImmutableSet.of(o1), preorder.get(1));
	assertEquals(ImmutableSet.of(o2), preorder.get(2));
	assertEquals(ImmutableSet.of(o3, o3bis), preorder.get(3));
	assertEquals(ImmutableSet.of(o4), preorder.get(4));
	s_logger.info("Preorder: {}.", preorder);
    }

    private static final Logger s_logger = LoggerFactory.getLogger(PreorderTest.class);

    @Test
    public void testPreorderToGraph() throws Exception {
	final DiGraph<String> g = GraphUtils.getDiGraphBetterWithTransitiveReduct(getPreorderStar());
	final Builder<Edge<String>> expectedB = ImmutableSet.builder();
	expectedB.add(Edge.create("a", "b"));
	expectedB.add(Edge.create("b", "a"));
	expectedB.add(Edge.create("a", "c"));
	expectedB.add(Edge.create("b", "c"));
	expectedB.add(Edge.create("c", "d"));
	expectedB.add(Edge.create("c", "e"));
	expectedB.add(Edge.create("d", "e"));
	expectedB.add(Edge.create("e", "d"));
	expectedB.add(Edge.create("a", "a"));
	expectedB.add(Edge.create("b", "b"));
	expectedB.add(Edge.create("c", "c"));
	expectedB.add(Edge.create("d", "d"));
	expectedB.add(Edge.create("e", "e"));
	final ImmutableSet<Edge<String>> expectedEdges = expectedB.build();
	assertEquals(expectedEdges, g.edgeSet());
    }

    @Test
    public void testPreorderToGraph2() throws Exception {
	final DiGraph<String> g = GraphUtils.getDiGraphBetterWithTransitiveReduct(getPreorderStraight());
	GraphUtils.computeTransitiveClosure(g);
	for (String a : getPreorderStraight().asSet()) {
	    for (String b : getPreorderStraight().asSet()) {
		final boolean origB = g.containsEdge(a, b);
		final boolean origM = getPreorderStraight().compare(a, b) >= 0;
		assertTrue("oops: " + a + ", " + b + ": origG=" + origB + ", origM=" + origM + ".", origB == origM);
	    }
	}
    }

    public Preorder<String> getPreorderStar() {
        final Preorder<String> p = new Preorder<String>();
        p.putAllAsHighest(ImmutableSet.of("a", "b"));
        p.putAllAsLowest(ImmutableSet.of("c"));
        p.putAllAsLowest(ImmutableSet.of("d", "e"));
        return p;
    }

    public Preorder<String> getPreorderDownArrow() {
	final Preorder<String> p = new Preorder<String>();
	p.putAllAsHighest(ImmutableSet.of("a", "b"));
	p.putAllAsLowest(ImmutableSet.of("c", "d"));
	p.putAllAsLowest(ImmutableSet.of("e"));
	return p;
    }

    public Preorder<String> getPreorderDownArrowDifferent() {
	final Preorder<String> p = new Preorder<String>();
	p.putAllAsHighest(ImmutableSet.of("a", "b", "d"));
	p.putAllAsLowest(ImmutableSet.of("c"));
	p.putAllAsLowest(ImmutableSet.of("e"));
	return p;
    }

}
