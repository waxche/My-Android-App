package edu.usc.congress;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class BillsInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tv_bill_id, tv_bill_title, tv_bill_type, tv_bill_sponsor;
    private TextView tv_bill_chamber, tv_bill_status, tv_bill_introduced_on, tv_bill_congress_url;
    private TextView tv_bill_versio_status, tv_bill_url;

    private ImageView iv_bill_favorite;

    String billId;
    JSONObject bill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills_info);

        setTitle("Bill Info");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String res = this.getIntent().getStringExtra("bill");
        try {
            bill = new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tv_bill_id = (TextView) findViewById(R.id.tv_bill_id_detail);
        tv_bill_title = (TextView) findViewById(R.id.tv_bill_title_detail);
        tv_bill_type = (TextView) findViewById(R.id.tv_bill_type_detail);
        tv_bill_sponsor = (TextView) findViewById(R.id.tv_sponsor_detail);
        tv_bill_chamber = (TextView) findViewById(R.id.tv_chamber_detail);
        tv_bill_status = (TextView) findViewById(R.id.tv_status_detail);
        tv_bill_introduced_on = (TextView) findViewById(R.id.tv_introduced_on_detail);
        tv_bill_congress_url = (TextView) findViewById(R.id.tv_congress_url_detail);
        tv_bill_versio_status = (TextView) findViewById(R.id.tv_version_status_detail);
        tv_bill_url = (TextView) findViewById(R.id.tv_bill_url_detail);

        try {

            billId = bill.getString("bill_id");
            tv_bill_id.setText(billId.toUpperCase());

            String title = bill.getString("official_title");
            tv_bill_title.setText(title);

            String billType = bill.getString("bill_type");
            tv_bill_type.setText(billType.toUpperCase());

            String sponsor = bill.getJSONObject("sponsor").getString("title") + "." + " "
                                + bill.getJSONObject("sponsor").getString("last_name") + "," + " "
                                + bill.getJSONObject("sponsor").getString("first_name");
            tv_bill_sponsor.setText(sponsor);

            String chamber = bill.getString("chamber");
            chamber = chamber.substring(0, 1).toUpperCase() + chamber.substring(1);
            tv_bill_chamber.setText(chamber);

            boolean status = bill.getJSONObject("history").getBoolean("active");
            tv_bill_status.setText(status == true ? "Active" : "New");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
            Date date = sdf.parse(bill.getString("introduced_on"));
            String introducedOn = dateFormat.format(date);
            tv_bill_introduced_on.setText(introducedOn);

            String congressURL = bill.getJSONObject("urls").getString("congress");
            tv_bill_congress_url.setText(congressURL);

            String versionStatus = bill.getJSONObject("last_version").getString("version_name");
            tv_bill_versio_status.setText(versionStatus);

            String billURL = bill.getJSONObject("last_version").getJSONObject("urls").getString("pdf");
            tv_bill_url.setText(billURL.equals("") ? "N.A" : billURL);

        } catch (ParseException e) {
            e.printStackTrace();
        }catch (JSONException e) {
            e.printStackTrace();
        }

        iv_bill_favorite = (ImageView) findViewById(R.id.btn_bill_favorite);

        int isExist = -1;
        for (int i=0; i<MainActivity.favoriteBillsData.size(); i++){
            try {
                if(MainActivity.favoriteBillsData.get(i).getString("bill_id").equals(billId)){
                    isExist = i;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Picasso.with(this).load(isExist == -1 ? R.drawable.star : R.drawable.active_star).resize(40, 40).into(iv_bill_favorite);
        iv_bill_favorite.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        int isExist = -1;
        for (int i=0; i<MainActivity.favoriteBillsData.size(); i++){
            try {
                if(MainActivity.favoriteBillsData.get(i).getString("bill_id").equals(billId)){
                    isExist = i;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (isExist == -1) {
            if (MainActivity.favoriteBillsData.add(bill)) {
                Toast.makeText(this, "This bill has been added.", Toast.LENGTH_LONG).show();
            }
            Picasso.with(this).load(R.drawable.active_star).resize(40, 40).into(iv_bill_favorite);
        } else {
            MainActivity.favoriteBillsData.remove(isExist);
            Toast.makeText(this, "This bill has been removed.", Toast.LENGTH_LONG).show();
            Picasso.with(this).load(R.drawable.star).resize(40, 40).into(iv_bill_favorite);
        }

        Collections.sort(MainActivity.favoriteBillsData, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject bill1, JSONObject bill2) {
                try {
                    return -(bill1.getString("introduced_on").compareTo(bill2.getString("introduced_on")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        MainActivity.favorBillsAdapter.notifyDataSetChanged();

    }
}
