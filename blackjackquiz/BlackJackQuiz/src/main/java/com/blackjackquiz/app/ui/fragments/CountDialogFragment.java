package com.blackjackquiz.app.ui.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackjackquiz.app.R;

/**
 * Created by elena on 6/28/15.
 */

public class CountDialogFragment extends KeyEventFragment { // implements CountingQuizFragment.CountReceivedCallback {

    public CountDialogFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_count_dialog, container, false);

        m_actualCount = (TextView) rootView.findViewById(R.id.actual_count);
        m_result = (TextView) rootView.findViewById(R.id.count_result);
        m_user_count = (EditText) rootView.findViewById(R.id.user_count);

        m_show_count = (Button) rootView.findViewById(R.id.show_count);
        m_cancel = (Button) rootView.findViewById(R.id.cancel);
        setupShowCountButton(m_show_count);
        setupCancelButton(m_cancel);
        setupUserCountEditText(m_user_count);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        m_user_count.setText("");
        m_actualCount.setVisibility(View.INVISIBLE);
        m_result.setVisibility(View.INVISIBLE);
    }

//    @Override
//    public void onCountReceived(int count) {
//        m_count = count;
//    }

    private void setupShowCountButton(Button showCountButton) {
        showCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_count = CountingQuizFragment.s_count;
                int userCount = Integer.valueOf(m_user_count.getText().toString());
                m_actualCount.setText("Actual count is: " + String.valueOf(m_count));
                if (userCount == m_count) {
                    m_result.setText("You are damn good");
                } else {
                    if (userCount < m_count + ALMOST_GOT_IT_CONST && userCount > m_count - ALMOST_GOT_IT_CONST) {
                        m_result.setText("You almost got it, bro");
                    } else {
                        m_result.setText("You are in trouble");
                    }
                }

                m_actualCount.setVisibility(View.VISIBLE);
                m_result.setVisibility(View.VISIBLE);
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

    private void setupUserCountEditText(final EditText editText) {

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    //Clear focus here from edittext
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
//        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//    /* When focus is lost check that the text field
//    * has valid values.
//    */
//                if (!hasFocus) {
//                    //editText.setFocusable(false);
//                    Log.d("ElenaT", "focus check ");
//                }
//            }
//        });
//
//        editText.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                    Log.d("ElenaT", "press ");
//                    //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    //imm.hideSoftInputFromWindow(URLText.getWindowToken(), 0);
//                    //editText.setFocusable(false);
//                    //editText.setFocusableInTouchMode(true);
//                    //editText.clearFocus();
////                    editText.setFocusableInTouchMode(false);
////                    editText.setFocusable(false);
////                    editText.setFocusableInTouchMode(true);
////                    editText.setFocusable(true);
//
//
//                }
//                return false;
//            }
//        });
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
