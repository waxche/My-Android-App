package edu.usc.congress;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class AboutMeActivity extends AppCompatActivity {

    private ImageView iv_my_image;
    private TextView tv_my_name;
    private TextView tv_my_student_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        setTitle("Bill Info");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        iv_my_image = (ImageView) findViewById(R.id.iv_about_me);
        tv_my_name = (TextView) findViewById(R.id.tv_my_name);
        tv_my_student_Id = (TextView) findViewById(R.id.tv_student_id);

        Picasso.with(this).load(R.drawable.myimage).resize(140, 180).into(iv_my_image);

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
