package de.fu_berlin.inf.dpp.ui.actions;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.picocontainer.Disposable;
import org.picocontainer.annotations.Inject;

import de.fu_berlin.inf.dpp.SarosPluginContext;
import de.fu_berlin.inf.dpp.User;
import de.fu_berlin.inf.dpp.annotations.Component;
import de.fu_berlin.inf.dpp.editor.AbstractSharedEditorListener;
import de.fu_berlin.inf.dpp.editor.EditorManager;
import de.fu_berlin.inf.dpp.editor.ISharedEditorListener;
import de.fu_berlin.inf.dpp.project.AbstractSarosSessionListener;
import de.fu_berlin.inf.dpp.project.ISarosSession;
import de.fu_berlin.inf.dpp.project.ISarosSessionListener;
import de.fu_berlin.inf.dpp.project.ISarosSessionManager;
import de.fu_berlin.inf.dpp.ui.ImageManager;
import de.fu_berlin.inf.dpp.ui.Messages;
import de.fu_berlin.inf.dpp.ui.util.SWTUtils;
import de.fu_berlin.inf.dpp.ui.util.selection.SelectionUtils;
import de.fu_berlin.inf.dpp.ui.util.selection.retriever.SelectionRetrieverFactory;
import de.fu_berlin.inf.dpp.util.Utils;

/**
 * This follow mode action is used to select the person to follow.
 * 
 * @author Christopher Oezbek
 * @author Edna Rosen
 */
@Component(module = "action")
public class FollowThisPersonAction extends Action implements Disposable {

    public static final String ACTION_ID = FollowThisPersonAction.class
        .getName();

    private static final Logger log = Logger
        .getLogger(FollowThisPersonAction.class.getName());

    protected ISarosSessionListener sessionListener = new AbstractSarosSessionListener() {
        @Override
        public void sessionStarted(ISarosSession newSarosSession) {
            updateActionEnablement();
        }

        @Override
        public void sessionEnded(ISarosSession oldSarosSession) {
            updateActionEnablement();
        }
    };

    protected ISharedEditorListener editorListener = new AbstractSharedEditorListener() {
        @Override
        public void followModeChanged(User user, boolean isFollowed) {
            updateActionEnablement();
        }
    };

    protected ISelectionListener selectionListener = new ISelectionListener() {
        @Override
        public void selectionChanged(IWorkbenchPart part, ISelection selection) {
            updateActionEnablement();
        }
    };

    @Inject
    protected ISarosSessionManager sessionManager;

    @Inject
    protected EditorManager editorManager;

    public FollowThisPersonAction() {
        super(Messages.FollowThisPersonAction_follow_title);

        SarosPluginContext.initComponent(this);

        setImageDescriptor(new ImageDescriptor() {
            @Override
            public ImageData getImageData() {
                return ImageManager.ICON_BUDDY_SAROS_FOLLOWMODE.getImageData();
            }
        });

        setToolTipText(Messages.FollowThisPersonAction_follow_tooltip);
        setId(ACTION_ID);

        sessionManager.addSarosSessionListener(sessionListener);
        editorManager.addSharedEditorListener(editorListener);
        SelectionUtils.getSelectionService().addSelectionListener(
            selectionListener);

        updateEnablement();
    }

    /**
     * @review runSafe OK
     */
    @Override
    public void run() {
        Utils.runSafeSync(log, new Runnable() {
            @Override
            public void run() {
                List<User> users = SelectionRetrieverFactory
                    .getSelectionRetriever(User.class).getSelection();

                if (!canBeExecuted(users)) {
                    log.warn("could not execute change follow mode action " //$NON-NLS-1$
                        + "because either no session is running, " //$NON-NLS-1$
                        + "more than one user is selected or " //$NON-NLS-1$
                        + "the selected user is the local user"); //$NON-NLS-1$
                    return;
                }

                User toFollow = users.get(0).equals(
                    editorManager.getFollowedUser()) ? null : users.get(0);

                editorManager.setFollowing(toFollow);
            }
        });
    }

    protected void updateActionEnablement() {
        SWTUtils.runSafeSWTAsync(log, new Runnable() {
            @Override
            public void run() {
                updateEnablement();
            }
        });
    }

    protected void updateEnablement() {

        List<User> users = SelectionRetrieverFactory.getSelectionRetriever(
            User.class).getSelection();

        if (!canBeExecuted(users)) {
            setEnabled(false);
            return;
        }

        if (users.get(0).equals(editorManager.getFollowedUser())) {
            setText(Messages.FollowThisPersonAction_stop_follow_title);
            setToolTipText(Messages.FollowThisPersonAction_stop_follow_tooltip);
        } else {
            setText(Messages.FollowThisPersonAction_follow_title);
            setToolTipText(Messages.FollowThisPersonAction_follow_tooltip);
        }

        setEnabled(true);
    }

    protected boolean canBeExecuted(List<User> users) {
        ISarosSession sarosSession = sessionManager.getSarosSession();

        return sarosSession != null && users.size() == 1
            && !users.get(0).isLocal();
    }

    @Override
    public void dispose() {
        SelectionUtils.getSelectionService().removeSelectionListener(
            selectionListener);
        sessionManager.removeSarosSessionListener(sessionListener);
        editorManager.removeSharedEditorListener(editorListener);
    }
}
