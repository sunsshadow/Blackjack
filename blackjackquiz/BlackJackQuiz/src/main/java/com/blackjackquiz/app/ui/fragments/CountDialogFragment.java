package com.blackjackquiz.app.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackjackquiz.app.R;

/**
 * Created by elena on 6/28/15.
 */

public class CountDialogFragment extends KeyEventFragment implements CountingQuizFragment.CountReceivedCallback {

    public CountDialogFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_count_dialog, container, false);

        m_actualCount = (TextView) rootView.findViewById(R.id.actual_count);
        Log.d("ElenaT", "m_actualCount " + m_actualCount);
        m_result = (TextView) rootView.findViewById(R.id.count_result);
        m_user_count = (EditText) rootView.findViewById(R.id.user_count);

        LinearLayout lin = (LinearLayout) rootView.findViewById(R.id.button_layout);
        m_show_count = (Button) rootView.findViewById(R.id.count_button);
        Log.d("ElenaT", "showCount " + m_show_count);
        Log.d("ElenaT", "lin " + lin);
        m_cancel = (Button) rootView.findViewById(R.id.cancel);
        setupShowCountButton(m_show_count);
        setupCancelButton(m_cancel);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCountReceived(int count) {
        m_count = count;
    }

    private void setupShowCountButton(Button showCountButton) {
        showCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int userCount = Integer.valueOf(m_user_count.getText().toString());
                m_actualCount.setText(m_count);
                if (userCount == m_count) {
                    m_result.setText("You are damn good");
                } else {
                    if (userCount < m_count + ALMOST_GOT_IT_CONST && userCount > m_count - ALMOST_GOT_IT_CONST) {
                        m_result.setText("You almost got it, bro");
                    } else {
                        m_result.setText("You are in trouble");
                    }
                }
            }
        });
    }

    private void setupCancelButton(Button cancelButton) {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCountFragment();
            }
        });
    }

    private void openCountFragment() {
        Fragment countDialogFragment = getFragmentManager().findFragmentByTag(getResources().getString(R.string.tag_countDialogFragment));
        Fragment countingQuizFragment = getFragmentManager().findFragmentByTag(getResources().getString(R.string.tag_countingQuizFragment));
        Fragment solutionTableFragment = getFragmentManager().findFragmentByTag(getResources().getString(R.string.tag_solutionTableFragment));
        Fragment blackJackQuizFragment = getFragmentManager().findFragmentByTag(getResources().getString(R.string.tag_blackJackQuizFragment));
        getFragmentManager().beginTransaction()
                .show(countingQuizFragment)
                .hide(solutionTableFragment)
                .hide(blackJackQuizFragment)
                .hide(countDialogFragment)
                .commit();
    }

    private int m_count;
    private TextView m_actualCount;
    private TextView m_result;
    private EditText m_user_count;
    private Button m_show_count;
    private Button m_cancel;
    private static final int ALMOST_GOT_IT_CONST = 3;
}
