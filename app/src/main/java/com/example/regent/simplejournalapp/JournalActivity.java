package com.example.regent.simplejournalapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.regent.simplejournalapp.database.model.AppDatabase;
import com.example.regent.simplejournalapp.database.model.JournalEntry;
import com.example.regent.simplejournalapp.utils.AppExecutors;
import com.example.regent.simplejournalapp.utils.ItemDividerDecoration;
import com.example.regent.simplejournalapp.utils.JournalAdapter;
import com.example.regent.simplejournalapp.utils.RecyclerTouchListener;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class JournalActivity extends AppCompatActivity implements JournalAdapter.ItemClickListener{

    private static final String TAG = JournalActivity.class.getSimpleName();
    private List<JournalEntry> mJournalEntryList = new ArrayList<>();
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    TextView journalCountDisplay;

    private JournalAdapter journalAdapter;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        firebaseAuth = FirebaseAuth.getInstance();
        journalAdapter = new JournalAdapter(this, this);

        recyclerView = findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(journalAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(decoration);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                AppExecutors.getInstance().diskIO().execute(new Runnable(){
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<JournalEntry> journals = journalAdapter.getJournalEntries();
                        mDb.taskDao().deleteJournal(journals.get(position));
                    }
                });
            }
        }).attachToRecyclerView(recyclerView);

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addJournalIntent = new Intent(JournalActivity.this, AddJournalActivity.class);
                startActivity(addJournalIntent);
            }
        });





    }



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



    /**
     * Shows alert dialog with EditText options to enter / edit
     * a journalEntry.
     * when shouldUpdate=true, it automatically displays old journalEntry and changes the
     * button text to UPDATE
     */
    private void showJournalDialog(final boolean shouldUpdate, final JournalEntry journalEntry, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.journal_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(JournalActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputJournal = view.findViewById(R.id.journal_detail);
        TextView dialogTitle = view.findViewById(R.id.journal_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.new_journal_title) : getString(R.string.update_journal_title));

        if (shouldUpdate && journalEntry != null) {
            inputJournal.setText(journalEntry.getJournal());
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
                    Toast.makeText(JournalActivity.this, "Enter your JournalEntry!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating journalEntry
                if (shouldUpdate && journalEntry != null) {
                    // update journalEntry by it's id
                    updateJournal(inputJournal.getText().toString(), position);
                } else {
                    // create new journalEntry
                    createJournal(inputJournal.getText().toString());
                }
            }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        Intent intent = new Intent(JournalActivity.this, AddJournalActivity.class);
        intent.putExtra(AddJournalActivity.EXTRA_TASK_ID, itemId);
        startActivity(intent);
    }
}
