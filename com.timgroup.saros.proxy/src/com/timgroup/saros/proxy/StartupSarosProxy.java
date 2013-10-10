package com.timgroup.saros.proxy;

import java.net.URL;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IStartup;
import org.picocontainer.annotations.Inject;

import com.timgroup.saros4intellij.proxy.server.RestService;

import de.fu_berlin.inf.dpp.Saros;
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
	    Logger.getLogger(getClass()).info("Starting");
	    
        setupLoggers();

        SarosPluginContext.reinject(this);
        
        logger.info("Started our plugin (timgroup)");
        
        sessionManager.addSarosSessionListener(sessionListener);
        saros.asyncConnect();
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
            this.session.addActivityProvider(spinUpEndpoint());
        }

        @Override
        public void postOutgoingInvitationCompleted(IProgressMonitor monitor, User user) { }

        @Override
        public void sessionEnded(ISarosSession project) { }

        private IActivityProvider spinUpEndpoint() {
            logger.info("Started");

            return new AbstractActivityProvider() {
                private boolean first = true;
                
                @Override
                public void exec(IActivity activity) {
                    logger.info(activity);

                    if (first) {
                        first = false;
                        
                        final RestService service = new RestService(new SarosForwardingNavigator(this, session), 
                                                                    new SarosForwardingEditor(this, session));
                        
                        Executors.newSingleThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                service.start();
                            }
                        });
                    }
                }
                
                
            };
        }
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
