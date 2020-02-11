package com.hhsfbla.mad.data;

import com.hhsfbla.mad.adapters.CompsAdapter;

import java.util.ArrayList;

public class Competition {

//    private ArrayList<String> competitors;
    private CompType compType;
    private String name;
    private String description;
    private int pic;


    public Competition() {
        this("", "", null, 0);
    }

    public Competition(String name, String description, CompType compType, int pic) {
        this.name = name;
        this.description = description;
        this.compType = compType;
        this.pic = pic;
    }


    public CompType getCompType() {
        return compType;
    }

    public void setCompType(CompType compType) {
        this.compType = compType;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }



//    public void addCompetitor(String user) {
//        competitors.add(user);
//    }
//
//    public void removeCompetitor(User user) {
//        competitors.remove(user);
//    }
//
//    public ArrayList<String> getCompetitors() {
//        return competitors;
//    }
//
//    public void setCompetitors(ArrayList<String> competitors) {
//        this.competitors = competitors;
//    }

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
