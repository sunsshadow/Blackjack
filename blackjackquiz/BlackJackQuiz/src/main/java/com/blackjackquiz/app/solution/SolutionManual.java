package com.blackjackquiz.app.solution;

import android.content.Context;
import android.database.Cursor;

import com.blackjackquiz.app.deck.Deck.Card;
import com.blackjackquiz.app.deck.Field.HandType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolutionManual {
    private static final Map<HandType, String> s_handToTable = new HashMap<>();
    public static final int MAX_FIELD = 21;
    private static final int SOFT_TO_HARD = 10;

    static {
        s_handToTable.put(HandType.Hard, "hard");
        s_handToTable.put(HandType.Soft, "soft");
        s_handToTable.put(HandType.Split, "split");
    }

    private static volatile SolutionManual s_solutionManual;

    // See makedb/makedb.rb for the MOVES_MAP mapping values
    public enum BlackJackAction {
        Hit(0),
        Double(1),
        Stand(2),
        Split(3),
        DoubleAfterSplit(4),
        Bust(5);


        BlackJackAction(int value) {
            this.value = value;
        }

        private final int value;
    }

    private static Map<Integer, BlackJackAction> s_codeToActionMap = new HashMap<>();

    static {
        for (BlackJackAction action : BlackJackAction.values()) {
            s_codeToActionMap.put(action.value, action);
        }
    }

    public synchronized static SolutionManual getInstance(Context context) {
        if (s_solutionManual == null) {
            s_solutionManual = new SolutionManual(context);
        }

        return s_solutionManual;
    }

    private SolutionManual(Context context) {
        m_db = new Database(context);
    }

    public BlackJackAction getSolutionForCards(final Card dealerCard,
                                               final Card playerCardOne,
                                               final Card playerCardTwo) {
        final HandType handType = getHandTypeFromCards(playerCardOne, playerCardTwo);
        final int playerCardValue = getValueFromCards(handType, playerCardOne, playerCardTwo);

        return DbUtils.singleItemQuery(m_db, new DbUtils.SingleItemQuerier<BlackJackAction>() {
            @Override
            public Cursor performQuery(Database.Transaction transaction) {
                return transaction.query("SELECT action FROM " + s_handToTable.get(handType) +
                                " WHERE dealer_card=? AND player_card_value=?",
                        new String[]{String.valueOf(dealerCard.rank.getValue()),
                                String.valueOf(playerCardValue)});
            }

            @Override
            public BlackJackAction process(Cursor cursor) {
                return s_codeToActionMap.get(cursor.getInt(0));
            }
        });
    }

    public BlackJackAction getSolutionForMultipleCards(final Card dealerCard,
                                                       final List<Card> playerCards) {
        final List<Integer> softValue = getSoftValue(playerCards);
        final HandType handType = getHandTypeFromMultipleCards(playerCards, softValue.get(0));
        final int playerCardValue = getValueFromMultipleCards(handType, playerCards, softValue.get(1));

        if (playerCardValue > MAX_FIELD) {
            return BlackJackAction.Bust;
        }

        return DbUtils.singleItemQuery(m_db, new DbUtils.SingleItemQuerier<BlackJackAction>() {
            @Override
            public Cursor performQuery(Database.Transaction transaction) {
                return transaction.query("SELECT action FROM " + s_handToTable.get(handType) +
                                " WHERE dealer_card=? AND player_card_value=?",
                        new String[]{String.valueOf(dealerCard.rank.getValue()),
                                String.valueOf(playerCardValue)});
            }

            @Override
            public BlackJackAction process(Cursor cursor) {
                return s_codeToActionMap.get(cursor.getInt(0));
            }
        });
    }

    private static HandType getHandTypeFromCards(Card cardOne, Card cardTwo) {
        if (cardOne.rank.equals(cardTwo.rank)) {
            return HandType.Split;
        }

        if (cardOne.isAce() || cardTwo.isAce()) {
            return HandType.Soft;
        } else {
            return HandType.Hard;
        }
    }

    private static List<Integer> getSoftValue(List<Card> cards) {
        int aceCount = 0;
        List<Integer> isSoft = new ArrayList<>();
        for (int i = 0; i < cards.size(); ++i) {
            if (cards.get(i).isAce()) {
                aceCount++;
            }
        }

        if (aceCount > 0) { // if there are aces this still can be a hard case
            int sum = getValueOfCards(cards);

            while (sum > MAX_FIELD) {
                sum -= SOFT_TO_HARD;
                aceCount--;
                if (aceCount <= 0) {
                    break;
                }
            }

            if (aceCount > 0) {
                isSoft.add(1);
            } else {
                isSoft.add(-1);
            }
            isSoft.add(sum);
        } else {
            isSoft.add(-1);
            isSoft.add(-1);
        }

        return isSoft;
    }

    private static HandType getHandTypeFromMultipleCards(List<Card> cards, int isSoft) {
        if (isSoft > 0) { // if there are aces this still can be a hard case
            return HandType.Soft;
        } else {
            if (cards.size() == 2 && cards.get(0).rank.equals(cards.get(1).rank)) {
                return HandType.Split;
            } else {
                return HandType.Hard;
            }
        }
    }

    private static int getValueFromCards(HandType handType, Card cardOne, Card cardTwo) {
        if (handType == HandType.Split) {
            return cardOne.rank.getValue();
        } else {
            return cardOne.rank.getValue() + cardTwo.rank.getValue();
        }
    }

    private static int getValueFromMultipleCards(HandType handType, List<Card> cards, int usedToBeSoftValue) {
        if (handType == HandType.Split) {
            return cards.get(0).rank.getValue();
        } else {
            if (usedToBeSoftValue > 0) {
                return usedToBeSoftValue;
            } else {
                return getValueOfCards(cards);
            }
        }
    }

    private static int getValueOfCards (List<Card> cards) {
        int sum = 0;
        for (int i = 0; i < cards.size(); ++i) {
            sum += cards.get(i).rank.getValue();
        }
        return sum;
    }

    private final Database m_db;
}
