package net.crystalapps.mint.location.library.callbacks;

/**
 * Created by Syed Owais Ali on 10/21/2018.
 */
public interface InitializationCallback {
    void onSuccess();
    default void onFailed(String error){}
    default void onPermissionDenied(){}
}
