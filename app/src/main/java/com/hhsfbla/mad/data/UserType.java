package com.hhsfbla.mad.data;

/**
 * Represents all the different types a user can be
 */
public enum UserType {

    /**
     * Member, lowest level
     */
    MEMBER {
        public String toString() {
            return "Member";
        }
    },
    /**
     * Officer: added ability to create and manage events
     */
    OFFICER {
        public String toString() {
            return "Officer";
        }
    },
    /**
     * Advisor: added ability to create and manage events as well as members and officers of the chapter
     */
    ADVISOR {
        public String toString() {
            return "Advisor";
        }
    }

}
