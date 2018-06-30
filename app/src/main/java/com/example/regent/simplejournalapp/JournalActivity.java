package com.example.regent.simplejournalapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.regent.simplejournalapp.database.JournalHelperDatabase;
import com.example.regent.simplejournalapp.database.model.Journal;
import com.example.regent.simplejournalapp.utils.JournalAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class JournalActivity extends AppCompatActivity implements View.OnClickListener{

    private List<Journal> journalList = new ArrayList<>();
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;

    private JournalAdapter journalAdapter;
    private JournalHelperDatabase helperDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        firebaseAuth = FirebaseAuth.getInstance();
        journalAdapter = new JournalAdapter(this, journalList);

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(journalAdapter);

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);

        helperDatabase = new JournalHelperDatabase(this);

    }

    /**
     * Inserting new journal in db
     * and refreshing the list
     */
    private void createJournal(String journal) {
        // inserting journal in db and getting
        // newly inserted journal id
        long id = helperDatabase.insertJournal(journal);

        // get the newly inserted journal from db
        Journal journal1 = helperDatabase.getJournal(id);

        if (journal1 != null) {
            // adding new journal to array list at 0 position
            journalList.add(0, journal1);

            // refreshing the list
            journalAdapter.notifyDataSetChanged();

        }
    }

    /**
     * Updating journal in the DB and updating
     * item in the list by its position
     */
    private void updateJournal(String journal, int position){
        Journal journal1 = journalList.get(position);

        // updating journal text
        journal1.setJournal(journal);

        // updating quote in DB
        helperDatabase.updateJournal(journal1);

        // refreshing the list
        journalList.set(position, journal1);

        journalAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_out, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_sign_out:
                if (firebaseAuth.getCurrentUser() != null){
                    firebaseAuth.signOut();
                    mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(JournalActivity.this, LoginActivity.class));
                        }
                    });

                } else {
//                    startActivity(new Intent(JournalActivity.this, JournalActivity.class));
                    finish();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                showJournalDialog(false, null, -1);
                return;
        }
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a journal.
     * when shouldUpdate=true, it automatically displays old journal and changes the
     * button text to UPDATE
     */
    private void showJournalDialog(final boolean shouldUpdate, final Journal journal, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.journal_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(JournalActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputJournal = view.findViewById(R.id.journal_detail);
        final String journalText = inputJournal.getText().toString();
        TextView dialogTitle = view.findViewById(R.id.journal_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.new_journal_title) : getString(R.string.update_journal_title));

        if (shouldUpdate && journal != null) {
            inputJournal.setText(journal.getJournal());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputJournal.getText().toString())) {
                    Toast.makeText(JournalActivity.this, "Enter your Journal!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating journal
                if (shouldUpdate && journal != null) {
                    // update journal by it's id
                    updateJournal(inputJournal.getText().toString(), position);
                } else {
                    // create new journal
                    createJournal(journalText);
                }
            }
        });
    }


}
