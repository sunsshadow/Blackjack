package com.blackjackquiz.app.deck;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.blackjackquiz.app.R;
import com.blackjackquiz.app.deck.Deck.Card;
import com.blackjackquiz.app.deck.Deck.Suite;
import com.blackjackquiz.app.logger.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CardImageLoader {
    private static final int IMAGE_INITIAL_OFFSET_X = 1;
    private static final int IMAGE_INITIAL_OFFSET_Y = 1;
    private static final int IMAGE_OFFSET_X = 73;
    private static final int IMAGE_OFFSET_Y = 98;
    private static final int IMAGE_WIDTH = 72;
    private static final int IMAGE_HEIGHT = 96;
    private static final int IMAGE_SCALING = 4;

    private static volatile CardImageLoader s_cardImageLoader;

    public static synchronized CardImageLoader getInstance(Context context) {
        if (s_cardImageLoader == null) {
            s_cardImageLoader = new CardImageLoader(context);
        }

        return s_cardImageLoader;
    }

    private CardImageLoader(final Context context) {
        ThreadPoolExecutor drawableLoader = new ThreadPoolExecutor(1,
                1,
                5000L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        drawableLoader.allowCoreThreadTimeOut(true);
        m_cardImages = drawableLoader.submit(new Callable<Map<Card, Bitmap>>() {
            @Override
            public Map<Card, Bitmap> call() throws Exception {
                return loadCardImages(context);
            }
        });
    }

    public Bitmap getBitmapForCard(Card card) {
        for (; ; ) {
            try {
                return m_cardImages.get().get(card);
            } catch (Exception e) {
                Logger.log(e, "Failed to get the card image, retrying");
            }
        }
    }

    private Map<Card, Bitmap> loadCardImages(Context context) {
        Map<Card, Bitmap> cardImages = new HashMap<>();
        Bitmap playingCardsBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.playing_cards);

        Suite[] suites = Suite.values();
        for (int i = 0; i < Deck.ALL_RANKS.length; ++i) {
            for (int j = 0; j < suites.length; ++j) {
                Bitmap cardBmp = Bitmap.createBitmap(playingCardsBmp,
                        IMAGE_INITIAL_OFFSET_X + IMAGE_OFFSET_X * i,
                        IMAGE_INITIAL_OFFSET_Y + IMAGE_OFFSET_Y * j,
                        IMAGE_WIDTH,
                        IMAGE_HEIGHT);
                Bitmap scaledCardBmp = Bitmap.createScaledBitmap(cardBmp, IMAGE_SCALING * IMAGE_WIDTH, IMAGE_SCALING * IMAGE_HEIGHT, false);
                cardImages.put(new Card(suites[j], Deck.ALL_RANKS[i]), scaledCardBmp);
            }
        }

        return cardImages;
    }

    private final Future<Map<Card, Bitmap>> m_cardImages;
}
