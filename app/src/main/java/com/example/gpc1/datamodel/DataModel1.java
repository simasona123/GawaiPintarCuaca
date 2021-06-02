package com.example.gpc1.datamodel;

public class DataModel1 {
    private int id;
    private String timestamp;
    private String result;

    public DataModel1(String timestamp, String result) {
        this.timestamp = timestamp;
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
