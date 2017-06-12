package edu.ucsb.cs.cs190i.rkuang.homies.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.UUID;

import edu.ucsb.cs.cs190i.rkuang.homies.R;
import edu.ucsb.cs.cs190i.rkuang.homies.adapters.EventAdapter;
import edu.ucsb.cs.cs190i.rkuang.homies.adapters.PostAdapter;
import edu.ucsb.cs.cs190i.rkuang.homies.models.Event;
import edu.ucsb.cs.cs190i.rkuang.homies.models.Item;
import edu.ucsb.cs.cs190i.rkuang.homies.models.User;

import static android.content.ContentValues.TAG;
import static edu.ucsb.cs.cs190i.rkuang.homies.R.layout.fragment_create_event;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateEventFragment extends DialogFragment  {

    DatabaseReference db;
    FirebaseUser user;
    String date;
    String time;

    private static EditText dateText;
    private static EditText timeText;

    public CreateEventFragment() {

    }

    public static CreateEventFragment newInstance() {
        Bundle args = new Bundle();
        CreateEventFragment fragment = new CreateEventFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        return inflater.inflate(fragment_create_event, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final CreateEventFragment me = this;
        final EditText nameEditText = (EditText) view.findViewById(R.id.name_field);
        dateText = (EditText) view.findViewById(R.id.date_field);
        timeText = (EditText) view.findViewById(R.id.time_field);
        final EditText editText = (EditText) view.findViewById(R.id.description_field);
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(v);
            }
        });
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickTime(v);
            }
        });
        Button button = (Button) view.findViewById(R.id.post_event_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: post message button clicked");
                String description = editText.getText().toString();
                String eventName = nameEditText.getText().toString();
                date = dateText.getText().toString();
                time = timeText.getText().toString();
                if (eventName.length() == 0)
                    Snackbar.make(me.getView(), "Enter a name for your event", Snackbar.LENGTH_SHORT).show();
                else if(date.length() == 0)
                    Snackbar.make(me.getView(), "Enter a day for your event", Snackbar.LENGTH_SHORT).show();
                else if(time.length() == 0)
                    Snackbar.make(me.getView(), "Enter a time for your event", Snackbar.LENGTH_SHORT).show();
                else if(description.length() == 0)
                    Snackbar.make(me.getView(), "Enter a description", Snackbar.LENGTH_SHORT).show();
                else {
                    EventAdapter.new_post = true;
                    String name = user.getDisplayName();
                    String photoURL = user.getPhotoUrl().toString();
                    String uid = user.getUid();

                    Event e = new Event(new User(name, photoURL, uid), eventName, date, time, description, UUID.randomUUID().toString());
                    db.child("events").child(e.getId()).setValue(e);
                    me.getDialog().dismiss();
                }
            }
        });
    }

    public void pickDate(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void pickTime(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog d = super.onCreateDialog(savedInstanceState);
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


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String extraA = "";
            String extraB = "";
            String am_pm = "AM";
            if(hourOfDay > 11) {
                am_pm = "PM";
                if(hourOfDay > 12) {
                    extraA = "0";
                    hourOfDay -= 12;
                }
            }
            else if(hourOfDay < 10) {
                if(hourOfDay == 0)
                    hourOfDay = 12;
                else
                    extraA = "0";
            }
            if(minute < 10)
                extraB = "0";
            timeText.setText(extraA + hourOfDay + ":" + extraB + minute + am_pm);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            dateText.setText(day + "/" + (month + 1) + "/" + year);
        }
    }
}
