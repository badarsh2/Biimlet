package example.com.miibletsample;

import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.gmail.GmailScopes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity implements View.OnClickListener{
    private NonSwipeablePager viewPager;
    LinearLayout[] layouts;
    com.google.api.services.calendar.Calendar mService;
    com.google.api.services.gmail.Gmail mServicegmail;
    public static List<EMail> dataStrings = new ArrayList<>();

    GoogleAccountCredential credential;
    private TextView mStatusText;
    private TextView mResultsText;
    final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY, GmailScopes.GMAIL_READONLY };
    private ImageSwitcher ads;
    private Handler adHandler;
    private Runnable adRunner;
    private int adIndex = 0;
    private static final int LOOP_TIME = 5000;
    private int active;

    public static final String MyPREFERENCES = "CalendarPrefs" ;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private LinearLayout frame1content, frame2content, frame3content, frame4content;
    private TextView frame1activate, frame2activate, frame3activate, frame4activate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //offerdialog();

        findViewById(R.id.tRequest).setOnClickListener(this);
        findViewById(R.id.tStores).setOnClickListener(this);
        findViewById(R.id.tFavorites).setOnClickListener(this);
        findViewById(R.id.tMore).setOnClickListener(this);
        findViewById(R.id.tNotes).setOnClickListener(this);
        findViewById(R.id.tRSS).setOnClickListener(this);
        layouts = new LinearLayout[] { (LinearLayout) findViewById(R.id.tRequest), (LinearLayout) findViewById(R.id.tStores), (LinearLayout) findViewById(R.id.tFavorites), (LinearLayout) findViewById(R.id.tMore), (LinearLayout) findViewById(R.id.tNotes), (LinearLayout) findViewById(R.id.tRSS) };

        viewPager = (NonSwipeablePager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        setSelectedTab(0);
        viewPager.setCurrentItem(0);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelectedTab(position);
                if (position == 0) {

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mStatusText = (TextView) findViewById(R.id.statustext1);
        mStatusText.setText("Retrieving data...");

        mResultsText = (TextView) findViewById(R.id.resultstext1);
        mResultsText.setVerticalScrollBarEnabled(true);
        mResultsText.setMovementMethod(new ScrollingMovementMethod());


        // Initialize credentials and service object.
        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff())
                .setSelectedAccountName(settings.getString(PREF_ACCOUNT_NAME, null));

        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
        GmailApi();
        ads = (ImageSwitcher) findViewById(R.id.ads);
        startAdLoop();

        findViewById(R.id.switch_acc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                credential.setSelectedAccountName(null);

                refreshResults();
                try {
                    FragmentGmail.listView.setVisibility(View.GONE);
                    FragmentGmail.progressBar.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                }
            }
        });

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        active = sharedpreferences.getInt("active", 1);

        frame2activate = (TextView) findViewById(R.id.frame2activate);
        frame2content = (LinearLayout) findViewById(R.id.frame2content);
        frame3activate = (TextView) findViewById(R.id.frame3activate);
        frame3content = (LinearLayout) findViewById(R.id.frame3content);
        frame4activate = (TextView) findViewById(R.id.frame4activate);
        frame4content = (LinearLayout) findViewById(R.id.frame4content);
        findViewById(R.id.viewpager).setVisibility(View.VISIBLE);
        activatecalendars();
        togglevisibility(false, 0);

    }

    private void activatecalendars() {
        togglevisibility(true, active);
        frame2activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frame2activate.setVisibility(View.GONE);
                frame2content.setVisibility(View.VISIBLE);
                togglevisibility(true, 2);
                editor = sharedpreferences.edit();
                editor.putInt("active", 2);
                editor.apply();
            }
        });
        frame3activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frame3activate.setVisibility(View.GONE);
                frame2content.setVisibility(View.VISIBLE);
                togglevisibility(true, 3);
                editor = sharedpreferences.edit();
                editor.putInt("active", 3);
                editor.apply();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            mStatusText.setText("Google Play Services required: " +
                    "after installing, close and relaunch this app.");
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    isGooglePlayServicesAvailable();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        credential.setSelectedAccountName(accountName);
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.commit();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    mStatusText.setText("Account unspecified.");
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode != RESULT_OK) {
                    chooseAccount();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupViewPager(ViewPager viewPager){
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentHome(), "Requests");
        adapter.addFragment(new FragmentCalendar(), "Stores");
        adapter.addFragment(new FragmentFavs(), "Favorites");
        adapter.addFragment(new FragmentWeather(), "Weather");
        adapter.addFragment(new FragmentGmail(), "Calendar");
        adapter.addFragment(new FragmentFavs(), "RSS");
        viewPager.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tRequest:
                setSelectedTab(0);
                viewPager.setCurrentItem(0, false);
                togglevisibility(false, 0);
                findViewById(R.id.viewpager).setVisibility(View.VISIBLE);
                break;
            case R.id.tStores:
                setSelectedTab(1);
                viewPager.setCurrentItem(1, false);
                //Toast.makeText(getApplicationContext(), String.valueOf(active), Toast.LENGTH_SHORT).show();
                togglevisibility(true, active);
                findViewById(R.id.viewpager).setVisibility(View.GONE);
                break;
            case R.id.tFavorites:
                setSelectedTab(2);
                viewPager.setCurrentItem(2, false);
                togglevisibility(false, 0);
                findViewById(R.id.viewpager).setVisibility(View.VISIBLE);
                break;
            case R.id.tMore:
                setSelectedTab(3);
                viewPager.setCurrentItem(3, false);
                togglevisibility(false, 0);
                findViewById(R.id.viewpager).setVisibility(View.VISIBLE);
                break;
            case R.id.tNotes:
                setSelectedTab(4);
                viewPager.setCurrentItem(4, false);
                togglevisibility(false, 0);
                findViewById(R.id.viewpager).setVisibility(View.VISIBLE);
                break;
            case R.id.tRSS:
                setSelectedTab(5);
                viewPager.setCurrentItem(5, false);
                togglevisibility(false, 0);
                findViewById(R.id.viewpager).setVisibility(View.VISIBLE);
                break;
        }
    }

    private void togglevisibility(Boolean stat, int calno)
    {
        if(stat==true) {
            if(calno==0) {
                findViewById(R.id.textslayout).setVisibility(View.VISIBLE);
                findViewById(R.id.textslayout2).setVisibility(View.GONE);
            }
            else if(calno==1) {
//                frame2activate.setVisibility(View.GONE);
//                frame2content.setVisibility(View.VISIBLE);
                findViewById(R.id.textslayout).setVisibility(View.VISIBLE);
                findViewById(R.id.textslayout2).setVisibility(View.GONE);
            }
            else if(calno==2) {
                frame2activate.setVisibility(View.GONE);
                frame2content.setVisibility(View.VISIBLE);
                findViewById(R.id.textslayout).setVisibility(View.VISIBLE);
                findViewById(R.id.textslayout2).setVisibility(View.VISIBLE);
            }
            else if(calno==3) {
                findViewById(R.id.textslayout).setVisibility(View.VISIBLE);
                findViewById(R.id.textslayout2).setVisibility(View.VISIBLE);
                findViewById(R.id.calendarframe4).setVisibility(View.VISIBLE);
                frame4content.setVisibility(View.GONE);
                frame4activate.setVisibility(View.VISIBLE);
                frame3activate.setVisibility(View.GONE);
                frame3content.setVisibility(View.VISIBLE);
                frame2activate.setVisibility(View.GONE);
                frame2content.setVisibility(View.VISIBLE);
            }
        }
        else {
            findViewById(R.id.textslayout).setVisibility(View.GONE);
            findViewById(R.id.textslayout2).setVisibility(View.GONE);
        }
    }

    private void setSelectedTab(int index) {
        for (int i =0; i< layouts.length; i++){
            layouts[i].setBackgroundColor(Color.TRANSPARENT);
        }
        layouts[index].setBackgroundColor(Color.parseColor("#333344"));
    }


    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {

            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void offerdialog(){
        //Toast.makeText(getApplicationContext(),notifobject.toString(),Toast.LENGTH_LONG).show();
        //freebielayout.getForeground().setAlpha(255);
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View ltdofferView = li.inflate(R.layout.popupofferlayout, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        final Button okbutton = (Button) ltdofferView.findViewById(R.id.okbutton);
        final ImageButton cancelbutton= (ImageButton) ltdofferView.findViewById(R.id.cancelbutton);
        final TextView caption = (TextView) ltdofferView.findViewById(R.id.offercaption);
        final TextView description = (TextView) ltdofferView.findViewById(R.id.offerdescription);
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
                //freebielayout.getForeground().setAlpha(0);
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
        //caption.setText(notifobject.getJSONObject("data").getString("title"));
        //description.setText(notifobject.getJSONObject("data").getString("description"));


        //alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        // show it
        //alertDialog.show();
    }

    private void refreshResults() {
        if (credential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            if (isDeviceOnline()) {

                new ApiAsyncTask(this).execute();
                new ApiAsyncTaskGmail(this).execute();
            } else {
                mStatusText.setText("No network connection available.");
            }
        }
    }

    /**
     * Clear any existing Google Calendar API data from the TextView and update
     * the header message; called from background threads and async tasks
     * that need to update the UI (in the UI thread).
     */
    public void clearResultsText() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mStatusText.setText("Retrieving data…");
//                mResultsText.setText("");
//            }
//        });
    }

    /**
     * Fill the data TextView with the given List of Strings; called from
     * background threads and async tasks that need to update the UI (in the
     * UI thread).
     * @param dataStrings a List of Strings to populate the main TextView with.
     */
    public void updateResultsText(final List<String> dataStrings) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dataStrings == null) {
                    mStatusText.setText("Error retrieving data!");
                } else if (dataStrings.size() == 0) {
                    mStatusText.setText("NO UPCOMING EVENTS.");
                } else {
                    mStatusText.setText("UPCOMING EVENTS");
                    mResultsText.setText(TextUtils.join("\n", dataStrings));
                }
            }
        });
    }
    public void updateResultsTextGmail(final List<EMail> dataStrings) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dataStrings == null) {
                    Toast.makeText(getApplicationContext(), "Error retrieving data!", Toast.LENGTH_SHORT).show();
                } else if (dataStrings.size() == 0) {
                    Toast.makeText(getApplicationContext(), "No data found.", Toast.LENGTH_SHORT).show();
                } else {
                    MainActivity.dataStrings = dataStrings;
                    try{
                        FragmentGmail.customAdapter.changeData(MainActivity.dataStrings);
                        FragmentGmail.listView.setAdapter(FragmentGmail.customAdapter);
                        FragmentGmail.listView.setVisibility(View.VISIBLE);
                        FragmentGmail.progressBar.setVisibility(View.GONE);
                    }catch (Exception e){}
                    Toast.makeText(getApplicationContext(), "Data retrieved using" +
                            " the Gmail API:", Toast.LENGTH_SHORT).show();

                    //listView.setAdapter(new CustomAdapter(getActivity(), dataStrings));
                    //mResultsText.setText(TextUtils.join("\n\n", dataStrings));
                }
            }
        });
    }
    /**
     * Show a status message in the list header TextView; called from background
     * threads and async tasks that need to update the UI (in the UI thread).
     * @param message a String to display in the UI header TextView.
     */
    public void updateStatus(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusText.setText(message);
            }
        });
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account.
     */
    private void chooseAccount() {
        startActivityForResult(
                credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
    }

    /**
     * Checks whether the device currently has a network connection.
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date. Will
     * launch an error dialog for the user to update Google Play Services if
     * possible.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        final int connectionStatusCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS ) {
            return false;
        }
        return true;
    }

    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     * @param connectionStatusCode code describing the presence (or lack of)
     *     Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                        connectionStatusCode,
                        MainActivity.this,
                        REQUEST_GOOGLE_PLAY_SERVICES);
                dialog.show();
            }
        });
    }

    private void startAdLoop() {
        final Context context = MainActivity.this;
        ads.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                ImageView myView = new ImageView(context);
                // myView.setBackgroundColor(0xFF000000);
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setAdjustViewBounds(true);
                myView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
                return myView;
            }
        });
        Animation in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        ads.setInAnimation(in);
        ads.setOutAnimation(out);
        ads.setImageResource(R.drawable.banner1);

        adHandler = new Handler();
        adRunner = new Runnable() {
            public void run() {
                if (adIndex == 0) {
                    ads.setImageResource(R.drawable.banner2);
                    adIndex++;
                } else if (adIndex == 1) {
                    ads.setImageResource(R.drawable.banner3);
                    adIndex++;
                } else {
                    ads.setImageResource(R.drawable.banner1);
                    adIndex = 0;
                }
                adHandler.postDelayed(this, LOOP_TIME);
            }
        };
        adHandler.postDelayed(adRunner, LOOP_TIME);
    }

    public void GmailApi(){
        // Initialize credentials and service object.
        mServicegmail = new com.google.api.services.gmail.Gmail.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Gmail API Android Quickstart")
                .build();
        /*if (isGooglePlayServicesAvailable()) {
            refreshResults();
        } else {
            Toast.makeText(getApplicationContext(), "Google Play Services required: " +
                    "after installing, close and relaunch this app.", Toast.LENGTH_SHORT).show();
        }*/
    }
}


