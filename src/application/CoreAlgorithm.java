package application;

import java.sql.SQLException;
import java.util.LinkedList;

public class CoreAlgorithm {
    public String[] calendarMatching(int meetingTime, LinkedList<LinkedList<String[]>> allTask) {
        LinkedList<String[]> calendar1 = allTask.poll();
        LinkedList<String[]> calendar2 = allTask.poll();
        LinkedList<String[]> mergedCalendar = mergeCalendars(calendar1, calendar2);
        LinkedList<String[]> flattened = flattenCalendar(mergedCalendar);

        while (!allTask.isEmpty())
        {
            mergedCalendar = mergeCalendars(flattened, allTask.poll());
            flattened = flattenCalendar(mergedCalendar);
        }

        LinkedList<String[]> available = availability(flattened, meetingTime);
        String[] returnCalendar = new String[available.size()];
        for (int i = 0; i < available.size(); i++)
        {
            returnCalendar[i] = available.get(i)[0] + " - " + available.get(i)[1];
        }
        return returnCalendar;
    }

    private LinkedList<String[]> mergeCalendars(LinkedList<String[]> calendar1, LinkedList<String[]> calendar2) {
        LinkedList<String[]> mergedCalendar = new LinkedList<>();
        int i = 0;
        int j = 0;
        while (i < calendar1.size() && j < calendar2.size()) {
            String[] meeting1 = calendar1.get(i);
            String[] meeting2 = calendar2.get(j);

            if (compareTimes(meeting1[0], meeting2[0]) == -1) {
                mergedCalendar.add(meeting1);
                i++;
            }
            else {
                mergedCalendar.add(meeting2);
                j++;
            }
        }

        try
        {
            for (int m = i; m < calendar1.size(); m++)
                mergedCalendar.add(calendar1.get(m));
            for (int n = j; n < calendar2.size(); n++)
                mergedCalendar.add(calendar1.get(n));
        }
        catch (IndexOutOfBoundsException e)
        {
        }
        return mergedCalendar;

    }

    private LinkedList<String[]> flattenCalendar(LinkedList<String[]> mergedCalendar) {
        LinkedList<String[]> flattened = new LinkedList<>();
        flattened.add(mergedCalendar.get(0));
        for (String[] strings : mergedCalendar) {
            String currMeetingStart = strings[0];
            String currMeetingEnd = strings[1];
            String prevMeetingStart = flattened.getLast()[0];
            String prevMeetingEnd = flattened.getLast()[1];

            if (compareTimes(prevMeetingEnd, currMeetingStart) == 1 || compareTimes(prevMeetingEnd, currMeetingStart) == 0) {
                String[] newPrevMeeting = new String[2];
                newPrevMeeting[0] = prevMeetingStart;
                if (compareTimes(prevMeetingEnd, currMeetingEnd) == 1 || compareTimes(prevMeetingEnd, currMeetingEnd) == 0)
                    newPrevMeeting[1] = prevMeetingEnd;
                else
                    newPrevMeeting[1] = currMeetingEnd;
                flattened.removeLast();
                flattened.addLast(newPrevMeeting);

            } else {
                flattened.add(strings);
            }
        }
        return flattened;
    }

    private LinkedList<String[]> availability(LinkedList<String[]> flattened, int meetingTime) {
        LinkedList<String[]> availableCalendar = new LinkedList<>();
        for (int i = 1; i < flattened.size(); i++) {
            String start = flattened.get(i-1)[1];
            String end = flattened.get(i)[0];
            String[] startEnd = new String[2];
            if (differenceBetween(end, start) >= meetingTime) {
                startEnd[0] = start;
                startEnd[1] = end;
                availableCalendar.add(startEnd);
            }
        }
        return availableCalendar;
    }

    private int compareTimes(String time1, String time2) {
        String[] timeArr1 = time1.split(":");
        String[] timeArr2 = time2.split(":");

        int timeCount1 = Integer.parseInt(timeArr1[0]) * 60 + Integer.parseInt(timeArr1[1]);
        int timeCount2 = Integer.parseInt(timeArr2[0]) * 60 + Integer.parseInt(timeArr2[1]);

        if (timeCount1 > timeCount2)
            return 1;   // Greater than
        else if (timeCount1 < timeCount2)
            return -1; // Less than
        else
            return 0;   // Same
    }

    private int differenceBetween(String time1, String time2) {
        String[] timeArr1 = time1.split(":");
        String[] timeArr2 = time2.split(":");

        int timeCount1 = Integer.parseInt(timeArr1[0]) * 60 + Integer.parseInt(timeArr1[1]);
        int timeCount2 = Integer.parseInt(timeArr2[0]) * 60 + Integer.parseInt(timeArr2[1]);

        return timeCount1 - timeCount2;
    }

    public static void main (String[] args)
    {
        CoreAlgorithm algorithm = new CoreAlgorithm();
        ControllerMainScreen2 controllerMainScreen2 = new ControllerMainScreen2();
        LinkedList<LinkedList<String[]>> allTask = new LinkedList<>();
        try {
            allTask.add(controllerMainScreen2.getTimePeriod("khoingu11@gmail.com"));
            allTask.add(controllerMainScreen2.getTimePeriod("quangngu@gmail.com"));
            allTask.add(controllerMainScreen2.getTimePeriod("cuongngu0512@gmail.com"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        String[] durationList = algorithm.calendarMatching(20, allTask);
        for (String s : durationList)
        {
            System.out.println(s);
        }

    }
}
