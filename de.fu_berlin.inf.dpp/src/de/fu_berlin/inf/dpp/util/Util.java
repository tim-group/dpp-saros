package de.fu_berlin.inf.dpp.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.codec.BinaryDecoder;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.picocontainer.annotations.Nullable;

import de.fu_berlin.inf.dpp.Saros;
import de.fu_berlin.inf.dpp.User;
import de.fu_berlin.inf.dpp.editor.internal.EditorAPI;
import de.fu_berlin.inf.dpp.net.JID;

/**
 * Static Utility functions
 */
public class Util {

    private static final Logger log = Logger.getLogger(Util.class.getName());

    protected static final Base64 base64Codec = new Base64();
    protected static final URLCodec urlCodec = new URLCodec();

    protected static String escape(String toEscape, BinaryEncoder encoder) {

        byte[] toEncode;
        try {
            toEncode = toEscape.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            toEncode = toEscape.getBytes();
        }

        byte[] encoded = {};
        try {
            encoded = encoder.encode(toEncode);
        } catch (EncoderException e) {
            log.error("can not escape", e);
        }

        try {
            return new String(encoded, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            return new String(encoded);
        }
    }

    protected static String unescape(String toUnescape, BinaryDecoder decoder) {

        byte[] toDecode;
        try {
            toDecode = toUnescape.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            toDecode = toUnescape.getBytes();
        }

        byte[] decoded = {};
        try {
            decoded = decoder.decode(toDecode);
        } catch (DecoderException e) {
            log.error("can not unescape", e);
        }

        try {
            return new String(decoded, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            return new String(decoded);
        }
    }

    public static String escapeBase64(String toEscape) {
        return escape(toEscape, base64Codec);
    }

    public static String unescapeBase64(String toUnescape) {
        return unescape(toUnescape, base64Codec);
    }

    public static String urlEscape(String toEscape) {
        return escape(toEscape, urlCodec);
    }

    public static String urlUnescape(String toUnescape) {
        return unescape(toUnescape, urlCodec);
    }

    public static String escapeCDATA(String toEscape) {
        if (toEscape == null || toEscape.length() == 0) {
            return "";
        } else {
            /*
             * HACK otherwise there are problems with XMLPullParser which I
             * don't understand...
             */
            if (toEscape.endsWith("]")) {
                return escapeCDATA(toEscape.substring(0, toEscape.length() - 1))
                    + "]";
            }
            if (!toEscape.endsWith("]]>") && toEscape.endsWith("]>")) {
                return escapeCDATA(toEscape.substring(0, toEscape.length() - 2))
                    + "]&gt;";
            }

            return "<![CDATA[" + toEscape.replaceAll("]]>", "]]]><![CDATA[]>")
                + "]]>";
        }
    }

    /**
     * Obtain a free port we can use.
     * 
     * @return A free port number.
     */
    public static int getFreePort() {
        ServerSocket ss;
        int freePort = 0;

        for (int i = 0; i < 10; i++) {
            freePort = (int) (10000 + Math.round(Math.random() * 10000));
            freePort = freePort % 2 == 0 ? freePort : freePort + 1;
            try {
                ss = new ServerSocket(freePort);
                freePort = ss.getLocalPort();
                ss.close();
                return freePort;
            } catch (IOException e) {
                log.error("Error while trying to find a free port:", e);
            }
        }
        try {
            ss = new ServerSocket(0);
            freePort = ss.getLocalPort();
            ss.close();
        } catch (IOException e) {
            log.error("Error while trying to find a free port:", e);
        }
        return freePort;
    }

    public static void close(Socket socketToClose) {
        if (socketToClose == null)
            return;

        try {
            socketToClose.close();
        } catch (IOException e) {
            // ignore
        }
    }

    public static void close(Closeable closeable) {
        if (closeable == null)
            return;
        try {
            closeable.close();
        } catch (IOException e) {
            // ignore
        }
    }

    /**
     * Returns a runnable that upon being run will wait the given time in
     * milliseconds and then run the given runnable.
     * 
     * The returned runnable supports being interrupted upon which the runnable
     * returns with the interrupted flag being raised.
     * 
     */
    public static Runnable delay(final int milliseconds, final Runnable runnable) {
        return new Runnable() {
            public void run() {
                try {
                    Thread.sleep(milliseconds);
                } catch (InterruptedException e) {
                    log.error("Code not designed to be interruptable", e);
                    Thread.currentThread().interrupt();
                    return;
                }
                runnable.run();
            }
        };
    }

    /**
     * Returns a callable that upon being called will wait the given time in
     * milliseconds and then call the given callable.
     * 
     * The returned callable supports being interrupted upon which an
     * InterruptedException is being thrown.
     * 
     */
    public static <T> Callable<T> delay(final int milliseconds,
        final Callable<T> callable) {
        return new Callable<T>() {
            public T call() throws Exception {

                Thread.sleep(milliseconds);
                return callable.call();
            }
        };
    }

    public static <T> Callable<T> retryEvery500ms(final Callable<T> callable) {
        return new Callable<T>() {
            public T call() {
                T t = null;
                while (t == null && !Thread.currentThread().isInterrupted()) {
                    try {
                        t = callable.call();
                    } catch (InterruptedIOException e) {
                        // Workaround for bug in Limewire RUDP
                        // https://www.limewire.org/jira/browse/LWC-2838
                        return null;
                    } catch (InterruptedException e) {
                        log.error("Code not designed to be interruptable", e);
                        Thread.currentThread().interrupt();
                        return null;
                    } catch (Exception e) {
                        // Log here for connection problems.
                        t = null;
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e2) {
                            log.error("Code not designed to be interruptable",
                                e);
                            Thread.currentThread().interrupt();
                            return null;
                        }
                    }
                }
                return t;
            }
        };
    }

    /**
     * Returns an iterable which will return the given iterator ONCE.
     * 
     * Subsequent calls to iterator() will throw an IllegalStateException.
     * 
     * @param <T>
     * @param it
     *            an Iterator to wrap
     * @return an Iterable which returns the given iterator ONCE.
     */
    public static <T> Iterable<T> asIterable(final Iterator<T> it) {
        return new Iterable<T>() {

            boolean returned = false;

            public Iterator<T> iterator() {
                if (returned)
                    throw new IllegalStateException(
                        "Can only call iterator() once.");

                returned = true;

                return it;
            }
        };
    }

    public static <T> List<T> reverse(T[] original) {
        return reverse(Arrays.asList(original));
    }

    public static <T> List<T> reverse(List<T> original) {
        List<T> reversed = new ArrayList<T>(original);
        Collections.reverse(reversed);
        return reversed;
    }

    public static PacketFilter orFilter(final PacketFilter... filters) {

        return new PacketFilter() {

            public boolean accept(Packet packet) {

                for (PacketFilter filter : filters) {
                    if (filter.accept(packet)) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public static String read(InputStream input) throws IOException {

        try {
            byte[] content = IOUtils.toByteArray(input);

            try {
                return new String(content, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return new String(content);
            }
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * Return a new Runnable which runs the given runnable but catches all
     * RuntimeExceptions and logs them to the given log.
     * 
     * Errors are logged and re-thrown.
     * 
     * This method does NOT actually run the given runnable, but only wraps it.
     * 
     * @param log
     *            The log to print any exception messages thrown which occur
     *            when running the given runnable. If null, the {@link Util#log}
     *            is used.
     * 
     */
    public static Runnable wrapSafe(@Nullable Logger log,
        final Runnable runnable) {

        if (log == null) {
            log = Util.log;
        }
        final Logger logToUse = log;

        /*
         * TODO Use the stack frame from the call to wrapSafe to provide better
         * information who created this runnable
         */
        return new Runnable() {
            public void run() {
                try {
                    runnable.run();
                } catch (RuntimeException e) {
                    logToUse.error("Internal Error:", e);
                } catch (Error e) {
                    logToUse.error("Internal Fatal Error:", e);

                    // Re-throw errors (such as an OutOfMemoryError)
                    throw e;
                }
            }
        };
    }

    /**
     * Run the given runnable in a new thread (with the given name) and log any
     * RuntimeExceptions to the given log.
     * 
     * @nonBlocking
     */
    public static void runSafeAsync(@Nullable String name, final Logger log,
        final Runnable runnable) {

        Thread t = new Thread(wrapSafe(log, runnable));
        if (name != null)
            t.setName(name);
        t.start();
    }

    /**
     * Run the given runnable in a new thread and log any RuntimeExceptions to
     * the given log.
     * 
     * @nonBlocking
     */
    public static void runSafeAsync(final Logger log, final Runnable runnable) {
        runSafeAsync(null, log, runnable);
    }

    /**
     * Run the given runnable in the SWT-Thread, log any RuntimeExceptions to
     * the given log and block until the runnable returns.
     * 
     * @blocking
     */
    public static void runSafeSWTSync(final Logger log, final Runnable runnable) {
        Display.getDefault().syncExec(wrapSafe(log, runnable));
    }

    /**
     * Class used for reporting the results of a Callable<T> inside a different
     * thread.
     * 
     * It is kind of like a Future Light.
     */
    public static class CallableResult<T> {
        public T result;
        public Exception e;
    }

    /**
     * Runs the given callable in the SWT Thread returning the result of the
     * computation or throwing an exception that was thrown by the callable.
     */
    public static <T> T runSWTSync(final Callable<T> callable) throws Exception {

        final CallableResult<T> result = new CallableResult<T>();

        Util.runSafeSWTSync(log, new Runnable() {
            public void run() {
                try {
                    result.result = callable.call();
                } catch (Exception e) {
                    result.e = e;
                }
            }
        });

        if (result.e != null)
            throw result.e;
        else
            return result.result;
    }

    public static String escapeForLogging(String s) {
        if (s == null)
            return null;

        return StringEscapeUtils.escapeJava(s);
        /*
         * // Try to put nice symbols for non-readable characters sometime
         * return s.replace(' ', '\uc2b7').replace('\t',
         * '\uc2bb').replace('\n','\uc2b6').replace('\r', '\uc2a4');
         */
    }

    /**
     * Run the given runnable in the SWT-Thread and log any RuntimeExceptions to
     * the given log.
     * 
     * @nonBlocking
     */
    public static void runSafeSWTAsync(final Logger log, final Runnable runnable) {
        Display.getDefault().asyncExec(wrapSafe(log, runnable));
    }

    /**
     * Run the given runnable and log any RuntimeExceptions to the given log and
     * block until the runnable returns.
     * 
     * @blocking
     */
    public static void runSafeSync(Logger log, Runnable runnable) {
        wrapSafe(log, runnable).run();
    }

    /**
     * Utility method to obtain a suitable runnable context to run a long
     * running operation in.
     * 
     * @return an IRunnableContext suitable to run an IRunnableWithProgress in,
     *         its progress is either reported to the active windows status bar
     *         or shown in an ProgressMonitorDialog, if no active window could
     *         be determined, e.g. on start or stop of the plugin
     * @SWT
     */
    public static IRunnableContext getRunnableContext() {

        IWorkbenchWindow window = EditorAPI.getAWorkbenchWindow();
        if (window != null) {
            return window;
        }
        log.warn("The active workbench window couldn't"
            + " be determined, running in Progress Monitor Dialog.");

        return new ProgressMonitorDialog(EditorAPI.getShell());
    }

    /**
     * @return The nickname associated with the given JID in the current roster
     *         or null if the current roster is not available or the nickname
     *         has not been set.
     */
    public static String getNickname(Saros saros, JID jid) {

        if (saros != null) {
            XMPPConnection connection = saros.getConnection();
            if (connection != null) {
                Roster roster = connection.getRoster();
                if (roster != null) {
                    RosterEntry entry = roster.getEntry(jid.getBase());
                    if (entry != null) {
                        String nickName = entry.getName();
                        if (nickName != null && nickName.trim().length() > 0) {
                            return nickName;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String getDisplayableName(RosterEntry entry) {
        String nickName = entry.getName();
        if (nickName != null && nickName.trim().length() > 0) {
            return nickName.trim();
        }
        return entry.getUser();
    }

    /**
     * Gets the name for a {@link User} for displaying.
     * 
     * If <i>participant</i> is the local user "You" is returned, otherwise the
     * nickname, if available, and the JID. If no nickname is known, only the
     * JID is returned.
     */
    public static String getName(Saros saros, User participant) {

        if (participant.isLocal()) {
            return "You";
        }

        String nickName = getNickname(saros, participant.getJID());
        String jidBase = participant.getJID().getBase();

        if (nickName != null && nickName.trim().length() > 0) {
            return nickName + " (" + jidBase + ")";
        }

        return jidBase;
    }

    /**
     * @swt Needs to be called from the SWT-UI thread, otherwise
     *      <code>null</code> is returned.
     */
    public static IViewPart findView(String id) {
        IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null)
            return null;

        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        if (window == null)
            return null;

        IWorkbenchPage page = window.getActivePage();
        if (page == null)
            return null;

        return page.findView(id);
    }

    /**
     * Return a string representation of the given paths suitable for debugging
     * by joining their OS dependent representation by ', '
     */
    public static String toOSString(final Set<IPath> paths) {
        StringBuilder sb = new StringBuilder();
        for (IPath path : paths) {
            if (sb.length() > 0)
                sb.append(", ");

            sb.append(path.toOSString());
        }
        return sb.toString();
    }

    /**
     * Crude check whether we are on the SWT thread
     */
    public static boolean isSWT() {

        if (Display.getCurrent() != null) {
            return true;
        }
        try {
            boolean platformSWT = PlatformUI.getWorkbench().getDisplay()
                .getThread() == Thread.currentThread();
            if (platformSWT) {
                log.warn("Running in PlatformSWT Thread"
                    + " which is not found with Display.getCurrent()");
            }
            return platformSWT;
        } catch (SWTException e) {
            try {
                boolean defaultSWT = Display.getDefault().getThread() == Thread
                    .currentThread();
                if (defaultSWT) {
                    log.warn("Running in Display.getDefault() but"
                        + " PlatformUI Display is already disposed.");
                }
                return defaultSWT;
            } catch (SWTException e2) {
                return false;
            }
        }
    }

    public static String getEclipsePlatformInfo() {
        return getBundleVersion(Platform.getBundle("org.eclipse.core.runtime"),
            "Unknown Eclipse Version");
    }

    public static String getBundleVersion(Bundle bundle, String defaultValue) {
        if (bundle == null)
            return defaultValue;

        Object version = bundle.getHeaders().get(Constants.BUNDLE_VERSION);
        if (version != null) {
            return version.toString();
        } else {
            return defaultValue;
        }
    }

    public static String getPlatformInfo() {

        String javaVersion = System.getProperty("java.version",
            "Unknown Java Version");
        String javaVendor = System.getProperty("java.vendor", "Unknown Vendor");
        String os = System.getProperty("os.name", "Unknown OS");
        String osVersion = System.getProperty("os.version", "Unknown Version");
        String hardware = System.getProperty("os.arch", "Unknown Architecture");

        StringBuilder sb = new StringBuilder();

        sb.append("  Java Version: " + javaVersion + "\n");
        sb.append("  Java Vendor: " + javaVendor + "\n");
        sb.append("  Eclipse Runtime Version: " + getEclipsePlatformInfo()
            + "\n");
        sb.append("  Operating System: " + os + " (" + osVersion + ")\n");
        sb.append("  Hardware Architecture: " + hardware);

        return sb.toString();
    }

    public static String prefix(JID jid) {
        if (jid == null) {
            return "[Unknown] ";
        } else {
            return "[" + jid.toString() + "] ";
        }
    }

    /**
     * Given a *File*name, this function will ensure that if the filename
     * contains directories, these directories are created.
     * 
     * Returns true if the directory of the given file already exists or the
     * file has no parent directory.
     * 
     * Returns false, if the directory could not be created.
     */
    public static boolean mkdirs(String filename) {
        File parent = new File(filename).getParentFile();

        if (parent == null || parent.isDirectory()) {
            return true;
        }

        if (!parent.mkdirs()) {
            log.error("Could not create Dir: " + parent.getPath());
            return false;
        } else
            return true;
    }

    /**
     * Tries to open the given URL string in the default external browser. The
     * Desktop API is deliberately not used for this because it only works with
     * Java 1.6.
     * 
     * @param urlString
     *            the URL to show as a String
     * @return true if the browser could be opened, false otherwise
     */
    public static boolean openExternalBrowser(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            log.error("Couldn't parse URL from string " + urlString, e);
            return false;
        }

        IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench()
            .getBrowserSupport();
        IWebBrowser browser;
        try {
            browser = browserSupport.getExternalBrowser();
            browser.openURL(url);
            return true;
        } catch (Exception e) {
            log.error("Couldn't open external browser", e);
            return false;
        }

    }

    /**
     * Tries to open the given URL string in Eclipse's internal browser. However
     * if the user specified in the preferences to use an external browser
     * instead, the external browser is tried to open.
     * 
     * @param urlString
     *            the URL to show as a String
     * @param title
     *            a string displayed in the browsers title area
     * @return true if the browser could be opened, false otherwise
     */
    public static boolean openInternalBrowser(String urlString, String title) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            log.error("Couldn't parse URL from string " + urlString, e);
            return false;
        }

        IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench()
            .getBrowserSupport();
        IWebBrowser browser;
        try {
            browser = browserSupport.createBrowser(
                IWorkbenchBrowserSupport.AS_EDITOR
                    | IWorkbenchBrowserSupport.LOCATION_BAR
                    | IWorkbenchBrowserSupport.NAVIGATION_BAR, null, title, "");
            browser.openURL(url);
            return true;
        } catch (Exception e) {
            log.error("Couldn't open internal Browser", e);
            return false;
        }
    }

    public static String getMessage(Throwable e) {
        if (e instanceof XMPPException) {
            return e.toString();
        }
        if (e instanceof ExecutionException && e.getCause() != null) {
            return getMessage(e.getCause());
        }
        return e.getMessage();
    }

    public static final int CHUNKSIZE = 16 * 1024;

    /**
     * Compresses the given byte array using a Java Deflater.
     */
    public static byte[] deflate(byte[] input, SubMonitor subMonitor) {

        subMonitor.beginTask("Deflate bytearray", input.length / CHUNKSIZE + 1);

        Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION);
        compressor.setInput(input);
        compressor.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);

        byte[] buf = new byte[CHUNKSIZE];
        while (!compressor.finished() && !subMonitor.isCanceled()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
            subMonitor.worked(1);
        }
        IOUtils.closeQuietly(bos);

        subMonitor.done();

        return bos.toByteArray();
    }

    /**
     * Uncompresses the given byte array using a Java Inflater.
     * 
     * If the operation fails (because the given byte array does not contain
     * data accepted by the inflater), null is returned.
     */
    public static byte[] inflate(byte[] input, SubMonitor subMonitor) {
        subMonitor.beginTask("Inflate bytearray", input.length / CHUNKSIZE + 1);
        try {
            Inflater decompressor = new Inflater();
            decompressor.setInput(input, 0, input.length);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
            byte[] buf = new byte[CHUNKSIZE];

            while (!decompressor.finished() && !subMonitor.isCanceled()) {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
                subMonitor.worked(1);
            }

            IOUtils.closeQuietly(bos);
            subMonitor.done();

            return bos.toByteArray();
        } catch (java.util.zip.DataFormatException ex) {
            log.error("Failed to inflate bytearray", ex);
        }
        return null;
    }

    public static String throughput(long length, long delta) {

        if (delta == 0) {
            return "";
        } else {
            return " (" + (length / 1024L) + "kb in " + delta + " ms at "
                + (1000L * length / 1024L) / delta + " kb/s)";
        }
    }

    /**
     * Turn an integer representing a file size into a human readable
     * representation. For instance 573 becomes 573byte, 16787 becomes 16KB.
     */ 
    public static String formatByte(long length) {
        if (length < 1 << 13) {
            return length + "byte";
        }
        if (length < 1 << 23) {
            return (length / 1024) + "KB";
        }
        return length / 1024 / 1024 + "MB";
    }
}
