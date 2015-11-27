package org.processmining.plugins.transitionsystem.miner.util;

public enum TSAbstractions {
	/**
	 * Both order and number will be preserved in the abstraction.
	 */
	SEQUENCE,
	/**
	 * Both order and number will be lost, only presence will be preserved.
	 */
	SET,
	/**
	 * Order will be lost, but number will be preserved.
	 */
	BAG;

	public String getLabel() {
		switch (this) {
			case SEQUENCE :
				return "Sequence";
			case SET :
				return "Set";
			case BAG :
				return "Multiset";
		}
		return null;
	}
}
