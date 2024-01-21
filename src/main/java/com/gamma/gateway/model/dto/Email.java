package com.gamma.gateway.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Email {
    private Long id;
    private Long userId;
    private String sender;
    private String recipient;
    private String emailObject;
    private String emailMessage;
    private List<Attachment> attachments;
    private LocalDateTime sentOn;

    public Email() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getEmailObject() {
        return emailObject;
    }

    public void setEmailObject(String emailObject) {
        this.emailObject = emailObject;
    }

    public String getEmailMessage() {
        return emailMessage;
    }

    public void setEmailMessage(String emailMessage) {
        this.emailMessage = emailMessage;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public LocalDateTime getSentOn() {
        return sentOn;
    }

    public void setSentOn(LocalDateTime sentOn) {
        this.sentOn = sentOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(id, email.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Email{" +
                "id=" + id +
                ", userId=" + userId +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", emailObject='" + emailObject + '\'' +
                ", emailMessage='" + emailMessage + '\'' +
                ", attachments=" + attachments +
                ", sentOn=" + sentOn +
                '}';
    }
}
