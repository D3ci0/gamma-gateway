package com.gamma.gateway.model.dto;

import java.util.LinkedList;
import java.util.List;

public class EmailResponseMessage {
    private List<Email> emails;
    private Result result;

    public EmailResponseMessage() {
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public void addEmail(Email email){
        if(emails == null){
            emails = new LinkedList<>();
        }
        emails.add(email);
    }

    @Override
    public String toString() {
        return "EmailResponseMessage{" +
                "emails=" + emails +
                ", result=" + result +
                '}';
    }
}
