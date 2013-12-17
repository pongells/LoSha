/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.HeaderProvider;
import org.jivesoftware.smackx.provider.HeadersProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.jivesoftware.smackx.pubsub.provider.AffiliationProvider;
import org.jivesoftware.smackx.pubsub.provider.AffiliationsProvider;
import org.jivesoftware.smackx.pubsub.provider.ConfigEventProvider;
import org.jivesoftware.smackx.pubsub.provider.EventProvider;
import org.jivesoftware.smackx.pubsub.provider.FormNodeProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemsProvider;
import org.jivesoftware.smackx.pubsub.provider.PubSubProvider;
import org.jivesoftware.smackx.pubsub.provider.RetractEventProvider;
import org.jivesoftware.smackx.pubsub.provider.SimpleNodeProvider;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionProvider;
import org.jivesoftware.smackx.pubsub.provider.SubscriptionsProvider;
import org.jivesoftware.smackx.search.UserSearch;

import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.tasks.PingTask;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.listeners.GeolocPacketListener;
import ch.usi.inf.bp.losha.xmpp.listeners.InitPacketListener;
import ch.usi.inf.bp.losha.xmpp.listeners.PingPacketListener;
import ch.usi.inf.bp.losha.xmpp.packets.InitPacket;
import ch.usi.inf.bp.losha.xmpp.packets.Ping;
import ch.usi.inf.bp.losha.xmpp.packets.providers.GeolocPacketProvider;
import ch.usi.inf.bp.losha.xmpp.packets.providers.InitPacketProvider;
import ch.usi.inf.bp.losha.xmpp.packets.providers.SubscriptionPacketProvider;

/**
 * Core Model class to interact with the XMPP server and Component.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class XMPPManager {
	public static final String LOCATION_COMPONENT = "location";
	public static final String PUBSUB_COMPONENT = "pubsub";
	public static final String SEARCH_COMPONENT = "search";

	// register SMACK providers
	// see: http://www.igniterealtime.org/builds/smack/docs/latest/documentation/providers.html
	static {
		final ProviderManager pm = ProviderManager.getInstance();
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
		pm.addIQProvider("pubsub", "http://jabber.org/protocol/pubsub", new PubSubProvider());
		pm.addIQProvider("pubsub", "http://jabber.org/protocol/pubsub#owner", new PubSubProvider());

		pm.addExtensionProvider("subscription", PubSubNamespace.OWNER.getXmlns(), new SubscriptionProvider());
		pm.addExtensionProvider("subscription", PubSubNamespace.BASIC.getXmlns(), new SubscriptionProvider());
		pm.addExtensionProvider("create", "http://jabber.org/protocol/pubsub", new SimpleNodeProvider());
		pm.addExtensionProvider("items", "http://jabber.org/protocol/pubsub", new ItemsProvider());
		pm.addExtensionProvider("item", "http://jabber.org/protocol/pubsub", new ItemProvider());
		pm.addExtensionProvider("item", "", new ItemProvider());
		pm.addExtensionProvider("subscriptions", "http://jabber.org/protocol/pubsub", new SubscriptionsProvider());
		pm.addExtensionProvider("subscriptions", "http://jabber.org/protocol/pubsub#owner", new SubscriptionsProvider());
		pm.addExtensionProvider("affiliations", "http://jabber.org/protocol/pubsub", new AffiliationsProvider());
		pm.addExtensionProvider("affiliation", "http://jabber.org/protocol/pubsub", new AffiliationProvider());
		pm.addExtensionProvider("options", "http://jabber.org/protocol/pubsub", new FormNodeProvider());
		pm.addExtensionProvider("configure", "http://jabber.org/protocol/pubsub#owner", new FormNodeProvider());
		pm.addExtensionProvider("default", "http://jabber.org/protocol/pubsub#owner", new FormNodeProvider());
		pm.addExtensionProvider("event", "http://jabber.org/protocol/pubsub#event", new EventProvider());
		pm.addExtensionProvider("configuration", "http://jabber.org/protocol/pubsub#event", new ConfigEventProvider());
		pm.addExtensionProvider("delete", "http://jabber.org/protocol/pubsub#event", new SimpleNodeProvider());
		pm.addExtensionProvider("options", "http://jabber.org/protocol/pubsub#event", new FormNodeProvider());
		pm.addExtensionProvider("items", "http://jabber.org/protocol/pubsub#event", new ItemsProvider());
		pm.addExtensionProvider("item", "http://jabber.org/protocol/pubsub#event", new ItemProvider());
		pm.addExtensionProvider("headers", "http://jabber.org/protocol/shim", new HeaderProvider());
		pm.addExtensionProvider("header", "http://jabber.org/protocol/shim", new HeadersProvider());
		pm.addExtensionProvider("retract", "http://jabber.org/protocol/pubsub#event", new RetractEventProvider());
		pm.addExtensionProvider("purge", "http://jabber.org/protocol/pubsub#event", new SimpleNodeProvider());
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());

		//my providers
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
		pm.addIQProvider("ping", "urn:xmpp:ping", new Ping());
		pm.addIQProvider("init", "ch:usi:inf:losha", new InitPacketProvider());
		pm.addIQProvider("subscription", "ch:usi:inf:losha", new SubscriptionPacketProvider());
		pm.addIQProvider("geoloc", "http://jabber.org/protocol/geoloc", new GeolocPacketProvider());

		pm.addExtensionProvider("geoloc", "http://jabber.org/protocol/geoloc", new GeolocPacketProvider());
	}

	private final LoShaService service;
	private ConnectionConfiguration config;
	private XMPPConnection connection;

	private XMPPConnectionManager connectionManager;
	private XMPPNodesManager nodesManager;
	private XMPPFriendsManager friendsManager;
	private AccountManager accountManager;

	public XMPPManager(final LoShaService service) {
		this.service = service;
	}

	/**
	 * Initializes a connection with the XMPP Server Example:
	 * ("test.dyndns.org", 5222, "pongells.local")
	 * 
	 * @param address
	 *            - The XMPP server address
	 * @param port
	 *            - The XMPP server port
	 * @param serviceName
	 *            - The XMPP service name
	 * @param foregroundService
	 */
	public void init(final String address, final int port, final String serviceName) {
		config = new ConnectionConfiguration(address, port, serviceName);
		//debug sent/received data
		config.setDebuggerEnabled(true);
		config.setReconnectionAllowed(false);
		config.setSecurityMode(SecurityMode.enabled);
		config.setCompressionEnabled(true);

		this.connection = new XMPPConnection(config);
		connectionManager = new XMPPConnectionManager(this, connection, serviceName);

		final String pubsubService = PUBSUB_COMPONENT + "." + serviceName;
		final String locationService = LOCATION_COMPONENT + "." + serviceName;
		final String searchService = SEARCH_COMPONENT + "." + serviceName;

		// listen for PING packets and answer with PONG (not implemented in smack)
		connection.addPacketListener(new PingPacketListener(connection),
				new PacketTypeFilter(Ping.class));

		// listen for INIT packets (from component!)
		final PacketFilter initFilter = new AndFilter(new PacketTypeFilter(InitPacket.class),
				new FromContainsFilter(locationService));
		connection.addPacketListener(new InitPacketListener(), initFilter);

		// listen for PUBSUB EVENT packets (geoloc received)
		final PacketFilter pubsubFilter = new AndFilter(new PacketTypeFilter(Message.class),
				new FromContainsFilter(pubsubService));
		connection.addPacketListener(new GeolocPacketListener(service), pubsubFilter);

		// init managers
		accountManager = new AccountManager(connection);
		nodesManager = new XMPPNodesManager(service, this, pubsubService, locationService);
		final Roster roster = connection.getRoster();
		friendsManager = new XMPPFriendsManager(this, nodesManager, roster, searchService);

		Utils.log("-> XMPP Manager created");
	}

	// ================== USERS ==================

	public XMPPFriendsManager getFriendsManager() {
		return friendsManager;
	}

	// ================== ACCOUNT ==================

	public AccountManager getAccountManager() {
		return accountManager;
	}

	// ================== NODES ==================

	public XMPPNodesManager getNodesManager() {
		return nodesManager;
	}

	// ================== CONNECTION ==================

	public XMPPConnectionManager getConnectionManager() {
		return connectionManager;
	}

	// ================== XMPP ==================

	public boolean sendPacket(final Packet packet) {
		if (connection.isConnected() && connection.isAuthenticated()) {
			connection.sendPacket(packet);
			return true;
		}
		return false;
	}

	public void sendPresenceSubscribe(final String jid) {
		final Presence response = new Presence(Presence.Type.subscribe);
		response.setTo(jid);
		response.setFrom(connection.getUser());
		if (sendPacket(response)) {
			Utils.log("-> Sent: " + response.toXML());
		}
	}

	/**
	 * @return my JID
	 */
	public String getUserJID() {
		return connection.getUser();
	}

	// makeVCard(username, name, email);
	public void setVCard(final String nick, final String name, final String email) {
		final VCard vCard = new VCard();
		try {
			vCard.load(connection);
			vCard.setNickName(nick);
			vCard.setFirstName(name);
			vCard.setLastName(name);
			vCard.setEmailHome(email);
			vCard.save(connection);
		} catch (final Exception e) {
			Utils.log("" + e);
		}
	}

	public void setVCardPicture(final byte[] picture) {
		final VCard vCard = new VCard();
		try {
			vCard.load(connection);
			vCard.setAvatar(picture, "image/png");
			vCard.save(connection);
		} catch (final XMPPException e) {
			Utils.log("-> Cannot load vCard to set picture..");
		}
	}

	public VCard getVCard(final String user) {
		final VCard vCard = new VCard();
		try {
			if (user.equals(connection.getUser())) {
				vCard.load(connection);
			} else {
				vCard.load(connection, user);
			}
		} catch (final XMPPException e) {
			Utils.log("-> Cannot load vCard of " + user);
		}
		return vCard;
	}

	public void ping() {
		new PingTask(connection).execute(null, null, null);
	}
}
