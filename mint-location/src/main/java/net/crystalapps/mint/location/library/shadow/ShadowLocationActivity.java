package net.crystalapps.mint.location.library.shadow;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Syed Owais Ali on 10/20/2018.
 */
public class ShadowLocationActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationSettingFragment fragment = LocationSettingFragment.getInstance();

        if (fragment != null && fragment.getResolvableApiException() != null) {
            try {
                fragment.getResolvableApiException().startResolutionForResult(this, 2019);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
                setResult(Activity.RESULT_CANCELED, getIntent().putExtra("error", e.getMessage()));
                exit();
            }
        }
        else {
            setResult(Activity.RESULT_CANCELED, getIntent().putExtra("error", "failed to show location setting."));
            exit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setResult(resultCode, data);
        exit();
    }

    private void exit() {
        finish();
        overridePendingTransition(0, 0);
    }

}
