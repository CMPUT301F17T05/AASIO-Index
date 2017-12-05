package com.cmput301.t05.habilect;

import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author amwhitta
 *         <p>
 *         Calculates different statistics for Habit Types based on their Habit Events
 */

public class StatisticsCalculator {

    private HabitType habit_type;

    public StatisticsCalculator(HabitType habit_type) {
        this.habit_type = habit_type;
    }

    /**
     * counts up the number of days in the habit type's weekly plan that have passed
     * since the start date to determine how many times the user could have created a
     * habit event if they were following their plan perfectly
     *
     * @return an int specifying the number of total opportunities
     * to create a habit event
     */
    private int totalEventOpportunities() {
        int count = 0;
        Calendar start = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        start.setTime(habit_type.getStartDate());
        while (start.before(today)) {
            switch (start.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.MONDAY:
                    if (habit_type.getWeeklyPlan()[0]) {
                        ++count;
                    }
                    break;
                case Calendar.TUESDAY:
                    if (habit_type.getWeeklyPlan()[1]) {
                        ++count;
                    }
                    break;
                case Calendar.WEDNESDAY:
                    if (habit_type.getWeeklyPlan()[2]) {
                        ++count;
                    }
                    break;
                case Calendar.THURSDAY:
                    if (habit_type.getWeeklyPlan()[3]) {
                        ++count;
                    }
                    break;
                case Calendar.FRIDAY:
                    if (habit_type.getWeeklyPlan()[4]) {
                        ++count;
                    }
                    break;
                case Calendar.SATURDAY:
                    if (habit_type.getWeeklyPlan()[5]) {
                        ++count;
                    }
                    break;
                case Calendar.SUNDAY:
                    if (habit_type.getWeeklyPlan()[6]) {
                        ++count;
                    }
                    break;
            }
            start.add(Calendar.DATE, 1);
        }
        return count;
    }

    private int totalEvents() {
        List<HabitEvent> events = habit_type.getHabitEvents();
        return events.size();
    }

    public int averageCompletion() {
        if (totalEventOpportunities() == 0) {
            return 0;
        }
        return ((totalEvents() / totalEventOpportunities()) * 100);
    }

    public DataPoint[] pastFourWeeks() {

        DataPoint[] points = new DataPoint[5];
        DataPoint point;
        List<HabitEvent> events = habit_type.getHabitEvents();

        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 7);

        // x corresponds to the x coordinate for week (week current to week 4)
        for (int x = 4; x >= 0; --x) {
            int y = 0;
            int i = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();

            // get the start and end of the week
            c.add(Calendar.DATE, -i - 7);
            Date start = c.getTime();
            c.add(Calendar.DATE, 6);
            Date end = c.getTime();
            Log.d("Debugging", start + " - " + end);

            // add up the habit events that were completed in that week
            for (HabitEvent e : events) {
                Date completion_date = e.getCompletionDate();
                Log.d("Debugging", "completion: " + completion_date.toString());
                if (completion_date.after(start) && completion_date.before(end)) {
                    ++y;
                }
            }
            point = new DataPoint(x, y);
            points[x] = point;

            //c.add(Calendar.DAY_OF_MONTH, -7);
        }

        return points;
    }
}
