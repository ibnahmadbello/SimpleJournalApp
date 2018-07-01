package com.example.regent.simplejournalapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.regent.simplejournalapp.database.model.Journal;

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
        sqLiteDatabase.execSQL(Journal.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older Table if it existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Journal.TABLE_NAME);

        // Create Table again
        onCreate(sqLiteDatabase);
    }

    public long insertJournal(String journal) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(Journal.COLUMN_JOURNAL_DETAIL, journal);

        // insert row
        long id = db.insert(Journal.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public Journal getJournal(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Journal.TABLE_NAME,
                new String[]{Journal.COLUMN_ID, Journal.COLUMN_JOURNAL_DETAIL, Journal.COLUMN_TIMESTAMP},
                Journal.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Journal journal = new Journal(
                cursor.getInt(cursor.getColumnIndex(Journal.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Journal.COLUMN_JOURNAL_DETAIL)),
                cursor.getString(cursor.getColumnIndex(Journal.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return journal;
    }

    public List<Journal> getAllJournal() {
        List<Journal> journals = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Journal.TABLE_NAME + " ORDER BY " +
                Journal.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Journal journal = new Journal();
                journal.setId(cursor.getInt(cursor.getColumnIndex(Journal.COLUMN_ID)));
                journal.setJournal(cursor.getString(cursor.getColumnIndex(Journal.COLUMN_JOURNAL_DETAIL)));
                journal.setTimestamp(cursor.getString(cursor.getColumnIndex(Journal.COLUMN_TIMESTAMP)));

                journals.add(journal);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return journals;
    }

    public int getJournalsCount() {
        String countQuery = "SELECT  * FROM " + Journal.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateJournal(Journal journal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Journal.COLUMN_JOURNAL_DETAIL, journal.getJournal());

        // updating row
        return db.update(Journal.TABLE_NAME, values, Journal.COLUMN_ID + " = ?",
                new String[]{String.valueOf(journal.getId())});
    }

    public void deleteJournal(Journal journal) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Journal.TABLE_NAME, Journal.COLUMN_ID + " = ?",
                new String[]{String.valueOf(journal.getId())});
        db.close();

        getJournalsCount();
    }



}
