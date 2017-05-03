package edu.usc.congress;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hehuiliang on 11/30/16.
 */

public class CommiListViewAdapter extends BaseAdapter implements View.OnClickListener{

    private ArrayList<JSONObject> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public CommiListViewAdapter(ArrayList<JSONObject> data, Context context) {
        this.data = data;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        CommiListViewComponents components;

        if (view == null){
            components = new CommiListViewComponents();
            view = layoutInflater.inflate(R.layout.committees_listview_layout, null);
            components.committeeId = (TextView) view.findViewById(R.id.tv_listview_committee_id);
            components.name = (TextView) view.findViewById(R.id.tv_committees_listview_name);
            components.chamber = (TextView) view.findViewById(R.id.tv_committees_listview_chamber);
            components.detail = (ImageView) view.findViewById(R.id.committees_listview_button);
            view.setTag(components);
        }else{
            components = (CommiListViewComponents) view.getTag();
        }

        JSONObject committee = (JSONObject) getItem(position);

        try {

            components.committeeId.setText(committee.getString("committee_id").toUpperCase());
            components.name.setText(committee.getString("name"));
            components.chamber.setText(committee.getString("chamber").substring(0, 1).toUpperCase() + committee.getString("chamber").substring(1));

            components.detail.setTag(position);
            //components.detail.setOnClickListener(this);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Committees JSON Decode", "Fail");
        }

        view.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        CommiListViewComponents components = (CommiListViewComponents) view.getTag();
        int index = (int) components.detail.getTag();
        Intent i = new Intent(context, CommitteeInfoActivity.class);
        i.putExtra("committee", getItem(index).toString());
        context.startActivity(i);

    }

    public final class CommiListViewComponents {

        public TextView committeeId;
        public TextView name;
        public TextView chamber;
        public ImageView detail;

    }
}
