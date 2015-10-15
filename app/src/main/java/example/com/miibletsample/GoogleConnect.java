package example.com.miibletsample;

import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

/**
 * Created by srivatsan on 15/10/15.
 */
public class GoogleConnect implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    int i;
    MainActivity mainActivity;
    private boolean mResolvingError=false;
    private static final int REQUEST_RESOLVE_ERROR = 1001;

    public GoogleConnect(int i, MainActivity mainActivity){
        this.i = i;
        this.mainActivity = mainActivity;
       // this.mResolvingError = mainActivity.mResolvingError;
    }
    @Override
    public void onConnected(Bundle bundle) {
        if (Plus.PeopleApi.getCurrentPerson(mainActivity.googleApiClients[i]) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mainActivity.googleApiClients[i]);
            String personName = currentPerson.getDisplayName();
            String personPhoto = currentPerson.getImage().getUrl();
            mainActivity.acntname[i].setText(personName);
            new DownloadImageTask(mainActivity.profimages[i]).execute(personPhoto);
            SharedPreferences settings = mainActivity.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(MainActivity.PREF_ACCOUNT_NAME+"profile_name" + i,personName);
            editor.putString(MainActivity.PREF_ACCOUNT_NAME+"profile_pic" + i,personPhoto);
            editor.commit();
            Log.d("zzzzzzzzz", personName + "\\" + personPhoto);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(mainActivity, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mainActivity.googleApiClients[i].connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            //showErrorDialog(result.getErrorCode());
            mResolvingError = true;

            Log.d("zzzzzzzzz","failed");
        }

    }
}