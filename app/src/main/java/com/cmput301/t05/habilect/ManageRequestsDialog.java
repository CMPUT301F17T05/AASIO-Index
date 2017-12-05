package com.cmput301.t05.habilect;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

/**
 * Created by rarog on 12/4/17.
 */

public class ManageRequestsDialog extends DialogFragment {
    Context context;
    UserAccount userAccount;
    ListView listView;

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_manage_requests, null);
        dialog.setContentView(view);

        context = getContext();
        userAccount = new UserAccount();
        userAccount.load(context);

        List<UUID> pendingRequest = userAccount.getPendingRequests();
        if(pendingRequest == null) {
            Toast.makeText(context, "No pending reuqest!", Toast.LENGTH_SHORT);
        } else {
            ManageRequestAdapter adapter = new ManageRequestAdapter(dialog, pendingRequest, context);

            listView = view.findViewById(R.id.manageRequestListView);
            listView.setAdapter(adapter);
        }


        return dialog;
    }

}
