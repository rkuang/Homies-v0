package edu.ucsb.cs.cs190i.rkuang.homies.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import edu.ucsb.cs.cs190i.rkuang.homies.R;
import edu.ucsb.cs.cs190i.rkuang.homies.fragments.ChargeFragment;
import edu.ucsb.cs.cs190i.rkuang.homies.fragments.UserProfileFragment;
import edu.ucsb.cs.cs190i.rkuang.homies.models.Item;
import edu.ucsb.cs.cs190i.rkuang.homies.models.User;

import static android.content.ContentValues.TAG;

/**
 * Adapter for Items RecyclerView on PostsFragment
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private ArrayList<Item> mData;
    private SimpleDateFormat dateFormat;
    private Context mContext;


    public PostAdapter() {
        mData = new ArrayList<>();
        dateFormat = new SimpleDateFormat("hh:mm a, MM/dd/yyyy", Locale.US);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false);
        mContext = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Item item = mData.get(position);
        final User user = item.getUser();

        final String username = item.getUser().getName();
        String date = dateFormat.format(item.getDate());
        String description = item.getDescription();
        final String uuid = item.getId();

        holder.userTextView.setText(username);
        holder.dateTextView.setText(date);
        holder.itemTextView.setText(description);

        final String avatarURL = mData.get(position).getUser().getImageURL();
        Picasso.with(holder.userAvatar.getContext()).load(avatarURL).into(holder.userAvatar);
        holder.userAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileFragment dialogFragment = UserProfileFragment.newInstance(uuid);
                FragmentActivity fa = (FragmentActivity) mContext;
                FragmentManager fm = fa.getSupportFragmentManager();
                Bundle b = new Bundle();
                b.putString("pic", avatarURL);
                b.putString("name", username);
                dialogFragment.setArguments(b);
                dialogFragment.show(fm , "fragment_user_profile");
            }
        });

        //holder.boughtMarker.setVisibility(View.INVISIBLE);

        if (firebaseUser.getUid().equals(user.getUid())) {
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.INVISIBLE);
        }

        if (item.isBought()) {
            holder.boughtMarker.setVisibility(View.VISIBLE);
        }
        else
            holder.boughtMarker.setVisibility(View.INVISIBLE);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser.getUid().equals(user.getUid())) {
                    if(item.isBought()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("Are you sure you have paid back this person?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseReference dref = FirebaseDatabase.getInstance().getReference();
                                        dref.child("users").child(firebaseUser.getUid()).child("owe").child(item.getId()).removeValue();
                                        dref.child("items").child(item.getId()).removeValue();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("Are you sure you want to delete this post?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (item.getEventID() != null) {
                                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("events")
                                                    .child(item.getEventID()).child("posts");
                                            db.child(item.getId()).removeValue();
                                        } else {
                                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("items");
                                            db.child(item.getId()).removeValue();
                                        }
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }
        });

        holder.buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.isBought()) {
                    Toast.makeText(v.getContext(), "This item has already been bought.", Toast.LENGTH_SHORT).show();
                }
                else {
                    holder.boughtMarker.setVisibility(View.VISIBLE);
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("items");
                    db.child(item.getId()).child("bought").setValue(true);

                    if (!firebaseUser.getUid().equals(user.getUid())) {
                        ChargeFragment charge = new ChargeFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("UserOwe", user.getName());
                        bundle.putString("Itemid", item.getId());
                        charge.setArguments(bundle);
                        charge.show(((Activity) v.getContext()).getFragmentManager(), "Charge User");
                    }
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addItem(Item item) {
        mData.add(0, item);
        Log.i(TAG, "addItem: Item added at " + mData.indexOf(item));
        notifyItemInserted(0);
    }

    public void removeItem(Item item) {
        Log.i(TAG, "removeItem: "+mData.indexOf(item));
        int position = mData.indexOf(item);

        mData.remove(item);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public void notifyItemChanged(Item item) {
        int position = mData.indexOf(item);
        mData.get(position).setBought(true);
        notifyItemChanged(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView userTextView;
        TextView dateTextView;
        TextView itemTextView;
        ImageView boughtMarker;

        ImageView userAvatar;

        ImageButton delete;
        ImageButton buy;

        ViewHolder(View itemView) {
            super(itemView);
            userTextView = (TextView) itemView.findViewById(R.id.user_textview);
            dateTextView = (TextView) itemView.findViewById(R.id.date_textview);
            itemTextView = (TextView) itemView.findViewById(R.id.item_textview);
            boughtMarker = (ImageView) itemView.findViewById(R.id.BOUGHT);

            userAvatar = (ImageView) itemView.findViewById(R.id.user_avatar);

            delete = (ImageButton) itemView.findViewById(R.id.delete_button);
            buy = (ImageButton) itemView.findViewById(R.id.buy_button);
        }
    }

    public boolean isEmpty(){
        if(getItemCount() == 0) return true;
        Log.i(TAG, "isEmpty: " + getItemCount());
        return false;
    }
}
