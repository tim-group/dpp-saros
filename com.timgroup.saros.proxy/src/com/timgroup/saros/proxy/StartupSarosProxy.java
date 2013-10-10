package com.timgroup.saros.proxy;

import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IStartup;
import org.picocontainer.annotations.Inject;

import de.fu_berlin.inf.dpp.Saros;
import de.fu_berlin.inf.dpp.SarosPluginContext;
import de.fu_berlin.inf.dpp.User;
import de.fu_berlin.inf.dpp.project.AbstractSarosSessionListener;
import de.fu_berlin.inf.dpp.project.ISarosSession;
import de.fu_berlin.inf.dpp.project.ISarosSessionListener;
import de.fu_berlin.inf.dpp.project.SarosSessionManager;

public class StartupSarosProxy implements IStartup {

    private static final String INTELLIJ_HOST = "localhost";
    private static final int INTELLIJ_PORT = 7374;
    
	@Override
	public void earlyStartup() {
	    Logger.getLogger(getClass()).info("Starting");
	    
        setupLoggers();

        SarosPluginContext.reinject(this);
        
        logger.info("Started our plugin (timgroup)");
        
        sessionManager.addSarosSessionListener(sessionListener);
	}

    private static final Logger logger = Logger.getLogger(StartupSarosProxy.class);


    @Inject
    private SarosSessionManager sessionManager;
    
    @Inject
    private Saros saros;
    
    protected ISarosSessionListener sessionListener = new AbstractSarosSessionListener() {
        
        private ISarosSession session;

        @Override
        public void sessionStarting(ISarosSession session) {
        }

        @Override
        public void preIncomingInvitationCompleted(IProgressMonitor monitor) {
        }

        @Override
        public void sessionStarted(ISarosSession session) {
            this.session = session;
            this.session.addActivityProvider(new HttpReceivingActivityProvider(session));
            this.session.addActivityProvider(new HttpForwardingActivityProvider(INTELLIJ_HOST, INTELLIJ_PORT));
        }

        @Override
        public void postOutgoingInvitationCompleted(IProgressMonitor monitor, User user) { }

        @Override
        public void sessionEnded(ISarosSession project) { }

    };
    
    protected void setupLoggers() {
        try {
            URL resource = getClass().getClassLoader()
                .getResource("saros.proxy.log4j.properties");
            PropertyConfigurator.configure(resource); //$NON-NLS-1$
        } catch (SecurityException e) {
            System.err.println("Could not start logging:"); //$NON-NLS-1$
            e.printStackTrace();
        }
    }
    

}
