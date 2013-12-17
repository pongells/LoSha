/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.listeners;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Packet;

import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationGranularity;
import ch.usi.inf.bp.losha.xmpp.packets.InitPacket;
import ch.usi.inf.bp.losha.xmpp.packets.LocationSharingRule;

/**
 * Listen for init packet from the Location Component
 * in case of error, the component is down and the app. functionalities
 * are limited.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class InitPacketListener implements PacketListener {
	private final LoShaService service;

	public InitPacketListener() {
		service = LoShaService.getService();
	}

	@Override
	public void processPacket(final Packet init) {
		if (init instanceof InitPacket) {
			final InitPacket initPacket = (InitPacket) init;

			if (initPacket.getError() != null) {
				Utils.log("-> Received init packet: Location Component is down.");
				service.setComponentOnline(false);
				service.notifyComponentDown();
			} else {
				service.setComponentOnline(true);
				// if first login, add default node
				if (initPacket.isFirstLogin()) {
					final LocationSharingRule rule = new LocationSharingRule(LocationGranularity.BEST);
					service.getNodesManager().createLocationNode("My Best Location", rule);
					Utils.log("-> Received init packet: Created default node");
				} else {
					// retrieve my location nodes from the server
					service.getNodesManager().loadLocationNodes();
					Utils.log("-> Received init packet: Location Component is running.");
				}
			}
		}
	}

}
