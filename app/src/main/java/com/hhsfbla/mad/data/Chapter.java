package com.hhsfbla.mad.data;

import java.util.ArrayList;

public class Chapter {

    private User creator;
    private String name;
    private ArrayList<String> users;
    private ArrayList<Event> events;

    private String instagramTag;
    private String facebookPage;
    private String description;

    private static final Competition[] comps = {
            new Competition("3-D Animation", "", CompType.TECH),
            new Competition("Accounting 1", "", CompType.WRITTEN),
            new Competition("Accounting 2", "", CompType.WRITTEN),
            new Competition("Advertising", "", CompType.WRITTEN),
            new Competition("Agribusiness", "", CompType.WRITTEN),
            new Competition("American Enterprise Project", "", CompType.PROJECT),
            new Competition("Banking and Financial Systems", "", CompType.CASESTUDY),
            new Competition("Broadcast Journalism", "", CompType.SPEAKING),
            new Competition("Business Calculations", "", CompType.WRITTEN),
            new Competition("Business Communication", "", CompType.WRITTEN),
            new Competition("Business Ethics", "", CompType.SPEAKING),
            new Competition("Business Financial Plan", "", CompType.SPEAKING),
            new Competition("Business Law", "", CompType.WRITTEN),
            new Competition("Business Plan", "", CompType.SPEAKING),
            new Competition("Client Service", "", CompType.SPEAKING),
            new Competition("Coding and Programming", "", CompType.TECH),
            new Competition("Community Service Project", "", CompType.PROJECT),
            new Competition("Computer Applications", "", CompType.PRODUCTION),
            new Competition("Computer Game and Simulation Programming", "", CompType.TECH),
            new Competition("Computer Problem Solving", "", CompType.WRITTEN),
            new Competition("Cyber Security", "", CompType.WRITTEN),
            new Competition("Database Design and Applications", "", CompType.PRODUCTION),
            new Competition("Digital Video Production", "", CompType.TECH),
            new Competition("E-Business", "", CompType.TECH),
            new Competition("Economics", "", CompType.WRITTEN),
            new Competition("Electronic Career Portfolio", "", CompType.TECH),
            new Competition("Emerging Business Issues", "", CompType.SPEAKING),
            new Competition("Entrepreneurship", "", CompType.CASESTUDY),
            new Competition("Future Business Leader", "", CompType.SPEAKING),
            new Competition("Global Business", "", CompType.CASESTUDY),
            new Competition("Graphic Design", "", CompType.TECH),
            new Competition("Health Care Administration", "", CompType.WRITTEN),
            new Competition("Help Desk", "", CompType.CASESTUDY),
            new Competition("Hospitality Management", "", CompType.CASESTUDY),
            new Competition("Impromptu Speaking", "", CompType.SPEAKING),
            new Competition("Insurance and Risk Management", "", CompType.WRITTEN),
            new Competition("Introduction to Business", "", CompType.WRITTEN),
            new Competition("Introduction to Business Communication", "", CompType.WRITTEN),
            new Competition("Introduction to Business Presentation", "", CompType.SPEAKING),
            new Competition("Introduction to Business Procedures", "", CompType.WRITTEN),
            new Competition("Introduction to FBLA", "", CompType.WRITTEN),
            new Competition("Introduction to Financial Math", "", CompType.WRITTEN),
            new Competition("Introduction to Information Technology", "", CompType.WRITTEN),
            new Competition("Introduction to Parliamentary Procedure", "", CompType.WRITTEN),
            new Competition("Introduction to Public Speaking", "", CompType.SPEAKING),
            new Competition("Job Interview", "", CompType.SPEAKING),
            new Competition("Journalism", "", CompType.WRITTEN),
            new Competition("LifeSmarts", "", CompType.WRITTEN),
            new Competition("Local Chapter Annual Business Report", "", CompType.SPEAKING),
            new Competition("Management Decision Making", "", CompType.CASESTUDY),
            new Competition("Management Information Systems", "", CompType.CASESTUDY),
            new Competition("Marketing", "", CompType.CASESTUDY),
            new Competition("Mobile Application Development", "", CompType.TECH),
            new Competition("Network Design", "", CompType.CASESTUDY),
            new Competition("Networking Concepts", "", CompType.WRITTEN),
            new Competition("Organizational Leadership", "", CompType.WRITTEN),
            new Competition("Parliamentary Procedure", "", CompType.SPEAKING),
            new Competition("Partnership with Business Project", "", CompType.PROJECT),
            new Competition("Personal Finance", "", CompType.WRITTEN),
            new Competition("Political Science", "", CompType.WRITTEN),
            new Competition("Public Service Announcement", "", CompType.SPEAKING),
            new Competition("Public Speaking", "", CompType.SPEAKING),
            new Competition("Publication Design", "", CompType.TECH),
            new Competition("Sales Presentation", "", CompType.SPEAKING),
            new Competition("Securities and Investments", "", CompType.WRITTEN),
            new Competition("Social Media Campaign", "", CompType.SPEAKING),
            new Competition("Sports and Entertainment Management", "", CompType.CASESTUDY),
            new Competition("Spreadsheet Applications", "", CompType.PRODUCTION),
            new Competition("Virtual Business Finance Challenge", "", CompType.SPEAKING),
            new Competition("Virtual Business Management Challenge", "", CompType.SPEAKING),
            new Competition("Website Design", "", CompType.TECH),
            new Competition("Word Processing", "", CompType.PRODUCTION),

    };

    public Chapter() {
        this("");
    }

    public Chapter(String name) {
        this.name = name;
        users = new ArrayList<String>();
        events = new ArrayList<Event>();
    }

    public void addMember(String user) {
        users.add(user);
    }

    public void removeMember(User user) {
        users.remove(user);
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void removeEvent(Event event) {
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

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
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

//    public static Competition[] getComps() {
//        return comps;
//    }


}
