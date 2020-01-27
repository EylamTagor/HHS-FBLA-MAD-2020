package com.hhsfbla.mad.data;

import java.util.ArrayList;

public class Competition {

    private ArrayList<String> competitors;
    private CompType compType;
    private String name;
    private String description;

    public Competition(String name, String description, CompType compType) {
        this.name = name;
        this.description = description;
        this.compType = compType;
    }

    public void addCompetitor(String user) {
        competitors.add(user);
    }

    public void removeCompetitor(User user) {
        competitors.remove(user);
    }

    public ArrayList<String> getCompetitors() {
        return competitors;
    }

    public void setCompetitors(ArrayList<String> competitors) {
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
