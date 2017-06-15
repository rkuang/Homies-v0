package edu.ucsb.cs.cs190i.rkuang.homies.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.UUID;

import edu.ucsb.cs.cs190i.rkuang.homies.R;
import edu.ucsb.cs.cs190i.rkuang.homies.adapters.GroupsAdapter;
import edu.ucsb.cs.cs190i.rkuang.homies.models.Group;
import edu.ucsb.cs.cs190i.rkuang.homies.models.Item;
import edu.ucsb.cs.cs190i.rkuang.homies.models.User;

import static android.content.ContentValues.TAG;
import static edu.ucsb.cs.cs190i.rkuang.homies.R.layout.fragment_create_group;

public class CreateGroupFragment extends DialogFragment {

    DatabaseReference db;

    public CreateGroupFragment() {

    }

    public static CreatePostFragment newInstance() {
        Bundle args = new Bundle();
        CreatePostFragment fragment = new CreatePostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseDatabase.getInstance().getReference();

        return inflater.inflate(fragment_create_group, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final CreateGroupFragment me = this;
        final EditText editText = (EditText) view.findViewById(R.id.description_field);
        Button button = (Button) view.findViewById(R.id.post_message_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: post message button clicked");
                String description = editText.getText().toString();
                if (description.length() != 0) {
                    ArrayList<String> users = new ArrayList<>();
                    users.add("John Doe");
                    Group g = new Group("Group1", 123, users);

                    db.child("groups").child(g.getGroupName()).setValue(g);
                    me.getDialog().dismiss();
                } else {
                    Snackbar.make(me.getView(), "Enter a description", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return d;
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;

        Window params = this.getDialog().getWindow();
        params.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setGravity(Gravity.BOTTOM);
    }
}
