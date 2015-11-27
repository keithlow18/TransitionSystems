package org.processmining.plugins.tscostanalyzer;

public class Amount {

	private static final int CENTS = 1;
	private static final int DOLLARS = 100 * CENTS;


	private final long cost;

	private final long cents;
	private final long dollars;

	public Amount(final long cost) {
		super();

		this.cost = cost;

		long temp = cost;
		
		dollars = temp / DOLLARS;
		temp = temp % DOLLARS;

		cents = temp;
	}

	public Amount(long c, long d) {
		super();

		cost = c + d * DOLLARS;
				
		cents = c;
		dollars = d;

	}

	public double getCents() {
		return cents;
	}

	public long getDollars() {
		return dollars;
	}

	public long getCost() {
		return cost;
	}
	/*
	private String getString(long value, String symbol) {
		return (value > 0) ? (Long.toString(value) + symbol) : "";
	}
	*/
	public String toString() {
		String amt = "$" + dollars + "." + cents;
		
		return amt;
		/*
		if ((dollars == 0)) {
			return (Long.toString(cents) + "c");
		}
		
		String d = getString(dollars, "$ ");
		String c = getString(cents, "cents ");
		if (!d.equals("")) {
			return d + c;
		}
		
		return c;
		*/
	}
}
