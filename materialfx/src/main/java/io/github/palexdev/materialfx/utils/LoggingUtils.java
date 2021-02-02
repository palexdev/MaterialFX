package io.github.palexdev.materialfx.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utils class for logging.
 */
public class LoggingUtils {
    public static final Logger logger = LogManager.getLogger("MaterialFX - " + LoggingUtils.class.getSimpleName());
    private static final Level EXCEPTION = Level.forName("EXCEPTION", 150);
    private static final StringWriter sw = new StringWriter();

    private LoggingUtils() {
    }

    /**
     * Gets the stacktrace of a {@code Throwable} as a String.
     *
     * @param ex The throwable/exception
     * @return the stacktrace as a String
     */
    public static String getStackTraceString(Throwable ex) {
        sw.flush();
        ex.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    /**
     * Logs the given {@code Throwable}'s stacktrace to the console.
     *
     * @param ex The throwable/exception
     */
    public static void logException(Throwable ex) {
        logger.log(EXCEPTION, getStackTraceString(ex));
    }

    /**
     * Logs the given {@code Throwable}'s exception to the console and adds the given String at the beginning.
     *
     * @param msg The extra message you want to log
     * @param ex  The throwable/exception
     */
    public static void logException(String msg, Throwable ex) {
        logger.log(EXCEPTION, msg + "\n" + getStackTraceString(ex));
    }
}
