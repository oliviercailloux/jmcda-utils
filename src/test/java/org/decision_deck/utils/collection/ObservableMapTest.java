package org.decision_deck.utils.collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.decision_deck.utils.collection.MapEvents.AdditionEvent;
import org.decision_deck.utils.collection.MapEvents.AdditionNewKeyEvent;
import org.decision_deck.utils.collection.MapEvents.PreRemovalEvent;
import org.junit.Test;

import com.google.common.eventbus.Subscribe;

@SuppressWarnings("boxing")
public class ObservableMapTest {
	private boolean m_seen = false;

	@Test
	public void testRemove() {
		final ObservableMap<String, Double> map = CollectionUtils.newObservableMap();
		final Object observer = getObserver();
		map.register(observer);
		map.put("s1", 1d);
		assertFalse(m_seen);
		map.remove("sNONE");
		assertFalse(m_seen);
		map.remove("s1");
		assertTrue(m_seen);
	}

	Object getObserver() {
		@SuppressWarnings("unused")
		final Object observer = new Object() {
			@Subscribe
			public void dontSeeEvent(AdditionEvent<String, Double> event) {
				m_seen = false;
			}

			@Subscribe
			public void seeEvent(PreRemovalEvent<String, Double> event) {
				m_seen = true;
			}
		};
		return observer;
	}

	@Test
	public void testAddNew() {
		final ObservableMap<String, Double> map = CollectionUtils.newObservableMap();
		map.register(new Object() {
			@SuppressWarnings("unused")
			@Subscribe
			public void seeEvent(AdditionNewKeyEvent<String, Double> event) {
				m_seen = true;
			}
		});
		map.put("s1", 1d);
		assertTrue(m_seen);
	}

	@Test
	public void testAdd() {
		final ObservableMap<String, Double> map = CollectionUtils.newObservableMap();
		map.register(new Object() {
			@SuppressWarnings("unused")
			@Subscribe
			public void seeEvent(AdditionEvent<String, Double> event) {
				m_seen = true;
			}
		});
		map.put("s1", 1d);
		assertTrue(m_seen);
	}

}
