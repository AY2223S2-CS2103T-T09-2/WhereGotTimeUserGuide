package seedu.address.model.timeslot;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Class to generate the entire week of schedule
 */
public class ScheduleWeek {
    private static final ObservableList<ScheduleDay> internalList = FXCollections.observableArrayList();

    public ScheduleWeek() {

        internalList.clear();
        ArrayList<Status> emptyCell = new ArrayList<>();
        for(int i = 0; i < 24; i++) {
            emptyCell.add(Status.EMPTY);
        }

        internalList.add(new ScheduleDay("Monday", emptyCell));
        internalList.add(new ScheduleDay("Tuesday", emptyCell));
        internalList.add(new ScheduleDay("Wednesday", emptyCell));
        internalList.add(new ScheduleDay("Thursday", emptyCell));
        internalList.add(new ScheduleDay("Friday", emptyCell));
        internalList.add(new ScheduleDay("Saturday", emptyCell));
        internalList.add(new ScheduleDay("Sunday", emptyCell));

    }

    public void setInternalList(ArrayList<ArrayList<Integer>> timetable) {
        internalList.clear();

        ArrayList<String> allDays = new ArrayList<>(asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
                "Saturday", "Sunday"));

        for (int i = 0; i < 7; i++) {
            ScheduleDay scheduleDay = new ScheduleDay(allDays.get(i), populateData(timetable.get(i)));
            internalList.add(scheduleDay);
        }

    }

    /**
     * Generating data for a single day. If the index matches with the index given in freeTimeSlots, this means that
     * the timeslot is marked as free. Otherwise, it can be assumed the timeslot is marked as busy
     * @param freeTimeSlots all index in which the status is free
     * @return a populated array of data in a single day
     */
    public ArrayList<Status> populateData(ArrayList<Integer> freeTimeSlots) {
        ArrayList<Status> timeSlot = new ArrayList<>();

        int count = 0;
        for (int i = 0; i < 24; i++) {
            int currFreeTimeSlotId = freeTimeSlots.get(count);
            if (currFreeTimeSlotId == i) {
                timeSlot.add(Status.FREE);
                if (count < freeTimeSlots.size() - 1) {
                    count++;
                }
            } else {
                timeSlot.add(Status.BUSY);
            }
        }

        System.out.println(timeSlot.toString());
        return timeSlot;
    }

    public ObservableList<ScheduleDay> getInternalList() {
        return internalList;
    }

}
