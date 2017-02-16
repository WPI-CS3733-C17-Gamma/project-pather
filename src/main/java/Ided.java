import java.util.concurrent.atomic.AtomicLong;

public class Ided {
    /**
     * Atmomic long to generate database ids
     */
    private static final AtomicLong UNIQUE_ID_GENERATOR = new AtomicLong(0);
    /**
     * Unique ID for every object to be added to the database
     */
    public final long id = UNIQUE_ID_GENERATOR.incrementAndGet();
}
