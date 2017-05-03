package edu.usc.congress;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;

public class CommitteeInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tv_committee_id, tv_committee_name, tv_committee_chamber, tv_committee_parent;
    private TextView tv_committee_contact, tv_committee_office;

    private ImageView iv_committee_chamber, btn_favorite_committee;

    private JSONObject committee;
    private String committeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_committee_info);

        setTitle("Committee Info");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String res = this.getIntent().getStringExtra("committee");
        try {
            committee = new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tv_committee_id = (TextView) findViewById(R.id.tv_committee_id_detail);
        tv_committee_name = (TextView) findViewById(R.id.tv_committee_name_detail);
        tv_committee_chamber = (TextView) findViewById(R.id.tv_committee_chamber_detail);
        tv_committee_parent = (TextView) findViewById(R.id.tv_committee_parent_detail);
        tv_committee_contact = (TextView) findViewById(R.id.tv_committee_contact_detail);
        tv_committee_office = (TextView) findViewById(R.id.tv_committee_office_detail);

        iv_committee_chamber = (ImageView) findViewById(R.id.iv_committee_chamber);

        try {

            committeeId = committee.getString("committee_id");
            tv_committee_id.setText(committee.getString("committee_id"));
            tv_committee_name.setText(committee.getString("name"));
            Picasso.with(this)
                    .load(committee.getString("chamber").equals("house") ? R.drawable.h : R.drawable.s)
                    .resize(30, 30).into(iv_committee_chamber);
            tv_committee_chamber.setText(committee.getString("chamber").substring(0, 1).toUpperCase() + committee.getString("chamber").substring(1));
            tv_committee_parent.setText(committee.has("parent_committee_id") ? committee.getString("parent_committee_id") : "N.A");
            tv_committee_contact.setText(committee.has("phone") || !committee.getString("phone").equals("null") ? committee.getString("phone") : "");
            tv_committee_office.setText(committee.has("office") ? committee.getString("office") : "N.A");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn_favorite_committee = (ImageView) findViewById(R.id.btn_committee_favorite);

        int isExist = -1;
        for (int i=0; i<MainActivity.favoriteCommiData.size(); i++){
            try {
                if(MainActivity.favoriteCommiData.get(i).getString("committee_id").equals(committeeId)){
                    isExist = i;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Picasso.with(this).load(isExist == -1 ? R.drawable.star : R.drawable.active_star).resize(40, 40).into(btn_favorite_committee);
        btn_favorite_committee.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        int isExist = -1;
        for (int i=0; i<MainActivity.favoriteCommiData.size(); i++){
            try {
                if(MainActivity.favoriteCommiData.get(i).getString("committee_id").equals(committeeId)){
                    isExist = i;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (isExist == -1) {
            if (MainActivity.favoriteCommiData.add(committee)) {
                Toast.makeText(this, "This bill has been added.", Toast.LENGTH_LONG).show();
            }
            Picasso.with(this).load(R.drawable.active_star).resize(40, 40).into(btn_favorite_committee);
        } else {
            MainActivity.favoriteCommiData.remove(isExist);
            Toast.makeText(this, "This bill has been removed.", Toast.LENGTH_LONG).show();
            Picasso.with(this).load(R.drawable.star).resize(40, 40).into(btn_favorite_committee);
        }

        Collections.sort(MainActivity.favoriteCommiData, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject committee1, JSONObject committee2) {
                try {
                    return committee1.getString("name").compareTo(committee2.getString("name"));
                } catch (Exception e) {
                    Log.e("Committees Comparison", "Fail");
                    return 0;
                }
            }
        });

        MainActivity.favorCommiAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
