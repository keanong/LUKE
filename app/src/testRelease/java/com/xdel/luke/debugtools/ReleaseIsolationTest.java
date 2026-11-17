package com.xdel.luke.debugtools;

import android.app.Application;

import com.xdel.luke.ExceptionHandler.R7CrashTestPoints;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.mail.NoSuchProviderException;
import javax.mail.Provider;
import javax.mail.Session;
import javax.mail.Transport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * R7 Stage 1: local JVM tests run only against the Release variant (src/main + src/release).
 * Debug-only classes are looked up by name, never referenced directly, so this file compiles in Release
 * and proves those classes are absent. No Android method is called; Application is used only as a type.
 * R7 Stage 1.5: also proves the fake SMTP transport, its isolation guard and the Debug JavaMail provider file
 * are absent, and that Release JavaMail resolves smtp and smtps to its own com.sun.mail.smtp transports.
 * Those checks read provider metadata only: no Transport is created or connected, so nothing touches the network.
 * R7 A7 Stage 2: also proves the Release R7CrashTestPoints is a stateless, empty at(String) whose
 * compiled class names no marker file, process kill, Android type or Debug-only class.
 */
public class ReleaseIsolationTest {

    private static final String PKG = "com.xdel.luke.debugtools.";
    private static final String PROVIDERS_RESOURCE = "META-INF/javamail.providers";
    private static final String DEBUG_VENDOR = "LUKE R7 Debug";

    @Test
    public void testsRunAgainstTheReleaseVariant() {
        //guards against this file ever being compiled into a Debug test run by mistake
        assertFalse(com.xdel.luke.BuildConfig.DEBUG);
    }

    @Test
    public void fakeEmailTransportIsAbsentFromRelease() {
        assertClassAbsent(PKG + "FakeEmailTransport");
    }

    @Test
    public void crashTriggerIsAbsentFromRelease() {
        assertClassAbsent(PKG + "R7DebugCrashTrigger");
        assertClassAbsent(PKG + "R7DebugCrashTrigger$R7DebugCrashException");
    }

    @Test
    public void releaseDebugTestHooksExistsAndIsFinal() throws ClassNotFoundException {
        Class<?> c = Class.forName(PKG + "DebugTestHooks", false, loader());
        assertEquals(DebugTestHooks.class, c);
        assertTrue(Modifier.isPublic(c.getModifiers()));
        assertTrue(Modifier.isFinal(c.getModifiers()));
    }

    @Test
    public void releaseDebugTestHooksDeclaresOnlyInstall() throws NoSuchMethodException {
        List<Method> declared = new ArrayList<>();
        for (Method m : DebugTestHooks.class.getDeclaredMethods()) {
            if (!m.isSynthetic()) {
                declared.add(m);
            }
        }
        assertEquals("Release DebugTestHooks must declare exactly one method", 1, declared.size());

        Method install = declared.get(0);
        assertEquals("install", install.getName());
        assertEquals(void.class, install.getReturnType());
        assertEquals(1, install.getParameterTypes().length);
        assertEquals(Application.class, install.getParameterTypes()[0]);
        assertTrue(Modifier.isPublic(install.getModifiers()));
        assertTrue(Modifier.isStatic(install.getModifiers()));
        assertEquals(0, install.getExceptionTypes().length);
        assertEquals(install, DebugTestHooks.class.getMethod("install", Application.class));
    }

    @Test
    public void releaseDebugTestHooksHasNoFakeEmailTransportAccessor() {
        try {
            DebugTestHooks.class.getMethod("fakeEmailTransport");
            fail("Release DebugTestHooks must not expose fakeEmailTransport()");
        } catch (NoSuchMethodException expected) {
            //expected
        }
        for (Method m : DebugTestHooks.class.getMethods()) {
            assertFalse("unexpected public method " + m.getName(), m.getName().equals("fakeEmailTransport"));
        }
    }

    @Test
    public void releaseDebugTestHooksHoldsNoStateAndCannotBeInstantiated() {
        for (Field f : DebugTestHooks.class.getDeclaredFields()) {
            assertTrue("unexpected field " + f.getName(), f.isSynthetic());
        }
        Constructor<?>[] constructors = DebugTestHooks.class.getDeclaredConstructors();
        assertEquals(1, constructors.length);
        assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
    }

    @Test
    public void installWithNullCompletesWithoutThrowing() {
        try {
            DebugTestHooks.install(null);
        } catch (Throwable t) {
            fail("Release install(null) must do nothing, but threw " + t);
        }
    }

    // ---------------- R7 Stage 1.5: fake SMTP transport isolation ----------------

    @Test
    public void fakeSmtpTransportIsAbsentFromRelease() {
        assertClassAbsent(PKG + "FakeSmtpTransport");
    }

    @Test
    public void transportIsolationGuardIsAbsentFromRelease() {
        assertClassAbsent(PKG + "TransportIsolationGuard");
        assertClassAbsent(PKG + "TransportIsolationGuard$IsolationFailure");
        assertClassAbsent(PKG + "TransportIsolationGuard$GuardAuthenticator");
    }

    @Test
    public void debugJavaMailProviderResourceIsAbsentFromRelease() throws IOException {
        for (ClassLoader cl : resourceLoaders()) {
            assertNull(PROVIDERS_RESOURCE + " visible through " + cl, cl.getResource(PROVIDERS_RESOURCE));
            //Collections.list drains the whole Enumeration, so every match on the classpath is counted
            List<URL> found = Collections.list(cl.getResources(PROVIDERS_RESOURCE));
            assertEquals(PROVIDERS_RESOURCE + " entries visible through " + cl + ": " + found, 0, found.size());
        }
    }

    @Test
    public void releaseSmtpResolvesToTheRealJavaMailTransport() throws NoSuchProviderException {
        Session session = Session.getInstance(new Properties());
        assertRealTransportProvider(session.getProvider("smtp"), "smtp", "com.sun.mail.smtp.SMTPTransport");
    }

    @Test
    public void releaseSmtpsResolvesToTheRealJavaMailSslTransport() throws NoSuchProviderException {
        Session session = Session.getInstance(new Properties());
        assertRealTransportProvider(session.getProvider("smtps"), "smtps", "com.sun.mail.smtp.SMTPSSLTransport");
    }

    @Test
    public void noRegisteredJavaMailProviderIsTheR7DebugEntry() {
        Provider[] all = Session.getInstance(new Properties()).getProviders();
        assertNotNull("JavaMail provider table", all);
        assertTrue("JavaMail registered no providers", all.length > 0);
        for (Provider p : all) {
            assertNotNull("null JavaMail provider entry", p);
            assertFalse("R7 Debug provider registered: " + p, DEBUG_VENDOR.equals(p.getVendor()));
            String className = p.getClassName();
            assertFalse("provider class from the LUKE debugtools package: " + p,
                    className != null && className.startsWith(PKG));
        }
    }

    // ---------------- R7 A7 Stage 2: crash test points ----------------

    @Test
    public void releaseCrashTestPointsIsFinalAndDeclaresOnlyAt() throws NoSuchMethodException {
        Class<?> c = R7CrashTestPoints.class;
        assertTrue(Modifier.isPublic(c.getModifiers()));
        assertTrue(Modifier.isFinal(c.getModifiers()));
        List<Method> declared = new ArrayList<>();
        for (Method m : c.getDeclaredMethods()) {
            if (!m.isSynthetic()) {
                declared.add(m);
            }
        }
        assertEquals("Release R7CrashTestPoints must declare exactly one method", 1, declared.size());
        Method at = declared.get(0);
        assertEquals("at", at.getName());
        assertEquals(void.class, at.getReturnType());
        assertEquals(1, at.getParameterTypes().length);
        assertEquals(String.class, at.getParameterTypes()[0]);
        assertTrue(Modifier.isPublic(at.getModifiers()));
        assertTrue(Modifier.isStatic(at.getModifiers()));
        assertEquals(0, at.getExceptionTypes().length);
        assertEquals(at, c.getMethod("at", String.class));
    }

    @Test
    public void releaseCrashTestPointsHoldsNoStateAndCannotBeInstantiated() {
        assertEquals(0, R7CrashTestPoints.class.getDeclaredFields().length);
        Constructor<?>[] constructors = R7CrashTestPoints.class.getDeclaredConstructors();
        assertEquals(1, constructors.length);
        assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
    }

    @Test
    public void releaseCrashTestPointsAtDoesNothingForEveryPoint() {
        String[] points = {"W0", "W1", "W3", "S4a", "S5a", "S6a", "", null};
        for (String p : points) {
            try {
                R7CrashTestPoints.at(p);
            } catch (Throwable t) {
                fail("Release at(" + p + ") must do nothing, but threw " + t);
            }
        }
    }

    @Test
    public void releaseCrashTestPointsReferencesNoMarkerKillOrDebugCode() throws IOException {
        //every class, method and string constant the class uses is named in its constant pool
        String code = new String(classBytes(R7CrashTestPoints.class), StandardCharsets.ISO_8859_1);
        String[] forbidden = {"crash_kill_at", "r7_debug", "killProcess", "myPid", "android/",
                "BuildConfig", "com/xdel/luke/objClasses/myApp", "com/xdel/luke/debugtools/",
                "java/io/File", "java/nio/file/"};
        for (String s : forbidden) {
            assertFalse("Release R7CrashTestPoints refers to " + s, code.contains(s));
        }
    }

    // ---------------- helpers ----------------

    private static ClassLoader loader() {
        return ReleaseIsolationTest.class.getClassLoader();
    }

    /** Fails unless the class cannot be found; initialize=false so nothing is executed even if it existed. */
    private static void assertClassAbsent(String name) {
        try {
            Class.forName(name, false, loader());
            fail(name + " must not be present in the Release build");
        } catch (ClassNotFoundException expected) {
            //expected
        }
    }

    /**
     * Provider metadata checks only. The class is loaded with initialize=false to confirm it is a real Transport
     * on the Release classpath, so none of its code runs and no Transport instance is created.
     */
    private static void assertRealTransportProvider(Provider p, String protocol, String className) {
        assertNotNull(protocol + ": no provider registered", p);
        assertEquals(protocol + ": provider type", Provider.Type.TRANSPORT, p.getType());
        assertEquals(protocol + ": provider protocol", protocol, p.getProtocol());
        assertEquals(protocol + ": provider class", className, p.getClassName());
        assertFalse(protocol + ": provider is the R7 Debug entry", DEBUG_VENDOR.equals(p.getVendor()));
        try {
            Class<?> c = Class.forName(className, false, loader());
            assertTrue(protocol + ": " + className + " is not a javax.mail.Transport", Transport.class.isAssignableFrom(c));
        } catch (ClassNotFoundException e) {
            fail(protocol + ": " + className + " is not on the Release classpath");
        }
    }

    /** Every class loader JavaMail 1.4.1 may read META-INF resources from, without duplicates. */
    private static List<ClassLoader> resourceLoaders() {
        List<ClassLoader> loaders = new ArrayList<>();
        addLoader(loaders, Thread.currentThread().getContextClassLoader());
        addLoader(loaders, loader());
        addLoader(loaders, Session.class.getClassLoader());
        addLoader(loaders, ClassLoader.getSystemClassLoader());
        return loaders;
    }

    private static void addLoader(List<ClassLoader> loaders, ClassLoader cl) {
        if (cl != null && !loaders.contains(cl)) {
            loaders.add(cl);
        }
    }

    /** The compiled class file, read from the test class path; nothing in it is executed. */
    private static byte[] classBytes(Class<?> c) throws IOException {
        String name = c.getName().replace('.', '/') + ".class";
        InputStream in = c.getClassLoader().getResourceAsStream(name);
        assertNotNull(name + " not found on the class path", in);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            for (int n; (n = in.read(buf)) != -1; ) {
                out.write(buf, 0, n);
            }
            return out.toByteArray();
        } finally {
            in.close();
        }
    }
}
