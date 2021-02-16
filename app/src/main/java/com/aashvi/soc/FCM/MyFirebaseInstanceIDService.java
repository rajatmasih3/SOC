package com.aashvi.soc.FCM;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.aashvi.soc.Helper.SharedHelper;
import com.aashvi.soc.Utils.Utilities;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    Utilities utils = new Utilities();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedHelper.putKey(getApplicationContext(),"device_token",""+refreshedToken);
        utils.print(TAG,"Refreshed Token===>"+refreshedToken);
    }
}