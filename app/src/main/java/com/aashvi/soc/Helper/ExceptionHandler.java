package com.aashvi.soc.Helper;

import android.app.Activity;
import android.view.View;

import com.android.volley.NetworkResponse;
import com.aashvi.soc.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import static com.aashvi.soc.SOC.trimMessage;

/**
 * Created by RAJKUMAR on 09-04-2017.
 */

public class ExceptionHandler {

    public static void handleException(Activity activity, NetworkResponse response, View view) {
        if (response != null && response.data != null) {
            try {
                JSONObject errorObj = new JSONObject(new String(response.data));

                if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                    try {
                        Snackbar.make(view, errorObj.optString("message"), Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    } catch (Exception e) {
                        Snackbar.make(view, activity.getString(R.string.something_went_wrong), Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                } else if (response.statusCode == 422) {

                    String json = trimMessage(new String(response.data));
                    if (json != "" && json != null) {
                        Snackbar.make(view, json, Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(view, activity.getString(R.string.please_try_again), Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }

                } else {
                    Snackbar.make(view, activity.getString(R.string.please_try_again), Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }

            } catch (Exception e) {
                Snackbar.make(view, activity.getString(R.string.something_went_wrong), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }


    }

}
