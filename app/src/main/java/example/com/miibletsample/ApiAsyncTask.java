package example.com.miibletsample;

import android.os.AsyncTask;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An asynchronous task that handles the Google Calendar API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class ApiAsyncTask extends AsyncTask<Void, Void, Void> {
    private MainActivity mActivity;
    List<String> eventStrings;
    int k=0;
    /**
     * Constructor.
     * @param activity MainActivity that spawned this task.
     */
    ApiAsyncTask(MainActivity activity) {
        this.mActivity = activity;
    }

    /**
     * Background task to call Google Calendar API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
            mActivity.clearResultsText();
            mActivity.updateResultsText(getDataFromApi());

        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    CalendarActivity.REQUEST_AUTHORIZATION);

        } catch (Exception e) {
            mActivity.updateStatus("The following error occurred:\n" +
                    e.getMessage());
        }

        return null;
    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     * @return List of Strings describing returned events.
     * @throws IOException
     */

    private List<String> getDataFromApi() throws IOException {
        // List the next 10 events from the primary calendar.
        BatchRequest b = mActivity.mService[0].batch();
        JsonBatchCallback<Events> bc = new JsonBatchCallback<Events>() {


            @Override
            public void onSuccess(Events events, HttpHeaders responseHeaders) throws IOException {
                List<Event> items = events.getItems();
                String eve = "";
                for (Event event : items) {
                    DateTime start = event.getStart().getDateTime();
                    if (start == null) {
                        // All-day events don't have start times, so just use
                        // the start date.
                        start = event.getStart().getDate();
                    }
                    eve+=String.format("%s (%s)", event.getSummary(), start);
                }
                eventStrings.add(eve);
            }

            @Override
            public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) throws IOException {

            }
        };
        DateTime now = new DateTime(System.currentTimeMillis());
        eventStrings = new ArrayList<>();
        for(int i=0;i<MainActivity.active;i++) {
            mActivity.mService[i].events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .queue(b,bc);
        }
        b.execute();
        return eventStrings;
    }

}