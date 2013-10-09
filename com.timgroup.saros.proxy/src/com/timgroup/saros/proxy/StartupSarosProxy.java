package com.timgroup.saros.proxy;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IStartup;
import org.picocontainer.annotations.Inject;

import com.timgroup.saros4intellij.proxy.Edit;
import com.timgroup.saros4intellij.proxy.Editor;
import com.timgroup.saros4intellij.proxy.NavigationResult;
import com.timgroup.saros4intellij.proxy.Navigator;
import com.timgroup.saros4intellij.proxy.Position;
import com.timgroup.saros4intellij.proxy.Result;
import com.timgroup.saros4intellij.proxy.server.RestService;

import de.fu_berlin.inf.dpp.Saros;
import de.fu_berlin.inf.dpp.SarosPluginContext;
import de.fu_berlin.inf.dpp.User;
import de.fu_berlin.inf.dpp.activities.SPath;
import de.fu_berlin.inf.dpp.activities.business.EditorActivity;
import de.fu_berlin.inf.dpp.activities.business.EditorActivity.Type;
import de.fu_berlin.inf.dpp.activities.business.IActivity;
import de.fu_berlin.inf.dpp.activities.business.TextEditActivity;
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
	    WriteSomeFile.doIt("earlyStartup", "earlyStartup");
	    
        setupLoggers();

        SarosPluginContext.reinject(this);
        
        WriteSomeFile.doIt("activator start", "activatorStart");
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
        public void postOutgoingInvitationCompleted(IProgressMonitor monitor,
                User user) {
        
        }

        @Override
        public void sessionEnded(ISarosSession project) {
 
        }

        private IActivityProvider spinUpEndpoint() {
            WriteSomeFile.doIt("spin up endpoint", "spinUpEndpoint");
            logger.info("Started");

            // jetty embedded
            // handler setup
            
            return new AbstractActivityProvider() {
                private boolean first = true;
                
                @Override
                public void exec(IActivity activity) {
                    System.out.println(activity);
                    logger.info(activity);
//                    Runnable r = new Runnable() {
//                        
//                        @Override
//                        public void run() {
//                            User user = session.getHost();
//                            Type type = EditorActivity.Type.ACTIVATED;
//                            IResource aResource = getRandomResource();
//                            if (aResource != null) {
//                                SPath spath = new SPath(aResource);
//                                EditorActivity activity = new EditorActivity(user, type, spath);
//                                logger.info("Firing event to open " + spath);
//                                fireActivity(activity);
//                            }
//                        }
//                        
//                        private IResource getRandomResource() {
//                            List<IProject> projects = new ArrayList<IProject>(session.getProjects());
//                            
//                            List<IFile> allResources = Arrays.asList(projects.get(0).getFile("/src/com/timgroup/alice/A.java"),
//                                                                     projects.get(0).getFile("/src/com/timgroup/alice/B.java"),
//                                                                     projects.get(0).getFile("/src/com/timgroup/alice/C.java"));
//                            if (allResources.isEmpty()) {
//                                return null;
//                            } else {
//                                return allResources.get(new Random().nextInt(allResources.size()));
//                            }
//                        }
//                    };
                    if (first) {
                        first = false;
//                        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(r, 3, 2, TimeUnit.SECONDS);
                        
                        final RestService service = new RestService(new Navigator() {

                            @Override
                            public Result goTo(String filename, Position position) {
                                User user = session.getHost();
                                Type type = EditorActivity.Type.ACTIVATED;
                                List<IProject> projects = new ArrayList<IProject>(session.getProjects());
                                IResource aResource = projects.get(0).getFile(filename);
                                SPath spath = new SPath(aResource);
                                EditorActivity activity = new EditorActivity(user, type, spath);
                                logger.info("Firing event to open " + spath);
                                fireActivity(activity);
                                return Result.success();
                            }
                            
                        }, 
                        new Editor() {
                            @Override
                            public Result edit(String filename, Edit edit) {
                                User user = session.getHost();
                                List<IProject> projects = new ArrayList<IProject>(session.getProjects());
                                IResource aResource = projects.get(0).getFile(filename);
                                SPath spath = new SPath(aResource);
                                TextEditActivity textActivity = new TextEditActivity(user, edit.position.offset, edit.textInserted, edit.textReplaced, spath);
                                logger.info("Firing event for editing " + spath + " " + textActivity);
                                fireActivity(textActivity);
                                
                                return Result.success();
                            }
                        });
                        
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
