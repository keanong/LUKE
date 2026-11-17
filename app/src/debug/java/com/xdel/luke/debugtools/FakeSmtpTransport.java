package com.xdel.luke.debugtools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;

/**
 * DEBUG BUILD ONLY. JavaMail transport registered for "smtp" and "smtps" by
 * src/debug/resources/META-INF/javamail.providers, so every Transport.send(...) in a Debug build ends here
 * instead of com.sun.mail.smtp.SMTPTransport.
 * It never opens a socket, never resolves a host and never asks the Authenticator for credentials:
 * protocolConnect() returns true without doing anything, which is the only connect step Service.connect()
 * delegates to a transport.
 * sendMessage() records the message through FakeEmailTransport.deliver(...) and reports success only if the
 * recorder returns ACCEPTED; every other outcome, a missing recorder, a missing attachment file or a MIME
 * structure that LUKE's Mail class does not produce is reported as a MessagingException, so Mail.send()
 * returns false exactly as for a failed real send.
 * Message structure produced by objClasses.Mail: a Multipart whose parts are FileDataSource attachments
 * (added by addAttachment) followed by one text/plain part (added by send()).
 * Recipients are the envelope recipients passed by Transport.send (TO, CC and BCC of the message); Mail sets TO only.
 */
public final class FakeSmtpTransport extends Transport {

    private static volatile FakeEmailTransport recorderForTest;

    /** Required by JavaMail: Session creates provider instances through this (Session, URLName) constructor. */
    public FakeSmtpTransport(Session session, URLName urlname) {
        super(session, urlname);
    }

    /** JVM unit tests only: record into the given transport instead of DebugTestHooks.fakeEmailTransport(). */
    static void setRecorderForTest(FakeEmailTransport recorder) {
        recorderForTest = recorder;
    }

    /**
     * Service.connect() calls this after resolving host and user from the URL and Session properties.
     * Returning true makes Service mark the transport connected without a host lookup or an Authenticator request;
     * nothing here touches the network.
     */
    @Override
    protected boolean protocolConnect(String host, int port, String user, String password) {
        return true;
    }

    @Override
    public void sendMessage(Message msg, Address[] addresses) throws MessagingException {
        FakeEmailTransport recorder = recorderForTest != null ? recorderForTest : DebugTestHooks.fakeEmailTransport();
        if (recorder == null) {
            throw new MessagingException("R7 fake SMTP: recorder unavailable, message not sent");
        }

        String[] to;
        String subject;
        StringBuilder body = new StringBuilder();
        List<File> attachments = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();
        try {
            to = recipients(addresses);
            subject = msg.getSubject();
            Object content = readContent(msg);
            if (content instanceof String) {
                body.append((String) content);
            } else if (content instanceof Multipart) {
                Multipart mp = (Multipart) content;
                int textParts = 0;
                for (int i = 0; i < mp.getCount(); i++) {
                    BodyPart part = mp.getBodyPart(i);
                    DataHandler dh = part.getDataHandler();
                    DataSource ds = dh == null ? null : dh.getDataSource();
                    if (ds instanceof FileDataSource) {
                        //attachment added by Mail.addAttachment(path): the file itself is hashed by the recorder, never read here
                        attachments.add(((FileDataSource) ds).getFile());
                        fileNames.add(part.getFileName());
                        continue;
                    }
                    String text = textContent(part);
                    if (text == null) {
                        throw unsupported("part " + i + " with content type " + part.getContentType());
                    }
                    if (textParts > 0) {
                        body.append('\n');
                    }
                    body.append(text);
                    textParts++;
                }
            } else {
                throw unsupported("message content " + (content == null ? "null" : content.getClass().getName()));
            }
        } catch (RuntimeException e) {
            throw new MessagingException("R7 fake SMTP: message could not be parsed, message not sent", e);
        }

        FakeEmailTransport.Outcome outcome;
        try {
            outcome = recorder.deliver(to, subject, body.toString(), attachments, fileNames, true);
        } catch (RuntimeException e) {
            throw new MessagingException("R7 fake SMTP: recorder failed, message not sent", e);
        }
        if (outcome != FakeEmailTransport.Outcome.ACCEPTED) {
            throw new MessagingException("R7 fake SMTP: message not accepted (" + outcome + ")");
        }
    }

    private static String[] recipients(Address[] addresses) {
        if (addresses == null) {
            return new String[0];
        }
        String[] to = new String[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            Address a = addresses[i];
            to[i] = a instanceof InternetAddress ? ((InternetAddress) a).getAddress() : String.valueOf(a);
        }
        return to;
    }

    private static Object readContent(Message msg) throws MessagingException {
        try {
            return msg.getContent();
        } catch (IOException e) {
            throw new MessagingException("R7 fake SMTP: message content unreadable, message not sent", e);
        }
    }

    /** The text of a text/plain part, or null for any other part. */
    private static String textContent(BodyPart part) throws MessagingException {
        if (!part.isMimeType("text/plain")) {
            return null;
        }
        try {
            Object c = part.getContent();
            return c instanceof String ? (String) c : null;
        } catch (IOException e) {
            throw new MessagingException("R7 fake SMTP: text part unreadable, message not sent", e);
        }
    }

    private static MessagingException unsupported(String what) {
        return new MessagingException("R7 fake SMTP: unsupported MIME structure (" + what + "), message not sent");
    }
}
