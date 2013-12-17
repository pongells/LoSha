/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.packets.providers;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import ch.usi.inf.bp.losha.xmpp.packets.InitPacket;

/**
 * Init Packet Provider
 * Smack providers are used to parse a XMPP packet into a java object.
 * http://www.igniterealtime.org/builds/smack/docs/latest/documentation/providers.html
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class InitPacketProvider implements IQProvider {

	@Override
	public IQ parseIQ(final XmlPullParser parser) throws Exception {
		int eventType = parser.getEventType();
		boolean done = false;
		boolean firstLogin = false;

		if (eventType != XmlPullParser.START_TAG) {
			//nothing
		}

		parser.nextTag();
		while (!done) {
			eventType = parser.getEventType();

			if (eventType == XmlPullParser.START_TAG) {
				final String elemName = parser.getName();

				if ("first".equals(elemName)) {
					firstLogin = true;
					parser.nextText();
				}

				parser.nextTag();
			} else if (eventType == XmlPullParser.END_TAG) {
				if ("init".equals(parser.getName())) {
					done = true;
				} else {
					parser.nextTag();
				}
			} else {
				parser.nextTag();
			}
		}

		InitPacket initPacket = null;
		if (firstLogin) {
			initPacket = new InitPacket(true);
		} else {
			initPacket = new InitPacket();
		}
		return initPacket;
	}
}
