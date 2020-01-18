package com.hhsfbla.mad.data;

public enum UserType {

    MEMBER {
        public String toString() {
            return "Member";
        }
    }, OFFICER {
        public String toString() {
            return "Officer";
        }
    }, ADVISOR {
        public String toString() {
            return "Advisor";
        }
    }

}
