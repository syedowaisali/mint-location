package net.crystalapps.mint.location.library.handlers

import androidx.fragment.app.FragmentActivity

import net.crystalapps.permission.runtime.library.callbacks.SettingOpener
import net.crystalapps.permission.runtime.library.models.Perm

/**
 * Created by Syed Owais Ali on 10/13/2018.
 */
interface LocationPermissionHandler<T : FragmentActivity> {

    fun handlePermanentDenied(perm: Perm<T>, settingOpener: SettingOpener<T>)
}
