package edu.usc.congress;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by hehuiliang on 11/25/16.
 */

public class FavoritesFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View favoritesLayout = inflater.inflate(R.layout.content_favorites, container, false);
        return favoritesLayout;
    }
}
