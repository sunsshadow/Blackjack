package com.blackjackquiz.app.ui.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.blackjackquiz.app.R;
import com.blackjackquiz.app.deck.CardImageLoader;
import com.blackjackquiz.app.deck.Field;
import com.blackjackquiz.app.solution.SolutionManual;

import java.util.HashMap;
import java.util.Map;


public class CountingQuizFragment extends KeyEventFragment {
    protected static final String TAG = CountingQuizFragment.class.getSimpleName();
    private static final int CORRECT_ANSWER_COLOR = Color.GREEN;
    private static final int WRONG_ANSWER_COLOR = Color.RED;
    private static final int UNUSED_ANSWER_COLOR = Color.GRAY;

    public CountingQuizFragment() {
        m_actionToButtons = new HashMap<>();
        m_actionButtonClickListener = new ActionButtonOnClickListener();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_counting_quiz, container, false);
        findCardImages(rootView);
        findActionButtons(rootView);
        addButtonsToActionMap();
        setupActionButtonClickListeners();

        Button nextFieldButton = (Button) rootView.findViewById(R.id.next_field_button);
        Button getCountButton = (Button) rootView.findViewById(R.id.count_button);
        setupNextFieldButton(nextFieldButton);
        setupGetCountButton(getCountButton);
        s_count = 0;
        newField();

        return rootView;
    }

    public void newField() {
        m_field = Field.newUnbiasedField();
        resetCardImages();
        resetButtonColors();
        s_count += m_field.count;
        Log.d(TAG, "count " + s_count);
        Log.d(TAG, "m_field.count " + m_field.count);
    }

    private void setupActionButtonClickListeners() {
        for (ActionButton actionButton : m_actionToButtons.values()) {
            actionButton.button.setOnClickListener(m_actionButtonClickListener);
        }
    }

    private void findCardImages(View rootView) {
        m_dealerCardImage = (ImageView) rootView.findViewById(R.id.dealer_card_img);
        m_playerCardOneImage = (ImageView) rootView.findViewById(R.id.player_card_one_img);
        m_playerCardTwoImage = (ImageView) rootView.findViewById(R.id.player_card_two_img);
    }

    private void findActionButtons(View rootView) {
        m_hitButton = (Button) rootView.findViewById(R.id.hit_button);
        m_dblButton = (Button) rootView.findViewById(R.id.dbl_button);
        m_stdButton = (Button) rootView.findViewById(R.id.std_button);
        m_splButton = (Button) rootView.findViewById(R.id.spl_button);
        m_dasButton = (Button) rootView.findViewById(R.id.das_button);
    }

    private void addButtonsToActionMap() {
        m_actionToButtons.put(SolutionManual.BlackJackAction.Hit, new ActionButton(m_hitButton, Color.RED));
        m_actionToButtons.put(SolutionManual.BlackJackAction.Double, new ActionButton(m_dblButton, Color.LTGRAY));
        m_actionToButtons.put(SolutionManual.BlackJackAction.Stand, new ActionButton(m_stdButton, Color.YELLOW));
        m_actionToButtons.put(SolutionManual.BlackJackAction.Split, new ActionButton(m_splButton, Color.GREEN));
        m_actionToButtons.put(SolutionManual.BlackJackAction.DoubleAfterSplit, new ActionButton(m_dasButton, Color.CYAN));
    }

    private void setupNextFieldButton(Button nextCardButton) {
        nextCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newField();
            }
        });
    }

    private void setupGetCountButton(Button getCountButton) {
        getCountButton.setOnClickListener(new View.OnClickListener() {
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
        Fragment completeGameFragment = getFragmentManager().findFragmentByTag(getResources().getString(R.string.tag_completeGameFragment));
        countDialogFragment.onStart();
        getFragmentManager().beginTransaction()
                .hide(countingQuizFragment)
                .hide(solutionTableFragment)
                .hide(blackJackQuizFragment)
                .show(countDialogFragment)
                .hide(completeGameFragment)
                .commit();
    }

    private void resetCardImages() {
        CardImageLoader cardImageLoader = CardImageLoader.getInstance(getActivity());
        m_dealerCardImage.setImageBitmap(cardImageLoader.getBitmapForCard(m_field.dealerCard));
        m_playerCardOneImage.setImageBitmap(cardImageLoader.getBitmapForCard(m_field.playerCardOne));
        m_playerCardTwoImage.setImageBitmap(cardImageLoader.getBitmapForCard(m_field.playerCardTwo));
    }

    private void resetButtonColors() {
        for (ActionButton actionButton : m_actionToButtons.values()) {
            actionButton.button.setBackgroundColor(actionButton.defaultColor);
        }
    }

    private class ActionButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            SolutionManual solMan = SolutionManual.getInstance(getActivity());
            SolutionManual.BlackJackAction action = solMan.getSolutionForCards(m_field.dealerCard,
                    m_field.playerCardOne,
                    m_field.playerCardTwo);
            ActionButton solutionButton = m_actionToButtons.get(action);

            // make all the other buttons grey
            for (ActionButton actionButton : m_actionToButtons.values()) {
                if (actionButton.button != solutionButton.button && actionButton.button != v) {
                    actionButton.button.setBackgroundColor(UNUSED_ANSWER_COLOR);
                }
            }

            if (solutionButton.button != v) {
                v.setBackgroundColor(WRONG_ANSWER_COLOR);
            }

            solutionButton.button.setBackgroundColor(CORRECT_ANSWER_COLOR);
        }
    }

    protected static class ActionButton {
        private ActionButton(Button button, int defaultColor) {
            this.button = button;
            this.defaultColor = defaultColor;
        }

        protected final Button button;
        protected final int defaultColor;
    }

    private ImageView m_dealerCardImage;
    private ImageView m_playerCardOneImage;
    private ImageView m_playerCardTwoImage;

    private Button m_hitButton;
    private Button m_dblButton;
    private Button m_stdButton;
    private Button m_splButton;
    private Button m_dasButton;

    private Field m_field;
    protected static int s_count;

    private final View.OnClickListener m_actionButtonClickListener;
    protected final Map<SolutionManual.BlackJackAction, ActionButton> m_actionToButtons;
}
