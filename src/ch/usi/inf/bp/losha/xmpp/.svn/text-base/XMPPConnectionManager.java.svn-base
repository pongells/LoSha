/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp;

import java.util.HashMap;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.XMPPError;

import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.listeners.XMPPConnectionListener;

/**
 * This class is used to manage connection, login, registration, ..
 * it is also used to send different presences based on the configuration.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class XMPPConnectionManager {
	private static final String ACTION_OK = "success";
	private final XMPPConnection connection;
	private final LoShaService service;
	private final XMPPManager xmpp;
	private final XMPPConnectionListener connectionListener;
	private final String serviceName;

	public XMPPConnectionManager(final XMPPManager xmpp, final XMPPConnection connection, final String serviceName) {
		this.service = LoShaService.getService();
		this.xmpp = xmpp;
		this.connection = connection;
		this.serviceName = serviceName;
		this.connectionListener = new XMPPConnectionListener(service);
	}

	/**
	 * Connect to the server
	 * 
	 * @return status message (either XMPPManager.ACTION_OK or error message)
	 */
	public String connect() {
		Utils.log("--> connecting..");
		String returnValue = null;

		try {
			connection.connect();
		} catch (final XMPPException e) {
			Utils.log("EX Cannot connect: " + e);
			if (e.toString().contains("No response from")) {
				returnValue = "No response from server.";
			}
			if (e.getWrappedThrowable() == null) {
				returnValue = "Connection problem.";
			} else {
				returnValue = e.getWrappedThrowable().getMessage();
			}
		} catch (final IllegalStateException e) {
			Utils.log("Already connected " + e);
		}

		if (isConnected()) {
			// set answer
			returnValue = ACTION_OK;
			// add listener
			connection.removeConnectionListener(connectionListener);
			connection.addConnectionListener(connectionListener);
			Utils.log("--> connected");
		}

		return returnValue;
	}


	/**
	 * Disconnect from the server
	 * 
	 * @return
	 */
	public boolean disconnect() {
		if (connection.isConnected()) {
			connection.disconnect();
			return true;
		} else {
			return false;
		}
	}

	public XMPPConnection getConnection() {
		return connection;
	}

	/**
	 * Login to the server
	 * 
	 * @param user
	 *            - the username (first part of JID)
	 * @param password
	 *            - the JID password
	 * @return status message (either XMPPManager.ACTION_OK or error message)
	 */
	public String login(final String user, final String password) {
		final String resource = "losha";
		String returnValue = null;

		try {
			connection.login(user + "@" + serviceName, password, resource);
		} catch (final XMPPException e) {
			returnValue = "Authentication failed.";
			disconnect(); // need this, otherwise smack problems
			Utils.log("Cannot login: " + e);
		} catch (final IllegalStateException e) {
			// already logged
			Utils.log("Already loggedin " + e);
		}

		if (isConnected() && isLoggedIn()) {
			service.getMe().init();
			returnValue = ACTION_OK;
			Utils.log("--> logged in");
			service.notifyLogged();
			initUserConnection();
		}
		return returnValue;
	}

	public void initUserConnection() {
		sendInitPresence();

		// notify the server component
		xmpp.getNodesManager().sendInitPacket();

		// first login after registration, create basic vCard
		if (service != null && service.isFirstLoginEver()) {
			xmpp.setVCard(service.getTempUser(), service.getTempName(), service.getTempMail());
			service.resetTempData();
		}
	}

	private void sendInitPresence() {
		Presence presence;
		if (!Preferences.isPushEnabled() && !Preferences.isDeliveryEnabled()) {
			presence = new Presence(Presence.Type.unavailable);
			presence.setMode(Mode.xa);
			presence.setStatus("not sharing nor receiving");
		} else if (!Preferences.isPushEnabled()) {
			presence = new Presence(Presence.Type.unavailable);
			presence.setMode(Mode.dnd);
			presence.setStatus("not receiving");
		} else if (!Preferences.isDeliveryEnabled()) {
			presence = new Presence(Presence.Type.available);
			presence.setMode(Mode.away);
			presence.setStatus("not sharing");
		} else {
			presence = new Presence(Presence.Type.available);
			presence.setMode(Mode.available);
			presence.setStatus("default");
		}
		xmpp.sendPacket(presence);
	}

	/**
	 * Register new user
	 * 
	 * @return
	 */
	public String register(final String username, final String password,
			final String name, final String email) {
		final HashMap<String, String> attributes = new HashMap<String, String>();
		if (name != null) {
			attributes.put("name", name);
		}
		if (email != null) {
			attributes.put("email", email);
		}

		String returnValue = null;

		try {
			xmpp.getAccountManager().createAccount(username, password, attributes);
			returnValue = ACTION_OK;
			service.notifyRegistered();
			service.setFirstLoginEver(true);
		} catch (final XMPPException e) {
			Utils.log("register error:" + e.getXMPPError());
			final XMPPError er = e.getXMPPError();
			if (er != null) {
				switch (er.getCode()) {
				case 409:
					returnValue = "UserName already in use";
					break;
				default:
					returnValue = e.getMessage();
				}
			}
		} catch (final IllegalStateException e) {
			returnValue = e.getMessage();
		}

		return returnValue;
	}

	public boolean isConnected() {
		return connection.isConnected();
	}

	public boolean isLoggedIn() {
		return connection.isAuthenticated();
	}
}
