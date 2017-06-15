package edu.ucsb.cs.cs190i.rkuang.homies.models;

import java.util.UUID;

/**
 * Created by Ky on 6/11/2017.
 */

public class Owe {
    private String itemid;
    private double money;

    public Owe(){
        this.itemid = UUID.randomUUID().toString();
        this.money = 0;
    }

    public Owe(String id, double m){
        this.itemid = id;
        this.money = m;
    }


    public String getItemid(){
        return itemid;
    }

    public double getAmount(){
        return money;
    }
}
