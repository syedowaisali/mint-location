package net.crystalapps.mint.location.library.handlers;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import net.crystalapps.permission.runtime.library.callbacks.SettingOpener;
import net.crystalapps.permission.runtime.library.models.Perm;

/**
 * Created by Syed Owais Ali on 10/13/2018.
 */
public interface LocationPermissionHandler<T extends FragmentActivity> {

    void handlePermanentDenied(@NonNull Perm<T> perm, @NonNull SettingOpener<T> settingOpener);
}
