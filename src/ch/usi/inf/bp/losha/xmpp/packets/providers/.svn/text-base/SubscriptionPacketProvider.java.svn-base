/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.packets.providers;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import ch.usi.inf.bp.losha.xmpp.nodes.SubscriptionAction;
import ch.usi.inf.bp.losha.xmpp.packets.SubscriptionPacket;

/**
 * Subscription Packet provider used to subscribe/remove users from nodes
 *
 * Smack providers are used to parse a XMPP packet into a java object.
 * http://www.igniterealtime.org/builds/smack/docs/latest/documentation/providers.html
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class SubscriptionPacketProvider implements IQProvider {

	@Override
	public IQ parseIQ(final XmlPullParser parser) throws Exception {
		final SubscriptionPacket subscription = new SubscriptionPacket();

		for (;;) {
			switch (parser.next()) {
			case XmlPullParser.START_TAG:
				final String name = parser.getName();

				if (name.equals("jid")) {
					subscription.setUserJID(parser.nextText());
					break;
				}
				if (name.equals("node")) {
					subscription.setNodeID(parser.nextText());
					break;
				}
				if (name.equals("action")) {
					final String actionString = parser.nextText();
					final SubscriptionAction action = SubscriptionAction
					.fromString(actionString);
					subscription.setAction(action);
					break;
				}

				System.out.println("Unknown subscription-type " + name);
				int stack = 1;
				do {
					switch (parser.next()) {
					case XmlPullParser.END_TAG:
						stack--;
						break;
					case XmlPullParser.START_TAG:
						stack++;
						break;
					}
				} while (stack > 0);
				break;

			case XmlPullParser.TEXT:
				break;

			case XmlPullParser.END_TAG:
				if (parser.getName().equals("subscription")) {
					return subscription;
				}
			}
		}
	}
}
