/*
 * MailMessage.java - This class holds an abstract base class for implementing a mailinglist-specific
 *                    message with required identifiers and content.
 * 
 * Copyright (C) 2010 Magnus Hagander <magnus@hagander.net>
 * 
 * This software is released under the BSD license.
 */
package net.hagander.mailinglistmoderator.backend;

/**
 * 
 * @author Magnus Hagander <magnus@hagander.net>
 * 
 *         This base class is simply a container for some common properties.
 */
public abstract class MailMessage {
	private String sender;
	private String subject;
	private String content;
	private statuslevel status = statuslevel.Defer;
    private String denialReason ="Denied because of Reasons!";

	public enum statuslevel {
		Accept, Reject, Defer, Denied
	};

	public MailMessage(String sender, String subject, String content) {
		/* 
		 * Create new strings to de-couple from large strings being returned
		 * in regex matches. We do this here to get it in a centralized location,
		 * even if it means we might duplicate once or twice too many.
		 * Also, limit the length of the content to 10000 bytes. NEW LIMIT!
		 */
		this.sender = new String(sender);
		this.subject = new String(subject);
        final int messageLimit = 10000;
        if (content.length() > messageLimit)
        {
            String newMessage =new String(content.substring(0, messageLimit));
            newMessage = newMessage.concat("-------------MESSAGE CUT SHORT AFTER 10000 BYTES!---------------");
            this.content = newMessage;

        }
		else
			this.content = new String(content);
	}

	public String getSender() {
		return sender;
	}

	public String getSubject() {
		return subject;
	}

	public String getContent() {
		return content;
	}

	public void setStatus(statuslevel status) {
		this.status = status;
	}

	public statuslevel getStatus() {
		return status;
	}

    public void setDenialReason(String denialReason) {
        this.denialReason = denialReason;
    }

    public String getDenialReason() {
        return denialReason;
    }
}
