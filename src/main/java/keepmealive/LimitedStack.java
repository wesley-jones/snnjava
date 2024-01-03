package keepmealive;

import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LimitedStack<T> extends LinkedList<T> {
	private static final long serialVersionUID = 1L;
	private static final int MAX_SIZE = 20;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	public void pushItem(T item) {
		Objects.requireNonNull(item, "Item cannot be null");

		// Acquire write lock to ensure exclusive access during modification
		lock.writeLock().lock();

		try {
			// Push the new number onto the stack
			addFirst(item);

			// If the size exceeds the limit, pop the oldest element
			if (size() > MAX_SIZE) {
				removeLast();
			}
		} finally {
			// Release write lock
			lock.writeLock().unlock();
		}
	}

	public ReadWriteLock getLock() {
		return lock;
	}
}
