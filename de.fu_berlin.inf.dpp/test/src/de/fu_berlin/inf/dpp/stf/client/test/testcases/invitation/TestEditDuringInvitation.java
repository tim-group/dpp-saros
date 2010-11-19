package de.fu_berlin.inf.dpp.stf.client.test.testcases.invitation;

import static org.junit.Assert.assertTrue;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fu_berlin.inf.dpp.stf.client.Musician;
import de.fu_berlin.inf.dpp.stf.client.test.helpers.InitMusician;
import de.fu_berlin.inf.dpp.stf.server.BotConfiguration;
import de.fu_berlin.inf.dpp.stf.server.SarosConstant;

public class TestEditDuringInvitation {

    private static final Logger log = Logger
        .getLogger(TestEditDuringInvitation.class);
    private static final String PROJECT = BotConfiguration.PROJECTNAME;
    private static final String CLS = BotConfiguration.CLASSNAME;
    private static final String PKG = BotConfiguration.PACKAGENAME;
    private static Musician carl;
    private static Musician alice;
    private static Musician bob;

    /**
     * Preconditions:
     * <ol>
     * <li>alice (Host, Driver), alice share a java project with bob and carl.</li>
     * <li>bob (Observer)</li>
     * <li>carl (Observer)</li>
     * </ol>
     * 
     * @throws RemoteException
     * @throws InterruptedException
     */
    @BeforeClass
    public static void initMusicians() throws RemoteException,
        InterruptedException {
        /*
         * initialize the musicians simultaneously
         */
        List<Musician> musicians = InitMusician.initMusiciansConcurrently(
            BotConfiguration.PORT_ALICE, BotConfiguration.PORT_BOB,
            BotConfiguration.PORT_CARL);
        alice = musicians.get(0);
        bob = musicians.get(1);
        carl = musicians.get(2);

        alice.mainMenu.newJavaProjectWithClass(PROJECT, PKG, CLS);
    }

    @AfterClass
    public static void resetSaros() throws RemoteException {
        carl.workbench.resetSaros();
        bob.workbench.resetSaros();
        alice.workbench.resetSaros();
    }

    @After
    public void cleanUp() throws RemoteException {
        carl.workbench.resetWorkbench();
        bob.workbench.resetWorkbench();
        alice.workbench.resetWorkbench();
    }

    /**
     * 
     * Steps:
     * <ol>
     * <li>Alice invites Bob.</li>
     * <li>Bob accepts the invitation</li>
     * <li>Alice gives Bob driver capability</li>
     * <li>Alice invites Carl</li>
     * <li>Bob changes data during the running invtiation of Carl.</li>
     * </ol>
     * 
     * 
     * Expected Results:
     * <ol>
     * <li>All changes that Bob has done should be on Carl's side. There should
     * not be an inconsistency.</li>.
     * </ol>
     * 
     * @throws RemoteException
     */
    @Test
    public void testEditDuringInvitation() throws RemoteException {
        log.trace("starting testEditDuringInvitation, alice.buildSession");
        alice.shareProjectWithDone(PROJECT,
            SarosConstant.CONTEXT_MENU_SHARE_PROJECT, bob);

        log.trace("alice.giveDriverRole");
        alice.sessionV.giveDriverRole(bob.state);

        assertTrue(bob.state.isDriver(alice.jid));

        log.trace("alice.inviteUser(carl");
        alice.sessionV.openInvitationInterface(carl.getBaseJid());

        log.trace("carl.confirmSessionInvitationWindowStep1");
        // waitUntilShellActive(SarosConstant.SHELL_TITLE_SESSION_INVITATION);
        carl.pEV.confirmFirstPageOfWizardSessionInvitation();

        log.trace("bob.setTextInJavaEditor");
        bob.editor.setTextInJavaEditorWithSave(
            BotConfiguration.CONTENTPATH, PROJECT, PKG, CLS);

        log.trace("carl.confirmSessionInvitationWindowStep2UsingNewproject");
        carl.pEV
            .confirmSecondPageOfWizardSessionInvitationUsingNewproject(PKG);

        log.trace("getTextOfJavaEditor");
        String textFromCarl = carl.editor.getTextOfJavaEditor(PROJECT,
            PKG, CLS);
        String textFormAlice = alice.editor.getTextOfJavaEditor(PROJECT,
            PKG, CLS);
        assertTrue(textFromCarl.equals(textFormAlice));

        log.trace("testEditDuringInvitation done");
    }
}