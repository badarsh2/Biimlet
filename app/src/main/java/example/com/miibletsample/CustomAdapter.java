package example.com.miibletsample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by srivatsan on 25/9/15.
 */
public class CustomAdapter extends BaseAdapter {
    List<EMail> modelData;
    Context context;
    private static LayoutInflater inflater=null;
    public CustomAdapter(Context context, List<EMail> modelData) {
        // TODO Auto-generated constructor stub
        this.modelData=modelData;
        this.context=context;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return modelData.size();
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
        TextView from;
        TextView subject;
        TextView snippet;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.mail_item_layout, null);
        holder.from=(TextView) rowView.findViewById(R.id.from);
        holder.subject=(TextView) rowView.findViewById(R.id.subject);
        holder.snippet=(TextView) rowView.findViewById(R.id.snippet);
        holder.from.setText(modelData.get(position).From);
        holder.subject.setText(modelData.get(position).Subject);
        holder.snippet.setText(modelData.get(position).Snippet);
        rowView.setTag(holder);
        return rowView;
    }
    public void changeData(List<EMail> modeldata) {
        this.modelData = modeldata;
    }

}