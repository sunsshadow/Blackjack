package com.blackjackquiz.app.ui.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.blackjackquiz.app.R;
import com.blackjackquiz.app.deck.CardImageLoader;
import com.blackjackquiz.app.deck.Deck;
import com.blackjackquiz.app.deck.ResponseField;
import com.blackjackquiz.app.solution.SolutionManual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by elena on 7/14/15.
 */
public class ResponseGameFragment extends KeyEventFragment {
    protected static final String TAG = ResponseGameFragment.class.getSimpleName();
    private static final int CORRECT_ANSWER_COLOR = Color.GREEN;
    private static final int WRONG_ANSWER_COLOR = Color.RED;
    private static final int UNUSED_ANSWER_COLOR = Color.GRAY;
    private static final int DEALER_MAX = 17; // hard 17

    public ResponseGameFragment() {
        m_actionToButtons = new HashMap<>();
        m_actionButtonClickListener = new ActionButtonOnClickListener();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_response_game, container, false);
        findActionButtons(rootView);
        addButtonsToActionMap();
        setupActionButtonClickListeners();

        m_nextFieldButton = (Button) rootView.findViewById(R.id.next_field_button);
        Button getCountButton = (Button) rootView.findViewById(R.id.count_button);
        m_players_layout = (LinearLayout) rootView.findViewById(R.id.players_layout);
        m_main_player_layout = (LinearLayout) rootView.findViewById(R.id.player_layout);
        m_dealer_layout = (LinearLayout) rootView.findViewById(R.id.dealer_layout);
        setupNextFieldButton(m_nextFieldButton);
        setupGetCountButton(getCountButton);
        s_count = 0;
        newField();

        return rootView;
    }

    public void newField() {
        removePlayersImageViews();
        m_field = ResponseField.newUnbiasedField();
        m_players = new ImageView[m_field.players.size()];
        resetCardImagesInit();
        resetButtonColors();
        s_count += m_field.count;
        Log.d("ElenaT", "m_players_layout.heigh " + m_players_layout.getHeight());
        Log.d(TAG, "Count " + s_count);
        Log.d(TAG, "Current field count " + m_field.count);
    }

    private void removePlayersImageViews() {
        if (m_players != null) {
            m_players_layout.removeAllViews();
        }
        if (m_main_player_layout != null) {
            m_main_player_layout.removeAllViews();
        }

        if (m_dealer_layout != null) {
            m_dealer_layout.removeAllViews();
        }

    }

    private void setupActionButtonClickListeners() {
        for (ActionButton actionButton : m_actionToButtons.values()) {
            actionButton.button.setOnClickListener(m_actionButtonClickListener);
        }
    }

    private void findActionButtons(View rootView) {
        m_hitButton = (Button) rootView.findViewById(R.id.hit_button);
        m_dblButton = (Button) rootView.findViewById(R.id.dbl_button);
        m_stdButton = (Button) rootView.findViewById(R.id.std_button);
        m_splButton = (Button) rootView.findViewById(R.id.spl_button);
        m_dasButton = (Button) rootView.findViewById(R.id.das_button);
    }

    private void addButtonsToActionMap() {
        int defaultColor = getResources().getColor(android.R.color.holo_blue_dark);
        m_actionToButtons.put(SolutionManual.BlackJackAction.Hit, new ActionButton(m_hitButton, defaultColor));
        m_actionToButtons.put(SolutionManual.BlackJackAction.Double, new ActionButton(m_dblButton, defaultColor));
        m_actionToButtons.put(SolutionManual.BlackJackAction.Stand, new ActionButton(m_stdButton, defaultColor));
        m_actionToButtons.put(SolutionManual.BlackJackAction.Split, new ActionButton(m_splButton, defaultColor));
        m_actionToButtons.put(SolutionManual.BlackJackAction.DoubleAfterSplit, new ActionButton(m_dasButton, defaultColor));
    }

    private void setupNextFieldButton(Button nextCardButton) {
        nextCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newField();
                m_nextFieldButton.setEnabled(false);
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
        Fragment responseGameFragment = getFragmentManager().findFragmentByTag(getResources().getString(R.string.tag_responseGameFragment));
        countDialogFragment.onStart();
        getFragmentManager().beginTransaction()
                .hide(countingQuizFragment)
                .hide(solutionTableFragment)
                .hide(blackJackQuizFragment)
                .hide(completeGameFragment)
                .show(countDialogFragment)
                .hide(responseGameFragment)
                .commit();
    }

    private void resetCardImagesInit() {
        CardImageLoader cardImageLoader = CardImageLoader.getInstance(getActivity());

        // dealer card init
        ImageView imageViewDealer = new ImageView(this.getActivity());
        Bitmap bitmapDealer = cardImageLoader.getBitmapForCard(m_field.dealerCard.get(0));
        //Log.d(TAG, "left " + m_field.playerCardOne.get(0).rank.getValue());
        imageViewDealer.setImageBitmap(bitmapDealer);
        RelativeLayout.LayoutParams paramsImageDealer = (new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        //paramsImagePlayerOne.topMargin = part * 3 * (i + 1) * 2;
        imageViewDealer.setLayoutParams(paramsImageDealer);
        paramsImageDealer.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imageViewDealer.setAdjustViewBounds(true);


        m_dealer_layout.addView(imageViewDealer);


        ImageView imageViewPlayerOne = new ImageView(this.getActivity());
        Bitmap bitmapPlayerOne = cardImageLoader.getBitmapForCard(m_field.playerCardOne.get(0));
        Log.d(TAG, "left " + m_field.playerCardOne.get(0).rank.getValue());
        imageViewPlayerOne.setImageBitmap(bitmapPlayerOne);
        RelativeLayout.LayoutParams paramsImagePlayerOne = (new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        //paramsImagePlayerOne.topMargin = part * 3 * (i + 1) * 2;
        imageViewPlayerOne.setLayoutParams(paramsImagePlayerOne);
        paramsImagePlayerOne.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        imageViewPlayerOne.setAdjustViewBounds(true);


        m_main_player_layout.addView(imageViewPlayerOne);

        ImageView imageViewPlayerTwo = new ImageView(this.getActivity());
        Bitmap bitmapPlayerTwo = cardImageLoader.getBitmapForCard(m_field.playerCardTwo.get(0));
        Log.d(TAG, "right " + m_field.playerCardTwo.get(0).rank.getValue());
        imageViewPlayerTwo.setImageBitmap(bitmapPlayerTwo);
        RelativeLayout.LayoutParams paramsImagePlayerTwo = (new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        imageViewPlayerTwo.setLayoutParams(paramsImagePlayerTwo);
        paramsImagePlayerTwo.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imageViewPlayerTwo.setAdjustViewBounds(true);
        m_main_player_layout.addView(imageViewPlayerTwo);

        for (int i = 0; i < m_field.players.size(); ++i) {
            RelativeLayout relativeLayout = new RelativeLayout(this.getActivity());
            RelativeLayout.LayoutParams params = (new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            relativeLayout.setLayoutParams(params);


            ImageView imageView = new ImageView(this.getActivity());
            Bitmap bitmap = cardImageLoader.getBitmapForCard(m_field.players.get(i).get(0));
            imageView.setImageBitmap(bitmap);
            RelativeLayout.LayoutParams params0 = (new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            imageView.setLayoutParams(params0);
            imageView.setAdjustViewBounds(true);

            m_players_layout.addView(imageView);
        }

    }

//    private void generateDeck(int width, int height, CardImageLoader cardImageLoader, int numberOfCards,
//                              List<Deck.Card> cards, LinearLayout destination, int numberOfDecks) {
//        RelativeLayout relativeLayoutPlayerOne = new RelativeLayout(this.getActivity());
//        RelativeLayout.LayoutParams paramsPlayerOne = (new RelativeLayout.LayoutParams(
//                (int) width / numberOfDecks,
//                height));
//        relativeLayoutPlayerOne.setLayoutParams(paramsPlayerOne);
//
//
//        int partHeight = (int) (height / (10 + 3 * numberOfCards - 1));
//        //int partWidth = (int) (width / (10 + 3 * (numberOfCards - 1)));
//
//        for (int i = 0; i < cards.size(); ++i) {
//            ImageView imageViewPlayerOne = new ImageView(this.getActivity());
//            Bitmap bitmapPlayerOne = cardImageLoader.getBitmapForCard(cards.get(i));
//            Log.d(TAG, "left " + i + " : " + cards.get(i).rank.getValue());
//            imageViewPlayerOne.setImageBitmap(bitmapPlayerOne);
//            RelativeLayout.LayoutParams paramsImagePlayerOne = (new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.MATCH_PARENT, partHeight * 10));
//            paramsImagePlayerOne.topMargin = partHeight * 3 * i;
//            imageViewPlayerOne.setLayoutParams(paramsImagePlayerOne);
//            paramsImagePlayerOne.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//            imageViewPlayerOne.setAdjustViewBounds(true);
//            relativeLayoutPlayerOne.addView(imageViewPlayerOne);
//        }
//        destination.addView(relativeLayoutPlayerOne);
//    }

    private void generateDeck(int width, int height, CardImageLoader cardImageLoader, int numberOfCards,
                              List<Deck.Card> cards, LinearLayout destination, int numberOfDecks) {
        RelativeLayout relativeLayoutPlayerOne = new RelativeLayout(this.getActivity());
        RelativeLayout.LayoutParams paramsPlayerOne = (new RelativeLayout.LayoutParams(
                (int) width / numberOfDecks,
                height));
        relativeLayoutPlayerOne.setLayoutParams(paramsPlayerOne);


        int partHeight = (int) (height / (10 + 3 * numberOfCards - 1));
        //int partWidth = (int) (width / (10 + 3 * (numberOfCards - 1)));

        for (int i = 0; i < cards.size(); ++i) {
            ImageView imageViewPlayerOne = new ImageView(this.getActivity());
            Bitmap bitmapPlayerOne = cardImageLoader.getBitmapForCard(cards.get(i));
            Log.d(TAG, "left " + i + " : " + cards.get(i).rank.getValue());
            imageViewPlayerOne.setImageBitmap(bitmapPlayerOne);
            RelativeLayout.LayoutParams paramsImagePlayerOne = (new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, partHeight * 10));
            paramsImagePlayerOne.topMargin = partHeight * 3 * i;
            imageViewPlayerOne.setLayoutParams(paramsImagePlayerOne);
            paramsImagePlayerOne.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            imageViewPlayerOne.setAdjustViewBounds(true);
            relativeLayoutPlayerOne.addView(imageViewPlayerOne);
        }
        destination.addView(relativeLayoutPlayerOne);
    }

    private void resetCardImagesResponse() {
        CardImageLoader cardImageLoader = CardImageLoader.getInstance(getActivity());
        int numberOfCards = Math.max(m_field.playerCardOne.size(), m_field.playerCardTwo.size());
        int heightDealer = m_dealer_layout.getHeight();
        int widthDealer = m_dealer_layout.getWidth();
        int heightMainPlayer = m_main_player_layout.getHeight();
        int widthMainPlayer = m_main_player_layout.getWidth();
        int heightPlayers = m_players_layout.getHeight();
        int widthPlayers = m_players_layout.getWidth();
        removePlayersImageViews();
        generateDeck(widthDealer, heightDealer, cardImageLoader, m_field.dealerCard.size(), m_field.dealerCard, m_dealer_layout, 1);
        generateDeck(widthMainPlayer, heightMainPlayer, cardImageLoader, numberOfCards, m_field.playerCardOne, m_main_player_layout, 2);
        generateDeck(widthMainPlayer, heightMainPlayer, cardImageLoader, numberOfCards, m_field.playerCardTwo, m_main_player_layout, 2);
        int playersNumberOfCards = getMaxNumberOfCards(m_field.players);
        for (int i = 0; i < m_field.players.size(); ++i) {
            generateDeck(widthPlayers, heightPlayers, cardImageLoader, playersNumberOfCards, m_field.players.get(i), m_players_layout, m_field.players.size());
        }
    }

    private int getMaxNumberOfCards(List<List<Deck.Card>> cards) {
        int max = 0;
        for (int i = 0; i < cards.size(); ++i) {
            if (cards.get(i).size() > max) {
                max = cards.get(i).size();
            }
        }

        return max;
    }

    private void resetButtonColors() {
        for (ActionButton actionButton : m_actionToButtons.values()) {
            actionButton.button.setBackgroundColor(actionButton.defaultColor);
            actionButton.button.setEnabled(true);
        }
    }

    @Override
    public void handleKeyEvent(KeyEvent event) {
        super.handleKeyEvent(event);
    }

    /**
     * The purpose of this app is to learn how to play bj. Thus,
     * the design decision was made to give a player unlimited number
     * of tries to pick the right strategy. The red button punishment is
     * more than enough :)
     */
    private class ActionButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            SolutionManual solMan = SolutionManual.getInstance(getActivity());
            List<Deck.Card> concat = new ArrayList<>(m_field.playerCardOne);
            concat.addAll(m_field.playerCardTwo);
            SolutionManual.BlackJackAction action = solMan.getSolutionForMultipleCards(m_field.dealerCard.get(0), concat);
            ActionButton solutionButton = m_actionToButtons.get(action);

            if (solutionButton.button != v) { // wrong strategy
                v.setBackgroundColor(WRONG_ANSWER_COLOR);
            } else { // right strategy
                // make all the other buttons grey
                for (ActionButton actionButton : m_actionToButtons.values()) {
                    if (actionButton.button != solutionButton.button && actionButton.button != v) {
                        actionButton.button.setBackgroundColor(UNUSED_ANSWER_COLOR);
                        actionButton.button.setEnabled(false);
                    }
                }

                solutionButton.button.setBackgroundColor(CORRECT_ANSWER_COLOR);
                generateResponseMainPlayer(action);
            }
        }
    }

    private void generateResponseMainPlayer(SolutionManual.BlackJackAction action) {
        switch (action) {
            case Double:
                m_field.generatePlayerCardOne();
                resetCardImagesResponse();
                standFurtherMainPlayer();
                break;
            case Hit:
                m_field.generatePlayerCardOne();
                resetCardImagesResponse();
                resetButtonColors();
                if (!isNotBust(m_field.playerCardOne, m_field.playerCardTwo)) {
                    standFurtherMainPlayer();
                }
                break;
            case Stand:
                standFurtherMainPlayer();
                break;
            case Split: // to be implemented
                m_field.generatePlayerCardOne();
                m_field.generatePlayerCardTwo();
                resetCardImagesResponse();
                standFurtherMainPlayer();
                break;
            case DoubleAfterSplit: // to be implemented
                break;
            case Bust:
                break;
            default: // should never reach this
                break;
        }
    }

    private boolean generateResponseOtherPlayers(SolutionManual.BlackJackAction action, int player) {
        switch (action) {
            case Double:
                m_field.generateOtherPlayersCard(player);
                return false;
            case Hit:
                m_field.generateOtherPlayersCard(player);
                return isNotBust(m_field.players.get(player), m_field.players.get(player + 1));
            case Stand:
                return false;
            case Split: // to be implemented
                m_field.generateOtherPlayersCard(player);
                m_field.generateOtherPlayersCard(player + 1);
                break;
            case DoubleAfterSplit: // to be implemented
                break;
            case Bust:
                return false;
            default: // should never reach this
                break;
        }

        return false;
    }

    private boolean isNotBust(List<Deck.Card> deck1, List<Deck.Card> deck2) {
        if (getTwoDeckValue(deck1, deck2) > SolutionManual.MAX_FIELD) { // bust
            return false;
        } else {
            return true;
        }
    }

    private int getTwoDeckValue(List<Deck.Card> deck1, List<Deck.Card> deck2) {
        return getOneDeckValue(deck1) + getOneDeckValue(deck2);
    }

    private int getOneDeckValue(List<Deck.Card> deck) {
        int value = 0;
        for (int i = 0; i < deck.size(); ++i) {
            value += deck.get(i).rank.getValue();
        }
        return value;
    }

    private void standFurtherMainPlayer() {
        SolutionManual solMan = SolutionManual.getInstance(getActivity());
        for (ActionButton actionButton : m_actionToButtons.values()) {
            actionButton.button.setBackgroundColor(UNUSED_ANSWER_COLOR);
            actionButton.button.setEnabled(false);
        }
        // players and dealer game logic starts here
        for (int i = 0; i < m_players.length; i += 2) {
            boolean flag = true;
            while (flag) {
                List<Deck.Card> concat = new ArrayList<>(m_field.players.get(i));
                concat.addAll(m_field.players.get(i + 1));
                SolutionManual.BlackJackAction action = solMan.getSolutionForMultipleCards(m_field.dealerCard.get(0), concat);
                flag = generateResponseOtherPlayers(action, i);
            }
        }
        dealerGameLogic();
        resetCardImagesResponse();
        m_nextFieldButton.setEnabled(true);
    }

    private void dealerGameLogic() {
        int sum = m_field.dealerCard.get(0).rank.getValue();
        while (sum < DEALER_MAX) {
            Deck.Card dealer = m_field.generateDealerCard();
            sum += dealer.rank.getValue();
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

    private ImageView[] m_players;
    public LinearLayout m_players_layout;
    public LinearLayout m_main_player_layout;
    private LinearLayout m_dealer_layout;

    private Button m_hitButton;
    private Button m_dblButton;
    private Button m_stdButton;
    private Button m_splButton;
    private Button m_dasButton;

    private Button m_nextFieldButton;
    private ResponseField m_field;
    protected static int s_count;

    private final View.OnClickListener m_actionButtonClickListener;
    protected final Map<SolutionManual.BlackJackAction, ActionButton> m_actionToButtons;
}
