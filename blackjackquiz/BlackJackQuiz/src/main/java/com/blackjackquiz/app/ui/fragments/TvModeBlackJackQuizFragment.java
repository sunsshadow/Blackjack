package com.blackjackquiz.app.ui.fragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TvModeBlackJackQuizFragment extends BlackJackQuizFragment
{
    private static final int FULLY_OPAQUE = 255;
    private static final int SEMI_OPAQUE  = 100;

    public TvModeBlackJackQuizFragment()
    {
        m_actionButtonFocusChangeListener = new ActionButtonFocusChangeListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setupActionButtonFocusListeners();
        return view;
    }

    @Override
    public void handleKeyEvent(KeyEvent event)
    {
        super.handleKeyEvent(event);
        if (event.getKeyCode() == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            newField();
        }
    }

    @Override
    protected void newField()
    {
        super.newField();
        resetAllButtonOpacities();
    }

    private void setupActionButtonFocusListeners()
    {
        for (ActionButton actionButton : m_actionToButtons.values())
        {
            actionButton.button.setOnFocusChangeListener(m_actionButtonFocusChangeListener);
        }
    }

    private void resetAllButtonOpacities()
    {
        for (ActionButton actionButton : m_actionToButtons.values())
        {
            setOpacityForButton(actionButton.button);
        }
    }

    private void setOpacityForButton(Button button)
    {
        if (button.isFocused())
        {
            button.getBackground().setAlpha(FULLY_OPAQUE);
        }
        else
        {
            button.getBackground().setAlpha(SEMI_OPAQUE);
        }
    }

    private class ActionButtonFocusChangeListener implements View.OnFocusChangeListener
    {
        @Override
        public void onFocusChange(View v, boolean hasFocus)
        {
            setOpacityForButton((Button) v);
        }
    }

    private final View.OnFocusChangeListener m_actionButtonFocusChangeListener;
}
