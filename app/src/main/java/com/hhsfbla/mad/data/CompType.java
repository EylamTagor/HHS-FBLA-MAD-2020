package com.hhsfbla.mad.data;

/**
 * Represents a variable made up by all of the different competitive event types
 */
public enum CompType {

    /**
     * Tech competitions
     */
    TECH {
        public String toString() {
            return "Tech";
        }
    },
    /**
     * Written competitions
     */
    WRITTEN {
        public String toString() {
            return "Written";
        }
    },
    /**
     * Speaking competitions
     */
    SPEAKING {
        public String toString() {
            return "Speaking";
        }
    },
    /**
     * Production competitions
     */
    PRODUCTION {
        public String toString() {
            return "Production";
        }
    },
    /**
     * Projects
     */
    PROJECT {
        public String toString() {
            return "Project";
        }
    },
    /**
     * Case studies
     */
    CASESTUDY {
        public String toString() {
            return "Case Study";
        }
    }
}
