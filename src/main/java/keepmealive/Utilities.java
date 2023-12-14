package keepmealive;

public class Utilities {

	// Utility method
	public static boolean hasPassedRefractoryPeriod(long currentSuperstep, long lastFired, long refractoryPeriod) {
		return (currentSuperstep - lastFired > refractoryPeriod);
	}

	// Other utility methods can be added here

}
