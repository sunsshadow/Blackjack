package com.blackjackquiz.app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackjackquiz.app.R;

/**
 * Created by elena on 6/28/15.
 */

public class CountDialogFragment extends KeyEventFragment  {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_count_dialog, container, false);



        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
