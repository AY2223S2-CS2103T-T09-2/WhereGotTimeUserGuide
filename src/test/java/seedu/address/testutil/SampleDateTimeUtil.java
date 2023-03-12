package seedu.address.testutil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A utility class containing a list of {@code LocalDateTime} objects to be used in tests.
 */
public class SampleDateTimeUtil {
    private static final DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final LocalDateTime TWO_O_CLOCK_VALID = LocalDateTime.parse("09/03/2023 14:00", newFormatter);
    public static final LocalDateTime THREE_O_CLOCK_VALID = LocalDateTime.parse("09/03/2023 15:00", newFormatter);

    public static final LocalDateTime FOUR_O_CLOCK_VALID = LocalDateTime.parse("09/03/2023 16:00", newFormatter);
    public static final LocalDateTime SIX_O_CLOCK_VALID = LocalDateTime.parse("09/03/2023 18:00", newFormatter);
    public static final LocalDateTime ONE_O_CLOCK_VALID = LocalDateTime.parse("09/03/2023 13:00", newFormatter);
}

