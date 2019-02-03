package org.decision_deck.utils;

import java.util.Observable;
import java.util.Observer;

import com.google.common.base.Preconditions;

public class ObservableTyped<O> {
	private final ObservableChangeable m_observable = new ObservableChangeable();

	@Override
	public int hashCode() {
		return m_observable.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return m_observable.equals(obj);
	}

	/**
	 * @param observer not <code>null</code>.
	 */
	public void addObserver(final IObserver<O> observer) {
		Preconditions.checkNotNull(observer);
		m_observable.addObserver(new Observer() {
			@Override
			public void update(Observable observable, Object arg) {
				@SuppressWarnings("unchecked")
				final O argTyped = (O) arg;
				observer.update(argTyped);
			}
		});
	}

	/**
	 * @param observer not <code>null</code>.
	 */
	public void deleteObserver(Observer observer) {
		Preconditions.checkNotNull(observer);
		m_observable.deleteObserver(observer);
	}

	public void notifyObserversChanged() {
		m_observable.setChanged();
		m_observable.notifyObservers();
		m_observable.clearChanged();
	}

	/**
	 * @param updated an information about some object that has been updated,
	 *                possibly <code>null</code>.
	 */
	public void notifyObserversChanged(O updated) {
		m_observable.setChanged();
		m_observable.notifyObservers(updated);
		m_observable.clearChanged();
	}

	public void deleteObservers() {
		m_observable.deleteObservers();
	}

	public int countObservers() {
		return m_observable.countObservers();
	}

	public ObservableTyped() {
		/** Public default constructor. */
	}
}
