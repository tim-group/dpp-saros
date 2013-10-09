package com.timgroup.saros.proxy;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleContext;
import org.picocontainer.annotations.Inject;

import de.fu_berlin.inf.dpp.SarosPluginContext;
import de.fu_berlin.inf.dpp.User;
import de.fu_berlin.inf.dpp.activities.business.IActivity;
import de.fu_berlin.inf.dpp.project.AbstractActivityProvider;
import de.fu_berlin.inf.dpp.project.AbstractSarosSessionListener;
import de.fu_berlin.inf.dpp.project.IActivityProvider;
import de.fu_berlin.inf.dpp.project.ISarosSession;
import de.fu_berlin.inf.dpp.project.ISarosSessionListener;
import de.fu_berlin.inf.dpp.project.SarosSessionManager;

public class StartupSarosProxy implements IStartup {

	@Override
	public void earlyStartup() {
	    System.out.println("Starting");
	    Logger.getLogger(getClass()).info("Starting");
	    WriteSomeFile.doIt("earlyStartup", "earlyStartup");
	    
        setupLoggers();

        SarosPluginContext.reinject(this);
        
        WriteSomeFile.doIt("activator start", "activatorStart");
        System.out.println("Started our plugin (timgroup)");
        logger.info("Started our plugin (timgroup)");
        
        sessionManager.addSarosSessionListener(sessionListener);
	}

    private static final Logger logger = Logger.getLogger(StartupSarosProxy.class);


    @Inject
    private SarosSessionManager sessionManager;
    
    protected ISarosSessionListener sessionListener = new AbstractSarosSessionListener() {

        @Override
        public void sessionStarting(ISarosSession session) {
        }

        @Override
        public void preIncomingInvitationCompleted(IProgressMonitor monitor) {
        }

        @Override
        public void sessionStarted(ISarosSession session) {
            session.addActivityProvider(spinUpEndpoint());
        }

        @Override
        public void postOutgoingInvitationCompleted(IProgressMonitor monitor,
                User user) {
        
        }

        @Override
        public void sessionEnded(ISarosSession project) {
 
        }

        private IActivityProvider spinUpEndpoint() {
            WriteSomeFile.doIt("spin up endpoint", "spinUpEndpoint");
            logger.info("Started");
            System.out.println("Started");
            // jetty embedded
            // handler setup
            
            // receive file open event
//            currentSession.getConcurrentDocumentClient().transformToJupiter(null);
            

            
            return new AbstractActivityProvider() {
                
                @Override
                public void exec(IActivity activity) {
                    System.out.println(activity);
                    logger.info(activity);
                }
            };
        }
    };
    
    protected void setupLoggers() {
        try {
            PropertyConfigurator.configure(getClass().getClassLoader()
                .getResource("saros.proxy.log4j.properties")); //$NON-NLS-1$
        } catch (SecurityException e) {
            System.err.println("Could not start logging:"); //$NON-NLS-1$
            e.printStackTrace();
        }
    }
    

}
