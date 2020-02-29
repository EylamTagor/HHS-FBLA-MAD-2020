package com.hhsfbla.mad.data;

import java.util.ArrayList;

/**
 * Represents an event held by a chapter, with a date and time, member attendees, a name and a location
 */
public class ChapterEvent {

    private String name;
    private ArrayList<String> attendees;
    private String date;
    private String time;
    private String location;
    private String description;
    private String facebookLink;
    private String pic;
    private int memberLimit;
    public static final int NO_LIMIT = -1;

    /**
     * Creates a new chapter event with no name, date, time, location, description, or facebook link
     */
    public ChapterEvent() {
        this("", "", "", "", "", "", null, NO_LIMIT);
    }

    /**
     * Creates a new chapter event with the given parameters
     * @param name the name of the event
     * @param date the date of the event
     * @param time the time of the event
     * @param location the location of the event
     * @param description a description of the event
     * @param facebookLink a facebook link to the event
     */
    public ChapterEvent(String name, String date, String time, String location, String description, String facebookLink, String pic, int limit) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.facebookLink = facebookLink;
        this.pic = pic;
        attendees = new ArrayList<String>();
        this.memberLimit = limit;
    }

    /**
     * Adds an attendee to the event
     * @param user the id of the new attendee
     */
    public void addAttendee(String user) {
        attendees.add(user);
    }

    /**
     * Removes an attendee from the event
     * @param user the id of the attendee to be removed
     */
    public void removeAttendee(User user) {
        attendees.remove(user);
    }

    /**
     * @return the name of the event
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the event
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return a list of the ids of the attendees of the event
     */
    public ArrayList<String> getAttendees() {
        return attendees;
    }

    /**
     * Sets the attendees of the event
     * @param attendees the new list of attendees
     */
    public void setAttendees(ArrayList<String> attendees) {
        this.attendees = attendees;
    }

    /**
     * @return the date of the event in the format mm/dd/yyy
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the event
     * @param date the new date of the event in the format mm/dd/yyyy
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the location of the event
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location of the event
     * @param location the new location of the event
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return a short description of the event
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the event
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the time of the event in the format hh:mm (military time)
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets the time of the event
     * @param time the new time of the event in the format hh:mm (military time)
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * @return the uri of the event picture
     */
    public String getPic() {
        return pic;
    }

    /**
     * Sets the uri of the event picture
     * @param pic the new uri
     */
    public void setPic(String pic) {
        this.pic = pic;
    }

    /**
     * @return a link to the facebook event corresponding to this event
     */
    public String getFacebookLink() {
        return facebookLink;
    }

    /**
     * Sets the facebook link of this event
     * @param facebookLink the new facebook link
     */
    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }

    /**
     * @return the limit to the number of members that can sign up for this event
     */
    public int getMemberLimit() {
        return memberLimit;
    }

    /**
     * Sets the new member limit for this event
     * @param memberLimit the new limit
     */
    public void setMemberLimit(int memberLimit) {
        this.memberLimit = memberLimit;
    }
}
