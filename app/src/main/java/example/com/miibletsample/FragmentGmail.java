package example.com.miibletsample;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sandeep on 21/8/15.
 */
public class FragmentGmail extends Fragment {

    public static ListView listView;
    public static ProgressBar progressBar;
    public static CustomAdapter customAdapter;
    public static TextView no_account;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_gmail, container, false);
        listView = (ListView)view.findViewById(R.id.listview);
        progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
        customAdapter = new CustomAdapter(getActivity(),MainActivity.dataStrings);
        no_account = (TextView)view.findViewById(R.id.no_account);
        List<EMail> e = MainActivity.dataStrings;
        listView.setAdapter(customAdapter);

        if (MainActivity.dataStrings.size()==0){
            listView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
            String name = settings.getString(MainActivity.PREF_ACCOUNT_NAME, "");
            if(name.equals("")){
                progressBar.setVisibility(View.GONE);
                no_account.setVisibility(View.VISIBLE);
            }
        }
        else {
            listView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            no_account.setVisibility(View.GONE);
        }

        return view;
    }

    /**
    /**
     * Called whenever this activity is pushed to the foreground, such as after
     * a call to onCreate().
     */
    @Override
    public void onResume() {
        super.onResume();

    }

}
