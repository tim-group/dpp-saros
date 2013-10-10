package com.timgroup.saros.proxy;

import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.timgroup.saros4intellij.proxy.server.RestService;

import de.fu_berlin.inf.dpp.activities.business.IActivity;
import de.fu_berlin.inf.dpp.project.AbstractActivityProvider;
import de.fu_berlin.inf.dpp.project.ISarosSession;

class HttpReceivingActivityProvider extends AbstractActivityProvider {
    private static final Logger logger = Logger.getLogger(ISarosSession.class);
    
    private boolean first = true;
    private final ISarosSession session;
    
    public HttpReceivingActivityProvider(ISarosSession session) {
        this.session = session;
    }

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
    
}