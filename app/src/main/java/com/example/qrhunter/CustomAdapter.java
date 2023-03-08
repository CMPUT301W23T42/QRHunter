package com.example.qrhunter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<UserListItem>{

    private TextView usernameTextView;
    private TextView scoreTextView;

    public CustomAdapter(Context context, ArrayList<UserListItem> usernames) {
        super(context, 0, usernames);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.player_list_items, parent, false);
        }else{
            view = convertView;
        }

        UserListItem userListItem = getItem(position);

        usernameTextView = view.findViewById(R.id.player_username_text_view);
        scoreTextView = view.findViewById(R.id.player_score_text_view);

        usernameTextView.setText(userListItem.getUsername());
        scoreTextView.setText(String.valueOf(userListItem.getScore()));

        return view;

    }
}
