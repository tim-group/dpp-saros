package com.timgroup.saros.proxy;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.framework.BundleActivator;
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

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}
	
	private static final Logger logger = Logger.getLogger(Activator.class);


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
            
            return new AbstractActivityProvider() {
                
                @Override
                public void exec(IActivity activity) {
                    System.out.println(activity);
                    logger.info(activity);
                }
            };
        }
    };

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

        SarosPluginContext.reinject(this);
        
        WriteSomeFile.doIt("activator start", "activatorStart");
        System.out.println("Started our plugin (timgroup)");
        logger.info("Started our plugin (timgroup)");
        
        setupLoggers();

        sessionManager.addSarosSessionListener(sessionListener);
	}
	
    protected void setupLoggers() {
        try {
            PropertyConfigurator.configure(Activator.class.getClassLoader()
                .getResource("saros.proxy.log4j.properties")); //$NON-NLS-1$
        } catch (SecurityException e) {
            System.err.println("Could not start logging:"); //$NON-NLS-1$
            e.printStackTrace();
        }
    }

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	

}
