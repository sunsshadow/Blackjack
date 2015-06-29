package com.blackjackquiz.app.deck;

public class Deck {
    /*
     * The ordering of the Suites and Ranks is IMPORTANT!! It must match the ordering of the cards image. While the
     * ordering could be hard coded in the CardImageLoader, this is just easier.
     */
    public static Rank[] ALL_RANKS = new Rank[]{SoftRank.Ace, HardRank.Two, HardRank.Three, HardRank.Four,
            HardRank.Five, HardRank.Six, HardRank.Seven, HardRank.Eight,
            HardRank.Nine, HardRank.Ten, HardRank.Jack, HardRank.Queen,
            HardRank.King};

    public static enum Suite {
        Clubs,
        Spades,
        Hearts,
        Diamonds
    }

    public static interface Rank {
        public int getCountValue();
        public int getValue();
        public boolean isAce();
    }

    public static enum HardRank implements Rank {
        Two(2, 1),
        Three(3, 1),
        Four(4, 1),
        Five(5, 1),
        Six(6, 1),
        Seven(7, 0),
        Eight(8, 0),
        Nine(9, 0),
        Ten(10, -1),
        Jack(10, -1),
        Queen(10, -1),
        King(10, -1);

        HardRank(int value, int count) {
            m_value = value;
            m_count_value = count;
        }

        @Override
        public int getCountValue() {
            return m_count_value;
        }

        @Override
        public int getValue() {
            return m_value;
        }

        @Override
        public boolean isAce() {
            return false;
        }

        private final int m_value;
        private final int m_count_value;
    }

    public static enum SoftRank implements Rank {
        Ace(11, -1);

        SoftRank(int value, int count) {
            m_value = value;
            m_count_value = count;
        }

        @Override
        public int getCountValue() {
            return m_count_value;
        }

        @Override
        public int getValue() {
            return m_value;
        }

        @Override
        public boolean isAce() {
            return true;
        }

        private final int m_value;
        private final int m_count_value;
    }

    public static class Card {
        Card(Suite suite, Rank rank) {
            this.suite = suite;
            this.rank = rank;
        }

        public boolean isAce() {
            return rank.isAce();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (!(o instanceof Card)) {
                return false;
            }

            Card card = (Card) o;
            return suite == card.suite && rank == card.rank;
        }

        @Override
        public int hashCode() {
            int result = suite.hashCode();
            result = 31 * result + rank.hashCode();
            return result;
        }

        public final Suite suite;
        public final Rank rank;
    }

    private Deck() {
    }

    static Card getRandomCard() {
        return getRandomCardOfRank(Randomizer.next(ALL_RANKS));
    }

    static Card getRandomHardCard() {
        HardRank hardRank = Randomizer.next(HardRank.values());
        return getRandomCardOfRank(hardRank);
    }

    static Card getRandomSoftCard() {
        return getRandomCardOfRank(SoftRank.Ace);
    }

    static Card getRandomCardOfRank(Rank rank) {
        Suite suite = Randomizer.next(Suite.values());
        return new Card(suite, rank);
    }
}
