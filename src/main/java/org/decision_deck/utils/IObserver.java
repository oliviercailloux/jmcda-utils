package org.decision_deck.utils;

/**
 * An observer, to be used with an observed object. This observer object will be
 * warned when the observed object is updated.
 * 
 * @author Olivier Cailloux
 * 
 * @param <O> the type of object passed to the update method and giving
 *        information about the updated object.
 */
public interface IObserver<O> {
	/**
	 * Called after an update happened.
	 * 
	 * @param updated an information about some object that has been updated,
	 *                possibly <code>null</code> except if the observed object
	 *                specifies otherwize.
	 */
	public void update(O updated);
}
