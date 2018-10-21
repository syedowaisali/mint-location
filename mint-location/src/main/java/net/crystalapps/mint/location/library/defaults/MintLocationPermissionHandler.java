package net.crystalapps.mint.location.library.defaults;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import net.crystalapps.mint.location.library.handlers.LocationPermissionHandler;
import net.crystalapps.permission.runtime.library.callbacks.SettingOpener;
import net.crystalapps.permission.runtime.library.models.Perm;

/**
 * Created by Syed Owais Ali on 10/21/2018.
 */

@SuppressWarnings({"WeakerAccess", "unused", "SameReturnValue"})
public class MintLocationPermissionHandler implements LocationPermissionHandler<FragmentActivity> {

    @Override
    public void handlePermanentDenied(@NonNull Perm<FragmentActivity> perm, @NonNull SettingOpener<FragmentActivity> settingOpener) {

        AlertDialog.Builder builder = getDialogBuilder(perm);
        builder.setTitle(getTitle(perm));
        builder.setMessage(getMessage(perm));
        builder.setPositiveButton(getPositiveButtonText(perm), (dialogInterface, which) -> settingOpener.open(perm.getCaller(), perm.getType()));
        builder.setNegativeButton(getNegativeButtonText(perm), (dialogInterface, which) -> settingOpener.doNothing(perm.getCaller(), perm.getType()));
        builder.create().show();
    }

    @NonNull
    protected AlertDialog.Builder getDialogBuilder(@NonNull Perm<FragmentActivity> perm) {
        return new AlertDialog.Builder(perm.getCaller());
    }

    @NonNull
    protected String getTitle(@NonNull Perm<FragmentActivity> perm) {
        return "Permission Missing";
    }

    @NonNull
    protected String getMessage(@NonNull Perm<FragmentActivity> perm) {
        return "Require " + perm.getType() + " permission to get updated location.";
    }

    @NonNull
    protected String getPositiveButtonText(@NonNull Perm<FragmentActivity> perm) {
        return "OPEN SETTINGS";
    }

    @NonNull
    protected String getNegativeButtonText(@NonNull Perm<FragmentActivity> perm) {
        return "NOT NOW";
    }
}
