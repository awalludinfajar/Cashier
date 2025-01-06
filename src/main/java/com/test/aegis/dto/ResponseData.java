package com.test.aegis.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ResponseData<T> {
    private boolean success;

    @JsonInclude(Include.NON_EMPTY)
    private String message;

    @JsonInclude(Include.NON_EMPTY)
    private List<String> messages;

    @JsonInclude(Include.NON_EMPTY)
    private List<T> list;
    
    @JsonInclude(Include.NON_EMPTY)
    private T data;

    public ResponseData() {}

    public ResponseData(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<String> getMessages() {
        return messages;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public String getMessage() {
        return message;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
