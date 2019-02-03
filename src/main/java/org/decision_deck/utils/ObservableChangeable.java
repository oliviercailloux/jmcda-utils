package org.decision_deck.utils;

import java.util.Observable;

public class ObservableChangeable extends Observable {
	@Override
	public synchronized void setChanged() {
		super.setChanged();
	}

	@Override
	public synchronized void clearChanged() {
		super.clearChanged();
	}
}