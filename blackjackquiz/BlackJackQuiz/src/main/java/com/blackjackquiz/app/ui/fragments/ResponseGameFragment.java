package com.blackjackquiz.app.ui.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.blackjackquiz.app.deck.CompleteField;
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

    public ResponseGameFragment() {
        m_actionToButtons = new HashMap<>();
        m_actionButtonClickListener = new ActionButtonOnClickListener();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_response_game, container, false);
        findCardImages(rootView);
        findActionButtons(rootView);
        addButtonsToActionMap();
        setupActionButtonClickListeners();

        Button nextFieldButton = (Button) rootView.findViewById(R.id.next_field_button);
        Button getCountButton = (Button) rootView.findViewById(R.id.count_button);
        m_players_layout = (LinearLayout) rootView.findViewById(R.id.players_layout);
        m_main_player_layout = (LinearLayout) rootView.findViewById(R.id.player_layout);
        setupNextFieldButton(nextFieldButton);
        setupGetCountButton(getCountButton);
        s_count = 0;
        newField();

        return rootView;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        Log.d("ElenaT", "m_players_layout.heigh onStart " + m_players_layout.getHeight());
//    }


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
    }

    private void setupActionButtonClickListeners() {
        for (ActionButton actionButton : m_actionToButtons.values()) {
            actionButton.button.setOnClickListener(m_actionButtonClickListener);
        }
    }

    private void findCardImages(View rootView) {
        m_dealerCardImage = (ImageView) rootView.findViewById(R.id.dealer_card_img);
        //m_dealerCardImage2 = (ImageView) rootView.findViewById(R.id.dealer_card_2_img);
        //m_playerCardOneImage = (ImageView) rootView.findViewById(R.id.player_card_one_img);
        //m_playerCardTwoImage = (ImageView) rootView.findViewById(R.id.player_card_two_img);
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
        m_dealerCardImage.setImageBitmap(cardImageLoader.getBitmapForCard(m_field.dealerCard.get(0)));
        //m_playerCardOneImage.setImageBitmap(cardImageLoader.getBitmapForCard(m_field.playerCardOne.get(0)));
        //m_playerCardTwoImage.setImageBitmap(cardImageLoader.getBitmapForCard(m_field.playerCardTwo.get(0)));
//        int height = m_main_player_layout.getHeight();
//        int width = (int) (m_main_player_layout.getWidth() / 2);
//        RelativeLayout relativeLayoutPlayerOne = new RelativeLayout(this.getActivity());
//        RelativeLayout.LayoutParams paramsPlayerOne = (new RelativeLayout.LayoutParams(
//                width,
//                height));
//        paramsPlayerOne.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        relativeLayoutPlayerOne.setLayoutParams(paramsPlayerOne);

//        //int height = m_main_player_layout.getHeight();
//        int numberOfCards = Math.max(m_field.playerCardOne.size(), m_field.playerCardTwo.size());
//        int part = (int) (height / (10 * 3 * numberOfCards));


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
        m_dealerCardImage.setImageBitmap(cardImageLoader.getBitmapForCard(m_field.dealerCard.get(0)));
        int numberOfCards = Math.max(m_field.playerCardOne.size(), m_field.playerCardTwo.size());
        //m_playerCardOneImage.setImageBitmap(cardImageLoader.getBitmapForCard(m_field.playerCardOne.get(0)));
        //m_playerCardTwoImage.setImageBitmap(cardImageLoader.getBitmapForCard(m_field.playerCardTwo.get(0)));
        int heightMainPlayer = m_main_player_layout.getHeight();
        int widthMainPlayer = m_main_player_layout.getWidth();
        int heightPlayers = m_players_layout.getHeight();
        int widthPlayers = m_players_layout.getWidth();
        removePlayersImageViews();
        generateDeck(widthMainPlayer, heightMainPlayer, cardImageLoader, numberOfCards, m_field.playerCardOne, m_main_player_layout, 2);
        generateDeck(widthMainPlayer, heightMainPlayer, cardImageLoader, numberOfCards, m_field.playerCardTwo, m_main_player_layout, 2);
//        RelativeLayout relativeLayoutPlayerOne = new RelativeLayout(this.getActivity());
//        RelativeLayout.LayoutParams paramsPlayerOne = (new RelativeLayout.LayoutParams(
//                (int) width / 2,
//                height));
//        //paramsPlayerOne.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        relativeLayoutPlayerOne.setLayoutParams(paramsPlayerOne);
//
//        //int height = m_main_player_layout.getHeight();
//        int numberOfCards = Math.max(m_field.playerCardOne.size(), m_field.playerCardTwo.size());
//        int partHeight = (int) (height / (10 + 3 * numberOfCards - 1));
//        int partWidth = (int) (width / (10 + 3 * (numberOfCards - 1)));
//
//        for (int i = 0; i < m_field.playerCardOne.size(); ++i) {
//            ImageView imageViewPlayerOne = new ImageView(this.getActivity());
//            Bitmap bitmapPlayerOne = cardImageLoader.getBitmapForCard(m_field.playerCardOne.get(i));
//            Log.d(TAG, "left " + i + " : " + m_field.playerCardOne.get(i).rank.getValue());
//            imageViewPlayerOne.setImageBitmap(bitmapPlayerOne);
//            RelativeLayout.LayoutParams paramsImagePlayerOne = (new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.MATCH_PARENT, partHeight * 10));
//            paramsImagePlayerOne.topMargin = partHeight * 3 * (i);
//            imageViewPlayerOne.setLayoutParams(paramsImagePlayerOne);
//            paramsImagePlayerOne.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//            imageViewPlayerOne.setAdjustViewBounds(true);
//            relativeLayoutPlayerOne.addView(imageViewPlayerOne);
//        }
//        m_main_player_layout.addView(relativeLayoutPlayerOne);

//        ImageView imageViewPlayerTwo = new ImageView(this.getActivity());
//        Bitmap bitmapPlayerTwo = cardImageLoader.getBitmapForCard(m_field.playerCardTwo.get(0));
//        Log.d(TAG, "right " + m_field.playerCardTwo.get(0).rank.getValue());
//        imageViewPlayerTwo.setImageBitmap(bitmapPlayerTwo);
//        RelativeLayout.LayoutParams paramsImagePlayerTwo = (new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT));
//        imageViewPlayerTwo.setLayoutParams(paramsImagePlayerTwo);
//        paramsImagePlayerTwo.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        imageViewPlayerTwo.setAdjustViewBounds(true);
//        m_main_player_layout.addView(imageViewPlayerTwo);
        int playersNumberOfCards = getMaxNumberOfCards(m_field.players);
        for (int i = 0; i < m_field.players.size(); ++i) {
            generateDeck(widthPlayers, heightPlayers, cardImageLoader, playersNumberOfCards, m_field.players.get(i), m_players_layout, m_field.players.size());
//            RelativeLayout relativeLayout = new RelativeLayout(this.getActivity());
//            RelativeLayout.LayoutParams params = (new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.WRAP_CONTENT,
//                    RelativeLayout.LayoutParams.MATCH_PARENT));
//            relativeLayout.setLayoutParams(params);
//
//
//            ImageView imageView = new ImageView(this.getActivity());
//            Bitmap bitmap = cardImageLoader.getBitmapForCard(m_field.players.get(i).get(0));
//            imageView.setImageBitmap(bitmap);
//            RelativeLayout.LayoutParams params0 = (new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT));
//            imageView.setLayoutParams(params0);
//            imageView.setAdjustViewBounds(true);

//            m_players_layout.addView(imageView);
//            RelativeLayout relativeLayout = new RelativeLayout(this.getActivity());
//            RelativeLayout.LayoutParams params = (new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.WRAP_CONTENT,
//                    RelativeLayout.LayoutParams.MATCH_PARENT));
//            relativeLayout.setLayoutParams(params);
//
//
//            ImageView imageView = new ImageView(this.getActivity());
//            Bitmap bitmap = cardImageLoader.getBitmapForCard(m_field.players[i]);
//            imageView.setImageBitmap(bitmap);
//            RelativeLayout.LayoutParams params0 = (new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT));
//            imageView.setLayoutParams(params0);
//            imageView.setAdjustViewBounds(true);
//
//            m_players_layout.addView(imageView);
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

    private void resetCardImagesResponse2() {
        CardImageLoader cardImageLoader = CardImageLoader.getInstance(getActivity());
        m_dealerCardImage.setImageBitmap(cardImageLoader.getBitmapForCard(m_field.dealerCard.get(0)));
        //m_dealerCardImage2.setImageBitmap(cardImageLoader.getBitmapForCard(m_field.playerCardOne));
        //m_playerCardOneImage.setImageBitmap(cardImageLoader.getBitmapForCard(m_field.playerCardOne.get(0)));
        //m_playerCardTwoImage.setImageBitmap(cardImageLoader.getBitmapForCard(m_field.playerCardTwo.get(0)));
        for (int i = 0; i < m_field.players.size(); ++i) {
            RelativeLayout relativeLayout = new RelativeLayout(this.getActivity());
            RelativeLayout.LayoutParams params = (new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            relativeLayout.setLayoutParams(params);
            //int height = m_players_layout.getMeasuredHeight();
//            m_players_layout.post(new Runnable() {
//                @Override
//                public void run() {
//                    //maybe also works height = ll.getLayoutParams().height;
//
//                    int height1  = m_players_layout.getHeight();
//                    Log.d("ElenaT", "m_players_layout height " + height1);
//
//                }
//            });


//            DisplayMetrics displayMetrics = this.getActivity().getApplicationContext().getResources().getDisplayMetrics();
//            int width4 = displayMetrics.widthPixels;
//            int height4 = displayMetrics.heightPixels;

            m_players_layout.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int width = m_players_layout.getMeasuredWidth();
            int height = m_players_layout.getMeasuredHeight();
            int part = (int) (height / (10 + 3 * 3));
//            Log.d("ElenaT", "width4 " + width4);
//            Log.d("ElenaT", "height4 " + height4);
            Log.d("ElenaT", "height " + height);
            Log.d("ElenaT", "width " + width);
            Log.d("ElenaT", "part " + part);

            ImageView imageView = new ImageView(this.getActivity());
            Bitmap bitmap = cardImageLoader.getBitmapForCard(m_field.players.get(i).get(0));
            imageView.setImageBitmap(bitmap);
            RelativeLayout.LayoutParams params0 = (new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            // height));
            //params0.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            imageView.setLayoutParams(params0);
            imageView.setAdjustViewBounds(true);
            //Log.d("ElenaT", "imageView " + imageView.getHeight());
            //relativeLayout.addView(imageView);

//            ImageView imageView1 = new ImageView(this.getActivity());
//            Bitmap bitmap1 = cardImageLoader.getBitmapForCard(m_field.players[i]);
//            imageView1.setImageBitmap(bitmap1);
//            RelativeLayout.LayoutParams params1 = (new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.MATCH_PARENT,
//                    part * 10));
//            params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//            params1.topMargin = part * 3;
//            imageView1.setLayoutParams(params1);
//            imageView1.setAdjustViewBounds(true);
//            relativeLayout.addView(imageView1);
//
//            ImageView imageView2 = new ImageView(this.getActivity());
//            Bitmap bitmap2 = cardImageLoader.getBitmapForCard(m_field.players[i]);
//            imageView1.setImageBitmap(bitmap2);
//            RelativeLayout.LayoutParams params2 = (new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.MATCH_PARENT,
//                    part * 10));
//            params2.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//            params2.topMargin = part * 3 * 2;
//            imageView2.setLayoutParams(params2);
//            imageView2.setAdjustViewBounds(true);
//            relativeLayout.addView(imageView2);

            //         m_players_layout.addView(relativeLayout);

//            ImageView imageView = new ImageView(this.getActivity());
//            Bitmap bitmap = cardImageLoader.getBitmapForCard(m_field.players[i]);
//            imageView.setImageBitmap(bitmap);
//            imageView.setLayoutParams(new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT));
//            imageView.setAdjustViewBounds(true);
//
            m_players_layout.addView(imageView);
        }

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
//            SolutionManual.BlackJackAction action = solMan.getSolutionForCards(m_field.dealerCard.get(0),
//                    m_field.playerCardOne.get(0),
//                    m_field.playerCardTwo.get(0));
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
                isBust();
                standFurther();
                break;
            case Hit:
                m_field.generatePlayerCardOne();
                resetCardImagesResponse();
                isBust();
                break;
            case Stand:
                standFurther();
                break;
            case Split:
                m_field.generatePlayerCardOne();
                m_field.generatePlayerCardTwo();
                resetCardImagesResponse();
                isBust();
                standFurther();
                break;
            case DoubleAfterSplit:
                break;
            default: // should never reach this
                break;
        }
    }

    private void generateResponseOtherPlayers(SolutionManual.BlackJackAction action) {
        switch (action) {
            case Double:
                isBust();
                standFurther();
                break;
            case Hit:
                isBust();
                break;
            case Stand:
                standFurther();
                break;
            case Split:
                break;
            case DoubleAfterSplit:
                break;
            default: // should never reach this
                break;
        }
    }

    private void isBust() {
        if (m_field.count > SolutionManual.MAX_FIELD) { // bust

        }
    }

    private void standFurther() {
        SolutionManual solMan = SolutionManual.getInstance(getActivity());
        for (ActionButton actionButton : m_actionToButtons.values()) {
            actionButton.button.setBackgroundColor(UNUSED_ANSWER_COLOR);
            actionButton.button.setEnabled(false);
        }
        // players and dealer game logic starts here
        for (int i = 0; i < m_players.length; ++i) {
            m_field.generateOtherPlayersCard(i);
            SolutionManual.BlackJackAction action = solMan.getSolutionForMultipleCards(m_field.dealerCard.get(0), m_field.players.get(i));
            generateResponseOtherPlayers(action);
        }
        resetCardImagesResponse();
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
    private ImageView m_dealerCardImage2;
    //private ImageView m_playerCardOneImage;
    //private ImageView m_playerCardTwoImage;
    private ImageView[] m_players;
    public LinearLayout m_players_layout;
    public LinearLayout m_main_player_layout;

    private Button m_hitButton;
    private Button m_dblButton;
    private Button m_stdButton;
    private Button m_splButton;
    private Button m_dasButton;

    private ResponseField m_field;
    protected static int s_count;

    private final View.OnClickListener m_actionButtonClickListener;
    protected final Map<SolutionManual.BlackJackAction, ActionButton> m_actionToButtons;
}
