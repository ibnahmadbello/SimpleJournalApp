package com.example.regent.simplejournalapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.regent.simplejournalapp.database.model.AppDatabase;
import com.example.regent.simplejournalapp.database.model.JournalEntry;
import com.example.regent.simplejournalapp.utils.AddJournalViewModel;
import com.example.regent.simplejournalapp.utils.AddJournalViewModelFactory;
import com.example.regent.simplejournalapp.utils.AppExecutors;

import java.util.Date;

public class AddJournalActivity extends AppCompatActivity {

    private static final String TAG = AddJournalActivity.class.getSimpleName();

    // Extra for the journal ID to be received in the intent
    public static final String EXTRA_JOURNAL_ID = "extraJournalId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_JOURNAL_ID = "instanceJournalId";

    // Constant for default journal id to be used when not in update mode
    private static final int DEFAULT_JOURNAL_ID = -1;

    EditText mEditText;
    Button mButton;

    private int mJournalId = DEFAULT_JOURNAL_ID;

    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        initViews();

        mDb = AppDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_JOURNAL_ID)){
            mJournalId = savedInstanceState.getInt(INSTANCE_JOURNAL_ID, DEFAULT_JOURNAL_ID);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_JOURNAL_ID)){
            mButton.setText(R.string.update_button);
            if (mJournalId == DEFAULT_JOURNAL_ID){
                // populate the UI
                mJournalId = intent.getIntExtra(EXTRA_JOURNAL_ID, DEFAULT_JOURNAL_ID);

                AddJournalViewModelFactory factory = new AddJournalViewModelFactory(mDb, mJournalId);

                final AddJournalViewModel viewModel = ViewModelProviders.of(this, factory).get(AddJournalViewModel.class);

                viewModel.getJournal().observe(this, new Observer<JournalEntry>() {
                    @Override
                    public void onChanged(@Nullable JournalEntry journalEntry) {
                        viewModel.getJournal().removeObserver(this);
                        populateUI(journalEntry);
                    }
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_JOURNAL_ID, mJournalId);
        super.onSaveInstanceState(outState);
    }

    private void initViews(){
        mEditText = findViewById(R.id.edit_text);
        mButton = findViewById(R.id.save_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    private void populateUI(JournalEntry entry){
        if (entry == null){
            return;
        }

        mEditText.setText(entry.getJournal());
    }

    public void onSaveButtonClicked(){
        String journal = mEditText.getText().toString();
        Date date = new Date();

        final JournalEntry journalEntry = new JournalEntry(journal, date);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (mJournalId == DEFAULT_JOURNAL_ID){
                    mDb.taskDao().insertJournal(journalEntry);
                } else {
                    journalEntry.setId(mJournalId);
                    mDb.taskDao().updateJournal(journalEntry);
                }
                finish();
            }
        });
    }
}
