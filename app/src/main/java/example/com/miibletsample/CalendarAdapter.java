package example.com.miibletsample;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by srivatsan on 25/9/15.
 */
public class CalendarAdapter extends BaseAdapter {
    List<String> dates, events, eventcolors;
    Context context;
    String currdate;
    private static LayoutInflater inflater=null;
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
    public CalendarAdapter(Context context, List<String> dates, List<String> events, List<String> eventcolors, String currdate) {
        // TODO Auto-generated constructor stub
        this.dates=dates;
        this.events=events;
        this.context=context;
        this.eventcolors=eventcolors;
        this.currdate=currdate;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 24;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView time;
        TextView event;
        LinearLayout back;
        //TextView snippet;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.calendar_item_layout, null);
        holder.event=(TextView) rowView.findViewById(R.id.eventview);
        holder.time=(TextView) rowView.findViewById(R.id.timeview);
        holder.back=(LinearLayout) rowView.findViewById(R.id.backlayout);
        //holder.snippet=(TextView) rowView.findViewById(R.id.snippet);
//        holder.from.setText(modelData.get(position).From);
//        holder.subject.setText(modelData.get(position).Subject);
//        holder.snippet.setText(modelData.get(position).Snippet);
        int ind = checkindex(position);
        holder.time.setText(String.format("%02d", position)+":00");
        if(ind!=-1) {
            if(currdate.equals((dates.get(ind)).split("T")[0])) {

                //Date date = (dates.get(ind)).split("T")[1].split(":")[0];
                //String newstring = new SimpleDateFormat("HH:mm").format(date);
                int hour = Integer.valueOf((dates.get(ind)).split("T")[1].split(":")[0]);
                //holder.time.setText(String.valueOf(hour));
                holder.event.setText(events.get(ind));
                int randomNum = 0 + (int) (Math.random() * 16);
                holder.back.setBackgroundColor(Color.parseColor(eventcolors.get(ind)));
                //System.out.println(newstring);
            }
        }
        rowView.setTag(holder);
        return rowView;
    }

    private int checkindex(int position) {
        for(int i=0;i<dates.size();i++) {
            //Log.d("ffffff", dates.get(i));
            //Date date = new SimpleDateFormat("HH:mm:ss.SSS+zz:yy").parse((dates.get(i).split("T"))[1]);
            //String newstring = new SimpleDateFormat("HH").format(date);
            //int hour = Integer.valueOf(newstring);
            int hour = Integer.valueOf((dates.get(i).split("T"))[1].split(":")[0]);
            Log.d("kkkkkk", String.valueOf(hour));
            if (hour == position) {
                Log.d("kkkkkk", "jjjjjj");
                return i;

                //System.out.println(newstring);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            }
        }
        return -1;
    }

    public void changeData(List<EMail> modeldata) {
        //this.modelData = modeldata;
    }

}