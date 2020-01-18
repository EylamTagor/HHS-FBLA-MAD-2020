package com.hhsfbla.mad.data;

import java.util.ArrayList;

public class Competition {

    private ArrayList<User> competitors;
    private CompType compType;
    private String name;
    private String description;

    public Competition(String name, String description, CompType compType) {
        this.name = name;
        this.description = description;
        this.compType = compType;
    }

    public void addCompetitor(User user) {
        competitors.add(user);
    }

    public void removeCompetitor(User user) {
        competitors.remove(user);
    }

    public ArrayList<User> getCompetitors() {
        return competitors;
    }

    public void setCompetitors(ArrayList<User> competitors) {
        this.competitors = competitors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
