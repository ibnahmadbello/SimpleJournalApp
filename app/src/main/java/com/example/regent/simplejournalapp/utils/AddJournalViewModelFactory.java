package com.example.regent.simplejournalapp.utils;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.example.regent.simplejournalapp.database.model.AppDatabase;

public class AddJournalViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mJournalId;

    public AddJournalViewModelFactory(AppDatabase database, int journalId){
        mDb = database;
        mJournalId = journalId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass){
        return (T) new AddJournalViewModel(mDb, mJournalId);
    }

}
