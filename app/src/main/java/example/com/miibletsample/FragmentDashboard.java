package example.com.miibletsample;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by sandeep on 21/8/15.
 */
public class FragmentDashboard extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        GregorianCalendar calendar = new GregorianCalendar();
        TextView dayText = (TextView) view.findViewById(R.id.daymonth);
        dayText.setText("" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US).toUpperCase(Locale.US));
        //monthText.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US).toUpperCase(Locale.US));
        return view;
    }
}
