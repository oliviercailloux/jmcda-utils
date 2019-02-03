package org.decision_deck.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.decision_deck.utils.relation.Preorder;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class PreorderTest {

	@Test
	public void testTr1() {
		final Preorder<String> pr = Preorder.create();
		assertTrue(pr.asPairs().isEmpty());
		pr.addEqTransitive("1", "1");
		assertFalse(pr.asPairs().isEmpty());
		assertEquals(1, pr.asPairs().size());
		assertEquals(ImmutableSet.of(Pair.create("1", "1")), pr.asPairs());
	}

	@SuppressWarnings("boxing")
	@Test
	public void testTrM1() {
		final Preorder<Integer> pr = Preorder.create();
		pr.addEqTransitive(1, 1);
		pr.addEqTransitive(4, 4);
		pr.addEqTransitive(5, 5);
		assertTrue(pr.asPairs().contains(Pair.create(1, 1)));
		assertFalse(pr.asPairs().contains(Pair.create(3, 3)));
		assertTrue(pr.asPairs().contains(Pair.create(5, 5)));
		assertEquals(3, pr.asPairs().size());
		pr.addEqTransitive(1, 1);
		assertEquals(3, pr.asPairs().size());
		/** 2 = 3. */
		pr.addEqTransitive(2, 3);
		assertTrue(pr.asPairs().contains(Pair.create(3, 3)));
		assertEquals(7, pr.asPairs().size());
		/** 2 = 3, 3 ≥ 4, 4 ≥ 5. */
		pr.addTransitive(3, 4);
		pr.addTransitive(4, 5);
		assertEquals(12, pr.asPairs().size());
		assertTrue(pr.asPairs().contains(Pair.create(2, 4)));
		assertTrue(pr.asPairs().contains(Pair.create(2, 5)));
		assertFalse(pr.asPairs().contains(Pair.create(4, 2)));
		/** 5 ≥ 2, creates a big equivalence class {2, 3, 4, 5}. */
		pr.addTransitive(5, 2);
		assertTrue(pr.asPairs().contains(Pair.create(4, 2)));
		assertFalse(pr.asPairs().contains(Pair.create(4, 1)));
		assertEquals(1 + 4 * 4, pr.asPairs().size());

		/** 1 = 3, creates a unique equivalence class. */
		pr.addEqTransitive(1, 3);
		assertEquals(5 * 5, pr.asPairs().size());
		assertTrue(pr.asPairs().contains(Pair.create(4, 1)));

	}

}
