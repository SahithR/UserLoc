package com.example.myapplication1;

import android.location.Location;

import java.io.Serializable;

public class UserLocation implements Serializable {
    Details d;
    Location l;

    public UserLocation() {
    }

    public UserLocation(Details d, Location l) {
        this.d = d;
        this.l = l;
    }

    public Details getDetails() {
        return d;
    }

    public void setDetails(Details d) {
        this.d = d;
    }

    public Location getLocation() {
        return l;
    }

    public void setLocation(Location l) {
        this.l = l;
    }
}
