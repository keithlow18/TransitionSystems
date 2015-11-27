package org.processmining.models.graphbased.directed.transitionsystem.payload;

import java.util.List;

import org.processmining.models.graphbased.directed.transitionsystem.State;
import org.processmining.models.graphbased.directed.transitionsystem.Transition;
import org.processmining.models.graphbased.directed.transitionsystem.TransitionSystemImpl;

/**
 * This class enables associating a specific strategy of "replaying" a sequence
 * on a transition system. For example, the simplest strategy would be to start
 * in the initial state and then replay one by one sequence element on each
 * transition. Here the new sate is determined purely based on the last sequence
 * element. However, there can be replay strategies can consider more than the
 * last element when determining the new state.
 * 
 * @author mpesic
 * 
 * @param <E>
 *            is the type of elements in the sequence.
 */
public class PayloadTransitionSystem<E> extends TransitionSystemImpl {
	/**
	 * The handler stores the logic of the replay strategy. The handler should
	 * be uset to replay arbitrary sequences on the transition system. The
	 * handler knows which element of the sequence fires which transition in the
	 * system.
	 */
	private final PayloadHandler<E> handler;

	/**
	 * The only constructor for this class.
	 * 
	 * @param label
	 *            is the transition system label.
	 * @param handler
	 *            is the specific PayloadHandler.
	 */
	public PayloadTransitionSystem(String label, PayloadHandler<E> handler) {
		super(label);
		this.handler = handler;
	}

	/**
	 * Returns the payload handler associated with this transition system.
	 * 
	 * @return
	 */
	public PayloadHandler<E> getPayloadHanlder() {
		return handler;
	}

	/**
	 * Returns the source state of the transition that corresponds to the i-th
	 * element.
	 * 
	 * @param sequence
	 * @param i
	 * @return
	 */
	public State getSourceState(List<E> sequence, int i) {
		Object identifier = handler.getSourceStateIdentifier(sequence, i);
		if (identifier != null) {
			return getNode(identifier);
		}
		return null;
	}

	public State getTargetState(List<E> sequence, int i) {
		Object identifier = handler.getTargetStateIdentifier(sequence, i);
		if (identifier != null) {
			return getNode(identifier);
		}
		return null;
	}

	/**
	 * Returns the transition that corresponds to the i-th element of the
	 * sequence. This is, of course with respect to the way the payload handler
	 * replays the sequence.
	 * 
	 * @param sequence
	 * @param i
	 * @return
	 */
	public Transition getTransition(List<E> sequence, int i) {
		Object sourceIdentifier = handler.getSourceStateIdentifier(sequence, i);
		if (sourceIdentifier != null) {
			Object targetIdentifier = handler.getTargetStateIdentifier(sequence, i);
			if (targetIdentifier != null) {
				E element = handler.getSequenceElement(sequence, i);
				Object identifier = handler.getTransitionIdentifier(element);
				if (identifier != null) {
					return findTransition(sourceIdentifier, targetIdentifier, identifier);
				}
			}
		}
		return null;
	}
}
