package com.example.regent.simplejournalapp.utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.regent.simplejournalapp.database.model.AppDatabase;
import com.example.regent.simplejournalapp.database.model.JournalEntry;

public class AddJournalViewModel extends ViewModel {

    private LiveData<JournalEntry> journal;

    public AddJournalViewModel(AppDatabase database, int journalId){
        journal = database.taskDao().loadJournalById(journalId);
    }

    public LiveData<JournalEntry> getJournal() {
        return journal;
    }
}
