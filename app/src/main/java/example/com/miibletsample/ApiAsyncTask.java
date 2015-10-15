package example.com.miibletsample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * An asynchronous task that handles the Google Calendar API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class ApiAsyncTask extends AsyncTask<Void, Void, Void> {
    private MainActivity mActivity;
    List<String> eventStrings;
    int k=0;
    public Event event;
    public static boolean ALL_DAY= false;
    String mDate;
    public int startyear=-1, startday=-1, startmonth=-1, startminute=-1, starthour=-1, endyear=-1, endday=-1, endmonth=-1, endminute=-1, endhour=-1;
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
        mActivity.findViewById(R.id.addeventbtn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addeventdialog("Account pending",0);
            }
        });
        mActivity.findViewById(R.id.addeventbtn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addeventdialog("Account pending",1);
            }
        });
        mActivity.findViewById(R.id.addeventbtn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addeventdialog("Account pending",2);
            }
        });
        mActivity.findViewById(R.id.addeventbtn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addeventdialog("Account pending",3);
            }
        });
    }

    ApiAsyncTask(MainActivity activity, String date) {
        this.mActivity = activity;
        this.mDate = date;
        mActivity.findViewById(R.id.addeventbtn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addeventdialog("Account pending",0);
            }
        });
        mActivity.findViewById(R.id.addeventbtn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addeventdialog("Account pending",1);
            }
        });
        mActivity.findViewById(R.id.addeventbtn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addeventdialog("Account pending",2);
            }
        });
        mActivity.findViewById(R.id.addeventbtn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addeventdialog("Account pending",3);
            }
        });
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
        mActivity.alldaytexts[0].setVisibility(View.INVISIBLE);
        mActivity.alldaytexts[1].setVisibility(View.INVISIBLE);
        mActivity.alldaytexts[2].setVisibility(View.INVISIBLE);
        mActivity.alldaytexts[3].setVisibility(View.INVISIBLE);
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
                        caladapter = new CalendarAdapter(mActivity, dt, et, ca, mDate){
                            @Override
                                public void allday(String str)
                            {
                                mActivity.alldaytexts[s-1].setVisibility(View.VISIBLE);
                                mActivity.alldaytexts[s-1].setText(str);
                            }
                        };
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
                    .setMaxResults(30)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .queue(b,bc);
        }
        b.execute();
        return eventStrings;
    }

    public void addeventdialog(String name, final int calsize){
        //Toast.makeText(getApplicationContext(),notifobject.toString(),Toast.LENGTH_LONG).show();
        //freebielayout.getForeground().setAlpha(255);
        LayoutInflater li = LayoutInflater.from(mActivity);
        final View ltdofferView = li.inflate(R.layout.calendar_event, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        final Button okbutton = (Button) ltdofferView.findViewById(R.id.okbutton);
        final ImageButton cancelbutton= (ImageButton) ltdofferView.findViewById(R.id.cancelbutton);
        final TextView accname = (TextView) ltdofferView.findViewById(R.id.acc_addevent);
        final TextView startdate = (TextView) ltdofferView.findViewById(R.id.startdate);
        final TextView starttime = (TextView) ltdofferView.findViewById(R.id.starttime);
        final TextView enddate = (TextView) ltdofferView.findViewById(R.id.enddate);
        final TextView endtime = (TextView) ltdofferView.findViewById(R.id.endtime);
        final EditText eventname = (EditText) ltdofferView.findViewById(R.id.eventname);

        final Calendar c = Calendar.getInstance();
        final int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minute = c.get(Calendar.MINUTE);
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);
        startdate.setText(day+"/"+(month+1)+"/"+year);
        enddate.setText(day+"/"+(month+1)+"/"+year);
        starttime.setText(hour+":"+String.format("%02d", minute));
        endtime.setText((hour+1)+":"+String.format("%02d", minute));
        startday=day;
        startmonth=month+1;
        startyear=year;
        starthour=hour;
        startminute=minute;
        endday=day;
        endmonth=month+1;
        endyear=year;
        endhour=hour+1;
        endminute=minute;

        alertDialogBuilder.setView(ltdofferView);
        alertDialogBuilder
                .setCancelable(true);
        // create alert dialog

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity= Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL;
        //alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        alertDialog.show();
        alertDialog.getWindow().setAttributes(lp);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                event = new Event()
                        .setSummary(eventname.getText().toString());

                DateTime startDateTime = new DateTime(startyear + "-" + String.format("%02d", (startmonth + 1)) + "-" + String.format("%02d", startday) + "T" + String.format("%02d", starthour) + ":" + String.format("%02d", startminute) + ":00+05:30");
                EventDateTime start = new EventDateTime()
                        .setDateTime(startDateTime);
                if (ALL_DAY) {
                    Calendar c = Calendar.getInstance();
                    starthour = c.get(Calendar.HOUR_OF_DAY);
                    startminute = c.get(Calendar.MINUTE);
                    startyear = c.get(Calendar.YEAR);
                    startmonth = c.get(Calendar.MONTH);
                    startday = c.get(Calendar.DAY_OF_MONTH);
                    startDateTime = new DateTime(startyear + "-" + String.format("%02d", (startmonth + 1)) + "-" + String.format("%02d", startday) + "T" + String.format("%02d", starthour) + ":" + String.format("%02d", startminute) + ":00+05:30");
                    start = new EventDateTime()
                            .setDateTime(startDateTime);
                }
                event.setStart(start);
                Log.d("zzzzzzzz", startDateTime.toString());
                DateTime endDateTime = new DateTime(endyear + "-" + String.format("%02d", (endmonth + 1)) + "-" + String.format("%02d", endday) + "T" + String.format("%02d", endhour) + ":" + String.format("%02d", endminute) + ":00+05:30");
                Log.d("zzzzzzzz", endDateTime.toString());
                EventDateTime end = new EventDateTime()
                        .setDateTime(endDateTime);
                if (ALL_DAY) {
                    Calendar c = Calendar.getInstance();
                    endhour = c.get(Calendar.HOUR_OF_DAY);
                    endminute = c.get(Calendar.MINUTE);
                    endyear = c.get(Calendar.YEAR);
                    endmonth = c.get(Calendar.MONTH);
                    endday = c.get(Calendar.DAY_OF_MONTH);
                    endDateTime = new DateTime(endyear + "-" + String.format("%02d", (endmonth + 1)) + "-" + String.format("%02d", endday) + "T" + String.format("%02d", endhour) + ":" + String.format("%02d", endminute) + ":00+05:30");
                    end = new EventDateTime()
                            .setDateTime(startDateTime);
                }
                event.setEnd(end);

                final String calendarId = "primary";
                new AddEventAsync() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {

                            event = mActivity.mService[calsize].events().insert(calendarId, event).execute();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();


            }
        });
        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                //freebielayout.getForeground().setAlpha(0);
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //freebielayout.getForeground().setAlpha(0);
            }
        });
        accname.setText(name);
        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bdl = new Bundle(3);
                bdl.putInt("year", startyear);
                bdl.putInt("month", startmonth);
                bdl.putInt("day", startday);
                DialogFragment df = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        // Do something with the date chosen by the user
                        startdate.setText(day + "/" + (month + 1) + "/" + year);
                        startday=day;
                        startmonth=month;
                        startyear=year;
                        //currdate = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day);
                    }
                };
                df.setArguments(bdl);
                df.show(mActivity.getSupportFragmentManager(), "DatePicker");
            }
        });

        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bdl = new Bundle(3);
                bdl.putInt("year", endyear);
                bdl.putInt("month", endmonth);
                bdl.putInt("day", endday);
                DialogFragment df = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        // Do something with the date chosen by the user
                        enddate.setText(day + "/" + (month + 1) + "/" + year);
                        endday = day;
                        endmonth = month;
                        endyear = year;
                        //currdate = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day);
                    }
                };
                df.setArguments(bdl);
                df.show(mActivity.getSupportFragmentManager(), "DatePicker");
            }
        });

        starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bdl = new Bundle(2);
                bdl.putInt("hour", starthour);
                bdl.putInt("minute", startminute);
                DialogFragment df = new TimePickerFragment() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Do something with the date chosen by the user
                        starttime.setText(hourOfDay + ":" + (minute));
                        startminute = minute;
                        starthour = hourOfDay;
                        //currdate = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day);
                    }
                };
                df.setArguments(bdl);
                df.show(mActivity.getSupportFragmentManager(), "TimePicker");
            }
        });

        endtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bdl = new Bundle(2);
                bdl.putInt("hour", starthour);
                bdl.putInt("minute", startminute);
                DialogFragment df = new TimePickerFragment() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Do something with the date chosen by the user
                        endtime.setText(hourOfDay + ":" + (minute));
                        endminute = minute;
                        endhour = hourOfDay;
                        //currdate = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day);
                    }
                };
                df.setArguments(bdl);
                df.show(mActivity.getSupportFragmentManager(), "DatePicker");
            }
        });

        final Switch alldayswitch = (Switch) ltdofferView.findViewById(R.id.alldayswitch);
        alldayswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ALL_DAY = true;
                    ltdofferView.findViewById(R.id.datesandtimes).setVisibility(View.GONE);
                } else {
                    ALL_DAY = false;
                    ltdofferView.findViewById(R.id.datesandtimes).setVisibility(View.VISIBLE);
                }
            }
        });
    }

}