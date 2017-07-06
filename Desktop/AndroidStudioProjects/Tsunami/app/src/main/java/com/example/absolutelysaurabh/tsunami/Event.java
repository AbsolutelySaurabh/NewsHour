package com.example.absolutelysaurabh.tsunami;

/**
 * Created by absolutelysaurabh on 24/3/17.
 */

public class Event {

    /** Title of the earthquake event */
    public final String title;

    /** Time that the earthquake happened (in milliseconds) */
    public final long time;

    /** Whether or not a tsunami alert was issued (1 if it was issued, 0 if no alert was issued) */
    public final int tsunamiAlert;

    /**
     * Constructs a new {@link Event}.
     *
     * @param eventTitle is the title of the earthquake event
     * @param eventTime is the time the earhtquake happened
     * @param eventTsunamiAlert is whether or not a tsunami alert was issued
     */
    public Event(String eventTitle, long eventTime, int eventTsunamiAlert) {
        title = eventTitle;
        time = eventTime;
        tsunamiAlert = eventTsunamiAlert;
    }
}
