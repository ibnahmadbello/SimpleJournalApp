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
import java.util.Locale;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder>{

    // Constant for date format
    private static final String DATE_FORMAT = "dd/MM/yyyy";

    // Member variable to handle item clicks
    private ItemClickListener mItemClickListener;

    private Context context;
    private List<JournalEntry> mJournalEntries;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    public JournalAdapter(Context context, ItemClickListener listener){
        this.context = context;
        mItemClickListener = listener;
    }

    public class JournalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView dot;
        public TextView timestamp;
        public TextView journaldetail;

        public JournalViewHolder(View view){
            super(view);
            dot = view.findViewById(R.id.dot);
            timestamp = view.findViewById(R.id.timestamp);
            journaldetail = view.findViewById(R.id.journal);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = mJournalEntries.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
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

        // Determine the values of the wanted data
        JournalEntry journalEntry = mJournalEntries.get(position);
        String journal = journalEntry.getJournal();
        String dueDate = dateFormat.format(journalEntry.getTimestamp());


        holder.journaldetail.setText(journal);

        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));

        // Formatting and displaying timestamp
        holder.timestamp.setText(dueDate);

    }

    @Override
    public int getItemCount() {
        return mJournalEntries.size();
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

    public List<JournalEntry> getJournalEntries(){
        return mJournalEntries;
    }

    // When data changes, this method updates the list of taskEntries
    // and notifies the adapter to use the new values on it
    public void setJournals(List<JournalEntry> journalEntries){
        mJournalEntries = journalEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener{
        void onItemClickListener(int itemId);
    }

}
