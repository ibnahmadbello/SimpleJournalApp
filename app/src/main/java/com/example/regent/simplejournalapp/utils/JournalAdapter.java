package com.example.regent.simplejournalapp.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.regent.simplejournalapp.R;
import com.example.regent.simplejournalapp.database.model.JournalEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder>{

    private Context context;
    private List<JournalEntry> mJournalEntryList;

    public JournalAdapter(Context context, List<JournalEntry> journalEntryList){
        this.context = context;
        this.mJournalEntryList = journalEntryList;
    }

    public class JournalViewHolder extends RecyclerView.ViewHolder{
        public TextView dot;
        public TextView timestamp;
        public TextView journaldetail;

        public JournalViewHolder(View view){
            super(view);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
            journaldetail = view.findViewById(R.id.journal);
        }

    }

    @Override
    public JournalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.journal_item_row, parent, false);
        return new JournalViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(JournalViewHolder holder, int position) {

        JournalEntry journalEntry = mJournalEntryList.get(position);

        holder.journaldetail.setText(journalEntry.getJournal());

        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));

        // Formatting and displaying timestamp
        holder.timestamp.setText(formatDate(journalEntry.getTimestamp()));

    }

    @Override
    public int getItemCount() {
        return mJournalEntryList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-06-28 15:32:42
     * Output: June 28
     */
    private String formatDate(String dateString) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateString);
//            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            SimpleDateFormat fmtOut = new SimpleDateFormat("EEEE, MMM d, yyyy HH:mm");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }

}
