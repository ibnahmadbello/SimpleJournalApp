package com.example.regent.simplejournalapp.database.model;

public class Journal {
    public static final String TABLE_NAME = "journals";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_JOURNAL_DETAIL = "journal";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String journal;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_JOURNAL_DETAIL + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public Journal() {
    }

    public Journal(int id, String journal, String timestamp) {
        this.id = id;
        this.journal = journal;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
