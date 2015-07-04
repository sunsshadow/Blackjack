package com.blackjackquiz.app.deck;

import android.util.Log;

import com.blackjackquiz.app.deck.Deck.Card;

public class Field {

    public enum HandType {
        Hard,
        Soft,
        Split
    }

    private Field(Card dealerCard, Card playerCardOne, Card playerCardTwo) {
        this.dealerCard = dealerCard;
        this.playerCardOne = playerCardOne;
        this.playerCardTwo = playerCardTwo;
        this.count = 0;
    }

    private Field(Card dealerCard, Card playerCardOne, Card playerCardTwo, int count) {
        this.dealerCard = dealerCard;
        this.playerCardOne = playerCardOne;
        this.playerCardTwo = playerCardTwo;
        this.count = count;
    }

    // the field is biased in that the type of hand (hard, soft, split) is evenly distributed
    public static Field newBiasedField() {
        HandType handType = Randomizer.next(HandType.values());
        switch (handType) {
            case Hard:
                return getHardField();
            case Soft:
                return getSoftField();
            case Split:
                return getSplitField();
            default:
                // this should never happen
                return null;
        }
    }

    public static Field newUnbiasedField() {
        return getRandomField();
    }

    private static Field getHardField() {
        return new Field(Deck.getRandomCard(), Deck.getRandomHardCard(), Deck.getRandomHardCard());
    }

    private static Field getSoftField() {
        return new Field(Deck.getRandomCard(), Deck.getRandomHardCard(), Deck.getRandomSoftCard());
    }

    private static Field getSplitField() {
        Card cardOne = Deck.getRandomCard();
        return new Field(Deck.getRandomCard(), cardOne, Deck.getRandomCardOfRank(cardOne.rank));
    }

    private static Field getRandomField() {
        Card dealer = Deck.getRandomCard();
        Card player1 = Deck.getRandomCard();
        Card player2 = Deck.getRandomCard();
        Log.d("ElenaT", "dealer.rank.getCountValue() " + dealer.rank.getCountValue());
        Log.d("ElenaT", "player1.rank.getCountValue() " + player1.rank.getCountValue());
        Log.d("ElenaT", "player2.rank.getCountValue() " + player2.rank.getCountValue());
        int count = dealer.rank.getCountValue() + player1.rank.getCountValue() + player2.rank.getCountValue();
        return new Field(dealer, player1, player2, count);
    }

    public final Card dealerCard;
    public final Card playerCardOne;
    public final Card playerCardTwo;
    public int count; // optional
}
