package edu.ucsb.cs.cs190i.rkuang.homies.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.ucsb.cs.cs190i.rkuang.homies.R;

/**
 * Created by Ky on 6/11/2017.
 */

public class ChargeFragment extends DialogFragment {
    private String itemid;
    private String name;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //give option to charge person for item. Mark post indicating item has been purchased.
        //use AlertDialog
        //After user enters price, update database. Each user should have a part indicating who they
        //owe money to and who owes them.

        itemid = getArguments().getString("Itemid");
        name = getArguments().getString("UserOwe");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View contentView = getActivity().getLayoutInflater().inflate(R.layout.charge_person, null);
        TextView purchaseFor = (TextView) contentView.findViewById(R.id.purchaseFor);
        Button postCharge = (Button) contentView.findViewById(R.id.post_charge_button);
        String str = purchaseFor.getText().toString() + " " + name + ": ";
        purchaseFor.setText(str);
        final EditText charge = (EditText) contentView.findViewById(R.id.money_entered);

        postCharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(charge.getText().toString().equals("")){
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter an amount.", Toast.LENGTH_SHORT).show();
                }
                else {
                    double money_entered = Double.parseDouble(charge.getText().toString());
                    money_entered = (double) Math.round(money_entered * 100d) / 100d;
                    charge(money_entered);
                    dismiss();
                }
            }
        });

        builder.setView(contentView).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        return builder.create();
    }

    public interface PostCharge {
        void onPostCharge(double amount, String itemid);
    }

    PostCharge postCharge;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        postCharge = (PostCharge) context;
    }

    public void charge(double amount) {
        postCharge.onPostCharge(amount, itemid);
    }
}
