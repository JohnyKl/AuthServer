package com.dreamlex;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

/**
 * Created by Johny on 24.11.2015.
 */
public class DebugLog extends Logger {
    public final static boolean DEBUG = true;
    public final static boolean INFO = true;
    public final static boolean ERROR = true;
    //public final static boolean WARNING = true;

    private DebugLog(String name) {
        super(name);
    }

    public static void info(String message) {
        if (INFO) {
            String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
            int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();

            Logger.getLogger(className + ": " + methodName + "(), " + lineNumber).info(message);
        }
    }

    public static void debug(String message) {
        if (DEBUG) {
            String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
            int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();

            Logger.getLogger(className + ": " + methodName + "(), " + lineNumber).debug(message);
        }
    }

    public static void error(String message) {
        if (ERROR) {
            String fullClassName = Thread.currentThread().getStackTrace()[2].getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
            int lineNumber = Thread.currentThread().getStackTrace()[2].getLineNumber();

            Logger.getLogger(className + ": " + methodName + "(), " + lineNumber).error(message);
        }
    }
}