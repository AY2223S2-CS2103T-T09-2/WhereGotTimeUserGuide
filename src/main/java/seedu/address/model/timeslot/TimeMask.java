package seedu.address.model.timeslot;

import java.util.ArrayList;

import seedu.address.model.event.IsolatedEvent;
import seedu.address.model.event.IsolatedEventList;
import seedu.address.model.event.RecurringEvent;

/**
 * Class to generate the bit masking for the events.
 */
public class TimeMask {
    // One day 24 hours -> 24 bits
    // If 0.5 hour granularity is desired, required to split into two separate ints
    private static final int WINDOW_RANGE = 7;
    private static final int SLOTS_PER_DAY = 24;
    // 0's represent free slot
    private final int[] weeklyOccupancy;

    public TimeMask() {
        weeklyOccupancy = new int[]{0, 0, 0, 0, 0, 0, 0};
    }

    public int getDayMask(int dayIndex) {
        checkValidDayIndex(dayIndex);
        return weeklyOccupancy[dayIndex];
    }

    private void mergeSingleDay(int dayIndex, int dayOccupancy) {
        checkValidDayIndex(dayIndex);
        weeklyOccupancy[dayIndex] = weeklyOccupancy[dayIndex] | dayOccupancy;
    }

    /**
     * Compare the base time mask with a time mask and update accordingly.
     * @param other TimeMask object
     */
    public void mergeMask(TimeMask other) {
        if (other == null) {
            // TODO: refactor
            throw new RuntimeException("Empty time mask!");
        }
        for (int i = 0; i < WINDOW_RANGE; i++) {
            mergeSingleDay(i, other.weeklyOccupancy[i]);
        }
    }

    /**
     * Compare the time mask with the isolated events and update the time mask.
     * @param isolatedEventList IsolatedEventList
     */
    public void mergeIsolatedEvents(IsolatedEventList isolatedEventList) {
        for (int i = 0; i < isolatedEventList.getSize(); i++) {
            IsolatedEvent event = isolatedEventList.getIsolatedEvent(i);

            int startIndex = event.getStartDayValue();
            int endIndex = event.getEndDayValue();
            int startTime = event.getStartDate().getHour();
            int endTime = event.getEndDate().getHour();

            if (startIndex != endIndex) {
                occupySlots(startIndex, startTime, endTime);
                occupySlots(endIndex, 0, endTime);
            } else {
                occupySlots(startIndex, startTime, endTime);
            }
        }
    }

    private void freeSlots(int dayIndex, int startHourIndex, int endHourIndex) {
        checkValidDayIndex(dayIndex);
        checkValidHourIndexes(startHourIndex, endHourIndex);

        // TODO: Check, requires JDK 11
        int startBits = Integer.parseInt("1".repeat(endHourIndex - startHourIndex + 1));
        int mask = ~(startBits << startHourIndex);

        weeklyOccupancy[dayIndex] = weeklyOccupancy[dayIndex] & mask;
    }

    private void occupySlots(int dayIndex, int startHourIndex, int endHourIndex) {
        checkValidDayIndex(dayIndex);
        checkValidHourIndexes(startHourIndex, endHourIndex);

        int startBits = Integer.parseInt("1".repeat(endHourIndex - startHourIndex + 1), 2);
        int mask = startBits << startHourIndex;
        weeklyOccupancy[dayIndex] = weeklyOccupancy[dayIndex] | mask;
    }

    private void checkValidDayIndex(int dayIndex) {
        if (dayIndex < 0 || dayIndex > WINDOW_RANGE) {
            // TODO: refactor
            throw new RuntimeException("Invalid day index!");
        }
    }

    private void checkValidHourIndexes(int ...hourIndexes) {
        for (int hour : hourIndexes) {
            if (hour < 0 || hour > 24) {
                // TODO: refactor
                throw new RuntimeException("Invalid hour index!");
            }
        }
    }

    /**
     * Update the bits in the TimeMask object according to the recurring event.
     * @param recurringEvent RecurringEvent
     * @param toOccupy boolean
     */
    public void modifyOccupancy(RecurringEvent recurringEvent, boolean toOccupy) {
        int dayIndex = recurringEvent.getDayValue();
        int startHourIndex = recurringEvent.getStartTime().getHour();
        int endHourIndex = recurringEvent.getEndTime().getHour();
        if (toOccupy) {
            occupySlots(dayIndex, startHourIndex, endHourIndex);
        } else {
            freeSlots(dayIndex, startHourIndex, endHourIndex);
        }
    }

//    public static ObservableList<Integer> getTimetable(TimeMask timeMask) {
//        ObservableList<Integer> linearTimetable = FXCollections.observableArrayList();
//        for (int offset = 0; offset < WINDOW_RANGE; offset++) {
//            linearTimetable.addAll(getTimeSlotIndexes(timeMask.getDayMask(offset), offset));
//        }
//        return linearTimetable;
//    }

    public static ArrayList<ArrayList<Integer>> getTimeSlotIndexes(TimeMask mask) {
        ArrayList<ArrayList<Integer>> twoDimensionalSlotList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            int dayMask = mask.getDayMask(i);
            ArrayList<Integer> daySlots = new ArrayList<>();
            int slots = 0;
            while (slots < SLOTS_PER_DAY) {
                if (dayMask % 2 == 0) {
                    daySlots.add(slots);
                }
                dayMask = dayMask >> 1;
                slots++;
            }
            twoDimensionalSlotList.add(daySlots);
        }
        return twoDimensionalSlotList;
    }
}
