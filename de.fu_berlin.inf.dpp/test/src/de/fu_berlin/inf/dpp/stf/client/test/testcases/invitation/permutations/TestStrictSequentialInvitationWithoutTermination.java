package de.fu_berlin.inf.dpp.stf.client.test.testcases.invitation.permutations;

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

public class TestStrictSequentialInvitationWithoutTermination {
    private static final Logger log = Logger
        .getLogger(TestStrictSequentialInvitationWithoutTermination.class);

    private static final String PROJECT = BotConfiguration.PROJECTNAME;
    private static final String CLS = BotConfiguration.CLASSNAME;
    private static final String PKG = BotConfiguration.PACKAGENAME;
    private static Musician carl = InitMusician.newCarl();
    private static Musician alice = InitMusician.newAlice();
    private static Musician bob = InitMusician.newBob();

    /**
     * Preconditions:
     * <ol>
     * <li>alice (Host, Driver)</li>
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

    /**
     * make sure, all opened xmppConnects, popup windows and editor are closed
     * and all existed projects are deleted.
     * 
     * @throws RemoteException
     */
    @AfterClass
    public static void resetSaros() throws RemoteException {
        carl.workbench.resetSaros();
        bob.workbench.resetSaros();
        alice.workbench.resetSaros();
    }

    /**
     * make sure,all opened popup windows and editors are closed.Set the line
     * delimiter to default.
     * 
     * @throws RemoteException
     */
    @After
    public void cleanUp() throws RemoteException {
        carl.workbench.resetWorkbench();
        carl.mainMenu.newTextFileLineDelimiter("Default");
        bob.workbench.resetWorkbench();
        bob.mainMenu.newTextFileLineDelimiter("Default");
        alice.workbench.resetWorkbench();
        alice.mainMenu.newTextFileLineDelimiter("Default");

    }

    /**
     * 
     * TODO the test failed and need to be fixed at first. <br/>
     * 
     * Steps:
     * <ol>
     * <li>alice is either in global or project-specific settings and chooses
     * explicitly one of the possible line delimiter.</li>
     * <li>carl invites all strictly sequentially, one after the other.</li>
     * <li>All participants accept (use new project).</li>
     * <li>Once the invitation has been completed, all leave the session again.</li>
     * </ol>
     * 
     * Result:
     * <ol>
     * <li></li>
     * <li></li>
     * <li>All participants find that the line terminator of the host is set in
     * their project settings.</li>
     * <li></li>
     * </ol>
     * 
     * @throws RemoteException
     * @throws InterruptedException
     */
    @Test
    public void testSetLineDelimiter() throws RemoteException,
        InterruptedException {
        alice.mainMenu.newTextFileLineDelimiter("Unix");

        alice.shareProjectWithDone(PROJECT,
            SarosConstant.CONTEXT_MENU_SHARE_PROJECT, carl, bob);

        String delimiterByAlice = alice.mainMenu.getTextFileLineDelimiter();
        String delimiterByCarl = carl.mainMenu.getTextFileLineDelimiter();
        String delimiterByBob = bob.mainMenu.getTextFileLineDelimiter();

        log.debug("delimiter by alice: " + delimiterByAlice
            + "delimiter by bob: " + delimiterByBob + "delimiter by carl: "
            + delimiterByCarl);

        /*
         * TODO there are some bugs in saros... need to be fixed. Now you will
         * get AssertionError here.
         */
        assertTrue(delimiterByAlice.equals(delimiterByCarl));
        assertTrue(delimiterByAlice.equals(delimiterByBob));

        alice.leaveSessionFirstByPeers(carl, bob);

    }
}