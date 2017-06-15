package edu.ucsb.cs.cs190i.rkuang.homies.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.ucsb.cs.cs190i.rkuang.homies.R;
import edu.ucsb.cs.cs190i.rkuang.homies.adapters.PostAdapter;
import edu.ucsb.cs.cs190i.rkuang.homies.models.Item;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventDetailsFragment extends Fragment {

    private FirebaseDatabase db;
    private DatabaseReference eventItemsRef;

    private TextView dateTimeTextView;
    private TextView eventNameTextView;
    private TextView plannerTextView;
    private TextView descriptionTextView;
    private TextView emptyView;

    private FloatingActionButton newPostButton;

    private RecyclerView recyclerView;

    private String eventName;
    private String description;
    private String hostName;
    private String date;
    private String time;
    private String uuid;

    private PostAdapter postAdapter;

    public EventDetailsFragment() {
        // Required empty public constructor
    }


    public static EventDetailsFragment newInstance() {
        Bundle args = new Bundle();
        EventDetailsFragment fragment = new EventDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = FirebaseDatabase.getInstance();
        eventItemsRef = db.getReference();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_event_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        postAdapter = new PostAdapter();

        Bundle b = this.getArguments();
        hostName = b.getString("name");
        eventName = b.getString("eventname");
        date = b.getString("date");
        time = b.getString("time");
        description = b.getString("details");
        uuid = b.getString("uuid");


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        dateTimeTextView = (TextView) view.findViewById(R.id.eventtime_textview);
        eventNameTextView = (TextView) view.findViewById(R.id.eventname_textview);
        plannerTextView = (TextView) view.findViewById(R.id.eventplanner_textview);
        descriptionTextView = (TextView) view.findViewById(R.id.eventdescription_textview);
        emptyView = (TextView) view.findViewById(R.id.noposts_textview);

        eventNameTextView.setText(eventName);
        plannerTextView.setText(hostName);
        dateTimeTextView.setText(date + '\n' + time);
        descriptionTextView.setText(description);

        recyclerView = (RecyclerView) view.findViewById(R.id.eventposts_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(postAdapter);
        eventItemsRef = db.getReference("events").child(uuid).child("posts");


        newPostButton = (FloatingActionButton) view.findViewById(R.id.addeventpost_button);
        newPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle s = new Bundle();
                s.putString("uuid", uuid);
                CreatePostInEventFragment dialogFragment = CreatePostInEventFragment.newInstance();
                dialogFragment.setArguments(s);
                dialogFragment.show(getFragmentManager(), "fragment_create_event_post");
            }
        });


        eventItemsRef.orderByChild("date").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Item item = dataSnapshot.getValue(Item.class);
                Log.i(TAG, "onChildAdded: added new item");
                emptyView.setVisibility(View.GONE);
                postAdapter.addItem(item);
                recyclerView.scrollToPosition(0);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Item item = dataSnapshot.getValue(Item.class);
                postAdapter.removeItem(item);
                if(postAdapter.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
