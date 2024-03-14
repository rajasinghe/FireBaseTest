package com.example.testproject76312;

public class Notes {
private String date;
private String title;
private String note;

private String id;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Notes(String date, String title, String note, String id) {
        this.date = date;
        this.title = title;
        this.note = note;
        this.id = id;
    }

    public Notes(String date, String title, String note) {
        this.date = date;
        this.title = title;
        this.note = note;
    }
    public Notes(){
        //empty constructor
    }
}
