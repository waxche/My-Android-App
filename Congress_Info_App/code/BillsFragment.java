package edu.usc.congress;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hehuiliang on 11/24/16.
 */

public class BillsFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View billsLayout = inflater.inflate(R.layout.content_bills, container, false);
        return billsLayout;
    }
}
