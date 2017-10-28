package com.github.lit.commons.context;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User : liulu
 * Date : 2017/4/1 19:29
 * version $Id: RunResult.java, v 0.1 Exp $
 */
@Getter
@Setter
@NoArgsConstructor
public class RunResult<T> {

    protected boolean success = true;

    protected T data;

    protected String code;

    protected List<String> messages;

    public RunResult(boolean success) {
        this.success = success;
    }

    public RunResult(T data) {
        this.data = data;
    }

    public RunResult(T data, String message) {
        this.data = data;
        addMessage(message);
    }

    public RunResult(boolean success, String message) {
        this.success = success;
        addMessage(message);
    }

    public RunResult(boolean success, String code, String message) {
        this.success = success;
        this.code = code;
        addMessage(message);
    }

    public void addMessage (String message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
    }

    public String getStrMessages () {
        if (messages == null || messages.isEmpty()) {
            return "";
        }
        return Arrays.toString(messages.toArray());
    }


}
