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

/**

This class extends ArrayAdapter<UserListItem> and implements Filterable. It is used as an adapter for a ListView
in order to display a list of UserListItem objects. The class contains two ArrayLists: originalUsernames and
filteredUsernames. originalUsernames is used to keep a copy of the original data and filteredUsernames is used
to store the filtered data. The class also contains a UserFilter object, which is used to filter the data based
on user input. The class provides methods to sort the data based on the score of the UserListItem objects.
*/

public class SearchAdapter extends ArrayAdapter<UserListItem> implements Filterable {
    private ArrayList<UserListItem> originalUsernames; // Keep a copy of original data
    private ArrayList<UserListItem> filteredUsernames; // Store filtered data
    private UserFilter userFilter;
    private TextView usernameTextView;
    private TextView scoreTextView;
    private TextView numberingTextView;

    /**
    Constructor for the SearchAdapter class. Initializes the adapter with a context and an ArrayList of UserListItem
    objects. It also initializes the filteredUsernames and originalUsernames ArrayLists with a copy of the passed in
    ArrayList.
    @param context The context of the adapter
    @param usernames An ArrayList of UserListItem objects
    */

    public SearchAdapter(Context context, ArrayList<UserListItem> usernames) {
        super(context, 0, usernames);
        this.filteredUsernames = new ArrayList<>(usernames);
        this.originalUsernames = new ArrayList<>(usernames);
    }

    /**
    Sorts the filteredUsernames ArrayList based on the score of the UserListItem objects. The sort is performed in
    descending order.
    */
    public void sortFilteredScores() {
        Collections.sort(filteredUsernames, new Comparator<UserListItem>() {
            @Override
            public int compare(UserListItem u1, UserListItem u2) {
                return Integer.compare(u2.getScore(), u1.getScore());
            }
        });
    }
    /**
    Sorts the originalUsernames ArrayList based on the score of the UserListItem objects. The sort is performed in
    descending order.
    */
    public void sortOriginalScores() {
        Collections.sort(originalUsernames, new Comparator<UserListItem>() {
            @Override
            public int compare(UserListItem u1, UserListItem u2) {
                return Integer.compare(u2.getScore(), u1.getScore());
            }
        });
    }

    /**
    Returns a View object that displays the UserListItem object at the specified position in the ListView.
    @param position The position of the UserListItem object to display
    @param convertView The old view to reuse, if possible
    @param parent The parent view
    @return A View object that displays the UserListItem object at the specified position in the ListView
    */
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

    /**
    Returns a filter that can be used to constrain data with a filtering pattern.
    @return a filter used to constrain data
    */
    @Override
    public Filter getFilter() {
        if (userFilter == null) {
            userFilter = new UserFilter();
        }
        return userFilter;
    }

    /**
    Returns the filtered list of usernames after applying a constraint pattern.
    @return the filtered list of usernames
    */
    public ArrayList<UserListItem> getFilteredList(){
        return filteredUsernames;
    }

    /**
    A private class used to perform filtering on the data.
    */
    private class UserFilter extends Filter {

        /**
        Performs filtering on the data based on the specified constraint.
        @param constraint the filtering constraint
        @return the results of the filtering operation
        */
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
                    if (username.getUsername() != null && username.getUsername().toLowerCase().contains(filterPattern)) {
                        filteredList.add(username);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        /**
        Publishes the results of the filtering operation to the UI thread.
        @param constraint the filtering constraint
        @param results the results of the filtering operation
        */
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