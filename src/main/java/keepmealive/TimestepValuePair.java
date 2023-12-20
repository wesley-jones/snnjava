package keepmealive;

import java.util.Objects;

public class TimestepValuePair<T> {
	private long superstep;
	private T value;

	public TimestepValuePair(long superstep, T value) {
		this.superstep = Objects.requireNonNull(superstep, "Superstep cannot be null");
		this.value = Objects.requireNonNull(value, "Value cannot be null");
	}

	public long getSuperstep() {
		return superstep;
	}

	public T getValue() {
		return value;
	}
}
