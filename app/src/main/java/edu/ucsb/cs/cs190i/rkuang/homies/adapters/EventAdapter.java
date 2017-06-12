package edu.ucsb.cs.cs190i.rkuang.homies.adapters;

import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import edu.ucsb.cs.cs190i.rkuang.homies.R;
import edu.ucsb.cs.cs190i.rkuang.homies.models.Event;
import edu.ucsb.cs.cs190i.rkuang.homies.models.Item;
import edu.ucsb.cs.cs190i.rkuang.homies.models.User;

import static android.content.ContentValues.TAG;

/**
 * Created by Timothy Kwong on 6/9/2017.
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    ArrayList<Event> mData;
    SimpleDateFormat dateFormat;
    Context mContext;
    public static boolean new_post;

    public EventAdapter() {
        mData = new ArrayList<>();
        new_post = false;
    }

    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_layout_simple , parent, false);
        mContext = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventAdapter.ViewHolder holder, int position) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Event event = mData.get(position);
        final User user = event.getUser();

        String name = event.getEventName();
        String time = event.getEventTime();

        holder.eventTextView.setText(name);
        holder.dateTimeTextView.setText(time);

        if (firebaseUser.getUid().equals(user.getUid())) {
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.INVISIBLE);
        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser.getUid().equals(user.getUid())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Are you sure you want to delete this post?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("events");
                                    db.child(event.getId()).removeValue();
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
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addItem(Event event) {
        mData.add(0, event);
        Log.i(TAG, "addItem: Event added at " + mData.indexOf(event));
        notifyItemInserted(0);
    }

    public void removeEvent(Event event) {
        Log.i(TAG, "removeItem: "+mData.indexOf(event));
        int position = mData.indexOf(event);

        mData.remove(event);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView eventTextView;
        public TextView dateTimeTextView;
        public ImageButton delete;


        public ViewHolder(View eventView) {
            super(eventView);
            eventTextView = (TextView) eventView.findViewById(R.id.eventname_textview);
            dateTimeTextView = (TextView) eventView.findViewById(R.id.datetime_textview);
            delete = (ImageButton) eventView.findViewById(R.id.delete_button2);


        }

        @Override
        public void onClick(View v) {
            }
    }

    public boolean isEmpty(){
        if(getItemCount() == 0) return true;
        Log.i(TAG, "isEmpty: " + getItemCount());
        return false;
    }
}
