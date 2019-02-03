package org.decision_deck.utils.matrix.mess;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;

import org.decision_deck.utils.collection.extensional_order.ExtensionalComparator;
import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

public class OrderDecoratedSetTest {
	@Test
	public void testOrder() throws Exception {
		final LinkedHashSet<String> decoratedSet = Sets.newLinkedHashSet();
		decoratedSet.add("Before1");
		decoratedSet.add("Before2");
		decoratedSet.add("Before3");
		final OrderDecoratedSet<String> ordered = OrderDecoratedSet.create(decoratedSet);
		assertEquals(3, ordered.size());
		assertTrue(Iterables.elementsEqual(decoratedSet, ordered));

		ordered.add("After");
		ordered.add("Final");
		assertEquals(5, ordered.size());
		assertEquals(5, decoratedSet.size());
		assertTrue(Iterables.elementsEqual(Arrays.asList("Before1", "Before2", "Before3", "After", "Final"), ordered));

		ordered.setComparator(Ordering.<String>natural());
		assertEquals(5, ordered.size());
		assertEquals(5, decoratedSet.size());
		assertTrue(Iterables.elementsEqual(Arrays.asList("Before1", "Before2", "Before3", "After", "Final"),
				decoratedSet));
		assertTrue(Iterables.elementsEqual(Arrays.asList("After", "Before1", "Before2", "Before3", "Final"), ordered));

		ordered.setOrderByDelegate();
		assertTrue(Iterables.elementsEqual(Arrays.asList("Before1", "Before2", "Before3", "After", "Final"), ordered));

		/** The rest seem to have problems. */
		// ordered.setSubsetComparator(ExtensionalComparator.create(Arrays.asList("Before3",
		// "After", "Ploum",
		// "Before2",
		// "Before1", "Final")));
		// assertTrue(Iterables.elementsEqual(Arrays.asList("Before1", "Before2",
		// "Before3", "After", "Final"),
		// decoratedSet));
		// assertTrue(Iterables.elementsEqual(Arrays.asList("Before3", "After",
		// "Before2", "Before1", "Final"),
		// ordered));
		//
		// ordered.remove("Before3");
		// assertTrue(Iterables.elementsEqual(Arrays.asList("Before1", "Before2",
		// "After", "Final"), decoratedSet));
		// assertTrue(Iterables.elementsEqual(Arrays.asList("After", "Before2",
		// "Before1", "Final"), ordered));
	}

	@SuppressWarnings("deprecation")
	@Test(expected = UnsupportedOperationException.class)
	public void testOrderIncomplete() throws Exception {
		final LinkedHashSet<String> decoratedSet = Sets.newLinkedHashSet();
		decoratedSet.add("Before1");
		decoratedSet.add("Before2");
		decoratedSet.add("Before3");
		final OrderDecoratedSet<String> ordered = OrderDecoratedSet.create(decoratedSet);
		assertEquals(3, ordered.size());
		assertTrue(Iterables.elementsEqual(decoratedSet, ordered));

		ordered.add("After");
		ordered.add("Final");
		assertEquals(5, ordered.size());
		assertEquals(5, decoratedSet.size());
		assertTrue(Iterables.elementsEqual(Arrays.asList("Before1", "Before2", "Before3", "After", "Final"), ordered));

		ordered.setSubsetComparator(
				ExtensionalComparator.create(Arrays.asList("Before3", "Before2", "Before1", "Final")));
	}
}
