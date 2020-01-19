package com.hhsfbla.mad.data;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class User {

    private String name;
    private Chapter chapter;
    private UserType userType;
    private String email;
    private ArrayList<ChapterEvent> myEvents;
    private String blurb;  // Officers
    private String phoneNumber;

    public User() {
        this("", null, "");
    }

    public User(String name, Chapter chapter, String email) {
        this.name = name;
        this.userType = UserType.MEMBER;
        this.chapter = chapter;
        this.email = email;
        myEvents = new ArrayList<ChapterEvent>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
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

    public ArrayList<ChapterEvent> getMyEvents() {
        return myEvents;
    }

    public void setMyEvents(ArrayList<ChapterEvent> myEvents) {
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
}
