package com.kuku.muku.kukumukubasicapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import static com.kuku.muku.kukumukubasicapp.AppsflyerBasicApp.LOG_TAG;

import com.appsflyer.AppsFlyerLib;
import com.appsflyer.migration.AppsFlyerMigrationHelper;
import com.kuku.muku.kukumukubasicapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null && intent.hasExtra("branch_force_new_session") && intent.getBooleanExtra("branch_force_new_session",false)) {
            Branch.sessionBuilder(this).withCallback(new Branch.BranchReferralInitListener() {
                @Override
                public void onInitFinished(JSONObject referringParams, BranchError error) {
                    if (error != null) {
                        Log.i("BranchSDK_Tester", "onNewIntent onInitFinished: error found!");
                        Log.e("BranchSDK_Tester", error.getMessage());
                    } else if (referringParams != null) {
                        Log.i("BranchSDK_Tester", "@@@@ onNewIntent onInitFinished: referringParams not null");
                        Log.i("BranchSDK_Tester", referringParams.toString());
                    }
                }
            }).reInit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Branch.sessionBuilder(this).withCallback(new Branch.BranchUniversalReferralInitListener() {
            @Override
            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {

                if (error != null) {
                    // Branch init failed. Start AppsFlyer immediately
                    Log.e("BranchSDK_Tester", "branch init failed. Caused by -" + error.getMessage());
                    AppsFlyerLib.getInstance().start(MainActivity.this);
                } else {
                    Log.i("BranchSDK_Tester", "branch init complete!");
                    boolean isBranchDeeplink = branchUniversalObject != null;
                    if (isBranchDeeplink) {
                        // Deep link flow
                        Log.i("BranchSDK_Tester", "title " + branchUniversalObject.getTitle());
                        Log.i("BranchSDK_Tester", "CanonicalIdentifier " + branchUniversalObject.getCanonicalIdentifier());
                        Log.i("BranchSDK_Tester", "metadata " + branchUniversalObject.getContentMetadata().convertToJson());

                        JSONObject sessionParams = branchUniversalObject.getContentMetadata().convertToJson();
                        try {
                            boolean isFirstSession = Boolean.parseBoolean(sessionParams.getString("+is_first_session"));
                            if(isFirstSession) {
                                // Deferred deep link
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    Branch.getInstance().getLastAttributedTouchData((jsonObject, latd_error) -> {
                                        // Read the data from the LATD jsonObject
                                        AppsFlyerMigrationHelper.setAttributionData(jsonObject);
                                        // On LATD collected
                                        AppsFlyerLib.getInstance().start(MainActivity.this);
                                    }, 7);
                                }, 3000);
                            } else {
                                // Direct deep link
                                AppsFlyerMigrationHelper.setDeepLinkingData(Branch.getInstance().getLatestReferringParams());
                                AppsFlyerLib.getInstance().start(MainActivity.this);
                            }
                            goToFruit(sessionParams.getString("fruit_name"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        // Organic install or launch
                        Log.i("BranchSDK_Tester", "@@@@ branchUniversalObject came back null");
                        AppsFlyerLib.getInstance().start(MainActivity.this);
                    }

                    if (linkProperties != null) {
                        Log.i("BranchSDK_Tester", "Channel " + linkProperties.getChannel());
                        Log.i("BranchSDK_Tester", "control params " + linkProperties.getControlParams());
                    } else {
                        Log.i("BranchSDK_Tester", "@@@@ linkProperties came back null");
                    }
                }
            }
        }).withData(this.getIntent().getData()).init();
        // init the LATD call from inside the session initialization callback

    }

    private void collectLatdFromBranch(Runnable onCollected) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Branch.getInstance().getLastAttributedTouchData((jsonObject, error) -> {
                // Read the data from the jsonObject
                AppsFlyerMigrationHelper.setAttributionData(jsonObject);
                onCollected.run();
            }, 7);
        }, 3000);
    }

    public void goToApples(View view) {
        goToFruit("Apples");
    }

    public void goToBananas(View view) {
        goToFruit("Bananas");
    }

    public void goToPeaches(View view) {
        goToFruit("Peaches");
    }

    private void goToFruit(String fruitName) {
        String fruitClassName = fruitName.concat("Activity");
        try {
            Class fruitClass = Class.forName(this.getPackageName().concat(".").concat(fruitClassName));
            Log.d(LOG_TAG, "Looking for class " + fruitClass);
            Intent intent = new Intent(getApplicationContext(), fruitClass);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            Log.d(LOG_TAG, "Failed to start activity for " + fruitName);
            e.printStackTrace();
        }
    }
}