package com.hhsfbla.mad.data;

import java.util.ArrayList;

public class Chapter {

    private User creator;
    private String name;
    private ArrayList<User> users;
    private ArrayList<ChapterEvent> events;

    private String instagramTag;
    private String facebookPage;
    private String description;

    private static final Competition[] comps = {new  Competition("Mobile Application Development", "", CompType.TECH), };


    public Chapter(String name, User creator) {
        this.name = name;
        this.creator = creator;
        users = new ArrayList<User>();
        events = new ArrayList<ChapterEvent>();
    }

    public void addMember(User user) {
        users.add(user);
    }

    public void removeMember(User user) {
        users.remove(user);
    }

    public void addEvent(ChapterEvent event) {
        events.add(event);
    }

    public void removeEvent(ChapterEvent event) {
        events.remove(event);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<ChapterEvent> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<ChapterEvent> events) {
        this.events = events;
    }

    public String getInstagramTag() {
        return instagramTag;
    }

    public void setInstagramTag(String instagramTag) {
        this.instagramTag = instagramTag;
    }

    public String getFacebookPage() {
        return facebookPage;
    }

    public void setFacebookPage(String facebookPage) {
        this.facebookPage = facebookPage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static Competition[] getComps() {
        return comps;
    }
}
