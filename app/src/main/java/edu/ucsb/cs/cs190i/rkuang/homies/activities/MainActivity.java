package edu.ucsb.cs.cs190i.rkuang.homies.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import edu.ucsb.cs.cs190i.rkuang.homies.R;
import edu.ucsb.cs.cs190i.rkuang.homies.fragments.ChargeFragment;
import edu.ucsb.cs.cs190i.rkuang.homies.fragments.GroupsFragment;
import edu.ucsb.cs.cs190i.rkuang.homies.fragments.PostsFragment;
import edu.ucsb.cs.cs190i.rkuang.homies.fragments.OweFragment;
import edu.ucsb.cs.cs190i.rkuang.homies.fragments.OwedFragment;
import edu.ucsb.cs.cs190i.rkuang.homies.models.Owe;
import edu.ucsb.cs.cs190i.rkuang.homies.models.User;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener, ChargeFragment.PostCharge {

    private FirebaseAuth firebaseAuth;
    private GoogleApiClient googleApiClient;
    private String photoURL;
    private String name;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView nav_avatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_avatar);
        TextView nav_name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_name);
        TextView nav_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_email);
        final TextView nav_owed = (TextView) navigationView.getHeaderView(0).findViewById(R.id.owed);
        final TextView nav_owe = (TextView) navigationView.getHeaderView(0).findViewById(R.id.owe);

        final FirebaseDatabase db = FirebaseDatabase.getInstance();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        } else {
            name = firebaseUser.getDisplayName();
            String email = firebaseUser.getEmail();
            photoURL = firebaseUser.getPhotoUrl().toString();
            String uid = firebaseUser.getUid();

            Picasso.with(this).load(photoURL).resize(200,200).into(nav_avatar);
            nav_name.setText(name);
            nav_email.setText(email);

            db.getReference().child("users").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("owed").exists()){
                        Iterator<DataSnapshot> snaps = dataSnapshot.child("owed").getChildren().iterator();
                        double sum = 0;
                        while(snaps.hasNext()){
                            DataSnapshot item = snaps.next();
                            double amt = ((Number)item.child("amount").getValue()).doubleValue();
                            sum += amt;
                        }
                        String dispOwed = String.format(getString(R.string.owed) +" $%.2f", sum);
                        nav_owed.setText(dispOwed);
                    }
                    else{
                        String dispOwed = getString(R.string.owed) + " $0.00";
                        nav_owed.setText(dispOwed);
                    }

                    if(dataSnapshot.child("owe").exists()){
                        Iterator<DataSnapshot> snaps = dataSnapshot.child("owe").getChildren().iterator();
                        double sum = 0;
                        while(snaps.hasNext()){
                            DataSnapshot item = snaps.next();
                            double amt = ((Number)item.child("amount").getValue()).doubleValue();
                            sum += amt;
                        }
                        String dispOwed = String.format(getString(R.string.owe)+" $%.2f", sum);
                        nav_owe.setText(dispOwed);
                    }
                    else{
                        String dispOwe = getString(R.string.owe) + " $0.00";
                        nav_owe.setText(dispOwe);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new PostsFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch(id) {
            default:
            case R.id.nav_home:
                fragment = new PostsFragment();
                break;
            case R.id.nav_groups:
                fragment = new GroupsFragment();
                break;
            case R.id.nav_owed:
                fragment = new OwedFragment();
                break;
            case R.id.nav_owe:
                fragment = new OweFragment();
                break;
            case R.id.nav_sign_out:
                firebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(googleApiClient);
                startActivity(new Intent(this, SignInActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                return true;
        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPostCharge(double amount, final String itemid) {
        Toast.makeText(this, String.format(Locale.US, "This person owes me: $%.2f", amount), Toast.LENGTH_SHORT).show();
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference dref = db.getReference();
        final Owe owed = new Owe(itemid, amount);
        dref.child("users").child(firebaseUser.getUid()).child("owed").child(itemid).setValue(owed);
        dref.child("items").child(itemid).child("user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String uid = user.getUid();
                dref.child("users").child(uid).child("owe").child(itemid).setValue(owed);
                Map<String, Object> userinfo = new HashMap<String, Object>();
                userinfo.put("name", firebaseUser.getDisplayName());
                userinfo.put("uid", firebaseUser.getUid());
                dref.child("users").child(uid).child("owe").child(itemid).updateChildren(userinfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
