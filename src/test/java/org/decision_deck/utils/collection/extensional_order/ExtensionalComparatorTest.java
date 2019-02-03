package org.decision_deck.utils.collection.extensional_order;

import static org.junit.Assert.assertTrue;

import org.decision_deck.utils.collection.extensional_order.ExtensionalComparator;
import org.junit.Test;

public class ExtensionalComparatorTest {
	@Test
	public void testExtensionalComparator() throws Exception {
		final ExtensionalComparator<String> comp = ExtensionalComparator.create();
		comp.addAsHighest("Str3");
		comp.addAsHighest("Str4");
		comp.addAsLowest("Str1");
		comp.addAfter("Str1", "Str2");
		assertTrue(comp.compare("Str1", "Str2") < 0);
		assertTrue(comp.compare("Str2", "Str3") < 0);
		assertTrue(comp.compare("Str3", "Str4") < 0);
		assertTrue(comp.compare("Str4", "Str2") > 0);
		assertTrue(comp.compare("Str4", "Str4") == 0);
	}
}
