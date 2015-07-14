package com.blackjackquiz.app.logger;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Logger shared by all classes
 */
class SharedLogger {
    private static final String TAG = SharedLogger.class.getSimpleName();
    private static final String OUTPUT_FILE = "blackjack.log";

    private static SharedLogger s_sharedLogger;
    private FileWriter m_output;

    static SharedLogger getInstance() {
        if (s_sharedLogger == null) {
            synchronized (SharedLogger.class) {
                if (s_sharedLogger == null) {
                    s_sharedLogger = new SharedLogger();
                }
            }
        }

        return s_sharedLogger;
    }

    private SharedLogger() {
        m_output = null;

        File fileDir = Environment.getExternalStorageDirectory();
        if (!fileDir.exists()) {
            if (fileDir.mkdirs()) {
                Log.e(TAG, "Failed to make directory to store log files.");
                return;
            }
        }

        String outputLogFileName = fileDir.getAbsoluteFile().toString() + "/" + OUTPUT_FILE;
        File outputFile = new File(outputLogFileName);
        try {
            m_output = new FileWriter(outputFile, false);
        } catch (IOException e) {
            Log.e(TAG, "Failed to open log file for writing.", e);
        }
    }

    public void log(Exception ex, Object object, String format, Object... args) {
        if (m_output != null) {
            String className;
            if (object instanceof Class) {
                className = ((Class) object).getSimpleName();
            } else {
                className = object.getClass().getSimpleName();
            }

            String msg;
            if (ex != null) {
                msg = String.format("%s [%s] %s: %s %s\n", new Date(), Thread.currentThread().getId(),
                        className, String.format(format, args), Log.getStackTraceString(ex));
            } else {
                msg = String.format("%s [%s] %s: %s\n", new Date(), Thread.currentThread().getId(),
                        className, String.format(format, args));
            }

            try {
                m_output.write(msg);
                m_output.flush();
            } catch (IOException e) {
                Log.e(TAG, "Failed to write to log file.", e);
            }
        }
    }
}
