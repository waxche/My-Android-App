package edu.usc.congress;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class LegislatorInfoActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView iv_legis_image;
    private ImageView iv_legis_party;

    private TextView tv_legis_name, tv_legis_email, tv_legis_chamber, tv_legis_contact;
    private TextView tv_legis_s_term, tv_legis_e_term, tv_legis_office, tv_legis_state;
    private TextView tv_legis_fax, tv_legis_birthday, tv_legis_party, tv_legis_term_presentage;

    private ImageView btn_website, btn_twitter, btn_facebook, btn_favorite;

    private ProgressBar pb_legis_term;

    private String twitterId;
    private String facebookId;
    private String website;

    private String bioguideId, party, name, email, chamber, contact, start, end, office, state, fax, birthday;
    private JSONObject legislator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legislator_info);
        setTitle("Legislator Info");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String res = this.getIntent().getStringExtra("legislator");
        try {
            legislator = new JSONObject(res);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            bioguideId = legislator.getString("bioguide_id");
            party = legislator.getString("party").equals("R") ? "Republican" : "Democrat";
            name = legislator.getString("title") + "." + " "
                            + legislator.getString("last_name") + "," + " "
                            + legislator.getString("first_name");
            email = legislator.getString("oc_email");
            chamber = legislator.getString("chamber").substring(0, 1).toUpperCase()
                            + legislator.getString("chamber").substring(1);
            contact = legislator.getString("phone");
            start = legislator.getString("term_start");
            end = legislator.getString("term_end");
            office = legislator.getString("office");
            state = legislator.getString("state");
            fax = legislator.getString("fax");
            birthday = legislator.getString("birthday");

            twitterId = legislator.getString("twitter_id");
            facebookId = legislator.getString("facebook_id");
            website = legislator.getString("website");

        } catch (Exception e) {
            e.printStackTrace();
        }

        tv_legis_name = (TextView) findViewById(R.id.tv_legislator_name);
        tv_legis_email = (TextView) findViewById(R.id.tv_legislator_email);
        tv_legis_chamber = (TextView) findViewById(R.id.tv_legislator_chamber);
        tv_legis_contact = (TextView) findViewById(R.id.tv_legislator_contact);
        tv_legis_s_term = (TextView) findViewById(R.id.tv_legislator_start_term);
        tv_legis_e_term = (TextView) findViewById(R.id.tv_legislator_end_term);
        tv_legis_office = (TextView) findViewById(R.id.tv_legislator_office);
        tv_legis_state = (TextView) findViewById(R.id.tv_legislator_state);
        tv_legis_fax = (TextView) findViewById(R.id.tv_legislator_fax);
        tv_legis_birthday = (TextView) findViewById(R.id.tv_legislator_birthday);
        tv_legis_party = (TextView) findViewById(R.id.tv_legislator_party);
        tv_legis_term_presentage = (TextView) findViewById(R.id.pb_legislator_term_presentage);

        int presentage = 0;

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);

            Date sDate = sdf.parse(start);
            start = dateFormat.format(sDate);

            Date eDate = sdf.parse(end);
            end = dateFormat.format(eDate);

            Date bDate = sdf.parse(birthday);
            birthday = dateFormat.format(bDate);

            Date today = new Date();
            if (today.getTime() > eDate.getTime()) {
                presentage = 100;
            } else {
                double between = (double) (eDate.getTime() - sDate.getTime());
                double still = (double) (today.getTime() - sDate.getTime());
                double rate = still / between;
                presentage = (int) (rate * 100);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Date Parsing", "Fail");
        }

        pb_legis_term = (ProgressBar) findViewById(R.id.pb_legislator_term_progress);
        pb_legis_term.setMax(100);
        pb_legis_term.setProgress(presentage);
        tv_legis_term_presentage.setText(String.valueOf(presentage) + "%");

        iv_legis_image = (ImageView) findViewById(R.id.iv_legislator_image);
        iv_legis_party = (ImageView) findViewById(R.id.iv_legislator_party);

        Picasso.with(this)
                .load("https://theunitedstates.io/images/congress/original/" + bioguideId + ".jpg")
                .resize(140, 180).into(iv_legis_image);
        Picasso.with(this)
                .load((party.equals("Republican") ? R.drawable.r : R.drawable.d))
                .resize(30, 30).into(iv_legis_party);

        tv_legis_party.setText(party);
        tv_legis_name.setText(name);
        tv_legis_email.setText(email);
        tv_legis_chamber.setText(chamber);
        tv_legis_contact.setText(contact.equals("null") ? "N.A" : contact);
        tv_legis_s_term.setText(start);
        tv_legis_e_term.setText(end);
        tv_legis_office.setText(office.equals("null") ? "N.A" : office);
        tv_legis_state.setText(state);
        tv_legis_fax.setText(fax.equals("null") ? "N.A" : fax);
        tv_legis_birthday.setText(birthday);

        btn_website = (ImageView) findViewById(R.id.btn_website);
        btn_twitter = (ImageView) findViewById(R.id.btn_twitter);
        btn_facebook = (ImageView) findViewById(R.id.btn_facebook);
        btn_favorite = (ImageView) findViewById(R.id.btn_favorite);

        Picasso.with(this).load(R.drawable.w).resize(40, 40).into(btn_website);
        Picasso.with(this).load(R.drawable.t).resize(40, 40).into(btn_twitter);
        Picasso.with(this).load(R.drawable.f).resize(40, 40).into(btn_facebook);

        int isExist = -1;
        for (int i=0; i<MainActivity.favoriteLegisData.size(); i++){
            try {
                if(MainActivity.favoriteLegisData.get(i).getString("bioguide_id").equals(bioguideId)){
                    isExist = i;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Picasso.with(this).load(isExist == -1 ? R.drawable.star : R.drawable.active_star).resize(40, 40).into(btn_favorite);

        btn_website.setOnClickListener(this);
        btn_twitter.setOnClickListener(this);
        btn_facebook.setOnClickListener(this);
        btn_favorite.setOnClickListener(this);

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

        Uri url = null;

        switch (view.getId()) {
            case R.id.btn_website:
                if (website.equals("null")) {
                    Toast.makeText(this, "The Legislator website is not available", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    url = Uri.parse(website);
                }
                break;
            case R.id.btn_twitter:
                if (twitterId.equals("null")) {
                    Toast.makeText(this, "The Legislator twitter is not available", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    url = Uri.parse("https://twitter.com/" + twitterId);
                }
                break;
            case R.id.btn_facebook:
                if (facebookId.equals("null")) {
                    Toast.makeText(this, "The Legislator facebook is not available", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    url = Uri.parse("https://www.facebook.com/" + facebookId);
                }
                break;
            case R.id.btn_favorite:
                int isExist = -1;
                for (int i=0; i<MainActivity.favoriteLegisData.size(); i++){
                    try {
                        if(MainActivity.favoriteLegisData.get(i).getString("bioguide_id").equals(bioguideId)){
                            isExist = i;
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (isExist == -1) {
                    if (MainActivity.favoriteLegisData.add(legislator)) {
                        Toast.makeText(this, "This legislator has been added.", Toast.LENGTH_LONG).show();
                    }
                    Picasso.with(this).load(R.drawable.active_star).resize(40, 40).into(btn_favorite);
                } else {
                    MainActivity.favoriteLegisData.remove(isExist);
                    Toast.makeText(this, "This legislator has been removed.", Toast.LENGTH_LONG).show();
                    Picasso.with(this).load(R.drawable.star).resize(40, 40).into(btn_favorite);
                }

                Collections.sort(MainActivity.favoriteLegisData, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject legislator1, JSONObject legislator2) {
                        try {
                            return legislator1.getString("last_name").compareTo(legislator2.getString("last_name"));
                        } catch (Exception e) {
                            Log.e("Legislators Comparison", "Fail");
                            return 0;
                        }
                    }
                });

                MainActivity.favorLegisAdapter.notifyDataSetChanged();
                break;
        }

        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setData(url);
        startActivity(intent);

    }
}
