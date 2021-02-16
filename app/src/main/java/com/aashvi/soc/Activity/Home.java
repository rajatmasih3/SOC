package com.aashvi.soc.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.aashvi.soc.Constants.URLHelper;
import com.aashvi.soc.Fragments.HomeCategoryFragment;
import com.aashvi.soc.Fragments.NavigationDrawerFragment;
import com.aashvi.soc.Fragments.PastTrips;
import com.aashvi.soc.Fragments.Payment;
import com.aashvi.soc.Fragments.ServiceFlowFragment;
import com.aashvi.soc.Helper.Keyname;
import com.aashvi.soc.Helper.SharedHelper;
import com.aashvi.soc.Listener.NavigationCallBack;
import com.aashvi.soc.Models.NavMenu;
import com.aashvi.soc.R;
import com.aashvi.soc.Utils.Utilities;
import com.aashvi.soc.SOC;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.aashvi.soc.SOC.trimMessage;

public class Home extends AppCompatActivity implements NavigationDrawerFragment.NavDrawerFgmtListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        NavigationCallBack, ServiceFlowFragment.ServiceFlowFgmtListener, HomeCategoryFragment.HomeCategoryFgmtListener, PastTrips.PastTripsListener {


    public static String TAG = "Home";
    Context context = Home.this;
    Activity activity = Home.this;
    NavigationDrawerFragment mNavigationDrawerFragment;
    Utilities utils = new Utilities();
    GoogleApiClient mGoogleApiClient;
    String current_lat = "", current_lng = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        setContentView(R.layout.home);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        if (mNavigationDrawerFragment != null)
            mNavigationDrawerFragment.setupDrawer(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id
                    .drawer_layout));


    }


    @Override
    public void onBackPressed() {
        Log.w(TAG, "onBackPressed: Backstack Count" + getSupportFragmentManager().getBackStackEntryCount());
        if (getSupportFragmentManager().getBackStackEntryCount() == 1){
            if (mNavigationDrawerFragment != null){
                mNavigationDrawerFragment.setNavMenuItems(NavMenu.HOME);
                super.onBackPressed();
            }

        }else{
            showExitDialog();
        }

    }


    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.exit_app_confirm));
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Home.super.onBackPressed();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Reset to previous seletion menu in navigation
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        });
        dialog.show();
    }

    @Override
    protected void onResume(){
        if (mNavigationDrawerFragment != null){
            mNavigationDrawerFragment.setNavMenuItems(NavMenu.HOME);
        }
        super.onResume();
    }

    public void switchContent(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
      /* if (mDrawerLayout != null && mNavigationDrawerFragment.i
      sDrawerOpen())
           mNavigationDrawerFragment.closeDrawer();*/
        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager
                    .beginTransaction();
            transaction.replace(R.id.container, fragment, tag);
            if (!(fragment instanceof HomeCategoryFragment) && !(fragment instanceof ServiceFlowFragment) )
                transaction.addToBackStack(tag);
            transaction.commit();
        }
    }

    private void showLogoutDialog() {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle(getString(R.string.app_name));
            builder.setMessage(getString(R.string.exit_confirm));

            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logout();
                }
            });

            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Reset to previous seletion menu in navigation
                    dialog.dismiss();
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.openDrawer(GravityCompat.START);
                }
            });
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }
            });
            dialog.show();
        }
    }

    private void logout() {
        logoutAPI();
    }



    public void logoutAPI() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", SharedHelper.getKey(context, "id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.LOGOUT, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                SharedHelper.putKey(context, "current_status", "");
                SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                Intent mainIntent = new Intent(context, BeginScreen.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                activity.finish();

                /*SharedHelper.putKey(context, "current_status", "");
                SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
                SharedHelper.putKey(context, "email", "");
                Intent goToLogin = new Intent(activity, BeginScreen.class);
                goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goToLogin);
                finish();*/
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.getString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                               /*refreshAccessToken();*/
                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down));
                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }

                }
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                Log.e("getHeaders: Token", SharedHelper.getKey(context, "access_token") + SharedHelper.getKey(context, "token_type"));
                headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        SOC.getInstance().addToRequestQueue(jsonObjectRequest);
    }


    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        /*Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();*/

        Snackbar snackbar = Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(this.getResources().getColor(R.color.black));
        TextView textView = (TextView) snackBarView.findViewById(R.id.snackbar_text);
        textView.setTextColor(this.getResources().getColor(R.color.white));
        snackbar.show();
    }


    @Override
    public void moveToHomeCategoryFragment() {
        HomeCategoryFragment subHomeFragment = HomeCategoryFragment.newInstance();
        addFragment(subHomeFragment, HomeCategoryFragment.TAG, false, true, true);
    }

    @Override
    public void onServiceFlowLogout() {
        showLogoutDialog();
    }

    @Override
    public void handleDrawer() {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        } else {
            mNavigationDrawerFragment.openDrawer();
        }
    }

    @Override
    public void menuClicked(NavMenu navMenu) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        Fragment currentFgmt = fragmentManager.findFragmentById(R.id.container);
        utils.print(TAG, "menuClicked: CurrentFgmt" + currentFgmt);
        String tag = "";
        switch (navMenu) {
            case HOME:
                fragment = fragmentManager.findFragmentByTag(HomeCategoryFragment.TAG);
                tag = HomeCategoryFragment.TAG;
                fragment = HomeCategoryFragment.newInstance();
                /*if (fragment == null) {
                    utils.print(TAG, "SubHomeFragment home fragment");
                    fragment = HomeCategoryFragment.newInstance();
                } else {
                    utils.print(TAG, "home fragment, no need to add");
                }*/
                break;
            case PAYMENT:
                fragment = fragmentManager.findFragmentByTag(Payment.TAG);
                /*FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);*/

                tag = Payment.TAG;
                if (fragment == null) {
                    utils.print(TAG, "Payment fragment");
                    fragment = Payment.newInstance();
                } else {
                    utils.print(TAG, "home fragment, no need to add");
                }

                break;
            case COUPON:
                SharedHelper.putKey(context, "current_status", "");
                startActivity(new Intent(context, CouponActivity.class));
                break;
            case WALLET:
                SharedHelper.putKey(context, "current_status", "");
                startActivity(new Intent(context, ActivityWallet.class));
                break;
            case SERVICE_HISTORY:
                /*fragment = fragmentManager.findFragmentByTag(PastTrips.TAG);
                tag = Payment.TAG;
                if (fragment == null) {
                    utils.print(TAG, "Payment fragment");
                    fragment = PastTrips.newInstance();
                } else {
                    utils.print(TAG, "home fragment, no need to add");
                }*/
                SharedHelper.putKey(context, "current_status", "");
                Intent intent = new Intent(context, HistoryActivity.class);
                intent.putExtra("tag","past");
                startActivity(intent);
                break;

            case HELP:
                //SharedHelper.putKey(context, "current_status", "");
                startActivity(new Intent(context, ActivityHelp.class));
               // overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                break;
            case SHARE:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, URLHelper.APP_LINK + " -via " + getString(R.string.app_name));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case LOGOUT:
                showLogoutDialog();
                break;
        }
        utils.print(TAG, "menuClicked: 1" + fragment);
        if (tag != null) {
            Fragment existingFragment = getSupportFragmentManager().findFragmentById(R.id.container);
            if (existingFragment != null) {
                if (!(existingFragment.getTag().equals(tag)))
                    switchContent(fragment, tag);
            } else
                switchContent(fragment, tag);
        }
    }

    @Override
    public void headerClicked() {
        Intent intent = new Intent(this, EditProfile.class);
        intent.putExtra(Keyname.EDIT_PROFILE, true);
        startActivity(intent);
    }

    @Override
    public void headerProfileClicked() {

    }

    @Override
    public void handleNavigationDrawer() {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
        } else {
            mNavigationDrawerFragment.openDrawer();
        }
    }

    @Override
    public void enableDisableNavigationDrawer(boolean isEnable) {
        if (mNavigationDrawerFragment != null){
            mNavigationDrawerFragment.enableDisableDrawer(isEnable);
        }
    }

    @Override
    public void moveToServiceFlowFragment() {
        ServiceFlowFragment subHomeFragment = ServiceFlowFragment.newInstance();
        addFragment(subHomeFragment, ServiceFlowFragment.TAG, false, true, true);
    }

    public void addFragment(Fragment fragment, String tag, boolean popBackStack, boolean hideCurrent, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragment != null) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.container, fragment, tag);
            if (popBackStack) {
                fragmentManager.popBackStack();
            }
            if (hideCurrent) {
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()
                            - 1).getName();
                    transaction.hide(fragmentManager.findFragmentByTag(fragmentTag));
                } else {
                    Fragment currentFgmt = fragmentManager.findFragmentById(R.id.container);
                    if (currentFgmt != null) {
                        transaction.hide(currentFgmt);
                    }
                }
            }
            if (addToBackStack) {
                transaction.addToBackStack(tag);
            }
            transaction.commit();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            current_lat = ""+String.valueOf(mLastLocation.getLatitude());
            current_lng = ""+String.valueOf(mLastLocation.getLongitude());
            SharedHelper.putKey(context, "current_lat", current_lat);
            SharedHelper.putKey(context, "current_lng", current_lng);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
