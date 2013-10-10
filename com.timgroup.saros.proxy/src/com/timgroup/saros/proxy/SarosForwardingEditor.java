package com.timgroup.saros.proxy;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import com.timgroup.saros4intellij.proxy.Edit;
import com.timgroup.saros4intellij.proxy.Editor;
import com.timgroup.saros4intellij.proxy.Result;

import de.fu_berlin.inf.dpp.User;
import de.fu_berlin.inf.dpp.activities.SPath;
import de.fu_berlin.inf.dpp.activities.business.TextEditActivity;
import de.fu_berlin.inf.dpp.project.AbstractActivityProvider;
import de.fu_berlin.inf.dpp.project.ISarosSession;

public final class SarosForwardingEditor implements Editor {
    private final AbstractActivityProvider activityProvider;
    private final ISarosSession session;
    private final Logger logger = Logger.getLogger(getClass());

    public SarosForwardingEditor(AbstractActivityProvider activityProvider, ISarosSession session) {
        this.activityProvider = activityProvider;
        this.session = session;
    }

    @Override
    public Result edit(String filename, Edit edit) {
        User user = session.getHost();
        List<IProject> projects = new ArrayList<IProject>(session.getProjects());
        IResource aResource = projects.get(0).getFile(filename);
        SPath spath = new SPath(aResource);
        TextEditActivity textActivity = new TextEditActivity(user, edit.position.offset, edit.textInserted, edit.textReplaced, spath);
        logger.info("Firing event for editing " + spath + " " + textActivity);
        activityProvider.fireActivity(textActivity);
        
        return Result.success();
    }
}