package com.blackjackquiz.app.logger;

public class Logger
{
    public static void log(Object object, String format, Object ... args)
    {
        SharedLogger.getInstance().log(null, object, format, args);
    }

    public static void log(Exception ex, Object object, String msg)
    {
        SharedLogger.getInstance().log(ex, object, msg);
    }
}
