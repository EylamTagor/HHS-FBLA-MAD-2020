package com.hhsfbla.mad.data;

import com.hhsfbla.mad.R;
import com.hhsfbla.mad.adapters.CompsAdapter;

import java.util.ArrayList;

/**
 * Represents an FBLA competitive event, with a competition type such as written, speaking, or tech, a name, a description, and a picture
 */
public class Competition {

    private CompType compType;
    private String name;
    private String description;
    private int pic;

    /**
     * Creates a new competition with no name, description, tpye, or picture
     */
    public Competition() {
        this("", "", null, 0);
    }

    /**
     * Creates a new competition with the given name, description, type, and picture
     * @param name the name of the competition
     * @param description a description of the competition
     * @param compType the type of competition
     * @param pic a picture representing the competition
     */
    public Competition(String name, String description, CompType compType, int pic) {
        this.name = name;
        this.description = description;
        this.compType = compType;
        this.pic = pic;
    }

    /**
     * @return the type of competition
     */
    public CompType getCompType() {
        return compType;
    }

    /**
     * Sets the type of this competition
     * @param compType the new competition tpye
     */
    public void setCompType(CompType compType) {
        this.compType = compType;
    }

    /**
     * @return the id of the picture representing the competition
     */
    public int getPic() {
        return pic;
    }

    /**
     * Sets the picture of the competition
     * @param pic the id of the new picture
     */
    public void setPic(int pic) {
        this.pic = pic;
    }

    /**
     * @return the name of the competition
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the competition
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description of the competition
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the competition
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
