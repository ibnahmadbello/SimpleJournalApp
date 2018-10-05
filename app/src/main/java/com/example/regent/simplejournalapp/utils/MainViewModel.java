package com.example.regent.simplejournalapp.utils;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.regent.simplejournalapp.database.model.AppDatabase;
import com.example.regent.simplejournalapp.database.model.JournalEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();
    private final LiveData<List<JournalEntry>> journal;

    public MainViewModel(@NonNull Application application) {
        super(application);

        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the Database");
        journal = database.taskDao().loadAllJournals();
    }

    public LiveData<List<JournalEntry>> getJournal() {
        return journal;
    }
}
