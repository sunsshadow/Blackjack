package com.blackjackquiz.app.solution;

import android.database.Cursor;

public class DbUtils {
    public interface QueryProcessor {
        Cursor performQuery(Database.Transaction transaction);

        void process(Cursor cursor);
    }

    public interface SingleItemQuerier<T> {
        Cursor performQuery(Database.Transaction transaction);

        T process(Cursor cursor);
    }

    public static void query(Database database, QueryProcessor processor) {
        Database.Transaction transaction = database.beginTransaction();
        try {
            query(transaction, processor);
            transaction.setSuccessful();
        } finally {
            transaction.endTransaction();
        }
    }

    public static void query(Database.Transaction transaction, QueryProcessor processor) {
        Cursor cursor = processor.performQuery(transaction);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    processor.process(cursor);
                    cursor.moveToNext();
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static <T> T singleItemQuery(Database db, SingleItemQuerier<T> querier) {
        Database.Transaction transaction = db.beginTransaction();
        try {
            T item = singleItemQuery(transaction, querier);
            transaction.setSuccessful();
            return item;
        } finally {
            transaction.endTransaction();
        }
    }

    public static <T> T singleItemQuery(Database.Transaction transaction, SingleItemQuerier<T> querier) {
        Cursor cursor = querier.performQuery(transaction);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                if (!cursor.isAfterLast()) {
                    return querier.process(cursor);
                }
            }

            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
