package com.taf.raghu.doorpick;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by raghu on 22/11/16.
 */
public class ScheduleAdapter extends ArrayAdapter<Schedules> {

    int resource;
    Context context;
    LayoutInflater inflater;
    ArrayList<Schedules> mSchedules;

    //LayoutInflater view;

    public ScheduleAdapter(Context context, ArrayList<Schedules> schedules) {
        super(context, R.layout.schedule_items, schedules);
        this.context = context;
        //this.context = context.getApplicationContext();
       // this.resource = resource;
        mSchedules = schedules;

//        if(mSchedules!=null){
//            view = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);}
//        else Toast.makeText(context, "No Results to Show", Toast.LENGTH_SHORT).show();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        inflater = LayoutInflater.from(context);
        View customVistview = inflater.inflate(R.layout.schedule_items,parent, false);

        TextView  sName = (TextView) customVistview.findViewById(R.id.txtSpName);
        TextView  sDesc = (TextView) customVistview.findViewById(R.id.txtSpDesc);
        TextView  sSts = (TextView) customVistview.findViewById(R.id.txtSpSts);

        Schedules sc = mSchedules.get(position);
        sName.setText(sc.getpName());
        sDesc.setText(sc.getpDesc());
        sSts.setText(sc.getsStatus());
        return customVistview;
    }



    //    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        RecyclerView.ViewHolder vh;
//        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(resource, parent, false);
//            vh = new RecyclerView.ViewHolder();
//            vh.nicknameV = (TextView) convertView.findViewById(R.id.nick_name);
//            vh.countV = (TextView) convertView.findViewById(R.id.view_count);
//            vh.streetV = (TextView) convertView.findViewById(R.id.house_street);
//            //    vh.cityV = (TextView) convertView.findViewById(R.id.house_city);
//            //   vh.stateV = (TextView) convertView.findViewById(R.id.house_state);
//            //    vh.zipV = (TextView) convertView.findViewById(R.id.house_zip);
//            vh.addressFullV = (TextView) convertView.findViewById(R.id.address_line2);
//            vh.statusV= (TextView) convertView.findViewById(R.id.statuscurr);
//            vh.houseImage = (ImageView) convertView.findViewById(R.id.house_image);
//            convertView.setTag(vh);
//
//        }else{
//            vh  = (RecyclerView.ViewHolder) convertView.getTag();
//
//        }
//
//        PropertyModel property = getItem(position);
//        System.out.println("ID "+ property.getKey());
//        String temp_view = property.getView_count();
//        if (temp_view == null || temp_view.equals("null")) temp_view = "0";
//        vh.countV.setText(temp_view + " views");
//        vh.streetV.setText(property.getStreet());
//        vh.nicknameV.setText(property.getNickname());
////        vh.stateV.setText(property.getState());
////        vh.cityV.setText(property.getCity());
////        vh.zipV.setText(property.getZip());
//        vh.addressFullV.setText(property.getState()+" "+property.getCity()+" "+property.getZip());
//        vh.statusV.setText(property.getStatus());
//        new DownloadImageTask(vh.houseImage).execute(property.getImages());
//
//
//        if(selected.equals(position)){
//            convertView.setBackgroundColor(Color.LTGRAY);
//        }
//
//
//
//        return convertView;
//    }
//

}
