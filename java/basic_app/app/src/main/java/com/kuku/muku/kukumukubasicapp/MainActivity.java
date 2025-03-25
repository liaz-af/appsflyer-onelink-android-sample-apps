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
import static com.kuku.muku.kukumukubasicapp.PreferencesHelper.isBranchLATDCollected;
import static com.kuku.muku.kukumukubasicapp.PreferencesHelper.setBranchLATDCollected;

import com.appsflyer.AppsFlyerLib;
import com.appsflyer.migration.AppsFlyerMigrationHelper;
import com.kuku.muku.kukumukubasicapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.ServerRequestGetLATD;
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

                boolean isDeeplinkScenario = branchUniversalObject != null;
                if (isBranchLATDCollected(MainActivity.this) && isDeeplinkScenario) {
                    // Not a first start, need to collect deeplinking data
                    AppsFlyerMigrationHelper.setDeepLinkingData(Branch.getInstance().getLatestReferringParams());
                }

                collectAndStartAppsFlyer();


                if (error != null) {
                    Log.e("BranchSDK_Tester", "branch init failed. Caused by -" + error.getMessage());
                } else {
                    Log.i("BranchSDK_Tester", "branch init complete!");
                    Log.i("BranchSDK_Tester", "****** Im here 000000");
                    if (branchUniversalObject != null) {
                        Log.i("BranchSDK_Tester", "****** Im here 1111111");
                        Log.i("BranchSDK_Tester", "title " + branchUniversalObject.getTitle());
                        Log.i("BranchSDK_Tester", "CanonicalIdentifier " + branchUniversalObject.getCanonicalIdentifier());
                        Log.i("BranchSDK_Tester", "metadata " + branchUniversalObject.getContentMetadata().convertToJson());

                        JSONObject sessionParams = branchUniversalObject.getContentMetadata().convertToJson();

                        Log.i("BranchSDK_Tester", "****** Im here 222222");

                        try {
                            goToFruit(sessionParams.getString("fruit_name"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        Log.i("BranchSDK_Tester", "@@@@ branchUniversalObject came back null");
                        Log.i("BranchSDK_Tester", "****** Im here 4444444");
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

//        getFirstReferringBranchUniversalObject();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void collectAndStartAppsFlyer() {
        if (isBranchLATDCollected(this)) {
            // This is not the first session, no need to collect LATD, start AF right away
            AppsFlyerLib.getInstance().start(this);
        } else {
            collectLatdFromBranch(() -> {
                // On LATD collected
                AppsFlyerLib.getInstance().start(this);
            });
            setBranchLATDCollected(this, true);
        }
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