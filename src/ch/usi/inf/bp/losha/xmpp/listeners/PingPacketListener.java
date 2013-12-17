/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.listeners;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;

import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.packets.Ping;

/**
 * Listen for Ping events and answer with a Pong
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class PingPacketListener implements PacketListener {
	private final Connection connection;

	public PingPacketListener(final Connection connection) {
		this.connection = connection;
	}

	@Override
	public void processPacket(final Packet packet) {
		if (packet instanceof Ping) {
			Utils.log("Ping? Pong!");

			final IQ pong = new IQ() {
				@Override
				public String getChildElementXML() {
					return null;
				}
			};
			pong.setType(Type.RESULT);
			pong.setTo(connection.getServiceName());
			pong.setPacketID(packet.getPacketID());
			connection.sendPacket(pong);
		}
	}

}
