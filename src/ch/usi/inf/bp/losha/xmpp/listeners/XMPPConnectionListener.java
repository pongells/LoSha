/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.listeners;

import org.jivesoftware.smack.ConnectionListener;

import android.os.Handler;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.utils.Utils;

/**
 * The XMPP connection listener, used to reconnect in case of errors and
 * notify the client of current state (connected/not)
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class XMPPConnectionListener implements ConnectionListener {
	Handler reconnectionHandler = new Handler();
	private final LoShaService service;
	private int reloginAttempt;
	private boolean reconnecting;

	public XMPPConnectionListener(final LoShaService service) {
		this.service = service;
	}

	@Override
	public void connectionClosed() {
		Utils.log("Connection closed");
		service.notifyDisconnected();
		reloginAttempt = 0;
	}

	@Override
	public void connectionClosedOnError(final Exception e) {
		Utils.log("Connection closed on error! " + e);
		service.notifyDisconnected();
		if (reloginAttempt < 5 && !reconnecting) {
			reconnecting = true;
			reconnectionHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					Utils.log("--> Reconnecting.. "+reloginAttempt);
					if (!service.getXMPPManager().getConnectionManager().isConnected()) {
						service.relogin();
					}
					reconnecting = false;
					reloginAttempt++;
				}
			}, 10000);
		}
	}

	@Override
	public void reconnectingIn(final int seconds) {
		Utils.log("reconnection in " + seconds);
	}

	@Override
	public void reconnectionFailed(final Exception e) {
		Utils.log("reconnection failed " + e);
		service.notifyDisconnected();
	}

	@Override
	public void reconnectionSuccessful() {
		Utils.log("reconnection ok");
		service.notifyLogged();
	}
}
