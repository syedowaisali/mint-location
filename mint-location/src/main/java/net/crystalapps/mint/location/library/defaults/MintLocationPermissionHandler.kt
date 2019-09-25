package net.crystalapps.mint.location.library.defaults

import android.app.AlertDialog
import androidx.fragment.app.FragmentActivity

import net.crystalapps.mint.location.library.handlers.LocationPermissionHandler
import net.crystalapps.permission.runtime.library.callbacks.SettingOpener
import net.crystalapps.permission.runtime.library.models.Perm

/**
 * Created by Syed Owais Ali on 10/21/2018.
 */

class MintLocationPermissionHandler : LocationPermissionHandler<FragmentActivity> {

    override fun handlePermanentDenied(perm: Perm<FragmentActivity>, settingOpener: SettingOpener<FragmentActivity>) {

        val builder = getDialogBuilder(perm)
        builder.setTitle(getTitle(perm))
        builder.setMessage(getMessage(perm))
        builder.setPositiveButton(getPositiveButtonText(perm)) { dialogInterface, which -> settingOpener.open(perm.caller, perm.type) }
        builder.setNegativeButton(getNegativeButtonText(perm)) { dialogInterface, which -> settingOpener.doNothing(perm.caller, perm.type) }
        builder.create().show()
    }

    protected fun getDialogBuilder(perm: Perm<FragmentActivity>): AlertDialog.Builder {
        return AlertDialog.Builder(perm.caller)
    }

    protected fun getTitle(perm: Perm<FragmentActivity>): String {
        return "Permission Missing"
    }

    protected fun getMessage(perm: Perm<FragmentActivity>): String {
        return "Require " + perm.type + " permission to get updated location."
    }

    protected fun getPositiveButtonText(perm: Perm<FragmentActivity>): String {
        return "OPEN SETTINGS"
    }

    protected fun getNegativeButtonText(perm: Perm<FragmentActivity>): String {
        return "NOT NOW"
    }
}
