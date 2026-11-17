package com.xdel.luke.debugtools;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Provider;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.net.SocketFactory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * R7 Stage 1.5: local JVM tests for the Debug-only FakeSmtpTransport and TransportIsolationGuard.
 * Every test Session comes from newSession(): its Authenticator counts credential requests, and its smtp/smtps
 * socket factory is TrapSocketFactory, which counts and refuses every socket request without creating a socket
 * (fallback to a plain socket disabled). tearDown() requires both counts to be zero, so reaching real SMTP code
 * or the Authenticator path of Service.connect (host lookup and credential request) fails the test instead of
 * touching the network. Messages use only .invalid addresses and never go through Mail or objGlobalMethods.
 * The recorder is a FakeEmailTransport in a JUnit TemporaryFolder with a counting process killer.
 */
public class FakeSmtpTransportTest {

    private static final String REAL_SMTP_CLASS = "com.sun.mail.smtp.SMTPTransport";
    private static final String PROVIDERS_RESOURCE = "META-INF/javamail.providers";
    private static final String R7_VENDOR = "LUKE R7 Debug";
    private static final String R7_VERSION = "1.5";
    private static final String SENDER = "r7-sender@invalid";
    private static final String TO = "r7-to@invalid";
    private static final String NO_NETWORK_HOST = "r7-no-network.invalid";
    /** SHA-256 of the three ASCII bytes "abc". */
    private static final String SHA256_ABC = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad";

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private TransportIsolationGuard.GuardAuthenticator auth;
    private AtomicInteger kills;
    private File recorderDir;
    private FakeEmailTransport recorder;

    @Before
    public void setUp() throws IOException {
        TrapSocketFactory.REQUESTS.set(0);
        auth = new TransportIsolationGuard.GuardAuthenticator();
        kills = new AtomicInteger();
        recorderDir = tmp.newFolder(FakeEmailTransport.DEBUG_DIR);
        recorder = new FakeEmailTransport(recorderDir, new Runnable() {
            @Override
            public void run() {
                kills.incrementAndGet();
            }
        });
        FakeSmtpTransport.setRecorderForTest(recorder);
    }

    @After
    public void tearDown() {
        FakeSmtpTransport.setRecorderForTest(null);
        assertEquals("a socket was requested: real SMTP code was reached", 0, TrapSocketFactory.REQUESTS.get());
        assertEquals("the Authenticator was asked for credentials", 0, auth.calls());
    }

    // ---------------- provider registration and lookup ----------------

    @Test
    public void smtpAndSmtpsResolveToTheR7DebugProviderEntry() throws MessagingException {
        Session session = newSession();
        for (String protocol : new String[]{"smtp", "smtps"}) {
            Provider p = session.getProvider(protocol);
            assertSame(protocol, Provider.Type.TRANSPORT, p.getType());
            assertEquals(protocol, p.getProtocol());
            assertEquals(FakeSmtpTransport.class.getName(), p.getClassName());
            assertEquals(R7_VENDOR, p.getVendor());
            assertEquals(R7_VERSION, p.getVersion());
        }
    }

    @Test
    public void noRealSmtpProviderIsRegistered() {
        for (Provider p : newSession().getProviders()) {
            assertFalse(String.valueOf(p), p.getClassName().startsWith("com.sun.mail.smtp."));
        }
    }

    @Test
    public void protocolAndAddressLookupsCreateFakeSmtpTransport() throws MessagingException {
        Session session = newSession();
        assertSame(FakeSmtpTransport.class, session.getTransport("smtp").getClass());
        assertSame(FakeSmtpTransport.class, session.getTransport("smtps").getClass());
        assertSame(FakeSmtpTransport.class, session.getTransport(new InternetAddress("someone@invalid")).getClass());
    }

    @Test
    public void connectNeverAsksTheAuthenticatorOrRequestsASocket() throws MessagingException {
        Session session = newSession();
        for (String protocol : new String[]{"smtp", "smtps"}) {
            Transport t = session.getTransport(protocol);
            //no password and an Authenticator present: Service.connect looks up the host and asks the Authenticator
            //only when protocolConnect declines, so zero Authenticator calls prove that path was not taken
            t.connect(NO_NETWORK_HOST, 25, "r7-user", null);
            assertTrue(protocol, t.isConnected());
            t.close();
        }
        assertEquals(0, auth.calls());
        assertEquals(0, TrapSocketFactory.REQUESTS.get());
    }

    // ---------------- TransportIsolationGuard ----------------

    @Test
    public void guardPassesWithoutAskingTheAuthenticator() {
        TransportIsolationGuard.verifySession(newSession(), auth);
        assertEquals(0, auth.calls());
        TransportIsolationGuard.verifyOrThrow();
    }

    @Test
    public void guardRejectsAnAdditionalRealSmtpProvider() {
        Session session = newSession();
        session.addProvider(new Provider(Provider.Type.TRANSPORT, "r7-real-smtp", REAL_SMTP_CLASS, "Sun Microsystems, Inc", "1.4.1"));
        assertGuardFails(session, auth, "real SMTP provider registered");
    }

    @Test
    public void guardRejectsADuplicateRealSmtpProviderForSmtp() throws MessagingException {
        Session session = newSession();
        session.addProvider(new Provider(Provider.Type.TRANSPORT, "smtp", REAL_SMTP_CLASS, "Sun Microsystems, Inc", "1.4.1"));
        //JavaMail keeps the first entry per protocol, so only the provider table scan can see the duplicate
        assertEquals(FakeSmtpTransport.class.getName(), session.getProvider("smtp").getClassName());
        assertGuardFails(session, auth, "real SMTP provider registered");
    }

    @Test
    public void guardRejectsARealSmtpProviderSelectedForSmtps() throws MessagingException {
        Session session = newSession();
        session.setProvider(new Provider(Provider.Type.TRANSPORT, "smtps", REAL_SMTP_CLASS, "Sun Microsystems, Inc", "1.4.1"));
        assertEquals(REAL_SMTP_CLASS, session.getProvider("smtps").getClassName());
        //the class check runs before getTransport, so the real transport is never instantiated
        assertGuardFails(session, auth, "smtps: provider class is " + REAL_SMTP_CLASS);
    }

    @Test
    public void guardRejectsWrongVendorOrVersion() throws MessagingException {
        String[][] entries = {{"Other Vendor", R7_VERSION}, {R7_VENDOR, "1.4"}};
        for (String[] e : entries) {
            Session session = newSession();
            session.setProvider(new Provider(Provider.Type.TRANSPORT, "smtp", FakeSmtpTransport.class.getName(), e[0], e[1]));
            //setProvider also sets mail.smtp.class, which getProvider resolves to the original entry of that class;
            //removing it lets the protocol table, and so the altered entry, decide
            session.getProperties().remove("mail.smtp.class");
            assertEquals(e[0], session.getProvider("smtp").getVendor());
            assertEquals(e[1], session.getProvider("smtp").getVersion());
            assertGuardFails(session, auth, "smtp: provider entry is not the R7 Debug entry");
        }
    }

    @Test
    public void guardRejectsAProviderThatIsNotATransport() throws MessagingException {
        Session session = newSession();
        session.setProvider(new Provider(Provider.Type.STORE, "smtp", FakeSmtpTransport.class.getName(), R7_VENDOR, R7_VERSION));
        session.getProperties().remove("mail.smtp.class");
        assertSame(Provider.Type.STORE, session.getProvider("smtp").getType());
        assertGuardFails(session, auth, "smtp: provider is not a transport");
    }

    @Test
    public void guardRejectsAWrongTransportClass() throws MessagingException {
        Session session = newSession();
        session.setProvider(new Provider(Provider.Type.TRANSPORT, "smtp", WrongTransport.class.getName(), R7_VENDOR, R7_VERSION));
        assertGuardFails(session, auth, "smtp: provider class is " + WrongTransport.class.getName());
    }

    @Test
    public void guardRejectsAWrongTransportInstanceOnTheAddressRoute() throws MessagingException {
        Session session = newSession();
        session.addProvider(new Provider(Provider.Type.TRANSPORT, "r7-wrong", WrongTransport.class.getName(), R7_VENDOR, R7_VERSION));
        session.setProtocolForAddress("rfc822", "r7-wrong");
        assertSame(WrongTransport.class, session.getTransport(new InternetAddress("someone@invalid")).getClass());
        assertGuardFails(session, auth, "rfc822 address transport is " + WrongTransport.class.getName());
    }

    @Test
    public void guardRejectsASessionWhoseAuthenticatorWasAlreadyAsked() {
        TransportIsolationGuard.GuardAuthenticator used = new TransportIsolationGuard.GuardAuthenticator();
        Session session = newSession(used);
        assertNull(used.getPasswordAuthentication());
        assertEquals(1, used.calls());
        assertGuardFails(session, used, "Authenticator was asked for credentials");
    }

    // ---------------- start-up ----------------

    @Test
    public void debugTestHooksInstallWithNullReturnsWithoutARecorder() {
        DebugTestHooks.install(null);
        assertNull(DebugTestHooks.fakeEmailTransport());
    }

    @Test
    public void verifyOrThrowFailsWhenARealSmtpProviderFileIsLoadedFirst() throws Exception {
        ClassLoader original = loadRealProviderFileFirst();
        try {
            //precondition: JavaMail really reads the extra file first, so the failure below is not vacuous
            assertEquals(REAL_SMTP_CLASS, Session.getInstance(new Properties()).getProvider("smtp").getClassName());
            try {
                TransportIsolationGuard.verifyOrThrow();
                fail("expected IsolationFailure");
            } catch (TransportIsolationGuard.IsolationFailure e) {
                assertTrue(e.getMessage(), e.getMessage().contains("smtp: provider class is " + REAL_SMTP_CLASS));
            }
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }

    @Test
    public void debugTestHooksInstallDoesNotConsultJavaMail() throws Exception {
        ClassLoader original = loadRealProviderFileFirst();
        try {
            //precondition: a real SMTP provider is registered first, which the guard would reject
            assertEquals(REAL_SMTP_CLASS, Session.getInstance(new Properties()).getProvider("smtp").getClassName());
            //the distributed Debug APK keeps real SMTP: install() must not run the isolation guard
            DebugTestHooks.install(null);
            assertNull(DebugTestHooks.fakeEmailTransport());
        } finally {
            Thread.currentThread().setContextClassLoader(original);
        }
    }

    // ---------------- Transport.send through FakeSmtpTransport ----------------

    @Test
    public void transportSendRecordsAMailStyleMessageWithAttachmentsInOrder() throws Exception {
        File first = attachment("report-1.csv", "abc");
        File second = attachment("report-2.csv", "abcd");
        MimeMessage msg = message("R7 subject");
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO));
        //same structure as Mail: FileDataSource attachments first, then one text/plain part
        msg.setContent(multipart(filePart(first, "report-1.csv"), filePart(second, "report-2.csv"), textPart("Body line")));
        Transport.send(msg);

        String r = onlyRecord();
        assertContains(r, "result: ACCEPTED\n");
        assertContains(r, "to: [" + TO + "]\n");
        assertContains(r, "subject: R7 subject\n");
        assertContains(r, "attachmentCount: 2\n");
        assertContains(r, "attachment.0.name: report-1.csv\n");
        assertContains(r, "attachment.0.path: " + first.getPath() + "\n");
        assertContains(r, "attachment.0.exists: true\n");
        assertContains(r, "attachment.0.bytes: 3\n");
        assertContains(r, "attachment.0.sha256: " + SHA256_ABC + "\n");
        assertContains(r, "attachment.1.name: report-2.csv\n");
        assertContains(r, "attachment.1.path: " + second.getPath() + "\n");
        assertContains(r, "attachment.1.bytes: 4\n");
        assertContains(r, "bodyChars: 9\n");
        assertContains(r, "body:\nBody line\n");
        assertTrue(r, r.endsWith(FakeEmailTransport.END_MARKER + "\n"));
        assertEquals(0, kills.get());
    }

    @Test
    public void envelopeRecipientsAreToCcAndBccInOrder() throws Exception {
        MimeMessage msg = message("R7 recipients");
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("r7-to1@invalid, r7-to2@invalid"));
        msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse("r7-cc@invalid"));
        msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse("r7-bcc@invalid"));
        msg.setText("Body");
        Transport.send(msg);

        String r = onlyRecord();
        assertContains(r, "to: [r7-to1@invalid, r7-to2@invalid, r7-cc@invalid, r7-bcc@invalid]\n");
        assertContains(r, "attachmentCount: 0\n");
        assertContains(r, "body:\nBody\n");
    }

    @Test
    public void attachmentFileNamesAreRecordedAsTextOnly() throws Exception {
        File file = attachment("actual.csv", "abc");
        String pathLikeName = "/storage/emulated/0/LUKE/../r7/named.csv";
        MimeMessage msg = message("R7 names");
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO));
        msg.setContent(multipart(filePart(file, pathLikeName), textPart("Body")));
        Transport.send(msg);

        String r = onlyRecord();
        assertContains(r, "attachment.0.name: " + pathLikeName + "\n");
        assertContains(r, "attachment.0.path: " + file.getPath() + "\n");
        assertContains(r, "attachment.0.sha256: " + SHA256_ABC + "\n");
        //the name selected no file: the recorder directory holds only its outbox
        assertArrayEquals(new String[]{FakeEmailTransport.OUTBOX_DIR}, recorderDir.list());
    }

    @Test
    public void missingAttachmentFailsTheSendAndIsRecordedWithoutKill() throws Exception {
        writeMode(FakeEmailTransport.MODE_ACCEPT_THEN_KILL);
        File present = attachment("present.csv", "abc");
        File missing = attachment("missing.csv", "abc");
        MimeMessage msg = message("R7 missing");
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO));
        msg.setContent(multipart(filePart(present, "present.csv"), filePart(missing, "missing.csv"), textPart("Body")));
        assertTrue(missing.delete());

        assertSendFails(msg, "message not accepted (ATTACHMENT_MISSING)");
        String r = onlyRecord();
        assertContains(r, "result: FAILED_ATTACHMENT_MISSING\n");
        assertContains(r, "attachment.0.exists: true\n");
        assertContains(r, "attachment.1.exists: false\n");
        assertEquals(0, kills.get());
        assertTrue(new File(recorderDir, FakeEmailTransport.MODE_FILE).isFile());
    }

    @Test
    public void unsupportedMimePartFailsTheSendAndIsNotRecorded() throws Exception {
        MimeBodyPart html = new MimeBodyPart();
        html.setContent("<b>R7</b>", "text/html");
        MimeMessage msg = message("R7 html");
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO));
        msg.setContent(multipart(textPart("Body"), html));

        assertSendFails(msg, "unsupported MIME structure");
        assertTrue(recorder.listRecords().isEmpty());
        assertEquals(0, kills.get());
    }

    @Test
    public void failModeMakesTheSendFailAfterRecording() throws Exception {
        writeMode(FakeEmailTransport.MODE_FAIL);
        assertSendFails(textMessage("R7 fail"), "message not accepted (FAILED_MODE)");
        assertContains(onlyRecord(), "result: FAILED\n");
    }

    @Test
    public void acceptThenKillThroughTransportSendRunsTheInjectedKillerOnce() throws Exception {
        writeMode(FakeEmailTransport.MODE_ACCEPT_THEN_KILL);
        Transport.send(textMessage("R7 kill 1"));
        assertEquals(1, kills.get());
        assertFalse(new File(recorderDir, FakeEmailTransport.MODE_FILE).exists());

        Transport.send(textMessage("R7 kill 2"));
        assertEquals(1, kills.get());
        assertEquals(2, recorder.listRecords().size());
    }

    @Test
    public void sendFailsWhenNoRecorderIsAvailable() throws Exception {
        FakeSmtpTransport.setRecorderForTest(null);
        //precondition: DebugTestHooks has no application in a JVM test, so it supplies no recorder either
        assertNull(DebugTestHooks.fakeEmailTransport());
        assertSendFails(textMessage("R7 no recorder"), "recorder unavailable");
        assertTrue(recorder.listRecords().isEmpty());
    }

    // ---------------- helpers ----------------

    private Session newSession() {
        return newSession(auth);
    }

    /** Session with the no-network traps; mail.from avoids the local host lookup JavaMail does for Message-IDs. */
    private static Session newSession(Authenticator authenticator) {
        Properties props = new Properties();
        props.setProperty("mail.from", SENDER);
        for (String protocol : new String[]{"smtp", "smtps"}) {
            String prefix = "mail." + protocol + ".";
            props.setProperty(prefix + "host", NO_NETWORK_HOST);
            props.setProperty(prefix + "port", "1");
            props.setProperty(prefix + "auth", "true");
            props.setProperty(prefix + "socketFactory.class", TrapSocketFactory.class.getName());
            props.setProperty(prefix + "socketFactory.fallback", "false");
        }
        return Session.getInstance(props, authenticator);
    }

    private MimeMessage message(String subject) throws MessagingException {
        MimeMessage msg = new MimeMessage(newSession());
        msg.setFrom(new InternetAddress(SENDER));
        msg.setSubject(subject);
        return msg;
    }

    private MimeMessage textMessage(String subject) throws MessagingException {
        MimeMessage msg = message(subject);
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO));
        msg.setText("Body");
        return msg;
    }

    private File attachment(String name, String content) throws IOException {
        File f = tmp.newFile(name);
        Files.write(f.toPath(), content.getBytes(StandardCharsets.US_ASCII));
        return f;
    }

    /** Attachment part built the way Mail.addAttachment builds it, with the given file name. */
    private static MimeBodyPart filePart(File file, String fileName) throws MessagingException {
        MimeBodyPart part = new MimeBodyPart();
        part.setDataHandler(new DataHandler(new FileDataSource(file.getPath())));
        part.setFileName(fileName);
        return part;
    }

    private static MimeBodyPart textPart(String text) throws MessagingException {
        MimeBodyPart part = new MimeBodyPart();
        part.setText(text);
        return part;
    }

    private static MimeMultipart multipart(MimeBodyPart... parts) throws MessagingException {
        MimeMultipart mp = new MimeMultipart();
        for (MimeBodyPart p : parts) {
            mp.addBodyPart(p);
        }
        return mp;
    }

    private void writeMode(String text) throws IOException {
        Files.write(new File(recorderDir, FakeEmailTransport.MODE_FILE).toPath(), text.getBytes(StandardCharsets.US_ASCII));
    }

    private String onlyRecord() throws IOException {
        List<File> records = recorder.listRecords();
        assertEquals(1, records.size());
        return new String(Files.readAllBytes(records.get(0).toPath()), StandardCharsets.UTF_8);
    }

    private static void assertContains(String text, String expected) {
        assertTrue(text, text.contains(expected));
    }

    private static void assertSendFails(Message msg, String expected) {
        try {
            Transport.send(msg);
        } catch (MessagingException e) {
            assertTrue(e.getMessage(), e.getMessage().contains(expected));
            return;
        }
        fail("expected MessagingException: " + expected);
    }

    private static void assertGuardFails(Session session, TransportIsolationGuard.GuardAuthenticator a, String expected) {
        try {
            TransportIsolationGuard.verifySession(session, a);
        } catch (TransportIsolationGuard.IsolationFailure e) {
            assertTrue(e.getMessage(), e.getMessage().contains(expected));
            return;
        }
        fail("expected IsolationFailure: " + expected);
    }

    /** Makes Sessions created on this thread read a provider file naming the real SMTP transport before the Debug one. */
    private ClassLoader loadRealProviderFileFirst() throws IOException {
        File f = tmp.newFile("real-javamail.providers");
        String entry = "protocol=smtp; type=transport; class=" + REAL_SMTP_CLASS + "; vendor=Sun Microsystems, Inc;\n";
        Files.write(f.toPath(), entry.getBytes(StandardCharsets.US_ASCII));
        Thread thread = Thread.currentThread();
        ClassLoader original = thread.getContextClassLoader();
        ClassLoader parent = original != null ? original : getClass().getClassLoader();
        thread.setContextClassLoader(new ProviderFileFirstLoader(parent, f.toURI().toURL()));
        return original;
    }

    /** Context class loader that lists one extra javamail.providers file before those on the class path. */
    private static final class ProviderFileFirstLoader extends ClassLoader {
        private final URL first;

        ProviderFileFirstLoader(ClassLoader parent, URL first) {
            super(parent);
            this.first = first;
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            Enumeration<URL> rest = super.getResources(name);
            if (!PROVIDERS_RESOURCE.equals(name)) {
                return rest;
            }
            List<URL> urls = new ArrayList<>();
            urls.add(first);
            urls.addAll(Collections.list(rest));
            return Collections.enumeration(urls);
        }
    }

    /** Transport that is not FakeSmtpTransport; it is only instantiated, never connected. */
    public static final class WrongTransport extends Transport {
        public WrongTransport(Session session, URLName urlname) {
            super(session, urlname);
        }

        @Override
        public void sendMessage(Message msg, Address[] addresses) throws MessagingException {
            throw new MessagingException("R7 test: WrongTransport never sends");
        }
    }

    /**
     * Socket factory named by every test Session for smtp and smtps. JavaMail's SocketFetcher, used only by the real
     * SMTP transport, loads it by name and calls getDefault(); every socket request is counted and refused, and
     * no socket is ever created.
     */
    public static final class TrapSocketFactory extends SocketFactory {
        static final AtomicInteger REQUESTS = new AtomicInteger();

        public static SocketFactory getDefault() {
            return new TrapSocketFactory();
        }

        @Override
        public Socket createSocket() throws IOException {
            throw refuse();
        }

        @Override
        public Socket createSocket(String host, int port) throws IOException {
            throw refuse();
        }

        @Override
        public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
            throw refuse();
        }

        @Override
        public Socket createSocket(InetAddress host, int port) throws IOException {
            throw refuse();
        }

        @Override
        public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
            throw refuse();
        }

        private static IOException refuse() {
            REQUESTS.incrementAndGet();
            return new IOException("R7 test: socket request refused");
        }
    }
}
