/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.listeners;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.pubsub.EventElement;
import org.jivesoftware.smackx.pubsub.ItemsExtension;
import org.jivesoftware.smackx.pubsub.PayloadItem;

import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.xmpp.packets.Geoloc;
import ch.usi.inf.bp.losha.xmpp.users.Friend;

/**
 * Listener for Geoloc notifications (incoming friends' locations)
 * Used to listen for location push events.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class GeolocPacketListener implements PacketListener {
	private final LoShaService service;

	public GeolocPacketListener(final LoShaService service) {
		this.service = service;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void processPacket(final Packet packet) {
		final EventElement event = (EventElement) packet.getExtension("event",
		"http://jabber.org/protocol/pubsub#event");
		if (event != null) {
			final ItemsExtension ext = (ItemsExtension) event.getExtensions().iterator().next();
			final PayloadItem<Geoloc> item = (PayloadItem<Geoloc>) ext.getItems().iterator().next();

			if (item != null) {
				final Geoloc geoloc = item.getPayload();
				final String friendJID = geoloc.getUserJID();
				final Friend f = service.getFriendsManager().getFriends().getFriend(friendJID);
				f.setCurrentNodeId(geoloc.getSourceNodeId());
				f.loadLocationData(geoloc.getLocation());
			} else {
				// not a geoloc.. discard.
			}
		} else {
			// it is not a pubsub event.. discard.
		}
	}

}
