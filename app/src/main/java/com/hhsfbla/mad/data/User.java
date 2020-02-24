package com.hhsfbla.mad.data;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class User {

    private String name;
    private String chapter;
    private UserType userType;
    private String email;
    private ArrayList<String> myEvents;
    private String blurb;  // Officers
    private String phoneNumber;
    private ArrayList<String> comps;
    private String pic;
    private String officerPos;

    public User() {
        this("", null, "");
    }

    public User(String name, String chapter, String email) {
        this.name = name;
        this.userType = UserType.MEMBER;
        this.chapter = chapter;
        this.email = email;
        myEvents = new ArrayList<String>();
        comps = new ArrayList<String>();
        pic = null;
        officerPos = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getMyEvents() {
        return myEvents;
    }


    public void setMyEvents(ArrayList<String> myEvents) {
        this.myEvents = myEvents;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ArrayList<String> getComps() {
        return comps;
    }

    public void setComps(ArrayList<String> comps) {
        this.comps = comps;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getOfficerPos() {
        return officerPos;
    }

    public void setOfficerPos(String officerPos) {
        this.officerPos = officerPos;
    }
}
