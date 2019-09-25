package net.crystalapps.mint.location.library.handlers;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import net.crystalapps.permission.runtime.library.callbacks.SettingOpener;
import net.crystalapps.permission.runtime.library.models.Perm;

/**
 * Created by Syed Owais Ali on 10/13/2018.
 */
public interface LocationPermissionHandler<T extends FragmentActivity> {

    void handlePermanentDenied(@NonNull Perm<T> perm, @NonNull SettingOpener<T> settingOpener);
}
