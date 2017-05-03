package edu.usc.congress;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by hehuiliang on 11/27/16.
 */

public class LegisListViewAdapter extends BaseAdapter implements View.OnClickListener{

    private static final String DEFAULT_IMG_DOMAIN = "https://theunitedstates.io/images/congress/original/";

    private ArrayList<JSONObject> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public LegisListViewAdapter(Context context, ArrayList<JSONObject> data) {

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

        LegisListViewComponents components = null;

        if (view == null){
            components = new LegisListViewComponents();
            view = layoutInflater.inflate(R.layout.legislators_listview_layout, null);
            components.image = (ImageView) view.findViewById(R.id.legislators_listview_image);
            components.name = (TextView) view.findViewById(R.id.legislators_listview_name);
            components.info = (TextView) view.findViewById(R.id.legislators_listview_info);
            components.detail = (ImageView) view.findViewById(R.id.legislators_listview_button);
            view.setTag(components);
        }else{
            components = (LegisListViewComponents) view.getTag();
        }

        try {

            JSONObject legislator = (JSONObject) getItem(position);
            Picasso.with(context)
                    .load(DEFAULT_IMG_DOMAIN + legislator.getString("bioguide_id") + ".jpg")
                    .resize(55, 70)
                    .into(components.image);

            components.name.setText(legislator.getString("last_name") + ", " + legislator.getString("first_name"));
            String detailInfo = "(" + legislator.getString("party") + ")" + legislator.getString("state_name") +
                                    " - " + "District" + " " + (legislator.getString("district").equals("null") ? "N.A" : legislator.getString("district"));
            components.info.setText(detailInfo);

            components.detail.setTag(position);
            //components.detail.setOnClickListener(this);

        }catch (Exception e) {
            e.printStackTrace();
            Log.e("JSON Legislator", "Fail to decode legislator JSONObject");
        }

        view.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        LegisListViewComponents components = (LegisListViewComponents) view.getTag();
        int index = (int) components.detail.getTag();
        Intent i = new Intent(context, LegislatorInfoActivity.class);
        Bundle bundle = new Bundle();
        JSONObject legislator = (JSONObject) getItem(index);
        i.putExtra("legislator", legislator.toString());

        /*try{
            String name = legislator.getString("title") + "." + " "
                            + legislator.getString("last_name") + "," + " "
                            + legislator.getString("first_name");
            String email = legislator.getString("oc_email");
            String chamber = legislator.getString("chamber").substring(0, 1).toUpperCase()
                                + legislator.getString("chamber").substring(1);
            String contact = legislator.getString("phone");

            String start = legislator.getString("term_start");

            String end = legislator.getString("term_end");

            String office = legislator.getString("office");
            String state = legislator.getString("state");
            String fax = legislator.getString("fax");
            String birthday = legislator.getString("birthday");

            String bioguideId = legislator.getString("bioguide_id");
            String twitterId = legislator.getString("twitter_id");
            String facebookId = legislator.getString("facebook_id");
            String website = legislator.getString("website");
            String party = legislator.getString("party").equals("R") ? "Republican" : "Democrat";

            bundle.putString("bioguideId", bioguideId);
            bundle.putString("twitterId", twitterId);
            bundle.putString("facebookId", facebookId);
            bundle.putString("website", website);
            bundle.putString("party", party);
            bundle.putString("name", name);
            bundle.putString("email", email);
            bundle.putString("chamber", chamber);
            bundle.putString("contact", contact);
            bundle.putString("start", start);
            bundle.putString("end", end);
            bundle.putString("office", office);
            bundle.putString("state", state);
            bundle.putString("fax", fax);
            bundle.putString("birthday", birthday);

            i.putExtras(bundle);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Legislator Info Passing", "Fail");
        }*/

        context.startActivity(i);

    }

    public final class LegisListViewComponents {

        public ImageView image;
        public TextView name;
        public TextView info;
        public ImageView detail;

    }

}
