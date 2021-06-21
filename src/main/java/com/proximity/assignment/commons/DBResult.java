package com.proximity.assignment.commons;

/**
 * @author sthammishetty on 18/06/21
 */
public class DBResult {

    private Object result;
    private Throwable cause;
    private boolean isSuccess;

    public DBResult() {
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
