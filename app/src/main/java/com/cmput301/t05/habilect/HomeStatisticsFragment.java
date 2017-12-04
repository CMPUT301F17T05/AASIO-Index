package com.cmput301.t05.habilect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import static com.cmput301.t05.habilect.UserProfile.HABILECT_USER_INFO;
import static com.cmput301.t05.habilect.UserProfile.HABILECT_USER_PREVIOUS_NUTRIENT_LEVEL_TIER_RANK_UP;

/**
 * @author ioltuszy
 */

public class HomeStatisticsFragment extends Fragment {
    FragmentManager fragmentManager;
    private Context context;

    private Map<Integer, Integer> tierThresholds = new HashMap<Integer, Integer>() {{
        put(1, 10);
        put(2, 21);
        put(3, 33);
        put(4, 46);
        put(5, 66);
        put(6, 87);
        put(7, 109);
        put(8, 132);
        put(9, 162);
        put(10, 193);
        put(11, 225);
        put(12, 258);
        put(13, 298);
        put(14, 348);
        put(15, 408);
        put(16, 478);
    }};

    TextView nutrientLevelTextView;
    ProgressBar nutrientLevelProgressBar;
    TextView nutrientLevelToNextTierTextView;
    ImageView treeGrowthImageView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_home_statistics, container, false);

        context = getActivity();

        fragmentManager = getActivity().getSupportFragmentManager();

        nutrientLevelTextView = (TextView) rootView.findViewById(R.id.nutrientLevelTextView);
        nutrientLevelProgressBar = (ProgressBar) rootView.findViewById(R.id.nutrientLevelProgressBar);
        nutrientLevelToNextTierTextView = (TextView) rootView.findViewById(R.id.nutrientLevelToNextTierTextView);
        treeGrowthImageView = (ImageView) rootView.findViewById(R.id.treeGrowthImageView);

        /**
         * Sets the user's tree growth image based on acquired level
         */
        setTreeGrowthImageView();

        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        setTreeGrowthImageView();
    }

    private void setTreeGrowthImageView() {

        final UserAccount user = new UserAccount();
        user.load(context);

        TreeGrowth profileTreeGrowth = user.getTreeGrowth();

        int nutrientLevel = profileTreeGrowth.getNutrientLevel();

        Log.i("NUTRIENTLEVEL: ", "" + nutrientLevel);

        nutrientLevelTextView.setText("Nutrient Level: " + nutrientLevel);

        //Base Tier 0
        if (nutrientLevel < tierThresholds.get(1)) {
            nutrientLevelProgressBar.setMax(tierThresholds.get(1));
            nutrientLevelProgressBar.setProgress(nutrientLevel);
            nutrientLevelToNextTierTextView.setText(nutrientLevel + "/" + tierThresholds.get(1));
            treeGrowthImageView.setImageResource(R.drawable.level_01);
        } else if (nutrientLevel >= tierThresholds.get(1) && nutrientLevel < tierThresholds.get(2)) {
            checkAndAdjustTierProperties(nutrientLevel, 1);
        } else if (nutrientLevel >= tierThresholds.get(2) && nutrientLevel < tierThresholds.get(3)) {
            checkAndAdjustTierProperties(nutrientLevel, 2);
        } else if (nutrientLevel >= tierThresholds.get(3) && nutrientLevel < tierThresholds.get(4)) {
            checkAndAdjustTierProperties(nutrientLevel, 3);
        } else if (nutrientLevel >= tierThresholds.get(4) && nutrientLevel < tierThresholds.get(5)) {
            checkAndAdjustTierProperties(nutrientLevel, 4);
        } else if (nutrientLevel >= tierThresholds.get(5) && nutrientLevel < tierThresholds.get(6)) {
            checkAndAdjustTierProperties(nutrientLevel, 5);
        } else if (nutrientLevel >= tierThresholds.get(6) && nutrientLevel < tierThresholds.get(7)) {
            checkAndAdjustTierProperties(nutrientLevel, 6);
        } else if (nutrientLevel >= tierThresholds.get(7) && nutrientLevel < tierThresholds.get(8)) {
            checkAndAdjustTierProperties(nutrientLevel, 7);
        } else if (nutrientLevel >= tierThresholds.get(8) && nutrientLevel < tierThresholds.get(9)) {
            checkAndAdjustTierProperties(nutrientLevel, 8);
        } else if (nutrientLevel >= tierThresholds.get(9) && nutrientLevel < tierThresholds.get(10)) {
            checkAndAdjustTierProperties(nutrientLevel, 9);
        } else if (nutrientLevel >= tierThresholds.get(10) && nutrientLevel < tierThresholds.get(11)) {
            checkAndAdjustTierProperties(nutrientLevel, 10);
        } else if (nutrientLevel >= tierThresholds.get(11) && nutrientLevel < tierThresholds.get(12)) {
            checkAndAdjustTierProperties(nutrientLevel, 11);
        } else if (nutrientLevel >= tierThresholds.get(12) && nutrientLevel < tierThresholds.get(13)) {
            checkAndAdjustTierProperties(nutrientLevel, 12);
        } else if (nutrientLevel >= tierThresholds.get(13) && nutrientLevel < tierThresholds.get(14)) {
            checkAndAdjustTierProperties(nutrientLevel, 13);
        } else if (nutrientLevel >= tierThresholds.get(14) && nutrientLevel < tierThresholds.get(15)) {
            checkAndAdjustTierProperties(nutrientLevel, 14);
        } else if (nutrientLevel >= tierThresholds.get(15) && nutrientLevel < tierThresholds.get(16)) {
            checkAndAdjustTierProperties(nutrientLevel, 15);
        } else if (nutrientLevel >= tierThresholds.get(16)) {
            checkAndAdjustTierProperties(nutrientLevel, 16);
        }
    }

    private void buildRankUpDialog(int tier) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Rank Up!");
        builder.setMessage("Congratulations! You have reached tier " + tier + ".");
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if(tier == 4){
            ImageView completedTreeImage = new ImageView(context);
            completedTreeImage.setImageResource(R.drawable.green_3);
            builder.setView(completedTreeImage);
        } else if(tier == 8){
            ImageView completedTreeImage = new ImageView(context);
            completedTreeImage.setImageResource(R.drawable.orange_3);
            builder.setView(completedTreeImage);
        }else if(tier == 12){
            ImageView completedTreeImage = new ImageView(context);
            completedTreeImage.setImageResource(R.drawable.purple_3);
            builder.setView(completedTreeImage);
        }else if(tier == 16){
            ImageView completedTreeImage = new ImageView(context);
            completedTreeImage.setImageResource(R.drawable.rainbow_3);
            builder.setView(completedTreeImage);
        }
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();
    }

    private void checkAndAdjustTierProperties(int nutrientLevel, int tier) {
        Log.i("INFORMATION", "nutlevel: " + nutrientLevel + " tier: " + tier);

        SharedPreferences sharedPreferences = context.getSharedPreferences(HABILECT_USER_INFO, Context.MODE_PRIVATE);
        String preference = sharedPreferences.getString(HABILECT_USER_PREVIOUS_NUTRIENT_LEVEL_TIER_RANK_UP, null);
        //if true, trigger rank up popup
        if (Integer.parseInt(preference) < tierThresholds.get(tier)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(UserProfile.HABILECT_USER_PREVIOUS_NUTRIENT_LEVEL_TIER_RANK_UP, Integer.toString(nutrientLevel));
            editor.commit();
            buildRankUpDialog(tier);
        }
        if(tier == 16){
            nutrientLevelProgressBar.setMax(tierThresholds.get(tier));
            nutrientLevelProgressBar.setProgress(nutrientLevel);
            nutrientLevelToNextTierTextView.setText("MAXED");
            treeGrowthImageView.setImageResource(R.drawable.all_trees_completed);
        }else{
            nutrientLevelProgressBar.setMax(tierThresholds.get(tier + 1));
            nutrientLevelProgressBar.setProgress(nutrientLevel);
            nutrientLevelToNextTierTextView.setText(nutrientLevel + "/" + tierThresholds.get(tier + 1));

            switch(tier){
                case 1:
                    treeGrowthImageView.setImageResource(R.drawable.level_02);
                    break;
                case 2:
                    treeGrowthImageView.setImageResource(R.drawable.level_03);
                    break;
                case 3:
                    treeGrowthImageView.setImageResource(R.drawable.level_04);
                    break;
                case 4:
                    treeGrowthImageView.setImageResource(R.drawable.level_06);
                    break;
                case 5:
                    treeGrowthImageView.setImageResource(R.drawable.level_07);
                    break;
                case 6:
                    treeGrowthImageView.setImageResource(R.drawable.level_08);
                    break;
                case 7:
                    treeGrowthImageView.setImageResource(R.drawable.level_09);
                    break;
                case 8:
                    treeGrowthImageView.setImageResource(R.drawable.level_11);
                    break;
                case 9:
                    treeGrowthImageView.setImageResource(R.drawable.level_12);
                    break;
                case 10:
                    treeGrowthImageView.setImageResource(R.drawable.level_13);
                    break;
                case 11:
                    treeGrowthImageView.setImageResource(R.drawable.level_14);
                    break;
                case 12:
                    treeGrowthImageView.setImageResource(R.drawable.level_16);
                    break;
                case 13:
                    treeGrowthImageView.setImageResource(R.drawable.level_17);
                    break;
                case 14:
                    treeGrowthImageView.setImageResource(R.drawable.level_18);
                    break;
                case 15:
                    treeGrowthImageView.setImageResource(R.drawable.level_19);
                    break;
            }
        }
    }

}
