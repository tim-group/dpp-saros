/**
 *
 */
package de.fu_berlin.inf.dpp.net.internal.extensions;

import de.fu_berlin.inf.dpp.annotations.Component;

@Component(module = "net")
public class CancelInviteExtension extends SarosSessionPacketExtension {

    private String errorMessage;

    public CancelInviteExtension(String sessionID, String errorMessage) {
        super(sessionID);
        if ((errorMessage != null) && (errorMessage.length() > 0))
            this.errorMessage = errorMessage;
    }

    /**
     * Returns the error message for this cancellation.
     * 
     * @return the error message or <code>null</code> if the remote contact
     *         cancelled the invitation manually
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    public static class Provider extends
        SarosSessionPacketExtension.Provider<CancelInviteExtension> {
        public Provider() {
            super("cancelInvitation", CancelInviteExtension.class);
        }
    }
}