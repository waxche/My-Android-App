package edu.usc.congress;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hehuiliang on 11/24/16.
 */

public class LegislatorsFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View legislatorsLayout = inflater.inflate(R.layout.content_legislators, container, false);
        return legislatorsLayout;
    }
}
