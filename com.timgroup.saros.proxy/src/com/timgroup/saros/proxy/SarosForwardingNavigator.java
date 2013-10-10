package com.timgroup.saros.proxy;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import com.timgroup.saros4intellij.proxy.Navigator;
import com.timgroup.saros4intellij.proxy.Position;
import com.timgroup.saros4intellij.proxy.Result;

import de.fu_berlin.inf.dpp.User;
import de.fu_berlin.inf.dpp.activities.SPath;
import de.fu_berlin.inf.dpp.activities.business.EditorActivity;
import de.fu_berlin.inf.dpp.activities.business.EditorActivity.Type;
import de.fu_berlin.inf.dpp.project.AbstractActivityProvider;
import de.fu_berlin.inf.dpp.project.ISarosSession;

public final class SarosForwardingNavigator implements Navigator {
    private final ISarosSession session;
    private final AbstractActivityProvider activityProvider;
    private final Logger logger = Logger.getLogger(getClass());

    public SarosForwardingNavigator(AbstractActivityProvider activityProvider, ISarosSession session) {
        this.activityProvider = activityProvider;
        this.session = session;
    }

    @Override
    public Result goTo(String filename, Position position) {
        User user = session.getLocalUser();
        Type type = EditorActivity.Type.ACTIVATED;
        List<IProject> projects = new ArrayList<IProject>(session.getProjects());
        IResource aResource = projects.get(0).getFile(filename);
        SPath spath = new SPath(aResource);
        EditorActivity activity = new EditorActivity(user, type, spath);
        logger.info("Firing event to open " + spath);
        activityProvider.fireActivity(activity);
        
        return Result.success();
    }
}