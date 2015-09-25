package example.com.miibletsample;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sandeep on 21/8/15.
 */
public class FragmentFavs extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mail, container, false);
//        mailRecyclerView = (RecyclerView) view.findViewById(R.id.mailRecyclerView);
//        mailRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        ArrayList<ItemMail> list = new ArrayList<>();
//        ItemMail im = new ItemMail("00", "Pay Electricity Bill", "Dear Customer, Your Electricity bill for the month of....");
//        list.add(im);
//        ItemMail im2 = new ItemMail("00", "Snow Clearing Schedule", "Dear Customer, Snow Clearing schedule for the month of....");
//        list.add(im2);
//        ItemMail im3 = new ItemMail("00", "Repair works on A45 Expressway", "Dear Customer, This is to inform you about the ....");
//        list.add(im3);
//        ItemMail im4 = new ItemMail("00", "Pay Electricity Bill", "Dear Customer, Your Electricity bill for the month of....");
//        list.add(im4);
//        ItemMail im5 = new ItemMail("00", "Follow-up for the complaint ID #11324", "Dear Customer, Regarding the complaint you had given on....");
//        list.add(im5);
//        ItemMail im6 = new ItemMail("00", "Central Square now has free 24x7 Wi-fi", "Dear Customer, This is to inform you that Wi-fi has....");
//        list.add(im6);
//        ItemMail im7 = new ItemMail("00", "Pay Electricity Bill", "Dear Customer, Your Electricity bill for the month of....");
//        list.add(im7);
//        ItemMail im8 = new ItemMail("00", "Inaugural of new bus routes", "Dear Customer, New buses have been inaugurated from....");
//        list.add(im8);
//
//        AdapterRecyclerViewMail adap1 = new AdapterRecyclerViewMail(list, getActivity());
//        mailRecyclerView.setAdapter(adap1);
        TextView[] froms = new TextView[]{
                (TextView)view.findViewById(R.id.item1).findViewById(R.id.from),
                (TextView)view.findViewById(R.id.item2).findViewById(R.id.from),
                (TextView)view.findViewById(R.id.item3).findViewById(R.id.from),
                (TextView)view.findViewById(R.id.item4).findViewById(R.id.from),
                (TextView)view.findViewById(R.id.item5).findViewById(R.id.from),
                (TextView)view.findViewById(R.id.item6).findViewById(R.id.from),
                (TextView)view.findViewById(R.id.item7).findViewById(R.id.from),
                (TextView)view.findViewById(R.id.item8).findViewById(R.id.from),
                (TextView)view.findViewById(R.id.item9).findViewById(R.id.from),
                (TextView)view.findViewById(R.id.item10).findViewById(R.id.from),
                (TextView)view.findViewById(R.id.item11).findViewById(R.id.from)};
        TextView[] subjects = new TextView[]{
                (TextView)view.findViewById(R.id.item1).findViewById(R.id.subject),
                (TextView)view.findViewById(R.id.item2).findViewById(R.id.subject),
                (TextView)view.findViewById(R.id.item3).findViewById(R.id.subject),
                (TextView)view.findViewById(R.id.item4).findViewById(R.id.subject),
                (TextView)view.findViewById(R.id.item5).findViewById(R.id.subject),
                (TextView)view.findViewById(R.id.item6).findViewById(R.id.subject),
                (TextView)view.findViewById(R.id.item7).findViewById(R.id.subject),
                (TextView)view.findViewById(R.id.item8).findViewById(R.id.subject),
                (TextView)view.findViewById(R.id.item9).findViewById(R.id.subject),
                (TextView)view.findViewById(R.id.item10).findViewById(R.id.subject),
                (TextView)view.findViewById(R.id.item11).findViewById(R.id.subject)};
        String[] s = new String[]{
                "Pay Electricity Bill",
                "Snow Clearing Schedule",
                "Repair works on A45 Expressway",
                "Pay Electricity Bill",
                "Follow-up for the complaint ID #11324",
                "Central Square now has free 24x7 Wi-fi",
                "Pay Electricity Bill",
                "Snow Clearing Schedule",
                "Repair works on A45 Expressway",
                "Pay Electricity Bill",
                "Follow-up for the complaint ID #11323"
        };
//
        String[] t = new String[]{
                "Dear Customer, Your Electricity bill for the month of....",
                "Dear Customer, Snow Clearing Schedule for the month of....",
                "Dear Customer, This is to inform you about the ....",
                "Dear Customer, Your Electricity bill for the month of....",
                "Dear Customer, Regarding the complaint you had given on....",
                "Dear Customer, This is to inform you that Wi-fi has....",
                "Dear Customer, Your Electricity bill for the month of....",
                "Dear Customer, Snow Clearing Schedule for the month of....",
                "Dear Customer, This is to inform you about the ....",
                "Dear Customer, Your Electricity bill for the month of....",
                "Dear Customer, Regarding the complaint you had given on...."
        };
//
        for(int i=0;i<11;i++)
        {
            froms[i].setText(s[i]);
            subjects[i].setText(t[i]);
        }
        //tv.setText("Pay Electricity Bill");
        return view;
    }
}
