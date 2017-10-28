package com.github.lit.commons.exception;

import com.github.lit.commons.context.RunResult;

import java.util.List;

/**
 * User : liulu
 * Date : 2017/3/20 21:04
 * version $Id: RunResultHolder.java, v 0.1 Exp $
 */
public class RunResultHolder {

    private static ThreadLocal<RunResult> RESULT_CONTEXT = new ThreadLocal<>();

    public static void addError(AppCheckedException checkedException) {
        add(false, checkedException.getErrorCode(), checkedException.getErrorMsg());
    }

    public static void addError(String error) {
        add(false, null, error);
    }

    public static void addError(String code, String error) {
        add(false, code, error);
    }

    public static void addMessage(String message) {
        add(true, null, message);
    }

    public static void addMessage(String code, String message) {
        add(true, code, message);
    }

    public static void add(boolean success, String code, String message) {
        RunResult runResult = RESULT_CONTEXT.get();
        if (runResult == null) {
            runResult = new RunResult();
            RESULT_CONTEXT.set(runResult);
        }
        runResult.setSuccess(success);
        runResult.setCode(code);
        runResult.addMessage(message);
    }

    /**
     * 是否成功
     *
     * @return
     */
    public static boolean isSuccess() {
        RunResult runResult = RESULT_CONTEXT.get();
        return runResult == null || runResult.isSuccess();
    }

    /**
     * 是否有错误
     *
     * @return
     */
    public static boolean hasError() {
        return !isSuccess();
    }

    public static RunResult getRunResult () {
        return getRunResult(true);
    }

    public static RunResult getRunResult (boolean isClear) {
        RunResult runResult = RESULT_CONTEXT.get();
        if (isClear) {
            RESULT_CONTEXT.remove();
        }
        return runResult;
    }

    public static String getStrMessages () {
        return getStrMessages(true);
    }

    public static String getStrMessages (boolean isClear) {
        RunResult runResult = RESULT_CONTEXT.get();
        if (runResult == null) {
            return null;
        }
        if (isClear) {
            RESULT_CONTEXT.remove();
        }
        return runResult.getStrMessages();
    }

    public static List<String> getMessages () {
        return getMessages(true);
    }

    public static List<String> getMessages (boolean isClear) {
        RunResult runResult = RESULT_CONTEXT.get();
        if (runResult == null) {
            return null;
        }
        if (isClear) {
            RESULT_CONTEXT.remove();
        }
        return runResult.getMessages();
    }

    public static String getCode () {
        return getCode(true);
    }

    public static String getCode (boolean isClear) {
        RunResult runResult = RESULT_CONTEXT.get();
        if (runResult == null) {
            return null;
        }
        if (isClear) {
            RESULT_CONTEXT.remove();
        }
        return runResult.getCode();
    }

    public static void clear() {
        RESULT_CONTEXT.remove();
    }

}
