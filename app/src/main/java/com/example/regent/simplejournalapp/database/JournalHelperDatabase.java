package com.example.regent.simplejournalapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.regent.simplejournalapp.database.model.JournalEntry;

import java.util.ArrayList;
import java.util.List;

public class JournalHelperDatabase extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "journals_db";

    public JournalHelperDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create Journals
        sqLiteDatabase.execSQL(JournalEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older Table if it existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + JournalEntry.TABLE_NAME);

        // Create Table again
        onCreate(sqLiteDatabase);
    }

    public long insertJournal(String journal) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(JournalEntry.COLUMN_JOURNAL_DETAIL, journal);

        // insert row
        long id = db.insert(JournalEntry.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public JournalEntry getJournal(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(JournalEntry.TABLE_NAME,
                new String[]{JournalEntry.COLUMN_ID, JournalEntry.COLUMN_JOURNAL_DETAIL, JournalEntry.COLUMN_TIMESTAMP},
                JournalEntry.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        JournalEntry journalEntry = new JournalEntry(
                cursor.getInt(cursor.getColumnIndex(JournalEntry.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(JournalEntry.COLUMN_JOURNAL_DETAIL)),
                cursor.getString(cursor.getColumnIndex(JournalEntry.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return journalEntry;
    }

    public List<JournalEntry> getAllJournal() {
        List<JournalEntry> journalEntries = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + JournalEntry.TABLE_NAME + " ORDER BY " +
                JournalEntry.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                JournalEntry journalEntry = new JournalEntry();
                journalEntry.setId(cursor.getInt(cursor.getColumnIndex(JournalEntry.COLUMN_ID)));
                journalEntry.setJournal(cursor.getString(cursor.getColumnIndex(JournalEntry.COLUMN_JOURNAL_DETAIL)));
                journalEntry.setTimestamp(cursor.getString(cursor.getColumnIndex(JournalEntry.COLUMN_TIMESTAMP)));

                journalEntries.add(journalEntry);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return journalEntries;
    }

    public int getJournalsCount() {
        String countQuery = "SELECT  * FROM " + JournalEntry.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateJournal(JournalEntry journalEntry) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(JournalEntry.COLUMN_JOURNAL_DETAIL, journalEntry.getJournal());

        // updating row
        return db.update(JournalEntry.TABLE_NAME, values, JournalEntry.COLUMN_ID + " = ?",
                new String[]{String.valueOf(journalEntry.getId())});
    }

    public void deleteJournal(JournalEntry journalEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(JournalEntry.TABLE_NAME, JournalEntry.COLUMN_ID + " = ?",
                new String[]{String.valueOf(journalEntry.getId())});
        db.close();

    }



}
