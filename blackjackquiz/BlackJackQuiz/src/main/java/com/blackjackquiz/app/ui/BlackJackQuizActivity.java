package com.blackjackquiz.app.ui;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.blackjackquiz.app.R;
import com.blackjackquiz.app.deck.CardImageLoader;
import com.blackjackquiz.app.solution.SolutionManual;
import com.blackjackquiz.app.ui.fragments.BlackJackQuizFragment;
import com.blackjackquiz.app.ui.fragments.CountDialogFragment;
import com.blackjackquiz.app.ui.fragments.CountingQuizFragment;
import com.blackjackquiz.app.ui.fragments.SolutionTableFragment;
import com.blackjackquiz.app.ui.fragments.TvModeBlackJackQuizFragment;

public class BlackJackQuizActivity extends Activity {
    private static volatile BlackJackQuizActivity s_blackJackActivity;

    public static BlackJackQuizActivity getActivity() {
        return s_blackJackActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_jack_quiz);

        UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
        m_inTvMode = uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;

        if (m_inTvMode) {
            m_blackJackQuizFragment = new TvModeBlackJackQuizFragment();
            m_mediaButtonReceiver = new ComponentName(getPackageName(), RemoteControlReceiver.class.getName());
            // @TODO (elena) CountingQuiz for FireTV mode
        } else {
            m_blackJackQuizFragment = new BlackJackQuizFragment();
            m_countingQuizFragment = new CountingQuizFragment();
            m_countDialogFragment = new CountDialogFragment();
        }

        m_solutionTableFragment = new SolutionTableFragment();
        s_blackJackActivity = this;

        getFragmentManager().beginTransaction()
                .add(R.id.container, m_blackJackQuizFragment, getResources().getString(R.string.tag_blackJackQuizFragment))
                .add(R.id.container, m_solutionTableFragment, getResources().getString(R.string.tag_solutionTableFragment))
                .add(R.id.container, m_countingQuizFragment, getResources().getString(R.string.tag_countingQuizFragment))
                .add(R.id.container, m_countDialogFragment, getResources().getString(R.string.tag_countDialogFragment))
                .hide(m_solutionTableFragment)
                .hide(m_blackJackQuizFragment)
                .hide(m_countDialogFragment)
                .show(m_countingQuizFragment)
                .commit();

        // this is so the initialization code is kicked off
        SolutionManual.getInstance(this);
        CardImageLoader.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (m_inTvMode) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.registerMediaButtonEventReceiver(m_mediaButtonReceiver);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (m_inTvMode) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.unregisterMediaButtonEventReceiver(m_mediaButtonReceiver);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_black_jack_quiz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.black_jack_quiz:
                getFragmentManager().beginTransaction()
                        .hide(m_solutionTableFragment)
                        .show(m_blackJackQuizFragment)
                        .hide(m_countingQuizFragment)
                        .hide(m_countDialogFragment)
                        .commit();
                return true;
            case R.id.solution_table:
                getFragmentManager().beginTransaction()
                        .hide(m_blackJackQuizFragment)
                        .show(m_solutionTableFragment)
                        .hide(m_countingQuizFragment)
                        .hide(m_countDialogFragment)
                        .commit();
                return true;
            case R.id.counting_quiz:
                getFragmentManager().beginTransaction()
                        .hide(m_blackJackQuizFragment)
                        .hide(m_solutionTableFragment)
                        .hide(m_countDialogFragment)
                        .show(m_countingQuizFragment)
                        .commit();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void handleMediaKeyEvent(KeyEvent keyEvent) {
        m_blackJackQuizFragment.handleKeyEvent(keyEvent);
        m_solutionTableFragment.handleKeyEvent(keyEvent);
        m_countingQuizFragment.handleKeyEvent(keyEvent);
        m_countDialogFragment.handleKeyEvent(keyEvent);
    }

    private BlackJackQuizFragment m_blackJackQuizFragment;
    private SolutionTableFragment m_solutionTableFragment;
    private CountingQuizFragment m_countingQuizFragment;
    private CountDialogFragment m_countDialogFragment;
    private boolean m_inTvMode;
    private ComponentName m_mediaButtonReceiver;
}
