package com.cmput301.t05.habilect;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * @author ioltuszy
 */

public class ViewHabitDialog extends DialogFragment {
    FragmentManager fragmentManager;

    private OnViewHabitListener onViewHabitListener;
    public void setOnViewHabitListener(OnViewHabitListener onViewHabitListener) {
        this.onViewHabitListener = onViewHabitListener;
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
        View view = inflater.inflate(R.layout.dialog_viewhabit, null);
        dialog.setContentView(view);

        fragmentManager = getActivity().getSupportFragmentManager();

        final Button followRequestButton = (Button) dialog.findViewById(R.id.followRequestButton);
        followRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FollowRequestDialog followRequestDialog = new FollowRequestDialog();
                followRequestDialog.setOnFollowRequestListener(new OnFollowRequestListener() {
                    @Override
                    public void OnAccepted() {
                        // TODO: implement OnAccepted
                    }

                    @Override
                    public void OnDeclined() {
                        // TODO: implement OnDeclined
                    }

                    @Override
                    public void OnIgnored() {
                        // TODO: implement OnIgnored
                    }
                });
                followRequestDialog.show(fragmentManager, "followRequestDialog");
            }
        });

        ((Button)dialog.findViewById(R.id.delete_button))
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onViewHabitListener.OnDeleted();
                    dialog.dismiss();
                }
            });
        ((Button)dialog.findViewById(R.id.save_button))
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onViewHabitListener.OnSaved();
                    dialog.dismiss();
                }
            });
        ((Button)dialog.findViewById(R.id.follow_button))
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onViewHabitListener.OnFollowed();
                    dialog.dismiss();
                }
            });
        ((Button)dialog.findViewById(R.id.cancel_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onViewHabitListener.OnCancelled();
                    dialog.dismiss();
                }
            });

        return dialog;
    }
}
