package com.hhsfbla.mad.data;

import java.util.ArrayList;

/**
 * Represents an FBLA chapter with members and events, and social media
 */
public class Chapter {

    private String name;
    private ArrayList<String> users;
    private ArrayList<ChapterEvent> events;

    private String instagramTag;
    private String facebookPage;
    private String website;
    private String description;
    private String location;

    /**
     * Creates a new Chapter with no name and location, no members or events, and no social media
     */
    public Chapter() {
        this("", "");
    }

    /**
     * Creates a new chapter with name as the name parameter and location as the location parameter, zero members and events
     *
     * @param name
     * @param location
     */
    public Chapter(String name, String location) {
        this.name = name;
        users = new ArrayList<String>();
        events = new ArrayList<ChapterEvent>();
        this.location = location;
    }

    /**
     * Adds a member to the chapter
     *
     * @param user the id of the new member
     */
    public void addMember(String user) {
        users.add(user);
    }

    /**
     * Removes a member from the chapter
     *
     * @param user the id of user to be removed
     */
    public void removeMember(String user) {
        users.remove(user);
    }

    /**
     * Adds an event to the chapter
     *
     * @param event the event to be added
     */
    public void addEvent(ChapterEvent event) {
        events.add(event);
    }

    /**
     * Removes an event from the chapter
     *
     * @param event the event to be removed
     */
    public void removeEvent(ChapterEvent event) {
        events.remove(event);
    }

    /**
     * @return the name of the chapter
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the chapter
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return a list of the ids of the chapter members
     */
    public ArrayList<String> getUsers() {
        return users;
    }

    /**
     * Sets the members of the chapter
     *
     * @param users the new list of members
     */
    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    /**
     * @return a list of the chapter's events
     */
    public ArrayList<ChapterEvent> getEvents() {
        return events;
    }

    /**
     * Sets the events of the chapter
     *
     * @param events the new list of events
     */
    public void setEvents(ArrayList<ChapterEvent> events) {
        this.events = events;
    }

    /**
     * @return the instagram tag of the chapter
     */
    public String getInstagramTag() {
        return instagramTag;
    }

    /**
     * Sets the instagram tag of the chapter
     *
     * @param instagramTag the new instagram tag
     */
    public void setInstagramTag(String instagramTag) {
        this.instagramTag = instagramTag;
    }

    /**
     * @return the facebbok username of the chapter
     */
    public String getFacebookPage() {
        return facebookPage;
    }

    /**
     * Sets the faceboook username of the chapter
     *
     * @param facebookPage the new facebook username
     */
    public void setFacebookPage(String facebookPage) {
        this.facebookPage = facebookPage;
    }

    /**
     * @return a short description of the chapter
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the chapter
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the location of the chapter
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of the chapter
     *
     * @param location the new location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the chapter's website url
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Sets the chapter's website url
     *
     * @param website the new url
     */
    public void setWebsite(String website) {
        this.website = website;
    }
}
