package com.blackjackquiz.app.deck;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elena on 7/26/15.
 */
public class ResponseField {
    private static final String TAG = ResponseField.class.getSimpleName();

    private ResponseField(List<Deck.Card> dealerCard, List<Deck.Card> playerCardOne,
                          List<Deck.Card> playerCardTwo, List<List<Deck.Card>> players, int count) {
        this.dealerCard = dealerCard;
        this.playerCardOne = playerCardOne;
        this.playerCardTwo = playerCardTwo;
        this.players = players;
        this.count = count;
    }

    public static ResponseField newUnbiasedField() {
        return getRandomField();
    }


    private static ResponseField getRandomField() {
        Field main = Field.newUnbiasedField();
        int count = 0;
        List<List<Deck.Card>> players = new ArrayList<>();
        for (int i = 0; i < CompleteField.PLAYERS_NUMBER * CompleteField.CARDS_NUMBER; ++i) {
            List<Deck.Card> level = new ArrayList<>();
            level.add(Deck.getRandomCard());
            players.add(level);
            count += level.get(0).rank.getCountValue();
        }

        count += main.count;
        List<Deck.Card> dealerCard = new ArrayList<>();
        dealerCard.add(main.dealerCard);
        List<Deck.Card> playerCardOne = new ArrayList<>();
        playerCardOne.add(main.playerCardOne);
        List<Deck.Card> playerCardTwo = new ArrayList<>();
        playerCardTwo.add(main.playerCardTwo);
        return new ResponseField(dealerCard, playerCardOne, playerCardTwo, players, count);
    }

    public Deck.Card generateDealerCard() {
        Deck.Card dealer = Deck.getRandomCard();
        count += dealer.rank.getCountValue();
        dealerCard.add(dealer);
        return dealer;
    }

    public void generatePlayerCardOne() {
        Deck.Card player1 = Deck.getRandomCard();
        count += player1.rank.getCountValue();
        playerCardOne.add(player1);
    }

    public void generatePlayerCardTwo() {
        Deck.Card player2 = Deck.getRandomCard();
        count += player2.rank.getCountValue();
        playerCardTwo.add(player2);
    }

    public void generateOtherPlayersCard(int player) {
        Deck.Card playerCard = Deck.getRandomCard();
        count += playerCard.rank.getCountValue();
        players.get(player).add(playerCard);
    }

    public final List<Deck.Card> dealerCard;
    public final List<Deck.Card> playerCardOne;
    public final List<Deck.Card> playerCardTwo;
    public final List<List<Deck.Card>> players;
    public int count; // optional
}
