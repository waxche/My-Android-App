package edu.usc.congress;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hehuiliang on 11/26/16.
 */

public class CongressThread extends Thread {

    //private static final String DOMAIN = "http://104.198.0.197:8080/";
    private static final String DOMAIN = "http://congress-149408.appspot.com/congress.php";
    //private static final String DOMAIN = "http://congress.api.sunlightfoundation.com/";
    private static final String APIKEY = "&apikey=e3e69d7d0ba94fcbbb5714b228d09337";

    private static final int LEGISLATORS = 1;
    private static final int BILLS_ACTIVE = 2;
    private static final int BILLS_NEW = 3;
    private static final int COMMITTEES = 4;

    private int operation;
    private Handler mHandler;

    public CongressThread(int operation, Handler mHandler) {
        this.operation = operation;
        this.mHandler = mHandler;
    }

    @Override
    public void run() {

        try{

            String link = "";

            switch(operation) {
                case LEGISLATORS:
                    link = DOMAIN + "?operation=legislators";
                    break;
                case BILLS_ACTIVE:
                    link = DOMAIN + "?operation=bills&active=true";
                    break;
                case BILLS_NEW:
                    link = DOMAIN + "?operation=bills";
                    break;
                case COMMITTEES:
                    link = DOMAIN + "?operation=committees";
                    break;
            }

            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream is = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            StringBuffer sb = new StringBuffer();
            String line = "";
            while((line = br.readLine()) != null) {
                sb.append(line);
            }

            String JSONString = sb.toString();

            JSONObject jsonObject = new JSONObject(JSONString);
            mHandler.obtainMessage(operation, jsonObject).sendToTarget();

        }catch (Exception e) {
            Log.e("Http Connection", "Fail");
        }

    }
}
