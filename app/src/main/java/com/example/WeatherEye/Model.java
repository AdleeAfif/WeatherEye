package com.example.WeatherEye;

import java.util.ArrayList;

public class Model {


    ArrayList<feeds> feeds;

    public ArrayList<Model.feeds> getFeeds() {
        return feeds;
    }

    public void setFeeds(ArrayList<Model.feeds> feeds) {
        this.feeds = feeds;
    }

    public class feeds {

        String created_at;

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getEntry_id() {
            return entry_id;
        }

        public void setEntry_id(String entry_id) {
            this.entry_id = entry_id;
        }

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public String getField2() {
            return field2;
        }

        public void setField2(String field2) {
            this.field2 = field2;
        }

        public String getField4() {
            return field4;
        }

        public void setField4(String field4) {
            this.field4 = field4;
        }

        String entry_id;
        String field1;
        String field2;
        String field4;
    }
}