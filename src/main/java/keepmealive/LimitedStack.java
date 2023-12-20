package keepmealive;

import java.util.LinkedList;
import java.util.Objects;

public class LimitedStack<T> extends LinkedList<T> {
	private static final long serialVersionUID = 1L;
	private static final int MAX_SIZE = 2;

	public void pushItem(T item) {
		Objects.requireNonNull(item, "Item cannot be null");
		// Push the new number onto the stack
		addFirst(item);

		// If the size exceeds the limit, pop the oldest element
		if (size() > MAX_SIZE) {
			removeLast();
		}
	}
}
