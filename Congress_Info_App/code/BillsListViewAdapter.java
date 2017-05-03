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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by hehuiliang on 11/29/16.
 */

public class BillsListViewAdapter extends BaseAdapter implements View.OnClickListener{

    private ArrayList<JSONObject> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public BillsListViewAdapter(ArrayList<JSONObject> data, Context context) {
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

        BillsListViewComponents components = null;

        if (view == null){
            components = new BillsListViewComponents();
            view = layoutInflater.inflate(R.layout.bills_listview_layout, null);
            components.billId = (TextView) view.findViewById(R.id.tv_bills_listview_id);
            components.title = (TextView) view.findViewById(R.id.tv_bills_listview_title);
            components.introduce = (TextView) view.findViewById(R.id.tv_bills_listview_introduce_on);
            components.detail = (ImageView) view.findViewById(R.id.bills_listview_button);
            view.setTag(components);
        }else{
            components = (BillsListViewComponents) view.getTag();
        }

        JSONObject bill = (JSONObject) getItem(position);
        try {
            components.billId.setText(bill.getString("bill_id").toUpperCase());

            String shortTitle = bill.getString("short_title");
            if(shortTitle.equals("null")) {
                components.title.setText(bill.getString("official_title"));
            } else {
                components.title.setText(bill.getString(shortTitle));
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
            Date iDate = sdf.parse(bill.getString("introduced_on"));
            components.introduce.setText(dateFormat.format(iDate));

            components.detail.setTag(position);
            //components.detail.setOnClickListener(this);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Bill JSON Decode", "Fail");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Introduce On Format", "Fail");
        }

        view.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        BillsListViewComponents components = (BillsListViewComponents) view.getTag();
        int index = (int) components.detail.getTag();
        Intent i = new Intent(context, BillsInfoActivity.class);
        JSONObject bill = (JSONObject) getItem(index);
        i.putExtra("bill", bill.toString());
        context.startActivity(i);

    }

    public final class BillsListViewComponents {

        public TextView billId;
        public TextView title;
        public TextView introduce;
        public ImageView detail;

    }
}
