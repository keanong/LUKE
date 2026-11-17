package com.xdel.luke.debugtools;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Provider;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;

/**
 * DEBUG BUILD ONLY. Fail-closed proof that JavaMail cannot reach a real SMTP server in this process.
 * Called first in DebugTestHooks.install(), outside any catch, so a failure propagates out of
 * myApp.onCreate() and the Debug application does not start.
 * It passes only if, in a fresh Session built the same way as Mail's (own properties, own Authenticator):
 *   - "smtp" and "smtps" both resolve to the R7 Debug provider entry for FakeSmtpTransport
 *     (type transport, matching protocol, class, vendor and version from META-INF/javamail.providers),
 *   - getTransport("smtp"), getTransport("smtps") and getTransport(rfc822 address), the route used by
 *     Transport.send, all create a FakeSmtpTransport instance,
 *   - no provider with a com.sun.mail.smtp class is registered at all (JavaMail loads its built-in SMTP
 *     defaults only when no provider file was found),
 *   - the Authenticator was never asked for credentials.
 * Nothing here connects: transports are only created, never connected, so no host lookup, no socket and no
 * Authenticator request can happen. They are not closed either, because close() of an unconnected transport
 * has nothing to release and must not run real SMTP code if a check has already failed.
 */
final class TransportIsolationGuard {

    static final String FAKE_CLASS = FakeSmtpTransport.class.getName();
    static final String EXPECTED_VENDOR = "LUKE R7 Debug";
    static final String EXPECTED_VERSION = "1.5";
    static final String REAL_SMTP_PACKAGE = "com.sun.mail.smtp.";
    static final String CHECK_ADDRESS = "r7-isolation-check@invalid";
    private static final String[] PROTOCOLS = {"smtp", "smtps"};
    private static final String FAILURE = "R7 Debug transport isolation not verified; refusing to start";

    private TransportIsolationGuard() {
    }

    /** Throws IllegalStateException unless every JavaMail SMTP route in this process resolves to FakeSmtpTransport. */
    static void verifyOrThrow() {
        GuardAuthenticator authenticator = new GuardAuthenticator();
        Session session;
        try {
            session = Session.getInstance(new Properties(), authenticator);
        } catch (Throwable t) {
            throw new IsolationFailure("JavaMail session could not be created: " + t, t);
        }
        verifySession(session, authenticator);
    }

    /** Package-private for JVM tests, which alter the provider table of their own Session to prove each failure. */
    static void verifySession(Session session, GuardAuthenticator authenticator) {
        try {
            for (String protocol : PROTOCOLS) {
                Provider p = session.getProvider(protocol);
                require(p != null, protocol + ": no provider registered");
                require(p.getType() == Provider.Type.TRANSPORT, protocol + ": provider is not a transport (" + p + ")");
                require(protocol.equals(p.getProtocol()), protocol + ": provider protocol is " + p.getProtocol());
                require(FAKE_CLASS.equals(p.getClassName()), protocol + ": provider class is " + p.getClassName());
                require(EXPECTED_VENDOR.equals(p.getVendor()) && EXPECTED_VERSION.equals(p.getVersion()),
                        protocol + ": provider entry is not the R7 Debug entry (" + p + ")");
                requireFakeInstance(session.getTransport(protocol), protocol + " transport");
            }
            requireFakeInstance(session.getTransport(new InternetAddress(CHECK_ADDRESS)), "rfc822 address transport");

            Provider[] all = session.getProviders();
            require(all != null, "provider table unavailable");
            for (Provider p : all) {
                String className = p == null ? null : p.getClassName();
                require(className == null || !className.startsWith(REAL_SMTP_PACKAGE),
                        "real SMTP provider registered (" + p + ")");
            }
        } catch (IsolationFailure e) {
            throw e;
        } catch (Throwable t) {
            throw new IsolationFailure("JavaMail provider check failed: " + t, t);
        }
        require(authenticator == null || authenticator.calls() == 0, "Authenticator was asked for credentials");
    }

    private static void requireFakeInstance(Transport t, String what) {
        require(t != null && t.getClass() == FakeSmtpTransport.class,
                what + " is " + (t == null ? "null" : t.getClass().getName()));
    }

    private static void require(boolean condition, String reason) {
        if (!condition) {
            throw new IsolationFailure(reason, null);
        }
    }

    /** Distinguishes the guard's own failures from unexpected exceptions, which are wrapped into it. */
    static final class IsolationFailure extends IllegalStateException {
        IsolationFailure(String reason, Throwable cause) {
            super(FAILURE + ": " + reason, cause);
        }
    }

    /** Must never be called: the guard only creates transports and never connects them. */
    static final class GuardAuthenticator extends Authenticator {
        private final AtomicInteger calls = new AtomicInteger();

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            calls.incrementAndGet();
            return null;
        }

        int calls() {
            return calls.get();
        }
    }
}
