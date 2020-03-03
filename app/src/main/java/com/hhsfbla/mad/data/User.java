package com.hhsfbla.mad.data;

import java.util.ArrayList;

/**
 * Represents a user of the app, a chapter member with a name, chapter, user type, email,
 * competitive event list, contact info,  and profile picture
 */
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

    /**
     * Creates a new user with no name, chapter, or email
     */
    public User() {
        this("", null, "");
    }

    /**
     * Creates a new user with the given name, chapter, and email
     *
     * @param name    the name of the user
     * @param chapter the id of the chapter the user belogns to
     * @param email   the email of the user
     */
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

    /**
     * @return the name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id of the chapter the user belongs to
     */
    public String getChapter() {
        return chapter;
    }

    /**
     * Sets the chapter the user belongs to
     *
     * @param chapter the id of the new chapter
     */
    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    /**
     * @return the type of user: member, officer, or advisor
     */
    public UserType getUserType() {
        return userType;
    }

    /**
     * Sets the type of user this is
     *
     * @param userType the new user type
     */
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    /**
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user
     *
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return a list of the ids of all the chapter events that the user has signed up for
     */
    public ArrayList<String> getMyEvents() {
        return myEvents;
    }

    /**
     * Sets the list of events the user has signed up for
     *
     * @param myEvents the new list of event ids
     */
    public void setMyEvents(ArrayList<String> myEvents) {
        this.myEvents = myEvents;
    }

    /**
     * @return a short blurb made by the user, only applicable to officers
     */
    public String getBlurb() {
        return blurb;
    }

    /**
     * Sets the blurb of the user
     *
     * @param blurb the new blurb
     */
    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    /**
     * @return the phone number of the user
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }


    /**
     * Sets the phone number of the user
     *
     * @param phoneNumber the new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return a list of the names of the competitions the user is competing in
     */
    public ArrayList<String> getComps() {
        return comps;
    }

    /**
     * Sets the list of competitions the user is competing in
     *
     * @param comps the new list of competition names
     */
    public void setComps(ArrayList<String> comps) {
        this.comps = comps;
    }

    /**
     * @return the string value of the uri of the user's profile picture
     */
    public String getPic() {
        return pic;
    }

    /**
     * Sets the profile picture of the user
     *
     * @param pic the string value of the uri of the new image
     */
    public void setPic(String pic) {
        this.pic = pic;
    }

    /**
     * @return the title of the officer
     */
    public String getOfficerPos() {
        return officerPos;
    }

    /**
     * Sets the title of the officer
     *
     * @param officerPos the new title
     */
    public void setOfficerPos(String officerPos) {
        this.officerPos = officerPos;
    }
}
