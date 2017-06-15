package edu.ucsb.cs.cs190i.rkuang.homies.models;

import java.util.Calendar;

/**
 * Item class. User can create Items with a description
 * Items are attached to Users.
 * Items are unique by UUID
 */

public class Item {

    private User user;
    private long date;
    private String description;
    private boolean bought;
    private String id;
    private String eventID;

    public Item() {
        // Required Empty Constructor for Firebase
    }

    public Item(User user, String description, String id) {
        Calendar c = Calendar.getInstance();

        this.user = user;
        this.date = c.getTimeInMillis();
        this.description = description;
        this.id = id;
        this.bought = false;
    }

    public Item(User user, String description, String id, String eventID){
        Calendar c = Calendar.getInstance();

        this.eventID = eventID;
        this.user = user;
        this.date = c.getTimeInMillis();
        this.description = description;
        this.id = id;
    }


    public User getUser() {
        return user;
    }

    public long getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "{user = "+ getUser() + ", " +
                "date = "+ getDate() + ", " +
                "item = "+ getDescription() +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Item) {
            Item o = (Item) obj;
            return this.id.equals(o.getId());
        }
        return false;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public String getEventID() {
        return eventID;
    }
}
