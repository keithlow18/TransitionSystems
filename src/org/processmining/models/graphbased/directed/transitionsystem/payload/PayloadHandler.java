package org.processmining.models.graphbased.directed.transitionsystem.payload;

import java.util.List;

/**
 * This interface represents the logic of a specific strategy of "replaying" a
 * sequence on a transition system.
 * 
 * @author mpesic
 * 
 * @param <E>
 *            is the type of elements in the sequence.
 */
public interface PayloadHandler<E> {
	/**
	 * Returns the state representation (i.e., identifier) of the source state
	 * of the transition that corresponds to the i-th element of the sequence.
	 * 
	 * @param sequence
	 * @param i
	 * @return
	 */
	public Object getSourceStateIdentifier(List<E> sequence, int i);

	/**
	 * Returns the state representation (i.e., identifier) of the target state
	 * of the transition that corresponds to the i-th element of the sequence.
	 * 
	 * @param sequence
	 * @param i
	 * @return
	 */
	public Object getTargetStateIdentifier(List<E> sequence, int i);

	/**
	 * Returns the transition representation of the given element.
	 * 
	 * @param element
	 * @return
	 */
	public Object getTransitionIdentifier(E element);

	/**
	 * Returns the i-th element of the sequence.
	 * 
	 * @param sequence
	 * @param i
	 * @return
	 */
	public E getSequenceElement(List<E> sequence, int i);
}
