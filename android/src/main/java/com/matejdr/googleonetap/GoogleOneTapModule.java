package com.matejdr.googleonetap;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

@ReactModule(name = GoogleOneTapModule.NAME)
public class GoogleOneTapModule extends ReactContextBaseJavaModule {
  public static final String NAME = "RNGoogleOneTap";

  private String webClientId;
  private SignInClient oneTapClient;
  private BeginSignInRequest signInRequest;
  private PromiseWrapper promiseWrapper;

  private static final int REQ_ONE_TAP = 2;

  private class RNGoogleOneTapSignInActivityEventListener extends BaseActivityEventListener {
    @Override
    public void onActivityResult(Activity activity, final int requestCode, final int resultCode, final Intent intent) {
      if (requestCode == REQ_ONE_TAP) {
        handleSignInTaskResult(intent);
      }
    }
  }

  public GoogleOneTapModule(ReactApplicationContext reactContext) {
    super(reactContext);

    oneTapClient = Identity.getSignInClient(reactContext);
    promiseWrapper = new PromiseWrapper();
    reactContext.addActivityEventListener(new RNGoogleOneTapSignInActivityEventListener());
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }


  @ReactMethod
  public void configure(
    final ReadableMap config,
    final Promise promise
  ) {
    this.webClientId = config.hasKey("webClientId") ? config.getString("webClientId") : null;

    promise.resolve(null);
  }

  @ReactMethod
  public void signIn(final Promise promise) {
    if (oneTapClient == null) {
      rejectWithNullClientError(promise);
      return;
    }

    final Activity activity = getCurrentActivity();

    if (activity == null) {
      promise.reject(GoogleOneTapModule.NAME, "activity is null");
      return;
    }


    Log.e(GoogleOneTapModule.NAME, "webClientId: " + webClientId);

    signInRequest = BeginSignInRequest.builder()
      .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
        .setSupported(true)
        // Your server's client ID, not your Android client ID.
        .setServerClientId(webClientId)
        // Show all accounts on the device.
        .setFilterByAuthorizedAccounts(false)
        .build())
      .build();

    promiseWrapper.setPromiseWithInProgressCheck(promise, "signIn");
    UiThreadUtil.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        oneTapClient = Identity.getSignInClient(activity);
        oneTapClient.beginSignIn(signInRequest)
          .addOnSuccessListener(activity, new OnSuccessListener<BeginSignInResult>() {
            @Override
            public void onSuccess(BeginSignInResult result) {
              try {
                activity.startIntentSenderForResult(
                  result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                  null, 0, 0, 0);
              } catch (IntentSender.SendIntentException e) {
                Log.e(GoogleOneTapModule.NAME, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                promise.reject(GoogleOneTapModule.NAME, e.getLocalizedMessage());
              }
            }
          })
          .addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              // No saved credentials found. Launch the One Tap sign-up flow, or
              // do nothing and continue presenting the signed-out UI.
              Log.e(GoogleOneTapModule.NAME, "No Google Accounts found: " + e.getLocalizedMessage());
              promise.reject(GoogleOneTapModule.NAME, e.getLocalizedMessage());
            }
          });
      }
    });
  }

  public void signOut(final Promise promise) {
    if (oneTapClient == null) {
      rejectWithNullClientError(promise);
      return;
    }

    oneTapClient.signOut()
      .addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
          handleSignOutOrRevokeAccessTask(task, promise);
        }
      });
  }

  private void rejectWithNullClientError(Promise promise) {
    promise.reject(GoogleOneTapModule.NAME, "oneTapClient is null - call configure first");
  }

  private void handleSignInTaskResult(@NonNull Intent intent) {
    try {
      SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(intent);

      WritableMap userParams = getUserProperties(credential);
      promiseWrapper.resolve(userParams);
    } catch (ApiException e) {
      int code = e.getStatusCode();

      Log.e("OneTap", "ApiException: " + e.getLocalizedMessage());
      switch (code) {
        case CommonStatusCodes.CANCELED:
        default:
          String errorDescription = GoogleSignInStatusCodes.getStatusCodeString(code);
          promiseWrapper.reject(String.valueOf(code), errorDescription);
      }
    }
  }

  private void handleSignOutOrRevokeAccessTask(@NonNull Task<Void> task, final Promise promise) {
    if (task.isSuccessful()) {
      promise.resolve(null);
    } else {
      int code = getExceptionCode(task);
      String errorDescription = GoogleSignInStatusCodes.getStatusCodeString(code);
      promise.reject(String.valueOf(code), errorDescription);
    }
  }

  private WritableMap getUserProperties(@NonNull SignInCredential signInCredential) {
    Uri photoUrl = signInCredential.getProfilePictureUri();

    WritableMap user = Arguments.createMap();
    user.putString("id", signInCredential.getId());
    user.putString("name", signInCredential.getDisplayName());
    user.putString("givenName", signInCredential.getGivenName());
    user.putString("familyName", signInCredential.getFamilyName());
    user.putString("password", signInCredential.getPassword());
    user.putString("photo", photoUrl != null ? photoUrl.toString() : null);

    WritableMap params = Arguments.createMap();
    params.putMap("user", user);
    params.putString("idToken", signInCredential.getGoogleIdToken());

    return params;
  }

  private int getExceptionCode(@NonNull Task<Void> task) {
    Exception e = task.getException();

    if (e instanceof ApiException) {
      ApiException exception = (ApiException) e;
      return exception.getStatusCode();
    }
    return CommonStatusCodes.INTERNAL_ERROR;
  }
}
