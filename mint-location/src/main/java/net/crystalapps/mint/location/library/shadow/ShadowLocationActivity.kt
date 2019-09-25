package net.crystalapps.mint.location.library.shadow

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/**
 * Created by Syed Owais Ali on 10/20/2018.
 */
class ShadowLocationActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fragment = LocationSettingFragment.instance

        if (fragment?.resolvableApiException != null) {
            try {
                fragment.resolvableApiException?.startResolutionForResult(this, 2019)
            } catch (e: IntentSender.SendIntentException) {
                e.printStackTrace()
                setResult(Activity.RESULT_CANCELED, intent.putExtra("error", e.message))
                exit()
            }

        } else {
            setResult(Activity.RESULT_CANCELED, intent.putExtra("error", "failed to show location setting."))
            exit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        setResult(resultCode, data)
        exit()
    }

    private fun exit() {
        finish()
        overridePendingTransition(0, 0)
    }

}
