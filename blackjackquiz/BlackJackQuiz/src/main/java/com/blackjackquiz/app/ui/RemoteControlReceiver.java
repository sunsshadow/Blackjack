package com.blackjackquiz.app.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class RemoteControlReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction()))
        {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event != null)
            {
                processKeyEvent(event);
            }
        }
    }

    private void processKeyEvent(KeyEvent event)
    {
        BlackJackQuizActivity activity = BlackJackQuizActivity.getActivity();
        if (activity != null)
        {
            activity.handleMediaKeyEvent(event);
        }
    }
}
