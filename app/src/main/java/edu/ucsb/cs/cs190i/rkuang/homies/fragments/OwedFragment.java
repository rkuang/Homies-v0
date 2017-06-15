package edu.ucsb.cs.cs190i.rkuang.homies.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import edu.ucsb.cs.cs190i.rkuang.homies.R;
import edu.ucsb.cs.cs190i.rkuang.homies.adapters.OweAdapter;
import edu.ucsb.cs.cs190i.rkuang.homies.models.Owe;

/**
 * Created by Ky on 6/14/2017.
 */

public class OwedFragment extends android.support.v4.app.Fragment {
    private RecyclerView recyclerView;
    private OweAdapter oweAdapter;

    private TextView emptyView;
    private TextView oweMessage;

    private FirebaseDatabase db;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseDatabase.getInstance();
        return inflater.inflate(R.layout.fragment_owed, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        emptyView = (TextView) view.findViewById(R.id.empty);
        oweMessage = (TextView) view.findViewById(R.id.owe_message);
        oweMessage.setText(R.string.youre_owed);

        oweAdapter = new OweAdapter(1);

        recyclerView = (RecyclerView) view.findViewById(R.id.mymoney);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(oweAdapter);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dref = db.getReference();
        dref.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("owed").exists()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    Iterator<DataSnapshot> snaps = dataSnapshot.child("owed").getChildren().iterator();
                    while (snaps.hasNext()) {
                        DataSnapshot item = snaps.next();
                        double amt = ((Number)item.child("amount").getValue()).doubleValue();
                        String itemid = item.child("itemid").getValue().toString();
                        Owe youreOwed = new Owe(itemid,amt);
                        oweAdapter.addPayers(youreOwed);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}