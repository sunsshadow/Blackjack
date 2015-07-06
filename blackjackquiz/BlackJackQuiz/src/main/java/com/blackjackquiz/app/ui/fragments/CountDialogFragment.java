package com.blackjackquiz.app.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blackjackquiz.app.R;

/**
 * Created by elena on 6/28/15.
 */

public class CountDialogFragment extends KeyEventFragment {

    public CountDialogFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_count_dialog, container, false);

        m_actualCount = (TextView) rootView.findViewById(R.id.actual_count_text);
        m_result_text = (TextView) rootView.findViewById(R.id.count_result_text);
        m_result_text_str = (TextView) rootView.findViewById(R.id.count_result_info_text);
        m_user_count_edit = (EditText) rootView.findViewById(R.id.user_count_edit);
        m_show_count_button = (Button) rootView.findViewById(R.id.show_count_button);
        Button cancel = (Button) rootView.findViewById(R.id.cancel_button);
        Button restartButton = (Button) rootView.findViewById(R.id.restart_button);
        setupShowCountButton(m_show_count_button);
        setupCancelButton(cancel);
        setupUserCountEditText(m_user_count_edit);
        setupRestartButton(restartButton);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        m_user_count_edit.setText("");
        m_actualCount.setVisibility(View.INVISIBLE);
        m_result_text.setVisibility(View.INVISIBLE);
        m_show_count_button.setEnabled(true);
        m_user_count_edit.setEnabled(true);
        m_result_text_str.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            m_callback = (CountRestartCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement CountRestartCallback");
        }
    }

    private void setupShowCountButton(final Button showCountButton) {
        showCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userCountStr = m_user_count_edit.getText().toString();
                if (userCountStr == null || userCountStr.equals("") || userCountStr.equals("-")) {
                    TryingToCheatDialogFragment tryingToCheatDialogFragment = new TryingToCheatDialogFragment();
                    tryingToCheatDialogFragment.show(getFragmentManager(), getResources().getString(R.string.tag_countDialogFragment));
                } else {
                    setCountResult();
                    m_actualCount.setVisibility(View.VISIBLE);
                    m_result_text.setVisibility(View.VISIBLE);
                    m_result_text_str.setVisibility(View.VISIBLE);
                    showCountButton.setEnabled(false);
                    m_user_count_edit.setEnabled(false);
                }
            }
        });
    }

    private void setCountResult() {
        m_count = CountingQuizFragment.s_count;
        int userCount = Integer.valueOf(m_user_count_edit.getText().toString());
        m_actualCount.setText("Actual count is: " + String.valueOf(m_count));

        if (userCount == m_count) {
            m_result_text.setText("Winner winner chicken dinner");
            m_result_text.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        } else {
            if (userCount < m_count + ALMOST_GOT_IT_CONST && userCount > m_count - ALMOST_GOT_IT_CONST) {
                m_result_text.setText("You almost got it, bro");
                m_result_text.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            } else {
                m_result_text.setText("You are in trouble");
                m_result_text.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            }
        }
    }

    private void setupCancelButton(Button cancelButton) {
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCountFragment();
            }
        });
    }

    private void setupRestartButton(Button restartButton) {
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountingQuizFragment.s_count = 0;
                m_callback.restartCount();
                openCountFragment();
            }
        });
    }


    private void setupUserCountEditText(final EditText editText) {

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    editText.setCursorVisible(false);
                }
                return false;
            }
        });

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                editText.setCursorVisible(true);
                return false;
            }
        });
    }

    public static class TryingToCheatDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getResources().getString(R.string.message_for_cheaters))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            return builder.create();
        }
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

    public interface CountRestartCallback {
        public void restartCount();
    }

    private int m_count;
    private CountRestartCallback m_callback; // to let counting quiz know to refresh

    private TextView m_actualCount;
    private TextView m_result_text;
    private TextView m_result_text_str;
    private EditText m_user_count_edit;
    private Button m_show_count_button;

    private static final int ALMOST_GOT_IT_CONST = 3;
}
