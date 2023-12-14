package keepmealive;

import java.util.LinkedList;

public class LimitedStack extends LinkedList<Long> {
	private static final long serialVersionUID = 1L;
	private static final int MAX_SIZE = 2;

	public void pushNumber(long number) {
		// Push the new number onto the stack
		addFirst(number);

		// If the size exceeds the limit, pop the oldest element
		if (size() > MAX_SIZE) {
			removeLast();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Long superstep : this) {
			sb.append(superstep).append(",");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1); // Remove the trailing comma
		}
		return sb.toString();
	}
}
