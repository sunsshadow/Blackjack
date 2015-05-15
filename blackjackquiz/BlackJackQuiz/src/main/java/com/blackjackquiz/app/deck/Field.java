package com.blackjackquiz.app.deck;

import com.blackjackquiz.app.deck.Deck.Card;

public class Field
{
    public enum HandType
    {
        Hard,
        Soft,
        Split
    }

    private Field(Card dealerCard, Card playerCardOne, Card playerCardTwo)
    {
        this.dealerCard = dealerCard;
        this.playerCardOne = playerCardOne;
        this.playerCardTwo = playerCardTwo;
    }

    // the field is biased in that the type of hand (hard, soft, split) is evenly distributed
    public static Field newBiasedField()
    {
        HandType handType = Randomizer.next(HandType.values());
        switch (handType)
        {
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

    private static Field getHardField()
    {
        return new Field(Deck.getRandomCard(), Deck.getRandomHardCard(), Deck.getRandomHardCard());
    }

    private static Field getSoftField()
    {
        return new Field(Deck.getRandomCard(), Deck.getRandomHardCard(), Deck.getRandomSoftCard());
    }

    private static Field getSplitField()
    {
        Card cardOne = Deck.getRandomCard();
        return new Field(Deck.getRandomCard(), cardOne, Deck.getRandomCardOfRank(cardOne.rank));
    }

    public final Card dealerCard;
    public final Card playerCardOne;
    public final Card playerCardTwo;
}
