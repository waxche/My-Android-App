package edu.usc.congress;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hehuiliang on 11/25/16.
 */

public class CommitteesFragment extends Fragment{

    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View committeesLayout = inflater.inflate(R.layout.content_committees, container, false);
        mListView = (ListView) committeesLayout.findViewById(R.id.listview_committees_house);
        return committeesLayout;
    }

    public ListView getmListView() {
        return mListView;
    }
}
