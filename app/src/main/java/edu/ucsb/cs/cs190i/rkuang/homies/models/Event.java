package edu.ucsb.cs.cs190i.rkuang.homies.models;

import java.util.Calendar;

/**
 * Created by Timothy Kwong on 6/9/2017.
 */

public class Event {

    private User user;
    private String eventName;
    private String description;
    private String eventTime;
    private String eventDate;
    private String id;

    public Event() {}

    public Event(User user, String name, String date, String eventTime, String description, String id){
        this.eventName = name;
        this.user = user;
        this.eventTime = eventTime;
        this.eventDate = date;
        this.description = description;
        this.id = id;
    }

    public User getUser(){ return user;}

    public String getDescription(){ return description;}

    public String getEventDate(){ return eventDate;}

    public String getEventTime(){ return eventTime;}

    public String getEventName(){ return eventName;}

    public String getId(){ return id;}

    @Override
    public boolean equals(Object obj) {
        Event o = (Event) obj;
        return this.id.equals(o .getId());
    }

}
