package com.hhsfbla.mad.data;

public enum CompType {

    TECH {
        public String toString() {
            return "Tech";
        }
    }, WRITTEN {
        public String toString() {
            return "Written";
        }
    }, SPEAKING {
        public String toString() {
            return "Speaking";
        }
    }, PRODUCTION {
        public String toString() {
            return "Production";
        }
    }, PROJECT {
        public String toString() {
            return "Project";
        }
    }, CASESTUDY {
        public String toString() {
            return "Case Study";
        }
    }
}
