/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.service;

import java.util.HashMap;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import ch.usi.inf.bp.losha.MainActivity;
import ch.usi.inf.bp.losha.R;
import ch.usi.inf.bp.losha.network.ConnectionChangeReceiver;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.tasks.FindUserTask;
import ch.usi.inf.bp.losha.tasks.LoginTask;
import ch.usi.inf.bp.losha.tasks.LogoutTask;
import ch.usi.inf.bp.losha.tasks.RegisterTask;
import ch.usi.inf.bp.losha.tasks.UserProfileTask;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.XMPPConnectionManager;
import ch.usi.inf.bp.losha.xmpp.XMPPFriendsManager;
import ch.usi.inf.bp.losha.xmpp.XMPPManager;
import ch.usi.inf.bp.losha.xmpp.XMPPNodesManager;
import ch.usi.inf.bp.losha.xmpp.users.Me;

/**
 * The service at the core of the application:
 * its main task is to refresh the current location and send locations updates.
 * 
 * It is used by all the activities to do background jobs
 * and can send messages to the activities (notifications of async events).
 * 
 * In a model-view-controller pattern the Activities would be the view,
 * and the XMPP package the Model. This is the Controller.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class LoShaService extends Service {
	// Singleton
	private static LoShaService service;
	public static LoShaService getService() {
		return service;
	}

	//location
	private LocationManager locationManager;
	private MyLocationListener locationListener;
	private String locationProvider;
	private int refreshDelay;
	private int deliveryDelay;

	// hashmap storing client messengers (activity title, messenger)
	private final HashMap<String, Messenger> clients = new HashMap<String, Messenger>();
	private final Handler locationDeliveryHandler = new Handler();
	private final Messenger mMessenger = new Messenger(new IncomingHandler());

	// model
	private LoginTask loginTask;
	private XMPPManager xmpp;
	private Me me;
	private boolean componentOnline;
	private ConnectionChangeReceiver connectionManager;
	private String lastUsername;
	private String lastPassword;
	private String tempUser;
	private String tempName;
	private String tempMail;
	private Boolean firstLoginEver = false;

	@Override
	public void onCreate() {
		super.onCreate();
		setNotification("LoSha Started!", "Status: Offline");
		initLocationService();
	}

	@SuppressWarnings("static-access")
	public void initLocationService() {
		this.service = this;
		this.xmpp = new XMPPManager(this);
		this.me = new Me(xmpp);
		this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		this.connectionManager = new ConnectionChangeReceiver();
		connectionManager.register();

		final Criteria criteria = new Criteria();
		locationProvider = locationManager.getBestProvider(criteria, false);
		final Location location = locationManager.getLastKnownLocation(locationProvider);

		if (location != null) {
			me.loadLocationData(location);
			Utils.log("-> Provider " + locationProvider + " has been selected.");
		} else {
			Utils.log("-> Provider not available.");
		}

		locationListener = new MyLocationListener(this);
	}

	public void setLastKnownLocation() {
		final Location location = locationManager.getLastKnownLocation(locationProvider);

		if (location != null) {
			me.loadLocationData(location);
			Utils.log("-> Setting last known location..");
		} else {
			Utils.log("-> Cannot find last known location..");
		}
	}

	/**
	 * Instantiates a new XMPP Manager based on Preferences
	 */
	public void initXMPPManager() {
		final String serverAddress = Preferences.getServerAddress();
		final String serverName = Preferences.getServerName();
		final int serverPort = Preferences.getServerPort();
		xmpp.init(serverAddress, serverPort, serverName);
		Utils.log("--> XMPP Manager initiated");
	}



	// ================== USERS ==================

	public XMPPFriendsManager getFriendsManager() {
		if (xmpp != null) {
			return xmpp.getFriendsManager();
		} else {
			return null;
		}
	}


	/**
	 * Search for users (Name, JID and Email)
	 * 
	 * @param toSearch
	 *            the user to search (can use wildcard *)
	 * @return list of found users
	 */
	public void findUsers(final String toSearch, final Messenger to) {
		final FindUserTask findUserTask = new FindUserTask(this, to);
		findUserTask.execute(toSearch);
	}

	/**
	 * Get the connected User (me)
	 * @return
	 * 			Me
	 */
	public Me getMe() {
		return me;
	}

	/**
	 * Get an user's vCard (extra info)
	 * 
	 * @param the
	 *            user id
	 * @param the
	 *            messenger to send the info
	 */
	public void getUserProfile(final String user, final Messenger to) {
		final UserProfileTask userProfileTask = new UserProfileTask(this, to);
		userProfileTask.execute(user);
	}

	// ================== GROUPS/NODES ==================

	public XMPPNodesManager getNodesManager() {
		if (xmpp != null) {
			return xmpp.getNodesManager();
		} else {
			return null;
		}
	}

	// ================== CONNECTION ==================

	public XMPPConnectionManager getConnectionManager() {
		if (xmpp != null) {
			return xmpp.getConnectionManager();
		} else {
			return null;
		}
	}

	/**
	 * Register to the server
	 * 
	 * @param username
	 * @param password
	 * @param name
	 * @param email
	 * @param msg
	 */
	public void register(final String username, final String password,
			final String name, final String email, final Messenger msg) {
		initXMPPManager();
		this.tempUser = username;
		this.tempMail = email;
		this.tempName = name;
		final RegisterTask regTask = new RegisterTask(this, msg);
		regTask.execute(username, password, name, email);
	}

	/**
	 * Login to the server
	 * 
	 * @param user
	 * @param pass
	 * @param msg
	 */
	public void login(final String user, final String pass, final Messenger to) {
		initXMPPManager();
		loginTask = new LoginTask(this, to);
		loginTask.execute(user, pass);
		lastUsername = user;
		lastPassword = pass;
		startLocationUpdate();
	}

	public void relogin() {
		login(lastUsername, lastPassword, mMessenger);
	}

	/**
	 * Disconnect from the server
	 */
	public void disconnect() {
		Preferences.setSessionInit(false);

		//block login if still running
		if (loginTask != null && loginTask.getStatus().equals(Status.RUNNING)) {
			loginTask.cancel(true);
		}

		//try to logout
		new LogoutTask(this).execute(null, null, null);

		//stop location sharing updates
		stopLocationUpdate();
	}

	public boolean isFirstLoginEver() {
		return firstLoginEver;
	}
	
	public void setFirstLoginEver(boolean first) {
		this.firstLoginEver = first;
	}
	
	public void resetTempData() {
		this.tempMail = null;
		this.tempName = null;
		this.tempUser = null;
		this.firstLoginEver = false;
	}

	public String getTempUser() {
		return tempUser;
	}
	public String getTempName() {
		return tempName;
	}
	public String getTempMail() {
		return tempMail;
	}
	
	// ================== SERVICE ==================

	@Override
	public IBinder onBind(final Intent intent) {
		return mMessenger.getBinder();
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags,
			final int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		stopForeground(true);
		if (xmpp != null) {
			disconnect();
		}
		connectionManager.unRegister();
		Utils.log("-> Service destroyed");
	}

	// ================== CLIENTS COMMUNICATION ==================

	private static final String ACTION_OK = "success";
	public static final int REGISTER_CLIENT = 0;
	public static final int LOGGED = 1;
	public static final int DISCONNECTED = 2;
	public static final int LOGIN_ANSWER = 3;
	public static final int REGISTER_ANSWER = 4;
	public static final int FOUND_USERS = 5;
	public static final int USER_PROFILE = 6;
	public static final int LOCATION = 7;
	public static final int FRIEND_UPDATE = 8;
	
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case REGISTER_CLIENT:
				registerClient((Activity) msg.obj, msg.replyTo);
				break;
			case LOGIN_ANSWER:
				final String result = (String) msg.obj;
				if (result != null) {
					if (result.equals(ACTION_OK)) {
						Preferences.setSessionInit(true);
					}
				}
				break;
			}
		}
	}

	public void registerClient(final Activity client, final Messenger msg) {
		final String activityTitle = (String) client.getTitle();
		clients.put(activityTitle, msg);
		Utils.log("--> registered client: " + activityTitle);
	}

	public void sendMessage(final Messenger to, final int messageType, final Object payload) {
		final Message msg = Message.obtain(null, messageType, payload);
		this.sendMessage(to, msg);
	}

	private void sendMessage(final Messenger to, final Message msg) {
		if (to == null) {
			return;
		}
		try {
			to.send(msg);
		} catch (final RemoteException e) {
			Utils.log("Cannot send message: activity offline");
		}
	}

	// ================== NOTIFICATIONS ==================

	public void notifyAllClients(final int messageType, final Object obj) {
		for (final Messenger to : clients.values()) {
			this.sendMessage(to, Message.obtain(null, messageType, obj));
		}
	}

	public void notifyComponentDown() {
		setNotification("Limited functionality", "Location Component is down.");
	}

	public void notifyDisconnected() {
		setNotification("Disconnected :(", "Status: Offline");
		notifyAllClients(DISCONNECTED, null);
	}

	public void notifyFriendUpdate() {
		notifyAllClients(FRIEND_UPDATE, null);
	}

	public void notifyLocationUpdated() {
		notifyAllClients(LOCATION, null);
	}

	public void notifyLogged() {
		setNotification("Connected", "Status: Online");
		notifyAllClients(LOGGED, null);
	}

	public void notifyRegistered() {
		setNotification("Registered", "Status: Offline");
	}

	private void setNotification(final String ticker, final String text) {
		int logo = R.drawable.logo_losha_notif_offline;
		final XMPPConnectionManager connection = getConnectionManager();
		if (connection != null && connection.isLoggedIn()) {
			logo = R.drawable.logo_losha_notif_connected;
		}
		
		final Notification notification = new Notification(logo, ticker, System.currentTimeMillis());

		final Intent pending = new Intent(this, MainActivity.class);
		pending.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		final PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				pending, Intent.FLAG_ACTIVITY_CLEAR_TOP);

		notification.setLatestEventInfo(this,
				getText(R.string.remote_service_label), text, contentIntent);
		startForeground(R.string.remote_service_label2, notification);
	}

	// ================== MY LOCATION ==================

	/**
	 * Runnable to deliver location each X seconds
	 */
	private final Runnable deliverLocationTask = new Runnable() {
		@Override
		public void run() {
			me.sendCurrentLocation();
			locationDeliveryHandler.postDelayed(deliverLocationTask,
					deliveryDelay * 1000);
		}
	};

	public void startLocationDelivery() {
		locationDeliveryHandler.removeCallbacks(deliverLocationTask);
		locationDeliveryHandler.postDelayed(deliverLocationTask, 500);
		Utils.log("-> Started location delivery (delay: " + deliveryDelay + "seconds)");
	}

	public void startLocationUpdate() {
		refreshDelay = Preferences.getLocationRefreshDelay();
		deliveryDelay = Preferences.getLocationDeliveryDelay();

		locationManager.requestLocationUpdates(locationProvider,
				refreshDelay * 1000, 0, locationListener);

		if (Preferences.isDeliveryEnabled()) {
			startLocationDelivery();
		}

		Utils.log("-> Started location update (min delay: " + refreshDelay + "seconds)");
	}

	public void stopLocationDelivery() {
		locationDeliveryHandler.removeCallbacks(deliverLocationTask);
		Utils.log("-> Stopped location delivery");
	}

	public void stopLocationUpdate() {
		locationManager.removeUpdates(locationListener);
		stopLocationDelivery();
		Utils.log("-> Stopped location updates");
	}

	// ================== VARIOUS ==================

	/**
	 * @return the XMPP manager
	 */
	public XMPPManager getXMPPManager() {
		return xmpp;
	}

	public boolean isComponentOnline() {
		return componentOnline;
	}

	public void setComponentOnline(final boolean online) {
		componentOnline = online;
	}

	/**
	 * Send a Ping Packet
	 */
	public void sendPing() {
		xmpp.ping();
	}

}