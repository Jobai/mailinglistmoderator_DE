/*
 * Majordomo2.java - This class holds implements mailinglist management for Majordomo2 lists.
 * 
 * Copyright (C) 2010 Magnus Hagander <magnus@hagander.net>
 * 
 * This software is released under the BSD license.
 */
package net.hagander.mailinglistmoderator.backend.providers;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.hagander.mailinglistmoderator.backend.ListServer;
import net.hagander.mailinglistmoderator.backend.MailMessage;
import net.hagander.mailinglistmoderator.backend.MailMessage.statuslevel;

/**
 * 
 * @author Magnus Hagander <magnus@hagander.net>
 * 
 */
public class Majordomo2 extends ListServer {
	public Majordomo2(String name, String rooturl, String password) {
		super(name, rooturl, password);
	}

	/*
	 * Regular expressions for matching message lists and contents.
	 */

	private static final Pattern enumMailPattern = Pattern
			.compile(
					"<td><input type=\"checkbox\" name=\"extra\"\\s+value=\"([^\"]+)\">",
					Pattern.DOTALL);
	private static final Pattern mailDetailsPattern = Pattern
			.compile(
					"<tr><td>From\\s+</td><td>([^<]+)</td>.*?<tr><td>Subject\\s+</td><td>([^<]+)</td>.*?<pre>\\s+([^<]+)\\s*</pre>",
					Pattern.DOTALL);

	/**
	 * Enumerate all messages on the list, and return them as an ArrayList.
	 */
	@Override
	protected ArrayList<MailMessage> EnumerateMessages() {
		ArrayList<MailMessage> messages = new ArrayList<MailMessage>();

		// Fetcha list of all the tokens in "consult" mode
		String page = FetchUrl(String.format(
				"%s?passw=%s&list=%s&func=showtokens-consult", rooturl,
				password, listname));

		Matcher m = enumMailPattern.matcher(page);
		while (m.find()) {
			/*
			 * Majordomo2, in it's infinite wisdom, doesn't include the subject
			 * line on the list. So we need to fetch the actual message contents
			 * once for each to get it.
			 */
			String url = String.format(
					"%s?passw=%s&list=%s&func=tokeninfo&extra=%s", rooturl,
					password, listname, m.group(1));
			String subpage = FetchUrl(url);
			if (subpage == null) {
				/*
				 * No tokeinfo returned here. Just ignore this message - maybe
				 * somebody moderated it while we were looking at others.
				 */
				continue;
			}
			Matcher sm = mailDetailsPattern.matcher(subpage);
			if (sm.find()) {
				messages.add(new Majordomo2Message(m.group(1), sm.group(1), sm
						.group(2), sm.group(3)));
			}

		}
		return messages;
	}

	/**
	 * Extremely trivial implementation of decoding some HTML escapes for nicer
	 * viewing.
	 */
	private static String trivialDecode(String s) {
		return s.replaceAll("&quot;", "\"").replaceAll("&lt;", "<").replaceAll(
				"&gt;", ">");
	}

	/**
	 * In majordomo2 we moderate each message individually.
	 */
	@Override
	public boolean doesIndividualModeration() {
		return true;
	}

	/**
	 * Apply any moderations to this list.
	 */
	@Override
	public boolean applyChanges(ListServerStatusCallbacks callbacks) {
		/*
		 * Collect all the messages we're actually going to do moderation on in
		 * it's own list.
		 */
		ArrayList<Majordomo2Message> msglist = new ArrayList<Majordomo2Message>();

		for (int i = 0; i < messages.size(); i++) {
			Majordomo2Message msg = (Majordomo2Message) messages.get(i);
			if (msg.getStatus() != statuslevel.Defer) {
				msglist.add(msg);
			}
		}
		if (msglist.size() == 0)
			/*
			 * Should never happen, but just in case, so we don't construct a
			 * bad URL
			 */
			return false;

		/*
		 * Now that we know how many, moderate each individual one.
		 */
		for (int i = 0; i < msglist.size(); i++) {
			callbacks.SetStatusMessage(String.format(
					"Moderating message %d of %d", i, msglist.size()));
			callbacks.SetProgressbarPercent(i * 100 / msglist.size());

			try {
				FetchUrl(String.format("%s?passw=%s&list=%s&func=%s&extra=%s",
						rooturl, password, listname, msglist.get(i)
								.getMajordomoFunc(), msglist.get(i).token));
			} catch (Exception ex) {
				callbacks.ShowError(ex.toString());
				return false;
			}
		}

		// Make sure we exit with a full progressbar.
		callbacks.SetProgressbarPercent(100);

		return true;
	}

	/**
	 * Implementation of MailMessage holding additional majordomo2-specific
	 * properties.
	 */
	private class Majordomo2Message extends MailMessage {
		private String token;

		public Majordomo2Message(String token, String sender, String subject,
				String content) {
			super(trivialDecode(sender), trivialDecode(subject), content);
			this.token = token;
		}

		/**
		 * Map the status code to the text representation to be used on a
		 * Majordomo2 form
		 */
		public String getMajordomoFunc() {
			switch (getStatus()) {
			case Accept:
				return "accept";
			case Reject:
				return "reject-quiet"; // configurable in the future to allow
										// non-quiet?
			}
			return "thisisnotafunctionandshouldneverbecalled";
		}
	}
}