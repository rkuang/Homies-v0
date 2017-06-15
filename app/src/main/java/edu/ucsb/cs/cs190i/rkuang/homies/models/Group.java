package edu.ucsb.cs.cs190i.rkuang.homies.models;

import java.util.ArrayList;

/**
 * Created by Wilson on 6/13/2017.
 */

public class Group {

    private String groupName;
    private long date;
    private ArrayList<String> users;

    public Group() {

    }

    public Group(String groupName, long date, ArrayList<String> users) {
        this.groupName = groupName;
        this.date = date;
        this.users = users;
    }

    public String getGroupName(){
        return groupName;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public long getDate() {
        return date;
    }
}
