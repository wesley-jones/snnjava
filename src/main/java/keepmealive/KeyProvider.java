package keepmealive;

public abstract class KeyProvider {
    public static String getKey() {
        throw new UnsupportedOperationException("Subclasses must provide their own implementation");
    }
}
