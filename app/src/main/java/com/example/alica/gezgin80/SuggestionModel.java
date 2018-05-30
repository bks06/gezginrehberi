package com.example.alica.gezgin80;

import java.util.List;

/**
 * Created by alica on 26.04.2018.
 */

public class SuggestionModel {
    private String key;
    private String header;
    private String exp;
    private List<String> locationNames;
    private List<String> locations;
    private int point;

    public SuggestionModel(String key, String header, String exp, List<String> locationNames, List<String> locations, int point){
        this.key = key;
        this.header = header;
        this.exp = exp;
        this.locationNames = locationNames;
        this.locations = locations;
        this.point=point;
    }
    public String getKey() {
        return key;
    }

    public String getHeader() {
        return header;
    }

    public String getExp() {
        return exp;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public List<String> getLocationNames() {
        return locationNames;
    }

    public List<String> getLocation() {
        return locations;
    }

    public void setLocation(List<String> location) {
        this.locations = locations;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
