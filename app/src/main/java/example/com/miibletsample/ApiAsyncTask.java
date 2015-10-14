package example.com.miibletsample;

import android.os.AsyncTask;
import android.util.Log;

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
    String mDate;
    CalendarAdapter caladapter;
    List<String> eventname, eventtime, colorarr;
    private String[] colors = new String[]{
            "#F44336",
            "#E91E63",
            "#9C27B0",
            "#673AB7",
            "#3F51B5",
            "#2196F3",
            "#03A9F4",
            "#00BCD4",
            "#009688",
            "#4CAF50",
            "#8BC34A",
            "#CDDC39",
            "#FFEB3B",
            "#FFC107",
            "#FF9800",
            "#BF360C",
            "#3E2723"

    };
    /**
     * Constructor.
     * @param activity MainActivity that spawned this task.
     */
    ApiAsyncTask(MainActivity activity) {
        this.mActivity = activity;
        this.mDate="01/01/1970";
    }

    ApiAsyncTask(MainActivity activity, String date) { this.mActivity = activity;
        this.mDate = date;}

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
                eventname = new ArrayList<>();
                eventtime = new ArrayList<>();
                colorarr = new ArrayList<>();
                for (Event event : items) {
                    DateTime start = event.getStart().getDateTime();
                    if (start == null) {
                        // All-day events don't have start times, so just use
                        // the start date.
                        start = event.getStart().getDate();
                    }
                    eventname.add(event.getSummary());
                    eventtime.add(String.format("%s", start));
                    int randomNum = 0 + (int)(Math.random()*16);
                    //String col = event.getColorId();
                    //Log.d("color",col.toString());
                    colorarr.add(colors[randomNum]);
                    eve+=String.format("%s (%s)", event.getSummary(), start);
                }
                eventStrings.add(eve);
                final List<String> et = eventname;
                final List<String> dt = eventtime;
                final List<String> ca = colorarr;
                final int s = eventStrings.size();
                Log.d("ppppppppppppppp", eventStrings.size() + "");
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        caladapter = new CalendarAdapter(mActivity, dt, et, ca, mDate);
                        mActivity.calendarlist[s-1].setAdapter(caladapter);
                    }
                });
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