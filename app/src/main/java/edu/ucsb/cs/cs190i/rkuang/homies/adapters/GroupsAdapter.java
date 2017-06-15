package edu.ucsb.cs.cs190i.rkuang.homies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import edu.ucsb.cs.cs190i.rkuang.homies.R;
import edu.ucsb.cs.cs190i.rkuang.homies.models.Group;

import static android.content.ContentValues.TAG;

/**
 * Created by Wilson on 6/13/2017.
 */

public class GroupsAdapter extends RecyclerView.Adapter< GroupsAdapter.ViewHolder > {

    private ArrayList<Group> mData;
    private Context mContext;

    public GroupsAdapter(){
        mData = new ArrayList<>();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_group, parent, false);
        mContext = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Group group = mData.get(position);
        final String groupName = group.getGroupName();

        holder.groupName.setText(groupName);


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void addGroup(Group group) {
        mData.add(0, group);
        Log.i(TAG, "addGroup: Group added at " + mData.indexOf(group));
        notifyItemInserted(0);
    }

    public void removeGroup(Group group) {
        Log.i(TAG, "removeGroup: "+mData.indexOf(group));
        int position = mData.indexOf(group);

        mData.remove(group);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView groupName;

        public ViewHolder(View itemView) {
            super(itemView);
            groupName = (TextView) itemView.findViewById(R.id.groupName);
        }
    }
}
