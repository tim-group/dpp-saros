package de.fu_berlin.inf.dpp.feedback;

import org.apache.log4j.Logger;

import de.fu_berlin.inf.dpp.Saros;
import de.fu_berlin.inf.dpp.annotations.Component;
import de.fu_berlin.inf.dpp.preferences.PreferenceConstants;
import de.fu_berlin.inf.dpp.project.AbstractSessionListener;
import de.fu_berlin.inf.dpp.project.ISessionListener;
import de.fu_berlin.inf.dpp.project.ISharedProject;
import de.fu_berlin.inf.dpp.project.SessionManager;

/**
 * The StatisticManager is supposed to gather statistic data and submit it at
 * the end of a session (if the user has opted in).
 * 
 * TODO Add more information to be gathered
 * 
 * @author Lisa Dohrmann
 */
@Component(module = "feedback")
public class StatisticManager extends AbstractFeedbackManager {

    protected static final Logger log = Logger.getLogger(StatisticManager.class
        .getName());

    public static final int STATISTIC_UNKNOWN = 0;
    public static final int STATISTIC_ALLOW = 1;
    public static final int STATISTIC_FORBID = 2;

    protected ISessionListener sessionListener = new AbstractSessionListener() {

        @Override
        public void sessionEnded(ISharedProject session) {
            // count all started sessions
            putSessionCount(getSessionCount() + 1);
            log.debug("Session count: " + getSessionCount());
        }
    };

    public StatisticManager(Saros saros, SessionManager sessionManager) {
        super(saros);

        ensureConsistentPreferences();

        sessionManager.addSessionListener(sessionListener);
    }

    /**
     * Ensures that the preferences the StatisticManager manages are consistent
     * after plugin start, i.e. if they are not existing in the global scope,
     * the value from the workspace (might be the default) is globally set. If
     * there exists a different value in the workspace than in the global scope,
     * then the local value is overwritten.<br>
     * <br>
     * 
     * This must be done for all values kept both globally and per workspace.
     */
    protected void ensureConsistentPreferences() {
        makePrefConsistent(PreferenceConstants.STATISTIC_ALLOW_SUBMISSION);
    }

    /**
     * Returns if the submission of statistic is allowed as a boolean.
     * 
     * @return true if it is allowed
     */
    public boolean isStatisticSubmissionAllowed() {
        return getStatisticSubmissionStatus() == STATISTIC_ALLOW;
    }

    /**
     * Returns whether the submission of statistic is allowed, forbidden or
     * unknown. The global preferences have priority but if the value wasn't
     * found there the value from the PreferenceStore (with fall back to the
     * default) is used.
     * 
     * @return 0 = unknown, 1 = allowed, 2 = forbidden
     */
    public int getStatisticSubmissionStatus() {
        int status = saros.getConfigPrefs().getInt(
            PreferenceConstants.STATISTIC_ALLOW_SUBMISSION, -1);

        if (status == -1)
            status = saros.getPreferenceStore().getInt(
                PreferenceConstants.STATISTIC_ALLOW_SUBMISSION);
        return status;
    }

    /**
     * Saves in the workspace and globally if the user wants to submit statistic
     * data.
     * 
     * @param allow
     */
    public void setStatisticSubmissionAllowed(boolean allow) {
        int submission = allow ? StatisticManager.STATISTIC_ALLOW
            : StatisticManager.STATISTIC_FORBID;

        setStatisticSubmission(submission);
    }

    protected void setStatisticSubmission(int submission) {
        // store in preference and configuration scope
        saros.getPreferenceStore().setValue(
            PreferenceConstants.STATISTIC_ALLOW_SUBMISSION, submission);
        saros.getConfigPrefs().putInt(
            PreferenceConstants.STATISTIC_ALLOW_SUBMISSION, submission);
        saros.saveConfigPrefs();
    }

    /**
     * Returns if the user gave an answer to the statistic submission.
     * 
     * @return true if the user either allowed or forbade the submission,
     *         otherwise false
     */
    public boolean hasStatisticAgreement() {
        return getStatisticSubmissionStatus() != STATISTIC_UNKNOWN;
    }

    /**
     * Returns the total number of sessions the user has used Saros for.
     * 
     * @return the number of session the user started or participated in
     *         (concerning all workspaces)
     */
    public long getSessionCount() {
        return saros.getConfigPrefs().getLong(
            PreferenceConstants.SESSION_COUNT, 0);
    }

    /**
     * Saves the session count in the global preferences.
     * 
     * @param count
     *            the number of sessions to save
     */
    public void putSessionCount(long count) {
        saros.getConfigPrefs()
            .putLong(PreferenceConstants.SESSION_COUNT, count);
        saros.saveConfigPrefs();
    }
}