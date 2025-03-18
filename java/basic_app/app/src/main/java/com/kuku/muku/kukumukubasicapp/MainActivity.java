package com.kuku.muku.kukumukubasicapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import io.branch.referral.ServerRequestGetLATD;
import io.branch.referral.util.LinkProperties;

public class MainActivity extends AppCompatActivity {
    private Handler handler;
    private Runnable delayedTask;
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
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Branch.sessionBuilder(this).withCallback(new Branch.BranchUniversalReferralInitListener() {
            @Override
            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                boolean isFirstSessionFlagCopy = sharedPreferences.getBoolean("First_Session", false);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("First_Session", false);
                editor.apply(); // Asynchronously saves the data


                if (error != null) {
                    Log.e("BranchSDK_Tester", "branch init failed. Caused by -" + error.getMessage());
                    AppsFlyerLib.getInstance().start(MainActivity.this);
                } else {
                    Log.i("BranchSDK_Tester", "branch init complete!");
                    Log.i("BranchSDK_Tester", "****** Im here 000000");
                    if (branchUniversalObject != null) {
                        Log.i("BranchSDK_Tester", "****** Im here 1111111");
                        Log.i("BranchSDK_Tester", "title " + branchUniversalObject.getTitle());
                        Log.i("BranchSDK_Tester", "CanonicalIdentifier " + branchUniversalObject.getCanonicalIdentifier());
                        Log.i("BranchSDK_Tester", "metadata " + branchUniversalObject.getContentMetadata().convertToJson());

                        JSONObject sessionParams = branchUniversalObject.getContentMetadata().convertToJson();

//                        boolean isFirstSession = Branch.getInstance().getLatestReferringParams().optBoolean("+is_first_session", false);
                        // The condition should be with true, but branch always returns is_first_session=false, so this it TEMP
                        if (isFirstSessionFlagCopy == true){
                            Log.i("BranchSDK_Tester", "****** Im here 222222");
                            delayedTask = () -> callLATD();
                            handler.postDelayed(delayedTask, 3000); // Delay execution by 3 seconds
                        } else {
                            Log.i("BranchSDK_Tester", "****** Im here 33333333");
                            AppsFlyerMigrationHelper.setDeepLinkingData(Branch.getInstance().getLatestReferringParams());
                            AppsFlyerLib.getInstance().start(MainActivity.this);
                        }

                        try {
                            goToFruit(sessionParams.getString("fruit_name"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        Log.i("BranchSDK_Tester", "@@@@ branchUniversalObject came back null");
                        Log.i("BranchSDK_Tester", "****** Im here 4444444");
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

//        getFirstReferringBranchUniversalObject();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Cancel any pending delayed task to avoid memory leaks
//        if (delayedTask != null) {
//            handler.removeCallbacks(delayedTask);
//        }
    }

    public void callLATD(){
        Branch.getInstance().getLastAttributedTouchData(
                new ServerRequestGetLATD.BranchLastAttributedTouchDataListener() {
                    @Override
                    public void onDataFetched(JSONObject jsonObject, BranchError error) {
                        Log.i("BranchSDK_Tester", "****** Im here 5555555");
                        AppsFlyerMigrationHelper.setAttributionData(jsonObject);
                        AppsFlyerLib.getInstance().start(MainActivity.this);
                    }
                }, 7);
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

//    public static void getFirstReferringBranchUniversalObject() {
//        BranchUniversalObject branchUniversalObject = null;
//        Branch branchInstance = Branch.getInstance();
//
//        if (branchInstance != null && branchInstance.getFirstReferringParams() != null) {
//            JSONObject firstParam = branchInstance.getFirstReferringParams();
//            try {
//                if (firstParam.has("+clicked_branch_link") && firstParam.getBoolean("+clicked_branch_link")) {
//                    branchUniversalObject = BranchUniversalObject.createInstance(firstParam);
//                    Log.i("BranchSDK_Tester", "getFirstReferringBranchUniversalObject First Params are: " + branchUniversalObject.getContentMetadata().convertToJson().toString());
//                } else {
//                    Log.i("BranchSDK_Tester", "getFirstReferringBranchUniversalObject First Params missing first click");
//                }
//            } catch (Exception ignore) {
//            }
//        } else {
//            Log.i("BranchSDK_Tester", "getFirstReferringBranchUniversalObject First Params is null");
//        }
//
//        return;
//    }

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