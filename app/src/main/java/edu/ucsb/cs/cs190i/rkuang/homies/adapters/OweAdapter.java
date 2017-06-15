package edu.ucsb.cs.cs190i.rkuang.homies.adapters;

import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.Locale;

import edu.ucsb.cs.cs190i.rkuang.homies.R;
import edu.ucsb.cs.cs190i.rkuang.homies.models.Owe;

/**
 * Created by Ky on 6/14/2017.
 */

public class OweAdapter extends RecyclerView.Adapter<OweAdapter.ViewHolder> {
    private ArrayList<Owe> payers = new ArrayList<>();
    private int owesOrOwed; //a flag that tells whether we're going to display owes (0) or owed(1) money

    public OweAdapter(int o){
        owesOrOwed = o;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payers , parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(getItemCount() != 0){
            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            final DatabaseReference dref = db.getReference();
            final Owe payer = payers.get(position);
            final double amtOwe = payer.getAmount();
            final String itemid = payer.getItemid();
            if(owesOrOwed == 0){
                dref.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String personYouOwe = dataSnapshot.child("owe").child(itemid).child("name").getValue().toString();
                        holder.payer.setText(personYouOwe);
                        holder.amount.setText(String.format(Locale.US,"$%.2f", amtOwe));
                        dref.child("items").child(itemid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String itemDesc = dataSnapshot.child("description").getValue().toString();
                                holder.item.setText(itemDesc);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            else{
                dref.child("users").child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        holder.amount.setText(String.format(Locale.US,"$%.2f", amtOwe));
                        dref.child("items").child(itemid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String itemDesc = dataSnapshot.child("description").getValue().toString();
                                String personOwesYou = dataSnapshot.child("user").child("name").getValue().toString();
                                holder.payer.setText(personOwesYou);
                                holder.item.setText(itemDesc);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

        }
    }

    @Override
    public int getItemCount() {
        return payers.size();
    }

    public void addPayers(Owe payer) {
        payers.add(payer);
        //Log.i(TAG, "addItem: Item added at " + mData.indexOf(item));
        notifyItemInserted(getItemCount()-1);
    }

    public void removePayer(Owe payer) {
        //Log.i(TAG, "removeItem: "+mData.indexOf(item));
        int position = payers.indexOf(payer);

        payers.remove(payer);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView payer;
        TextView item;
        TextView amount;

        public ViewHolder(View itemView) {
            super(itemView);
            payer = (TextView) itemView.findViewById(R.id.payer);
            item = (TextView) itemView.findViewById(R.id.item);
            amount = (TextView) itemView.findViewById(R.id.amount);
        }
    }
}