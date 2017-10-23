package com.cmput301.t05.habilect;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * @author ioltuszy
 */

public class FollowRequestDialog extends DialogFragment {
    private OnFollowRequestListener onFollowRequestListener;
    public void setOnFollowRequestListener(OnFollowRequestListener onFollowRequestListener) {
        this.onFollowRequestListener = onFollowRequestListener;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_followrequest, null);
        dialog.setContentView(view);
        ((Button)dialog.findViewById(R.id.accept_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFollowRequestListener.OnAccepted();
                dialog.dismiss();
            }
        });
        ((Button)dialog.findViewById(R.id.decline_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFollowRequestListener.OnDeclined();
                dialog.dismiss();
            }
        });
        ((Button)dialog.findViewById(R.id.ignore_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFollowRequestListener.OnIgnored();
                dialog.dismiss();
            }
        });
        return dialog;
    }
}
