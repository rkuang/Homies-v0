package edu.ucsb.cs.cs190i.rkuang.homies.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import edu.ucsb.cs.cs190i.rkuang.homies.R;
import edu.ucsb.cs.cs190i.rkuang.homies.models.User;

import static edu.ucsb.cs.cs190i.rkuang.homies.R.layout.fragment_user_profile;

/**
 * Created by Ky on 6/10/2017.
 */

public class UserProfileFragment extends DialogFragment {

    private static String uid;

    public UserProfileFragment() {

    }

    public static UserProfileFragment newInstance(String user_id) {
        Bundle args = new Bundle();
        UserProfileFragment fragment = new UserProfileFragment();
        fragment.setArguments(args);

        uid = user_id;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ImageView profile_pic = (ImageView) view.findViewById(R.id.profpic);
        TextView username     = (TextView)  view.findViewById(R.id.username);

        Bundle b = this.getArguments();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");

        String name = b.getString("name");
        //String email = db.child(uid).child("name").toString(); //WILL NOT WORK BECAUSE EMAIL NOT STORED ON DB
        String photoURL = b.getString("pic");
//                String thisUid = b.getString("pic");
        Picasso.with(this.getContext()).load(photoURL).resize(500,500).into(profile_pic);
//        String dispUname = username.getText() + " " + name;
        //String dispUmail = user_email.getText() + " " + email;
        //String dispUid = userid.getText() + " " + uid;
        username.setText(name);
        //user_email.setText(dispUmail);
        //userid.setText(dispUid);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        DisplayMetrics metrics = getResources().getDisplayMetrics();
//        int width = metrics.widthPixels;
//        int height = metrics.heightPixels;
//
//        Window params = this.getDialog().getWindow();
//        params.setLayout(8*width/10, 8*height/10);
//    }
}
