package com.blackjackquiz.app.solution;

import android.content.Context;
import android.database.Cursor;
import com.blackjackquiz.app.deck.Deck.Card;
import com.blackjackquiz.app.deck.Field.HandType;

import java.util.HashMap;
import java.util.Map;

public class SolutionManual
{
    private static final Map<HandType, String> s_handToTable = new HashMap<>();

    static
    {
        s_handToTable.put(HandType.Hard, "hard");
        s_handToTable.put(HandType.Soft, "soft");
        s_handToTable.put(HandType.Split, "split");
    }

    private static volatile SolutionManual s_solutionManual;

    // See makedb/makedb.rb for the MOVES_MAP mapping values
    public enum BlackJackAction
    {
        Hit(0),
        Double(1),
        Stand(2),
        Split(3),
        DoubleAfterSplit(4);

        BlackJackAction(int value)
        {
            this.value = value;
        }

        private final int value;
    }

    private static Map<Integer, BlackJackAction> s_codeToActionMap = new HashMap<>();

    static
    {
        for (BlackJackAction action : BlackJackAction.values())
        {
            s_codeToActionMap.put(action.value, action);
        }
    }

    public synchronized static SolutionManual getInstance(Context context)
    {
        if (s_solutionManual == null)
        {
            s_solutionManual = new SolutionManual(context);
        }

        return s_solutionManual;
    }

    private SolutionManual(Context context)
    {
        m_db = new Database(context);
    }

    public BlackJackAction getSolutionForCards(final Card dealerCard,
                                               final Card playerCardOne,
                                               final Card playerCardTwo)
    {
        final HandType handType = getHandTypeFromCards(playerCardOne, playerCardTwo);
        final int playerCardValue = getValueFromCards(handType, playerCardOne, playerCardTwo);

        return DbUtils.singleItemQuery(m_db, new DbUtils.SingleItemQuerier<BlackJackAction>()
        {
            @Override
            public Cursor performQuery(Database.Transaction transaction)
            {
                return transaction.query("SELECT action FROM " + s_handToTable.get(handType) +
                                                 " WHERE dealer_card=? AND player_card_value=?",
                                         new String[]{String.valueOf(dealerCard.rank.getValue()),
                                                      String.valueOf(playerCardValue)});
            }

            @Override
            public BlackJackAction process(Cursor cursor)
            {
                return s_codeToActionMap.get(cursor.getInt(0));
            }
        });
    }

    private static HandType getHandTypeFromCards(Card cardOne, Card cardTwo)
    {
        if (cardOne.rank.equals(cardTwo.rank))
        {
            return HandType.Split;
        }

        if (cardOne.isAce() || cardTwo.isAce())
        {
            return HandType.Soft;
        }
        else
        {
            return HandType.Hard;
        }
    }

    private static int getValueFromCards(HandType handType, Card cardOne, Card cardTwo)
    {
        if (handType == HandType.Split)
        {
            return cardOne.rank.getValue();
        }
        else
        {
            return cardOne.rank.getValue() + cardTwo.rank.getValue();
        }
    }

    private final Database m_db;
}
