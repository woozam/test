package com.foodfly.gcm.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class MyInstanceIDListenerService extends InstanceIDListenerService {

    /**
     * Called if server rotates InstanceID token. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     *
     */
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        // Fetch updated Instance ID token and notify our app's server of any changes
        // (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}