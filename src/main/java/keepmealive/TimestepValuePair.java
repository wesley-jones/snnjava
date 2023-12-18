package keepmealive;

public class TimestepValuePair<T> {
	private long superstep;
	private T value;

	public TimestepValuePair(long superstep, T value) {
		this.superstep = superstep;
		this.value = value;
	}

	public long getSuperstep() {
		return superstep;
	}

	public T getValue() {
		return value;
	}
}
