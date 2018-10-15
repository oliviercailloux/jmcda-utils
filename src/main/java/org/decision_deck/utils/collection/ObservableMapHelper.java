package org.decision_deck.utils.collection;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.decision_deck.utils.collection.MapEvents.AdditionEvent;
import org.decision_deck.utils.collection.MapEvents.AdditionNewKeyEvent;
import org.decision_deck.utils.collection.MapEvents.ClearEvent;
import org.decision_deck.utils.collection.MapEvents.PreAdditionEvent;
import org.decision_deck.utils.collection.MapEvents.PreAdditionNewKeyEvent;
import org.decision_deck.utils.collection.MapEvents.PreClearEvent;
import org.decision_deck.utils.collection.MapEvents.PreRemovalEvent;
import org.decision_deck.utils.collection.MapEvents.PreUniqueRemovalEvent;
import org.decision_deck.utils.collection.MapEvents.UniqueRemovalEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

class ObservableMapHelper<K, V> {

    private final Map<K, V> m_delegate;

    public ObservableMapHelper(Map<K, V> delegate) {
	m_delegate = delegate;
    }

    public void clear(Set<java.util.Map.Entry<K, V>> entrySet) {
        for (Entry<K, V> entry : entrySet) {
            m_eventBus.post(new PreRemovalEvent<K, V>(entry.getKey(), entry.getValue()));
        }
        m_eventBus.post(new PreClearEvent<K, V>());
    
	m_delegate.clear();
    
        /**
         * This is incorrect, as the map is not empty. To support post-removal events, we would need to copy the entire
         * map before clearing it.
         */
        // for (Entry<K, V> entry : entrySet) {
        // m_eventBus.post(new RemovalEvent<K, V>(entry.getKey(), entry.getValue()));
        // }
        m_eventBus.post(new ClearEvent<K, V>());
    }

    public V remove(K key, V value) {
	m_eventBus.post(new PreUniqueRemovalEvent<K, V>(key, value));
	m_delegate.remove(key);
	m_eventBus.post(new UniqueRemovalEvent<K, V>(key, value));
	return value;
    }

    /**
     * Registers all handler methods on <code>object</code> to receive events. A handler method is one that is marked
     * with the {@link Subscribe} annotation.
     * 
     * @param observer
     *            object whose handler methods should be registered.
     */
    public void register(Object observer) {
	m_eventBus.register(observer);
    }

    /**
     * Unregisters all handler methods on a registered <code>object</code>.
     * 
     * @param observer
     *            object whose handler methods should be unregistered.
     * @throws IllegalArgumentException
     *             if the object was not previously registered.
     */
    public void unregister(Object observer) {
	m_eventBus.unregister(observer);
    }

    public V put(K key, V value, boolean contained, V previousValue) {
        if (contained) {
            m_eventBus.post(new PreRemovalEvent<K, V>(key, previousValue));
            m_eventBus.post(new PreAdditionEvent<K, V>(key, value));
        } else {
            m_eventBus.post(new PreAdditionNewKeyEvent<K, V>(key, value));
        }
    
        final V previous = m_delegate.put(key, value);
    
        if (contained) {
            m_eventBus.post(new UniqueRemovalEvent<K, V>(key, previousValue));
            m_eventBus.post(new AdditionEvent<K, V>(key, value));
        } else {
            m_eventBus.post(new AdditionNewKeyEvent<K, V>(key, value));
        }
        return previous;
    }

    private final EventBus m_eventBus = new EventBus("map");

}
