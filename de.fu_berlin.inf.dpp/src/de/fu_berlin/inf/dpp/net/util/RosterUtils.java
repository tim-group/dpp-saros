package de.fu_berlin.inf.dpp.net.util;

import java.util.Iterator;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.packet.DiscoverInfo.Identity;
import org.jivesoftware.smackx.packet.DiscoverItems;
import org.jivesoftware.smackx.search.UserSearch;
import org.picocontainer.annotations.Inject;

import de.fu_berlin.inf.dpp.Saros;
import de.fu_berlin.inf.dpp.SarosPluginContext;
import de.fu_berlin.inf.dpp.net.JID;
import de.fu_berlin.inf.dpp.net.SarosNet;
import de.fu_berlin.inf.dpp.ui.util.DialogUtils;
import de.fu_berlin.inf.dpp.ui.util.SWTUtils;

/**
 * Utility class for classic {@link Roster} operations
 * 
 * @author bkahlert
 */
public class RosterUtils {
    private static final Logger log = Logger.getLogger(RosterUtils.class);

    @Inject
    private static SarosNet defaultNetwork;

    /*
     * HACK this should be initialized in a better way and removed if resolving
     * nicknames is removed from the User class
     */

    static {
        SarosPluginContext.initComponent(new RosterUtils());
    }

    private RosterUtils() {
        // no public instantiation allowed
    }

    protected static class DialogContent {

        public DialogContent(String dialogTitle, String dialogMessage,
            String invocationTargetExceptionMessage) {
            super();
            this.dialogTitle = dialogTitle;
            this.dialogMessage = dialogMessage;
            this.invocationTargetExceptionMessage = invocationTargetExceptionMessage;
        }

        /**
         * Title displayed in the question dialog
         */
        String dialogTitle;

        /**
         * Message displayed in the question dialog
         */
        String dialogMessage;

        /**
         * Detailed message for the InvocationTargetMessage
         */
        String invocationTargetExceptionMessage;
    }

    protected static DialogContent getDialogContent(XMPPException e) {

        // FIXME: use e.getXMPPError().getCode(); !

        if (e.getMessage().contains("item-not-found")) {
            return new DialogContent("Buddy Unknown",
                "The buddy is unknown to the XMPP/Jabber server.\n\n"
                    + "Do you want to add the buddy anyway?",
                "Buddy unknown to XMPP/Jabber server.");
        }

        if (e.getMessage().contains("remote-server-not-found")) {
            return new DialogContent("Server Not Found",
                "The responsible XMPP/Jabber server could not be found.\n\n"
                    + "Do you want to add the buddy anyway?",
                "Unable to find the responsible XMPP/Jabber server.");

        }

        if (e.getMessage().contains("501")) {
            return new DialogContent(
                "Unsupported Buddy Status Check",
                "The responsible XMPP/Jabber server does not support status requests.\n\n"
                    + "If the buddy exists you can still successfully add him.\n\n"
                    + "Do you want to try to add the buddy?",
                "Buddy status check unsupported by XMPP/Jabber server.");
        }

        if (e.getMessage().contains("503")) {
            return new DialogContent(
                "Unknown Buddy Status",
                "For privacy reasons the XMPP/Jabber server does not reply to status requests.\n\n"
                    + "If the buddy exists you can still successfully add him.\n\n"
                    + "Do you want to try to add the buddy?",
                "Unable to check the buddy status.");
        }

        if (e.getMessage().contains("No response from the server")) {
            return new DialogContent(
                "Server Not Responding",
                "The responsible XMPP/Jabber server is not connectable.\n"
                    + "The server is either inexistent or offline right now.\n\n"
                    + "Do you want to add the buddy anyway?",
                "The XMPP/Jabber server did not respond.");
        }

        return new DialogContent("Unknown Error",
            "An unknown error has occured:\n\n" + e.getMessage() + "\n\n"
                + "Do you want to add the buddy anyway?", "Unknown error: "
                + e.getMessage());
    }

    /**
     * @param sarosNet
     *            network component that should be used to resolve the nickname
     *            or <code>null</code> to use the default one
     * @param jid
     *            the JID to resolve the nickname for
     * @return The nickname associated with the given JID in the current roster
     *         or null if the current roster is not available or the nickname
     *         has not been set.
     */
    public static String getNickname(SarosNet sarosNet, JID jid) {

        if (sarosNet == null)
            sarosNet = defaultNetwork;

        if (sarosNet == null)
            return null;

        Connection connection = sarosNet.getConnection();
        if (connection == null)
            return null;

        Roster roster = connection.getRoster();
        if (roster == null)
            return null;

        RosterEntry entry = roster.getEntry(jid.getBase());
        if (entry == null)
            return null;

        String nickName = entry.getName();
        if (nickName != null && nickName.trim().length() > 0) {
            return nickName;
        }
        return null;
    }

    public static String getDisplayableName(RosterEntry entry) {
        String nickName = entry.getName();
        if (nickName != null && nickName.trim().length() > 0) {
            return nickName.trim();
        }
        return entry.getUser();
    }

    /**
     * Creates the given account on the given XMPP server.
     * 
     * @blocking
     * 
     * @param server
     *            the server on which to create the account
     * @param username
     *            for the new account
     * @param password
     *            for the new account
     * @throws XMPPException
     *             exception that occurs while registering
     */
    public static void createAccount(String server, String username,
        String password) throws XMPPException {

        Connection connection = new XMPPConnection(server);

        connection.connect();

        String errorMessage = isAccountCreationPossible(connection, username);

        if (errorMessage != null)
            throw new XMPPException(errorMessage);

        AccountManager manager = connection.getAccountManager();
        manager.createAccount(username, password);

        connection.disconnect();
    }

    /**
     * Checks whether a {@link Roster} account with the given username on the
     * given server can be created.
     * <p>
     * <b>IMPORTANT:</b> Returns null if the account creation is possible.
     * 
     * @param connection
     *            to the server to check
     * @param username
     *            to be used for account creation
     * @return null if account creation is possible; otherwise error message
     *         which describes why the account creation can not be perfomed.
     */
    public static String isAccountCreationPossible(Connection connection,
        String username) {
        String errorMessage = null;

        Registration registration = null;
        try {
            registration = getRegistrationInfo(username, connection);
        } catch (XMPPException e) {
            log.error("Server " + connection.getHost()
                + " does not support XEP-0077"
                + " (In-Band Registration) properly:", e);
        }
        if (registration != null && registration.getError() != null) {
            if (registration.getAttributes().containsKey("registered")) {
                errorMessage = "Account " + username
                    + " already exists on the server.";
            } else if (!registration.getAttributes().containsKey("username")) {
                if (registration.getInstructions() != null) {
                    errorMessage = "Registration via Saros not possible.\n\n"
                        + "Please follow these instructions:\n"
                        + registration.getInstructions();
                } else {
                    errorMessage = "Registration via Saros not possible.\n\n"
                        + "Please see the server's web site for\n"
                        + "informations for how to create an account.";
                }
            } else {
                errorMessage = "No in-band registration. Please create account on provider's website.";
                log.warn("Unknow registration error: "
                    + registration.getError().getMessage());
            }
        }

        return errorMessage;
    }

    /**
     * Adds given contact to the {@link Roster}.
     * 
     * @param connection
     * @param jid
     * @param nickname
     */
    public static void addToRoster(Connection connection, final JID jid,
        String nickname) throws XMPPException {

        if (connection == null)
            throw new NullPointerException("connection is null");

        if (jid == null)
            throw new NullPointerException("jid is null");

        try {
            boolean jidOnServer = isJIDonServer(connection, jid);
            if (!jidOnServer) {
                boolean cancel = false;
                try {
                    cancel = SWTUtils.runSWTSync(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return !DialogUtils
                                .openQuestionMessageDialog(
                                    null,
                                    "Buddy Unknown",
                                    "You entered a valid XMPP/Jabber server.\n\n"
                                        + "Unfortunately your entered JID is unknown to the server.\n"
                                        + "Please make sure you spelled the JID correctly.\n\n"
                                        + "Do you want to add the buddy anyway?");
                        }
                    });
                } catch (Exception e) {
                    log.debug("Error opening questionMessageDialog", e);
                }

                if (cancel) {
                    throw new XMPPException(
                        "Please make sure you spelled the JID correctly.");
                }
                log.debug("The buddy " + jid
                    + " couldn't be found on the server."
                    + " The user chose to add it anyway.");

            }
        } catch (XMPPException e) {
            final DialogContent dialogContent = getDialogContent(e);

            boolean cancel = false;

            try {
                cancel = SWTUtils.runSWTSync(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return !DialogUtils.openQuestionMessageDialog(null,
                            dialogContent.dialogTitle,
                            dialogContent.dialogMessage);
                    }
                });
            } catch (Exception e1) {
                log.debug("Error opening questionMessageDialog", e);
            }

            if (cancel)
                throw new XMPPException(
                    dialogContent.invocationTargetExceptionMessage);

            log.warn("Problem while adding a buddy. User decided to add buddy anyway. Problem:\n"
                + e.getMessage());
        }

        connection.getRoster().createEntry(jid.getBase(), nickname, null);
    }

    /**
     * Removes given buddy from the {@link Roster}.
     * 
     * @blocking
     * 
     * @param rosterEntry
     *            the buddy that is to be removed
     * @throws XMPPException
     *             is thrown if no connection is established.
     */
    public static void removeFromRoster(Connection connection,
        RosterEntry rosterEntry) throws XMPPException {
        if (!connection.isConnected()) {
            throw new XMPPException("Not connected");
        }
        connection.getRoster().removeEntry(rosterEntry);
    }

    /**
     * Returns whether the given JID can be found on the server.
     * 
     * @blocking
     * 
     * @param connection
     * @throws XMPPException
     *             if the service discovery failed
     */
    public static boolean isJIDonServer(Connection connection, JID jid)
        throws XMPPException {

        ServiceDiscoveryManager sdm = ServiceDiscoveryManager
            .getInstanceFor(connection);

        boolean discovered = sdm.discoverInfo(jid.getRAW()).getIdentities()
            .hasNext();

        if (!discovered && jid.isBareJID())
            discovered = sdm.discoverInfo(jid.getBase() + "/" + Saros.RESOURCE)
                .getIdentities().hasNext();

        return discovered;
    }

    /**
     * Retrieve XMPP Registration information from a server.
     * 
     * This implementation reuses code from Smack but also sets the from element
     * of the IQ-Packet so that the server could reply with information that the
     * account already exists as given by XEP-0077.
     * 
     * To see what additional information can be queried from the registration
     * object, refer to the XEP directly:
     * 
     * http://xmpp.org/extensions/xep-0077.html
     */
    public static synchronized Registration getRegistrationInfo(
        String toRegister, Connection connection) throws XMPPException {
        Registration reg = new Registration();
        reg.setTo(connection.getServiceName());
        reg.setFrom(toRegister);
        PacketFilter filter = new AndFilter(new PacketIDFilter(
            reg.getPacketID()), new PacketTypeFilter(IQ.class));
        PacketCollector collector = connection.createPacketCollector(filter);
        connection.sendPacket(reg);
        IQ result = (IQ) collector.nextResult(SmackConfiguration
            .getPacketReplyTimeout());

        // Stop queuing results
        collector.cancel();

        if (result == null) {
            // TODO This exception is shown incorrectly to the user!!
            throw new XMPPException("No response from server.");
        } else if (result.getType() == IQ.Type.ERROR) {
            throw new XMPPException(result.getError());
        } else {
            return (Registration) result;
        }
    }

    /**
     * Returns the service for a user directory. The user directory can be used
     * to perform search queries.
     * 
     * @param connection
     *            the current XMPP connection
     * @param service
     *            a service, normally the domain of a XMPP server
     * @return the service for the user directory or <code>null</code> if it
     *         could not be determined
     * 
     * @See {@link UserSearch#getSearchForm(Connection con, String searchService)}
     */
    public static String getUserDirectoryService(Connection connection,
        String service) {

        ServiceDiscoveryManager manager = ServiceDiscoveryManager
            .getInstanceFor(connection);

        DiscoverItems items;

        try {
            items = manager.discoverItems(service);
        } catch (XMPPException e) {
            log.error("discovery for service '" + service + "' failed", e);
            return null;
        }

        Iterator<DiscoverItems.Item> iter = items.getItems();
        while (iter.hasNext()) {
            DiscoverItems.Item item = iter.next();
            try {
                Iterator<Identity> identities = manager.discoverInfo(
                    item.getEntityID()).getIdentities();
                while (identities.hasNext()) {
                    Identity identity = identities.next();
                    if ("user".equalsIgnoreCase(identity.getType())) {
                        return item.getEntityID();
                    }
                }
            } catch (XMPPException e) {
                log.warn("could not query identity: " + item.getEntityID(), e);
            }
        }

        iter = items.getItems();

        // make a good guess
        while (iter.hasNext()) {
            DiscoverItems.Item item = iter.next();

            String entityID = item.getEntityID();

            if (entityID == null)
                continue;

            if (entityID.startsWith("vjud.") || entityID.startsWith("search.")
                || entityID.startsWith("users.") || entityID.startsWith("jud.")
                || entityID.startsWith("id."))
                return entityID;
        }

        return null;
    }

    /**
     * Returns the service for multiuser chat.
     * 
     * @param connection
     *            the current XMPP connection
     * @param service
     *            a service, normally the domain of a XMPP server
     * @return the service for the multiuser chat or <code>null</code> if it
     *         could not be determined
     */
    public static String getMultiUserChatService(Connection connection,
        String service) {

        ServiceDiscoveryManager manager = ServiceDiscoveryManager
            .getInstanceFor(connection);

        DiscoverItems items;

        try {
            items = manager.discoverItems(service);
        } catch (XMPPException e) {
            log.error("discovery for service '" + service + "' failed", e);
            return null;
        }

        Iterator<DiscoverItems.Item> iter = items.getItems();
        while (iter.hasNext()) {
            DiscoverItems.Item item = iter.next();
            try {
                Iterator<Identity> identities = manager.discoverInfo(
                    item.getEntityID()).getIdentities();
                while (identities.hasNext()) {
                    Identity identity = identities.next();
                    if ("text".equalsIgnoreCase(identity.getType())
                        && "conference"
                            .equalsIgnoreCase(identity.getCategory())) {
                        return item.getEntityID();
                    }
                }
            } catch (XMPPException e) {
                log.warn("could not query identity: " + item.getEntityID(), e);
            }
        }

        return null;
    }
}