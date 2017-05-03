package edu.usc.congress;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Fragments
    private static final int LEGISLATORS = 1;
    private static final int BILLS_ACTIVE = 2;
    private static final int BILLS_NEW = 3;
    private static final int COMMITTEES = 4;
    private static final int FAVORITES = 5;

    private LegislatorsFragment legislatorsFragment;
    private BillsFragment billsFragment;
    private CommitteesFragment committeesFragment;
    private FavoritesFragment favoritesFragment;

    // Legislators
    private TabLayout legislatorsTabLayout;
    private ViewPager legislatorsViewPager;
    private TabLayout billsTabLayout;
    private ViewPager billsViewPager;
    private TabLayout committeesTabLayout;
    private ViewPager committeesViewPager;
    private TabLayout favoritesTabLayout;
    private ViewPager favoritesViewPager;

    private ArrayList<JSONObject> legislatorsData;
    private ArrayList<JSONObject> legisHouseData;
    private ArrayList<JSONObject> legisSenateData;

    private ArrayList<JSONObject> activeBillsData;
    private ArrayList<JSONObject> newBillsData;

    private ArrayList<JSONObject> commiHouseData;
    private ArrayList<JSONObject> commiSenateData;
    private ArrayList<JSONObject> commiJointData;

    public static ArrayList<JSONObject> favoriteLegisData;
    public static ArrayList<JSONObject> favoriteBillsData;
    public static ArrayList<JSONObject> favoriteCommiData;

    public static LegisListViewAdapter favorLegisAdapter;
    public static BillsListViewAdapter favorBillsAdapter;
    public static CommiListViewAdapter favorCommiAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initiate Fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        legislatorsFragment = new LegislatorsFragment();
        transaction.add(R.id.fragment_content, legislatorsFragment);
        billsFragment = new BillsFragment();
        transaction.add(R.id.fragment_content, billsFragment);
        committeesFragment = new CommitteesFragment();
        transaction.add(R.id.fragment_content, committeesFragment);
        favoritesFragment = new FavoritesFragment();
        transaction.add(R.id.fragment_content, favoritesFragment);

        transaction.commit();

        // set the default fragment
        setFragment(1);
        setTitle("Legislators");

        favoriteLegisData = new ArrayList<JSONObject>();
        favoriteBillsData = new ArrayList<JSONObject>();
        favoriteCommiData = new ArrayList<JSONObject>();

        SharedPreferences preferences = getSharedPreferences("user", this.MODE_PRIVATE);
        int favorLegisSize = preferences.getInt("favorLegisSize", 0);
        for (int i=0; i<favorLegisSize; i++) {
            try {
                favoriteLegisData.add(new JSONObject(preferences.getString("Legis:"+i, "")));
            } catch (JSONException e) {e.printStackTrace();

            }
        }
        int favorBillsSize = preferences.getInt("favorBillsSize", 0);
        for (int i=0; i<favorBillsSize; i++) {
            try {
                favoriteBillsData.add(new JSONObject(preferences.getString("Bills:"+i, "")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        int favorCommiSize = preferences.getInt("favorCommiSize", 0);
        for (int i=0; i<favorCommiSize; i++) {
            try {
                favoriteCommiData.add(new JSONObject(preferences.getString("Commi:"+i, "")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        favorLegisAdapter = new LegisListViewAdapter(this, favoriteLegisData);
        favorBillsAdapter = new BillsListViewAdapter(favoriteBillsData, this);
        favorCommiAdapter = new CommiListViewAdapter(favoriteCommiData, this);

    }

    // Fragment Initiation
    private void setFragment(int index) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        switch (index) {
            case LEGISLATORS:
                hideFragment(transaction);
                transaction.show(legislatorsFragment);
                break;
            case BILLS_ACTIVE:
                hideFragment(transaction);
                transaction.show(billsFragment);
                break;
            case COMMITTEES:
                hideFragment(transaction);
                transaction.show(committeesFragment);
                break;
            case FAVORITES:
                hideFragment(transaction);
                transaction.show(favoritesFragment);
                break;
        }

        transaction.commit();

    }

    private void hideFragment(FragmentTransaction transaction){

        if(legislatorsFragment != null){
            transaction.hide(legislatorsFragment);
        }
        if(billsFragment != null){
            transaction.hide(billsFragment);
        }
        if(committeesFragment != null){
            transaction.hide(committeesFragment);
        }
        if(favoritesFragment != null){
            transaction.hide(favoritesFragment);
        }

    }

    private ListView lv_state;
    private ListView lv_house;
    private ListView lv_senate;
    private ListView lv_favor;

    private ListView lv_active_bills;
    private ListView lv_new_bills;

    private ListView lv_house_committees;
    private ListView lv_senate_committees;
    private ListView lv_joint_committees;

    private ListView lv_favorite_legislators;
    private ListView lv_favorite_bills;
    private ListView lv_favorite_committees;


    private Handler mHandler;

    @Override
    protected void onStart() {

        super.onStart();
        if(legislatorsTabLayout == null || legislatorsViewPager == null) initLegislatorView();
        if(billsTabLayout == null || billsViewPager == null) initBillsView();
        if(committeesTabLayout == null || committeesViewPager == null) initCommitteesView();
        if(favoritesTabLayout == null || favoritesViewPager == null) initFavoritesView();

        if(legislatorsData == null) initStatesView();
    }

    // Legislators View Initiation
    private void initLegislatorView() {

        legislatorsViewPager = (ViewPager) legislatorsFragment.getView().findViewById(R.id.vp_view_legislators);
        legislatorsTabLayout = (TabLayout) legislatorsFragment.getView().findViewById(R.id.tabs_legislators);
        legislatorsViewPager.setOffscreenPageLimit(3);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View states = mInflater.inflate(R.layout.by_states, null);
        View house = mInflater.inflate(R.layout.house, null);
        View senate = mInflater.inflate(R.layout.senate, null);

        List<View> mViewList = new ArrayList<View>();
        mViewList.add(states);
        mViewList.add(house);
        mViewList.add(senate);

        List<String> mTitleList = new ArrayList<String>();
        mTitleList.add("BY STATES");
        mTitleList.add("HOUSE");
        mTitleList.add("SENATE");

        legislatorsTabLayout.setTabMode(TabLayout.MODE_FIXED);
        legislatorsTabLayout.addTab(legislatorsTabLayout.newTab().setText(mTitleList.get(0)));
        legislatorsTabLayout.addTab(legislatorsTabLayout.newTab().setText(mTitleList.get(1)));
        legislatorsTabLayout.addTab(legislatorsTabLayout.newTab().setText(mTitleList.get(2)));

        MyPagerAdapter legislatorsAdapter = new MyPagerAdapter(mViewList, mTitleList);

        legislatorsViewPager.setAdapter(legislatorsAdapter);//给ViewPager设置适配器
        legislatorsTabLayout.setupWithViewPager(legislatorsViewPager);//将TabLayout和ViewPager关联起来。
        legislatorsTabLayout.setTabsFromPagerAdapter(legislatorsAdapter);//给Tabs设置适配器

    }

    // Bill View Initiation
    private void initBillsView() {

        billsViewPager = (ViewPager) billsFragment.getView().findViewById(R.id.vp_view_bills);
        billsTabLayout = (TabLayout) billsFragment.getView().findViewById(R.id.tabs_bills);
        billsViewPager.setOffscreenPageLimit(2);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View activeBills = mInflater.inflate(R.layout.active_bills, null);
        View newBills = mInflater.inflate(R.layout.new_bills, null);

        List<View> mViewList = new ArrayList<View>();
        mViewList.add(activeBills);
        mViewList.add(newBills);

        List<String> mTitleList = new ArrayList<String>();
        mTitleList.add("ACTIVE BILLS");
        mTitleList.add("NEW BILLS");

        billsTabLayout.setTabMode(TabLayout.MODE_FIXED);
        billsTabLayout.addTab(billsTabLayout.newTab().setText(mTitleList.get(0)));
        billsTabLayout.addTab(billsTabLayout.newTab().setText(mTitleList.get(1)));

        MyPagerAdapter billsAdapter = new MyPagerAdapter(mViewList, mTitleList);

        billsViewPager.setAdapter(billsAdapter);//给ViewPager设置适配器
        billsTabLayout.setupWithViewPager(billsViewPager);//将TabLayout和ViewPager关联起来。
        billsTabLayout.setTabsFromPagerAdapter(billsAdapter);//给Tabs设置适配器

    }

    // Committees View Initiation
    private void initCommitteesView() {

        committeesViewPager = (ViewPager) committeesFragment.getView().findViewById(R.id.vp_view_committees);
        committeesTabLayout = (TabLayout) committeesFragment.getView().findViewById(R.id.tabs_committees);
        committeesViewPager.setOffscreenPageLimit(3);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View house = mInflater.inflate(R.layout.committees_house, null);
        View senate = mInflater.inflate(R.layout.committees_senate, null);
        View joint = mInflater.inflate(R.layout.committees_joint, null);

        List<View> mViewList = new ArrayList<View>();
        mViewList.add(house);
        mViewList.add(senate);
        mViewList.add(joint);

        List<String> mTitleList = new ArrayList<String>();
        mTitleList.add("HOUSE");
        mTitleList.add("SENATE");
        mTitleList.add("JOINT");

        committeesTabLayout.setTabMode(TabLayout.MODE_FIXED);
        committeesTabLayout.addTab(committeesTabLayout.newTab().setText(mTitleList.get(0)));
        committeesTabLayout.addTab(committeesTabLayout.newTab().setText(mTitleList.get(1)));
        committeesTabLayout.addTab(committeesTabLayout.newTab().setText(mTitleList.get(2)));

        MyPagerAdapter committeesAdapter = new MyPagerAdapter(mViewList, mTitleList);

        committeesViewPager.setAdapter(committeesAdapter);//给ViewPager设置适配器
        committeesTabLayout.setupWithViewPager(committeesViewPager);//将TabLayout和ViewPager关联起来。
        committeesTabLayout.setTabsFromPagerAdapter(committeesAdapter);//给Tabs设置适配器

    }

    // Favorites View Initiation
    private View legislators;
    private View bills;
    private View committees;
    private void initFavoritesView() {

        favoritesViewPager = (ViewPager) favoritesFragment.getView().findViewById(R.id.vp_view_favorites);
        favoritesTabLayout = (TabLayout) favoritesFragment.getView().findViewById(R.id.tabs_favorites);
        favoritesViewPager.setOffscreenPageLimit(3);

        LayoutInflater mInflater = LayoutInflater.from(this);
        legislators = mInflater.inflate(R.layout.legislators, null);
        bills = mInflater.inflate(R.layout.bills, null);
        committees = mInflater.inflate(R.layout.committees, null);

        List<View> mViewList = new ArrayList<View>();
        mViewList.add(legislators);
        mViewList.add(bills);
        mViewList.add(committees);

        List<String> mTitleList = new ArrayList<String>();
        mTitleList.add("LEGISLATORS");
        mTitleList.add("BILLS");
        mTitleList.add("COMMITTEES");

        favoritesTabLayout.setTabMode(TabLayout.MODE_FIXED);
        favoritesTabLayout.addTab(favoritesTabLayout.newTab().setText(mTitleList.get(0)));
        favoritesTabLayout.addTab(favoritesTabLayout.newTab().setText(mTitleList.get(1)));
        favoritesTabLayout.addTab(favoritesTabLayout.newTab().setText(mTitleList.get(2)));

        MyPagerAdapter favoritesAdapter = new MyPagerAdapter(mViewList, mTitleList);

        favoritesViewPager.setAdapter(favoritesAdapter);//给ViewPager设置适配器
        favoritesTabLayout.setupWithViewPager(favoritesViewPager);//将TabLayout和ViewPager关联起来。
        favoritesTabLayout.setTabsFromPagerAdapter(favoritesAdapter);//给Tabs设置适配器

    }

    // State View Initiation
    private void initStatesView() {

        mHandler = new MyHandler();
        CongressThread congress = new CongressThread(LEGISLATORS, mHandler);
        congress.start();

    }

    private void initActiveAndNewBills() {

        CongressThread congress = new CongressThread(BILLS_ACTIVE, mHandler);
        congress.start();
        congress = new CongressThread(BILLS_NEW, mHandler);
        congress.start();

    }

    private void initCommittees() {

        CongressThread congress = new CongressThread(COMMITTEES, mHandler);
        congress.start();

    }

    private void initFavorite() {

        /*lv_favorite_legislators = (ListView) favoritesFragment.getView().findViewById(R.id.listview_favorite_legislators);
        lv_favorite_bills = (ListView) favoritesFragment.getView().findViewById(R.id.listview_favorite_bills);
        lv_favorite_committees = (ListView) favoritesFragment.getView().findViewById(R.id.listview_favorite_committees);*/
        lv_favorite_legislators = (ListView) legislators.findViewById(R.id.listview_favorite_legislators);
        lv_favorite_bills = (ListView) bills.findViewById(R.id.listview_favorite_bills);
        lv_favorite_committees = (ListView) committees.findViewById(R.id.listview_favorite_committees);

        lv_favorite_legislators.setAdapter(favorLegisAdapter);
        lv_favorite_bills.setAdapter(favorBillsAdapter);
        lv_favorite_committees.setAdapter(favorCommiAdapter);

        getIndexList(FAVORITES_LEGISLATOR);
        displayIndex(FAVORITES_LEGISLATOR);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_legislators) {
            setTitle("Legislators");
            setFragment(LEGISLATORS);
        } else if (id == R.id.nav_bills) {
            if(activeBillsData == null || newBillsData == null) initActiveAndNewBills();
            setTitle("Bills");
            setFragment(BILLS_ACTIVE);
        } else if (id == R.id.nav_committees) {
            if (commiHouseData == null || commiSenateData == null || commiJointData == null) initCommittees();
            setTitle("Committees");
            setFragment(COMMITTEES);
        } else if (id == R.id.nav_favorite) {
            setTitle("Favorites");
            setFragment(FAVORITES);
            initFavorite();
        } else if (id == R.id.nav_about_me) {
            Intent i = new Intent(this, AboutMeActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    Map<String, Integer> states_mapIndex;
    Map<String, Integer> house_mapIndex;
    Map<String, Integer> senate_mapIndex;
    Map<String, Integer> favor_mapIndex;


    private static String[] alphabetArray = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
    HashMap<Integer,String> state_alphaIndexMap;
    HashMap<Integer,String> house_alphaIndexMap;
    HashMap<Integer,String> senate_alphaIndexMap;
    HashMap<Integer,String> favor_alphaIndexMap;

    private static final int LEGIS_STATES = 1;
    private static final int LEGIS_HOUSE = 2;
    private static final int LEGIS_SENATE = 3;
    private static final int FAVORITES_LEGISLATOR = 4;

    private void getIndexList(int legisOperation) {

        //mapIndex = new LinkedHashMap<String, Integer>();
        switch (legisOperation) {
            case LEGIS_STATES:
                states_mapIndex = new LinkedHashMap<String, Integer>();
                state_alphaIndexMap = new HashMap<Integer, String>();
                for (int i = 0; i < alphabetArray.length; i++) {
                    String alphaLetter = alphabetArray[i];
                    int j = startWithAlphabetIndex(legisOperation, alphaLetter);
                    if (j != -1) {
                        states_mapIndex.put(alphabetArray[i], j);
                        state_alphaIndexMap.put(j, alphabetArray[i]);
                    }
                }
                break;
            case LEGIS_HOUSE:
                house_mapIndex = new LinkedHashMap<String, Integer>();
                house_alphaIndexMap = new HashMap<Integer, String>();
                for (int i = 0; i < alphabetArray.length; i++) {
                    String alphaLetter = alphabetArray[i];
                    int j = startWithAlphabetIndex(legisOperation, alphaLetter);
                    if (j != -1) {
                        house_mapIndex.put(alphabetArray[i], j);
                        house_alphaIndexMap.put(j, alphabetArray[i]);
                    }
                }
                break;
            case LEGIS_SENATE:
                senate_mapIndex = new LinkedHashMap<String, Integer>();
                senate_alphaIndexMap = new HashMap<Integer, String>();
                for (int i = 0; i < alphabetArray.length; i++) {
                    String alphaLetter = alphabetArray[i];
                    int j = startWithAlphabetIndex(legisOperation, alphaLetter);
                    if (j != -1) {
                        senate_mapIndex.put(alphabetArray[i], j);
                        senate_alphaIndexMap.put(j, alphabetArray[i]);
                    }
                }
                break;
            case FAVORITES_LEGISLATOR:
                favor_mapIndex = new LinkedHashMap<String, Integer>();
                favor_alphaIndexMap = new HashMap<Integer, String>();
                for (int i = 0; i < alphabetArray.length; i++) {
                    String alphaLetter = alphabetArray[i];
                    int j = startWithAlphabetIndex(legisOperation, alphaLetter);
                    if (j != -1) {
                        favor_mapIndex.put(alphabetArray[i], j);
                        favor_alphaIndexMap.put(j, alphabetArray[i]);
                    }
                }
                break;

        }
    }

    private void displayIndex(int legisOperation) {

        switch (legisOperation) {
            case LEGIS_STATES:
                LinearLayout stateIndexLayout = (LinearLayout) findViewById(R.id.side_index);
                TextView stateTextView;
                List<String> stateIndexList = new ArrayList<String>(states_mapIndex.keySet());
                for (String index : stateIndexList) {
                    stateTextView = (TextView) getLayoutInflater().inflate(R.layout.side_index_item, null);
                    stateTextView.setText(index);
                    stateTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView selectedView = (TextView) view;
                            lv_state.setSelection(states_mapIndex.get(selectedView.getText()));
                        }
                    });
                    stateIndexLayout.addView(stateTextView);
                }
                break;
            case LEGIS_HOUSE:
                LinearLayout houseIndexLayout = (LinearLayout) findViewById(R.id.house_side_index);
                TextView houseTextView;
                List<String> houseIndexList = new ArrayList<String>(house_mapIndex.keySet());
                for (String index : houseIndexList) {
                    houseTextView = (TextView) getLayoutInflater().inflate(R.layout.side_index_item, null);
                    houseTextView.setText(index);
                    houseTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView selectedView = (TextView) view;
                            lv_house.setSelection(house_mapIndex.get(selectedView.getText()));
                        }
                    });
                    houseIndexLayout.addView(houseTextView);
                }
                break;
            case LEGIS_SENATE:
                LinearLayout senateIndexLayout = (LinearLayout) findViewById(R.id.senate_side_index);
                TextView senateTextView;
                List<String> senateIndexList = new ArrayList<String>(senate_mapIndex.keySet());
                for (String index : senateIndexList) {
                    senateTextView = (TextView) getLayoutInflater().inflate(R.layout.side_index_item, null);
                    senateTextView.setText(index);
                    senateTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView selectedView = (TextView) view;
                            lv_senate.setSelection(senate_mapIndex.get(selectedView.getText()));
                        }
                    });
                    senateIndexLayout.addView(senateTextView);
                }
                break;
            case FAVORITES_LEGISLATOR:
                LinearLayout favorIndexLayout = (LinearLayout) legislators.findViewById(R.id.favorite_side_index);
                TextView favorTextView;
                List<String> favorIndexList = new ArrayList<String>(favor_mapIndex.keySet());
                for (String index : favorIndexList) {
                    favorTextView = (TextView) getLayoutInflater().inflate(R.layout.side_index_item, null);
                    favorTextView.setText(index);
                    favorTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView selectedView = (TextView) view;
                            lv_favorite_legislators.setSelection(favor_mapIndex.get(selectedView.getText()));
                        }
                    });
                    favorIndexLayout.addView(favorTextView);
                }
                break;
        }

    }

    public int startWithAlphabetIndex(int legisOperation, String alphabet){
        int retValue = -1;

        switch (legisOperation) {
            case LEGIS_STATES:
                try {
                    for (int j = 0; j < legislatorsData.size(); j++) {
                        if (legislatorsData.get(j).getString("state_name").startsWith(alphabet)) {
                            retValue = j;
                            return retValue;
                        }
                    }
                } catch (Exception e){
                    Log.i("startWithAlphabetIndex", "States Fail");
                }
                break;
            case LEGIS_HOUSE:
                try {
                    for (int j = 0; j < legisHouseData.size(); j++) {
                        if (legisHouseData.get(j).getString("last_name").startsWith(alphabet)) {
                            retValue = j;
                            return retValue;
                        }
                    }
                } catch (Exception e){
                    Log.i("startWithAlphabetIndex", "House Fail");
                }
                break;
            case LEGIS_SENATE:
                try {
                    for (int j = 0; j < legisSenateData.size(); j++) {
                        if (legisSenateData.get(j).getString("last_name").startsWith(alphabet)) {
                            retValue = j;
                            return retValue;
                        }
                    }
                } catch (Exception e){
                    Log.i("startWithAlphabetIndex", "Senate Fail");
                }
                break;
            case FAVORITES_LEGISLATOR:
                try {
                    for (int j = 0; j < favoriteLegisData.size(); j++) {
                        if (favoriteLegisData.get(j).getString("last_name").startsWith(alphabet)) {
                            retValue = j;
                            return retValue;
                        }
                    }
                } catch (Exception e){
                    Log.i("startWithAlphabetIndex", "favorite Fail");
                }
                break;
        }

        return retValue;
    }

    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case LEGISLATORS:
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    try {
                        JSONArray res = jsonObject.getJSONArray("results");
                        legislatorsData = new ArrayList<JSONObject>();
                        for(int i=0; i<res.length(); i++) {
                            JSONObject data = res.getJSONObject(i);
                            legislatorsData.add(data);
                        }

                        Collections.sort(legislatorsData, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject legislator1, JSONObject legislator2) {
                                try {
                                    int stateRes = legislator1.getString("state_name").compareTo(legislator2.getString("state_name"));
                                    if (stateRes != 0) {
                                        return stateRes;
                                    }else {
                                        return legislator1.getString("last_name").compareTo(legislator2.getString("last_name"));
                                    }
                                } catch (Exception e) {
                                    Log.e("Legislators Comparison", "Fail");
                                    return 0;
                                }
                            }
                        });

                        lv_state = (ListView) legislatorsFragment.getView().findViewById(R.id.listview_states);
                        LegisListViewAdapter adapter = new LegisListViewAdapter(getBaseContext(), legislatorsData);
                        lv_state.setAdapter(adapter);

                        getIndexList(LEGIS_STATES);
                        displayIndex(LEGIS_STATES);

                        legisHouseData = new ArrayList<JSONObject>();
                        legisSenateData = new ArrayList<JSONObject>();
                        extractHouseAndSenateData();

                        Collections.sort(legisHouseData, new Comparator<JSONObject>() {
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

                        Collections.sort(legisSenateData, new Comparator<JSONObject>() {
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

                        lv_house = (ListView) findViewById(R.id.listview_house);
                        LegisListViewAdapter houseAdapter = new LegisListViewAdapter(getBaseContext(), legisHouseData);
                        lv_house.setAdapter(houseAdapter);

                        getIndexList(LEGIS_HOUSE);
                        displayIndex(LEGIS_HOUSE);

                        lv_senate = (ListView) findViewById(R.id.listview_senate);
                        LegisListViewAdapter senateAdapter = new LegisListViewAdapter(getBaseContext(), legisSenateData);
                        lv_senate.setAdapter(senateAdapter);

                        getIndexList(LEGIS_SENATE);
                        displayIndex(LEGIS_SENATE);

                    }catch (Exception e){
                        e.printStackTrace();
                        Log.e("LEGISLATORSEXCEPTION", "Decodeing Legislators JSON data fails");
                    }

                    break;
                case BILLS_ACTIVE:

                    JSONObject ActiveBillsJSONObject = (JSONObject) msg.obj;

                    try{

                        JSONArray res = ActiveBillsJSONObject.getJSONArray("results");

                        activeBillsData = new ArrayList<JSONObject>();

                        for(int i=0; i<res.length(); i++){
                            JSONObject data = res.getJSONObject(i);
                            activeBillsData.add(data);
                        }

                        Collections.sort(activeBillsData, new Comparator<JSONObject>() {
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

                        lv_active_bills = (ListView) billsFragment.getView().findViewById(R.id.listview_active_bills);
                        BillsListViewAdapter ativeBillsAdapter = new BillsListViewAdapter(activeBillsData, getBaseContext());
                        lv_active_bills.setAdapter(ativeBillsAdapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("BILLSEXCEPTION", "Decodeing Bills JSON data fails");
                    }

                    break;
                case BILLS_NEW:

                    JSONObject newBillsJSONObject = (JSONObject) msg.obj;

                    try{

                        JSONArray res = newBillsJSONObject.getJSONArray("results");
                        newBillsData = new ArrayList<JSONObject>();

                        for(int i=0; i<res.length(); i++){
                            JSONObject data = res.getJSONObject(i);
                            newBillsData.add(data);
                        }

                        Collections.sort(newBillsData, new Comparator<JSONObject>() {
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

                        lv_new_bills = (ListView) billsFragment.getView().findViewById(R.id.listview_new_bills);
                        BillsListViewAdapter newBillsAdapter = new BillsListViewAdapter(newBillsData, getBaseContext());
                        lv_new_bills.setAdapter(newBillsAdapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("BILLSEXCEPTION", "Decodeing Bills JSON data fails");
                    }

                    break;
                case COMMITTEES:

                    JSONObject committeesJSONObject = (JSONObject) msg.obj;

                    try {

                        JSONArray res = committeesJSONObject.getJSONArray("results");
                        commiHouseData = new ArrayList<JSONObject>();
                        commiSenateData = new ArrayList<JSONObject>();
                        commiJointData = new ArrayList<JSONObject>();

                        for(int i=0; i<res.length(); i++){
                            JSONObject data = res.getJSONObject(i);
                            String chamber = data.getString("chamber");
                            if (chamber.equals("house")) commiHouseData.add(data);
                            if (chamber.equals("senate")) commiSenateData.add(data);
                            if (chamber.equals("joint")) commiJointData.add(data);
                        }

                        lv_house_committees = (ListView) committeesFragment.getView().findViewById(R.id.listview_committees_house);
                        lv_senate_committees = (ListView) committeesFragment.getView().findViewById(R.id.listview_committees_senate);
                        lv_joint_committees = (ListView) committeesFragment.getView().findViewById(R.id.listview_committees_joint);

                        Collections.sort(commiHouseData, new Comparator<JSONObject>() {
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

                        Collections.sort(commiSenateData, new Comparator<JSONObject>() {
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

                        Collections.sort(commiJointData, new Comparator<JSONObject>() {
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

                        CommiListViewAdapter houseAdapter = new CommiListViewAdapter(commiHouseData, getBaseContext());
                        CommiListViewAdapter senateAdapter = new CommiListViewAdapter(commiSenateData, getBaseContext());
                        CommiListViewAdapter jointAdapter = new CommiListViewAdapter(commiJointData, getBaseContext());

                        lv_house_committees.setAdapter(houseAdapter);
                        lv_senate_committees.setAdapter(senateAdapter);
                        lv_joint_committees.setAdapter(jointAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case FAVORITES:
                    break;
            }

        }
    }

    private void extractHouseAndSenateData() {

        for(int i=0; i<legislatorsData.size(); i++){
            try{
                JSONObject obj = legislatorsData.get(i);
                if(obj.getString("chamber").equals("house")){
                    legisHouseData.add(obj);
                }else{
                    legisSenateData.add(obj);
                }
            }catch (Exception e) {
                e.printStackTrace();
                Log.e("ExtractHouseData", "Fail");
            }
        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        SharedPreferences preferences = getSharedPreferences("user",this.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int favorLegisSize = favoriteLegisData.size();
        editor.putInt("favorLegisSize", favorLegisSize);
        for(int i=0; i<favorLegisSize; i++){
            editor.putString("Legis:" + i, favoriteLegisData.get(i).toString());
        }
        int favorBillsSize = favoriteBillsData.size();
        editor.putInt("favorBillsSize", favorBillsSize);
        for(int i=0; i<favorBillsSize; i++){
            editor.putString("Bills:" + i, favoriteBillsData.get(i).toString());
        }
        int favorCommiSize = favoriteCommiData.size();
        editor.putInt("favorCommiSize", favorCommiSize);
        for(int i=0; i<favorCommiSize; i++){
            editor.putString("Commi:" + i, favoriteCommiData.get(i).toString());
        }
        editor.commit();

    }
}
