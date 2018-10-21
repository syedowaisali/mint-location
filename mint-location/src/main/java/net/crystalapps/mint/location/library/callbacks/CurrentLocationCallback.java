package net.crystalapps.mint.location.library.callbacks;

/**
 * Created by Syed Owais Ali on 10/20/2018.
 */

public interface CurrentLocationCallback extends EasyLocationCallback{
    default void onPermissionDenied(){}
}
