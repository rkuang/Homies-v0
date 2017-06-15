package edu.ucsb.cs.cs190i.rkuang.homies.models;

/**
 * Users for identifying items and groups (to be implemented)
 */

public class User {

    private String name;
    private String email;
    private String imageURL;
    private String uid;

    public User() {
        // Required empty constructor for Firebase
    }

    public User(String name, String email, String url, String uid) {
        this.name = name;
        this.email = email;
        this.imageURL = url;
        this.uid = uid;
    }


    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getUid() {
        return uid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User o = (User) obj;
            return this.uid.equals(o.uid);
        }
        return false;
    }
}
