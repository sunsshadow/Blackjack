package com.blackjackquiz.app.solution;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackjackquiz.app.R;
import com.blackjackquiz.app.logger.Logger;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Opens the database, creating if necessary
 */
class Database {
    private final String DATABASE_FILE_NAME = "blackjack.db";

    Database(Context context) {
        m_context = context;
        ThreadPoolExecutor dbOpenThreadPool = new ThreadPoolExecutor(1,
                1,
                5000L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        dbOpenThreadPool.allowCoreThreadTimeOut(true);
        m_db = dbOpenThreadPool.submit(new Callable<SQLiteDatabase>() {
            @Override
            public SQLiteDatabase call() throws Exception {
                return open();
            }
        });
    }

    public Transaction beginTransaction() {
        return new Transaction(getDatabase());
    }

    // all transactions are write transactions for the moment
    public class Transaction {
        Transaction(SQLiteDatabase db) {
            m_db = db;
            m_db.beginTransaction();
        }

        public void execSql(String sql) {
            m_db.execSQL(sql);
        }

        public void execSql(String sql, String[] args) {
            m_db.execSQL(sql, args);
        }

        public long insertOrThrow(String table, ContentValues values) {
            return m_db.insertOrThrow(table, null, values);
        }

        public Cursor query(String sql, String[] args) {
            return m_db.rawQuery(sql, args);
        }

        public void setSuccessful() {
            m_db.setTransactionSuccessful();
        }

        public void endTransaction() {
            m_db.endTransaction();
        }

        private final SQLiteDatabase m_db;
    }

    private SQLiteDatabase getDatabase() {
        while (true) {
            try {
                return m_db.get();
            } catch (InterruptedException e) {
                Logger.log(e, "Interrupted while getting the database");
            } catch (ExecutionException e) {
                Logger.log(e, "Error during execution of getting the database");
            }
        }
    }

    private SQLiteDatabase open() throws IOException {
        File dbFile = m_context.getDatabasePath(DATABASE_FILE_NAME);
        if (!dbFile.exists()) {
            copyDatabaseFromResources(dbFile);
        }

        return m_context.openOrCreateDatabase(DATABASE_FILE_NAME, Context.MODE_ENABLE_WRITE_AHEAD_LOGGING, null);
    }

    private void copyDatabaseFromResources(File outputFile) throws IOException {
        // ignore return value because it doesn't help
        outputFile.getParentFile().mkdirs();

        if (!outputFile.createNewFile()) {
            throw new IOException("Tried to create a new file when file already exists");
        }

        OutputStream outputStream = new FileOutputStream(outputFile);
        InputStream dbFileInputStream = m_context.getResources().openRawResource(R.raw.blackjack);
        try {
            byte[] buf = new byte[1024];
            int len;
            while ((len = dbFileInputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
        } finally {
            closeQuietly(dbFileInputStream);
            closeQuietly(outputStream);
        }
    }

    private void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            Logger.log(e, "Failed to close closeable: %s", closeable);
        }
    }

    private final Context m_context;
    private volatile Future<SQLiteDatabase> m_db;
}
