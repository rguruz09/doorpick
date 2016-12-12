package com.taf.raghu.doorpick;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Raghu on 12/10/2016.
 */

public class SchedulesListFragment extends ListFragment {

    private ArrayList<Schedules> scheduleArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        scheduleArrayList = ScheduleListSington.get(getActivity()).getOrdersListInfos();

        ScheduleAdapter adapter = new ScheduleAdapter(this.getContext(),scheduleArrayList);
        setListAdapter(adapter);
        //new UrlActivity().execute(ordersListInfos);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View ordersview = inflater.inflate(R.layout.fragment_schedule_list,container,false);
        ListView lv = (ListView)ordersview.findViewById(android.R.id.list);
        return ordersview;

    }



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Schedules schedule = scheduleArrayList.get(position);

        Intent i = new Intent(getActivity().getApplicationContext(), ScheduleDetailsActivity.class);
        i.putExtra("position", position );
        startActivity(i);

//        SingleOrderDetailFragment fragment = new SingleOrderDetailFragment();
//        Bundle args = new Bundle();
//        args.putSerializable(SingleOrderDetailFragment.EXTRA_ID, orderid);
//        fragment.setArguments(args);
//        getFragmentManager().beginTransaction().replace(R.id.mainpage_fragment,fragment).commit();

    }

}
