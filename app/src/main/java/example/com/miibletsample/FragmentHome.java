package example.com.miibletsample;


import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

/**
 * Created by adarsh on 21/8/15.
 */
public class FragmentHome extends Fragment {
    private TextSwitcher alerts;
    private Handler alertHandler;
    private Runnable alertRunner;
    private int alertIndex = 0;
    private static final int LOOP_TIME = 5000;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        alerts = (TextSwitcher) view.findViewById(R.id.alerts);
        startAlertLoop();
        return view;
    }

    private void startAlertLoop() {
        final Context context = getActivity().getApplicationContext();
        alerts.setFactory(new ViewSwitcher.ViewFactory() {
            public View makeView() {
                TextView myView = new TextView(context);
                myView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                myView.setTextAppearance(context, android.R.style.TextAppearance_Medium);
                myView.setLayoutParams(new TextSwitcher.LayoutParams(
                        ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
                return myView;
            }
        });
        // Animation in = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        // Animation out = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
        Animation in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        alerts.setInAnimation(in);
        alerts.setOutAnimation(out);
        alerts.setText(Html.fromHtml("<b>Work on Water Mains</b><br />Ongoing work on water mains in the vicinity."));
//        alerts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (alertIndex == 0) {
//                    Intent i = new Intent(context, WarningActivity.class);
//                    i.putExtra("warning_type", "water_cut");
//                    startActivity(i);
//                    alertIndex++;
//                } else {
//                    Intent i = new Intent(context, WarningActivity.class);
//                    i.putExtra("warning_type", "roadwork");
//                    startActivity(i);
//                    alertIndex = 0;
//                }
//            }
//        });

        alertHandler = new Handler();
        alertRunner = new Runnable() {
            public void run() {
                if (alertIndex == 0) {
                    alerts.setText(Html.fromHtml("<b>Roadwork at E322 Near Lincoln</b><br />Ongoing roadworks and traffic congestion."));
                    alertIndex++;
                } else {
                    alerts.setText(Html.fromHtml("<b>Work on Water Mains</b><br />Ongoing work on water mains in the vicinity."));
                    alertIndex = 0;
                }
                alertHandler.postDelayed(this, LOOP_TIME);
            }
        };
        alertHandler.postDelayed(alertRunner, LOOP_TIME);
    }
}
