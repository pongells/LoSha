/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.NodeExtension;
import org.jivesoftware.smackx.pubsub.NodeType;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubElementType;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.pubsub.SubscriptionsExtension;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smackx.pubsub.packet.PubSubNamespace;
import org.jivesoftware.smackx.pubsub.packet.SyncPacketSend;

import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.tasks.LoadFriendLocation;
import ch.usi.inf.bp.losha.tasks.LoadFriendsLocation;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationNode;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationNodesRepository;
import ch.usi.inf.bp.losha.xmpp.nodes.SubscriptionAction;
import ch.usi.inf.bp.losha.xmpp.packets.Geoloc;
import ch.usi.inf.bp.losha.xmpp.packets.InitPacket;
import ch.usi.inf.bp.losha.xmpp.packets.LocationSharingRule;
import ch.usi.inf.bp.losha.xmpp.packets.NodeDelete;
import ch.usi.inf.bp.losha.xmpp.packets.NodeUpdate;
import ch.usi.inf.bp.losha.xmpp.packets.SubscriptionPacket;
import ch.usi.inf.bp.losha.xmpp.packets.UsageInfo;
import ch.usi.inf.bp.losha.xmpp.users.Friend;

/**
 * Uses a PubSubManager to create modify and delete nodes (groups).
 * 
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class XMPPNodesManager extends PubSubManager {
	private final LoShaService service;
	private final XMPPManager xmpp;
	private final String locationService;
	private final String pubsubService;
	private final LocationNodesRepository nodes;

	public XMPPNodesManager(final LoShaService service, final XMPPManager xmpp,
			final String pubsubService, final String locationService) {
		super(xmpp.getConnectionManager().getConnection(), pubsubService);
		this.service = service;
		this.xmpp = xmpp;
		this.locationService = locationService;
		this.pubsubService = pubsubService;
		this.nodes = new LocationNodesRepository();
	}

	/**
	 * Create a LocationNode on the server.
	 */
	public LeafNode createLocationNode(final String nodeTitle, final LocationSharingRule sharingRule) {
		final String user = service.getMe().getUsername();
		final int titleHash = nodeTitle.hashCode();
		final String nodeID = user + "/" + titleHash;

		final ConfigureForm form = new ConfigureForm(FormType.submit);
		// send to connected users only
		form.setPresenceBasedDelivery(true);
		// limited access to who may publish loc
		form.setPublishModel(PublishModel.publishers);
		// limited access to items and subscription
		form.setAccessModel(AccessModel.roster);
		// send data (geoloc)
		form.setDeliverPayloads(true);
		// keep items
		form.setPersistentItems(true);
		// not a collection node
		form.setNodeType(NodeType.leaf);

		form.setCollection(user);
		form.setTitle(nodeTitle);
		form.setRosterGroupsAllowed(Arrays.asList(nodeTitle));
		form.setMaxItems(1); // only 1 per node (latest position)

		final FormField description = new FormField("pubsub#description");
		description.setType("text-single");
		form.addField(description);
		form.setAnswer("pubsub#description",
				Utils.toDisableTags(sharingRule.toXML()));

		// do not notify subscribers..
		form.setNotifyRetract(false);
		form.setNotifyConfig(false);
		form.setNotifyDelete(false);

		LeafNode pubsubNode = null;
		try {
			pubsubNode = (LeafNode) createNode(nodeID, form);
			// remove my subscription to the node (added by default)
			pubsubNode.unsubscribe(service.getMe().getBareJID());
			// store the node locally
			storeNode(pubsubNode);
			Utils.log("-> Node '" + nodeTitle + "' created");
		} catch (final XMPPException e1) {
			Utils.log("-> Failed to create node " + nodeTitle + " --> " + e1);
			return null;
		}
		return pubsubNode;
	}

	/**
	 * Get all the subscriptions to a node
	 * @param nodeID
	 * 			the node id
	 * @return
	 * 			list of subscriptions
	 */
	public List<Subscription> getSubscriptions(final String nodeID) {
		List<Subscription> subList = null;

		try {
			// THIS IS NOT IMPLEMENTED INTO SMACK (BUG)
			final PubSub request = new PubSub();
			request.setTo(pubsubService);
			request.setType(Type.GET);
			request.setPubSubNamespace(PubSubNamespace.OWNER);
			request.addExtension(new NodeExtension(
					PubSubElementType.SUBSCRIPTIONS_OWNER, nodeID));
			final PubSub reply = (PubSub) SyncPacketSend.getReply(
					xmpp.getConnectionManager().getConnection(), request);
			final SubscriptionsExtension subElem = (SubscriptionsExtension) reply
			.getExtension(PubSubElementType.SUBSCRIPTIONS);

			subList = subElem.getSubscriptions();

		} catch (final XMPPException e) {
			Utils.log("-> Error: could not retrieve subscriptions.");
		}

		return subList;
	}

	/**
	 * Retrieve location nodes from the server and populate HashMap.
	 */
	public void loadLocationNodes() {
		ArrayList<Affiliation> affis = null;
		try {
			// request nodes that I am affiliated to (owner or access)
			affis = (ArrayList<Affiliation>) getAffiliations();

			if (affis != null) {
				for (final Affiliation affi : affis) {
					final Affiliation.Type type = affi.getType();
					final String nodeID = affi.getNodeId();
					// he node labeled "history" contains the history of my
					// latest positions..
					// I don't need to show it (i.e. I don't want to subscribe a
					// friend to it)
					if (!nodeID.contains("history")) {
						final LeafNode node = (LeafNode) getNode(nodeID);
						if (type.equals(Affiliation.Type.owner)) {
							// if owner: store my node into the repository
							storeNode(node);
						}
					}
				}
			}

			refreshLocationsAsync();
		} catch (final XMPPException e) {
			Utils.log("-> Could not load affiliations! " + e);
		}
	}

	public void refreshLocationsAsync() {
		new LoadFriendsLocation().execute(null,null,null);
	}

	public void refreshLocations() {
		List<Subscription> subList = null;
		try {
			// THIS IS NOT IMPLEMENTED INTO SMACK (BUG)
			final PubSub request = new PubSub();
			request.setTo(pubsubService);
			request.setType(Type.GET);
			request.setPubSubNamespace(PubSubNamespace.BASIC);
			request.addExtension(new NodeExtension(
					PubSubElementType.SUBSCRIPTIONS));
			final PubSub reply = (PubSub) SyncPacketSend.getReply(
					xmpp.getConnectionManager().getConnection(), request);
			final SubscriptionsExtension subElem = (SubscriptionsExtension) reply
			.getExtension(PubSubElementType.SUBSCRIPTIONS);

			subList = subElem.getSubscriptions();
			for (final Subscription s : subList) {
				final LeafNode node = (LeafNode) getNode(s.getNode());
				initFriendLocation(node, s.getId());
			}
			if (subList.isEmpty()) {
				service.notifyLocationUpdated();
			}
		} catch (final XMPPException e) {
			Utils.log("-> Error: could not retrieve subscriptions.");
		}
	}

	public void refreshLocationAsync(final Friend friend) {
		new LoadFriendLocation().execute(friend);
	}

	public void refreshLocation(final Friend friend) {
		try {
			final String currentNodeId = friend.getCurrentNodeId();
			if (currentNodeId != null) {
				final LeafNode currentNode = (LeafNode) getNode(currentNodeId);
				final Subscription sub = currentNode.getSubscriptions().get(0);
				initFriendLocation(currentNode, sub.getId());
			}
			service.notifyLocationUpdated();
		} catch (final XMPPException e) {
			Utils.log("-> Error: could not retrieve node.");
		}
	}

	private void initFriendLocation(final LeafNode node, final String subscriptionID) {
		try {
			final List<PayloadItem<Geoloc>> items = node.getItems(1, subscriptionID);
			if (!items.isEmpty()) {
				final PayloadItem<Geoloc> item = items.get(0);
				final Geoloc geoloc = item.getPayload();
				final String friendJID = geoloc.getUserJID();
				final Friend friend = xmpp.getFriendsManager().getFriends().getFriend(friendJID);
				friend.setCurrentNodeId(geoloc.getSourceNodeId());
				friend.loadLocationData(geoloc.getLocation());
				Utils.log("--> received last location of: " + geoloc.getText());
			}
		} catch (final XMPPException e) {
			Utils.log("-> Could not retrieve node items! " + e);
		}
	}

	public void removeSubscription(final LeafNode node, final Subscription subscription) {
		try {
			node.unsubscribe(subscription.getJid(), subscription.getId());
		} catch (final XMPPException e) {
			Utils.log("-> Error: could not remove user subscription.");
		}
	}

	public void sendGeoloc(final Geoloc geoloc) {
		if (Preferences.isKeepHistoryEnabled()) {
			geoloc.setText("keep");
		} else {
			geoloc.setText(null);
		}
		final IQ geolocIQ = geoloc.getIQ();
		geolocIQ.setTo(locationService);
		xmpp.sendPacket(geolocIQ);
	}

	public void sendInitPacket() {
		final InitPacket iq = new InitPacket();
		iq.setTo(locationService);
		xmpp.sendPacket(iq);
	}

	public void sendSubscriptionUpdate(final String userJID, final String nodeID, final SubscriptionAction action) {
		final SubscriptionPacket subscription = new SubscriptionPacket(userJID, nodeID, action);
		subscription.setTo(locationService);
		xmpp.sendPacket(subscription);
	}

	public void storeNode(final LeafNode node) {
		final LocationNode myNode = new LocationNode(node);
		nodes.addNode(myNode.getNodeTitle(), myNode);
	}

	public void deleteNode(final LocationNode node) {
		try {
			this.deleteNode(node.getNodeID());
			nodes.deleteNode(node.getNodeTitle());
			final NodeDelete delPacket = new NodeDelete(node.getNodeID());
			delPacket.setTo(locationService);
			delPacket.setType(Type.RESULT);
			xmpp.sendPacket(delPacket);
		} catch (final XMPPException e) {
			Utils.log("--> Error: cannot remove node "+node.getNodeTitle());
		}
	}

	public void sendNodeUpdate(final String nodeID) {
		final NodeUpdate update = new NodeUpdate(nodeID);
		update.setTo(locationService);
		update.setType(Type.RESULT);
		xmpp.sendPacket(update);
	}

	public LocationNodesRepository getNodes() {
		return nodes;
	}

	public void sendUsageLocationSeen(final String userJID) {
		final UsageInfo usage = new UsageInfo(userJID);
		usage.setTo(locationService);
		usage.setType(Type.RESULT);
		xmpp.sendPacket(usage);
		Utils.log("--> Usage sent: "+usage.toXML());
	}
}
