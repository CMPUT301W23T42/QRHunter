package com.example.qrhunter.searchPlayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qrhunter.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchAdapter extends ArrayAdapter<UserListItem> implements Filterable {
    private ArrayList<UserListItem> originalUsernames; // Keep a copy of original data
    private ArrayList<UserListItem> filteredUsernames;
    private UserFilter userFilter;
    private TextView usernameTextView;
    private TextView scoreTextView;
    private TextView numberingTextView;

    public SearchAdapter(Context context, ArrayList<UserListItem> usernames) {
        super(context, 0, usernames);
        this.filteredUsernames = new ArrayList<>(usernames);
        this.originalUsernames = new ArrayList<>(usernames);
    }

    public void sortFilteredScores() {
        Collections.sort(filteredUsernames, new Comparator<UserListItem>() {
            @Override
            public int compare(UserListItem u1, UserListItem u2) {
                return Integer.compare(u2.getScore(), u1.getScore());
            }
        });
    }

    public void sortOriginalScores() {
        Collections.sort(originalUsernames, new Comparator<UserListItem>() {
            @Override
            public int compare(UserListItem u1, UserListItem u2) {
                return Integer.compare(u2.getScore(), u1.getScore());
            }
        });
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.player_list_items, parent, false);
        } else {
            view = convertView;
        }

        usernameTextView = view.findViewById(R.id.player_username_text_view);
        scoreTextView = view.findViewById(R.id.player_score_text_view);
        numberingTextView = view.findViewById(R.id.numbering_text_view);

        if (filteredUsernames.size() > 0 && position < filteredUsernames.size()) {
            UserListItem userListItem = filteredUsernames.get(position);

            usernameTextView.setText(userListItem.getUsername());
            scoreTextView.setText(String.valueOf(userListItem.getScore()));
        } else {
            // Set text views to empty strings for empty items
            usernameTextView.setText("");
            scoreTextView.setText("");
        }

        numberingTextView.setText(String.valueOf(position + 1));

        return view;
    }


    @Override
    public Filter getFilter() {
        if (userFilter == null) {
            userFilter = new UserFilter();
        }
        return userFilter;
    }

    public ArrayList<UserListItem> getFilteredList(){
        return filteredUsernames;
    }

    private class UserFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            ArrayList<UserListItem> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                // If no constraint given, return the original data
                filteredList.addAll(originalUsernames);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (UserListItem username : originalUsernames) {
                    if (username.getUsername().toLowerCase().contains(filterPattern)) {
                        filteredList.add(username);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredUsernames.clear();
            if (results.count > 0) {
                filteredUsernames.addAll((ArrayList<UserListItem>) results.values);
            }
            notifyDataSetChanged();
        }

    }
}