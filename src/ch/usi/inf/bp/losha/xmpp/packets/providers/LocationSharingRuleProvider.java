/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.packets.providers;

import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

import ch.usi.inf.bp.losha.xmpp.nodes.LocationGranularity;
import ch.usi.inf.bp.losha.xmpp.packets.Geoloc;
import ch.usi.inf.bp.losha.xmpp.packets.LocationSharingRule;

/**
 * Location Sharing Rule Provider used to parse a node (group) rule.
 * 
 * Smack providers are used to parse a XMPP packet into a java object.
 * http://www.igniterealtime.org/builds/smack/docs/latest/documentation/providers.html
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class LocationSharingRuleProvider implements PacketExtensionProvider {

	@Override
	public PacketExtension parseExtension(final XmlPullParser parser)
	throws Exception {
		final LocationSharingRule sharingRule = new LocationSharingRule();

		for (;;) {
			switch (parser.next()) {
			case XmlPullParser.START_TAG:
				final String name = parser.getName();

				if (name.equals("granularity")) {
					final String granularity = parser.getAttributeValue("", "level");
					final LocationGranularity gran = LocationGranularity.fromString(granularity);
					sharingRule.setGranularity(gran);
					if (gran.equals(LocationGranularity.CUSTOM)) {
						int radius;
						try {
							radius = Integer.parseInt(parser.getAttributeValue("", "radius"));
						} catch (final NumberFormatException e) {
							radius = 0;
						}
						sharingRule.setRadius(radius);
					}
					break;
				}
				if (name.equals("geoloc")) {
					// GeoLocPacket geoLoc = (GeoLocPacket)
					// PacketParserUtils.parsePacketExtension("geoloc",
					// "http://jabber.org/protocol/geoloc", parser);
					sharingRule.setGeoLoc((Geoloc) new GeolocPacketProvider().parseExtension(parser));
					break;
				}
				if (name.equals("time")) {
					final String fromTime = parser
					.getAttributeValue("", "from");
					final String toTime = parser.getAttributeValue("", "to");
					sharingRule.setFromTime(fromTime);
					sharingRule.setToTime(toTime);
					break;
				}

				System.out.println("Unknown-type " + name);
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
				if (parser.getName().equals("rule")) {
					return sharingRule;
				}
			}
		}
	}
}
