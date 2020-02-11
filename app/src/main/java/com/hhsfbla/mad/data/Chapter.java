package com.hhsfbla.mad.data;

import java.util.ArrayList;

public class Chapter {

    private String name;
    private ArrayList<String> users;
    private ArrayList<ChapterEvent> events;

    private String instagramTag;
    private String facebookPage;
    private String description;
    private String location;


    public Chapter() {
        this("", "");
    }

    public Chapter(String name, String location) {
        this.name = name;
        users = new ArrayList<String>();
        events = new ArrayList<ChapterEvent>();
        this.location = location;
    }

    public void addMember(String user) {
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

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


}
