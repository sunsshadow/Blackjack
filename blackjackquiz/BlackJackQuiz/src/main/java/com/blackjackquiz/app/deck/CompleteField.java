package com.blackjackquiz.app.deck;

import com.blackjackquiz.app.deck.Deck.Card;

public class CompleteField extends Field {
    private static final String TAG = CompleteField.class.getSimpleName();

    private static final int PLAYERS_NUMBER = 3; // @TODO dynamic number of players if needed
    private static final int CARDS_NUMBER = 2; // there are two cards initially in blackjack

    private CompleteField(Field mainPlayer, Card[] players, int count) {
        super(mainPlayer.dealerCard, mainPlayer.playerCardOne, mainPlayer.playerCardTwo, count);
        this.players = players;
    }

    public static CompleteField getRandomCompleteField() {
        Field main = Field.newUnbiasedField();

        int count = 0;
        Card players[] = new Card[PLAYERS_NUMBER * CARDS_NUMBER];
        for (int i = 0; i < players.length; ++i) {
            players[i] = Deck.getRandomCard();
            count += players[i].rank.getCountValue();
        }

        count += main.count;
        return new CompleteField(main, players, count);
    }

    public final Card players[];
}
