package de.fu_berlin.inf.dpp.feedback;

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import de.fu_berlin.inf.dpp.util.CausedIOException;

/**
 * The FileSubmitter class provides static methods to upload a file to a server.
 * 
 * @author Lisa Dohrmann
 */
public class FileSubmitter {

    protected static final Logger log = Logger.getLogger(FileSubmitter.class
        .getName());

    /** the temporary URL of our Apache Tomcat server */
    public static final String SERVER_URL_TEMP = "http://brazzaville.imp.fu-berlin.de:5900/";
    /** the URL of our Apache Tomcat server */
    public static final String SERVER_URL = "http://projects.mi.fu-berlin.de/saros/";
    /** the name of the Servlet that is supposed to handle the upload */
    public static final String SERVLET_NAME = "SarosStatisticServer/fileupload";

    /** Value for connection timeout */
    protected static final int TIMEOUT = 5000;

    /**
     * Convenience wrapper method for
     * {@link #uploadFile(File, String, SubMonitor)}. <br>
     * The statistic file is first tried to upload to the server specified by
     * {@link #SERVER_URL} and then to our temporary server
     * {@link #SERVER_URL_TEMP}. <br>
     * <br>
     * Because the statistic file upload is supposed to take place behind the
     * scenes, no user feedback is reported back at this time <br>
     * <br>
     * TODO Some progress feedback is actually desirable, so the
     * NullProgressMonitor should be replaced. Progress of the statistic
     * submission should be shown in the workbench window's status bar (see
     * IWorkbenchWindow#run()).
     * 
     * @param file
     *            the file to upload
     * @throws IOException
     *             is thrown, if the upload failed; the exception wraps the
     *             target exception that contains the main cause for the failure
     * 
     * @blocking
     */
    public static void uploadStatisticFile(File file) throws IOException {
        /*
         * TODO this first call is expected to fail at the moment, because the
         * tomcat server isn't yet installed on projects.mi.fu-berlin.de/saros
         */
        try {
            uploadFile(file, SERVER_URL + SERVLET_NAME, SubMonitor
                .convert(new NullProgressMonitor()));
            return;
        } catch (IOException e) {
            log.debug(String.format(
                "Because the real server is not running right now, "
                    + "the following message is expected: %s. %s", e
                    .getMessage(), e.getCause().getMessage()));
        }
        uploadFile(file, SERVER_URL_TEMP + SERVLET_NAME, SubMonitor
            .convert(new NullProgressMonitor()));
    }

    /**
     * Tries to upload the given file to the given server (via POST method).
     * 
     * @param file
     *            the file to upload
     * @param server
     *            the URL of the server, that is supposed to handle the file
     * @param progress
     *            a SubMonitor to report progress to
     * @throws IOException
     *             is thrown, if the upload failed; the exception wraps the
     *             target exception that contains the main cause for the failure
     * 
     * @blocking
     */
    public static void uploadFile(File file, String server, SubMonitor progress)
        throws IOException {
        if (file == null || !file.exists()) {
            throw new CausedIOException("Upload not possible",
                new IllegalArgumentException(
                    "The file that should be uploaded was"
                        + " either null or nonexistent"));
        }

        // TODO report progress to the SubMonitor

        // begin task of unknown length
        progress.beginTask("Uploading file " + file.getName(), 10000);

        PostMethod post = new PostMethod(server);
        // holds the status response after the method was executed
        int status = 0;

        post.getParams().setBooleanParameter(
            HttpMethodParams.USE_EXPECT_CONTINUE, true);

        /*
         * retry the method 3 times, but not if the request was send
         * successfully
         */
        post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
            new DefaultHttpMethodRetryHandler(3, false));

        try {
            // create a multipart request for the file
            Part[] parts = { new FilePart(file.getName(), file) };
            post.setRequestEntity(new MultipartRequestEntity(parts, post
                .getParams()));

            HttpClient client = new HttpClient();
            /*
             * connection has to be established within the timeout, otherwise a
             * ConnectTimeoutException is thrown
             */
            client.getHttpConnectionManager().getParams().setConnectionTimeout(
                TIMEOUT);

            log.info("Trying to upload file " + file.getName() + " to "
                + server + " ...");

            // try to upload the file
            status = client.executeMethod(post);

            // examine status response
            if (status == HttpStatus.SC_OK) {
                log.info("Upload successfull. Server response: "
                    + IOUtils.toString(post.getResponseBodyAsStream()));
                return;
            }

        } catch (ConnectTimeoutException e) {
            // couldn't connect within the timeout
            throw new CausedIOException("Couldn't connect to host " + server, e);
        } catch (Exception e) {
            throw new CausedIOException(
                "An internal error occurred while trying to upload file "
                    + file.getName(), e);
        } finally {
            post.releaseConnection();
            progress.done();
        }
        // upload failed
        throw new CausedIOException("Upload failed", new RuntimeException(
            "Server response: " + status + " "
                + HttpStatus.getStatusText(status)));
    }
}