package com.example.qrhunter.qrProfile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.qrhunter.R;

/**
 * This class defines a fragment used to add comments
 */
public class AddCommentFragment extends DialogFragment {
    Context mContext;
    interface AddCommentDialogListener{
        public void addComment(String comment);
    }

    public AddCommentFragment(){
        super();
    }

    private AddCommentDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof AddCommentDialogListener){
            listener = (AddCommentDialogListener) context;
        }else{
            throw new RuntimeException(context+"must implement AddCommentDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.add_comment_fragment,null);
        EditText comment = view.findViewById(R.id.user_comment_context);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        return builder
                .setView(view)
                .setTitle("Add new comment")
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String commentText = comment.getText().toString();
                        listener.addComment(commentText);
                    }
                }).create();
    }
}
