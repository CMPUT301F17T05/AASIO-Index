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
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rarog on 12/4/17.
 */

public class ShowUserDialog extends DialogFragment {
    private Context context;
    private ListView userList;
    private SearchView searchView;
    UserAccount userAccount;
    static ArrayList<UserAccount> userAccountList = new ArrayList<>();

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
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_show_users, null);
        dialog.setContentView(view);

        context = getContext();

        userList = view.findViewById(R.id.showUserDialogListView);
        SocialAddFriendAdapter adapter = new SocialAddFriendAdapter(dialog, userAccountList, context);
        userList.setAdapter(adapter);

        searchView = view.findViewById(R.id.showUserDialogSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                userAccountList.clear();
                userAccountList.addAll(UserAccount.findSimilarDisplayNames(s));
                adapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });


        return dialog;

    }
}
