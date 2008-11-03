package de.fu_berlin.inf.dpp.net.jingle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import org.jivesoftware.smackx.jingle.JingleSession;
import org.jivesoftware.smackx.jingle.media.JingleMediaSession;
import org.jivesoftware.smackx.jingle.media.PayloadType;
import org.jivesoftware.smackx.jingle.nat.TransportCandidate;

import de.fu_berlin.inf.dpp.net.internal.JingleFileTransferData;
import de.fu_berlin.inf.dpp.net.jingle.receiver.FileTransferTCPReceiver;
import de.fu_berlin.inf.dpp.net.jingle.transmitter.FileTransferTCPTransmitter;

public class FileTransferSession extends JingleMediaSession {

    private IFileTransferTransmitter transmitter = null;
    private IFileTransferReceiver receiver = null;
    // private XMPPConnection connection;

    /* transfer information */
    private JingleFileTransferData[] transferData;
    private JingleFileTransferProcessMonitor monitor;
    private IJingleFileTransferListener listener;

    public FileTransferSession(PayloadType payloadType,
	    TransportCandidate remote, TransportCandidate local,
	    String mediaLocator, JingleSession jingleSession) {
	super(payloadType, remote, local, mediaLocator, jingleSession);
	initialize();
    }

    public FileTransferSession(PayloadType payloadType,
	    TransportCandidate remote, TransportCandidate local,
	    String mediaLocator, JingleSession jingleSession,
	    JingleFileTransferData[] transferData,
	    JingleFileTransferProcessMonitor monitor) {
	super(payloadType, remote, local, mediaLocator, jingleSession);

	this.transferData = transferData;
	this.monitor = monitor;

	initialize();
    }

    @Override
    public void initialize() {
	JingleSession session = getJingleSession();
	if (!((session != null) && (session.getInitiator().equals(session
		.getConnection().getUser())))) {
	    // if (this.getJingleSession() instanceof JingleSession) {
	    try {

		this.receiver = new FileTransferTCPReceiver(InetAddress
			.getByName(getRemote().getIp()), getRemote().getPort(),
			getLocal().getPort());
		this.receiver.addJingleFileTransferListener(this.listener);
		// /* call listener. */
		// if (listener != null) {
		// listener
		// .incommingFileTransfer(((FileTransferTCPReceiver) receiver)
		// .getMonitor());
		// }
		System.out.println("Receiving on:"
			+ this.receiver.getLocalPort());
	    } catch (UnknownHostException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	} else {
	    try {

		InetAddress remote = InetAddress.getByName(getRemote().getIp());
		System.out.println("local Port: " + getLocal().getPort());
		this.transmitter = new FileTransferTCPTransmitter(getLocal()
			.getPort(), remote, getRemote().getPort(),
			this.transferData, this.monitor);
		this.transmitter.sendFileData(this.transferData);
		this.transmitter.addJingleFileTransferListener(this.listener);

	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

    }

    /**
     * Set transmit activity. If the active is true, the instance should
     * trasmit. If it is set to false, the instance should pause transmit.
     * 
     * @param active
     *            active state
     */
    @Override
    public void setTrasmit(boolean active) {
	this.transmitter.setTransmit(true);

    }

    /**
     * For NAT Reasons this method does nothing. Use startTransmit() to start
     * transmit and receive jmf
     */
    @Override
    public void startReceive() {
	// Do nothing.

    }

    /**
     * Starts transmission and for NAT Traversal reasons start receiving also.
     */
    @Override
    public void startTrasmit() {
	new Thread(this.transmitter).start();
    }

    @Override
    public void stopReceive() {
	if (this.receiver != null) {
	    this.receiver.stop();
	}
    }

    /**
     * Stops transmission and for NAT Traversal reasons stop receiving also.
     */
    @Override
    public void stopTrasmit() {
	if (this.transmitter != null) {
	    this.transmitter.stop();
	}
    }

    public JingleFileTransferProcessMonitor getProcessMonitor() {
	return this.monitor;
    }

    /**
     * send new data with current session
     */
    public void sendFileData(JingleFileTransferData[] transferData)
	    throws JingleSessionException {
	IJingleFileTransferConnection conn = null;
	if (this.receiver != null) {
	    conn = this.receiver;
	}
	if (this.transmitter != null) {
	    conn = this.transmitter;
	}

	if (conn == null) {
	    throw new JingleSessionException("connection stream not exists.");
	}

	/* send data with existing streams */
	conn.sendFileData(transferData);
    }

    /*
     * TODO: 1. Diese beiden Methoden auslagern 2. Listener Liste umsetzen.
     */

    public void addJingleFileTransferListener(
	    IJingleFileTransferListener listener) {
	this.listener = listener;
	if (this.receiver != null) {
	    this.receiver.addJingleFileTransferListener(listener);
	}
	if (this.transmitter != null) {
	    this.transmitter.addJingleFileTransferListener(listener);
	}
    }

    public void removeJingleFileTransferListener(
	    IJingleFileTransferListener listener) {
	this.listener = null;
	if (this.receiver != null) {
	    removeJingleFileTransferListener(listener);
	}
	if (this.transmitter != null) {
	    removeJingleFileTransferListener(listener);
	}
    }

}
