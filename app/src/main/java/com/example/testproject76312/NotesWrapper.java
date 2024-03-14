package com.example.testproject76312;

import java.util.Map;

public class NotesWrapper {
    private Map<String,Notes> notes;

    public NotesWrapper(Map<String, Notes> notes) {
        this.notes = notes;
    }

    public Map<String, Notes> getNotes() {
        return notes;
    }

    public void setNotes(Map<String, Notes> notes) {
        this.notes = notes;
    }
}
