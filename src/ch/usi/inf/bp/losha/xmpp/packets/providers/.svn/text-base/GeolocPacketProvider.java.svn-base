/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.packets.providers;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.PacketExtensionProvider;
import org.xmlpull.v1.XmlPullParser;

import ch.usi.inf.bp.losha.xmpp.packets.Geoloc;

/**
 * Geoloc (XEP-0080) Payload provider for Items
 * Smack providers are used to parse a XMPP packet into a java object.
 * http://www.igniterealtime.org/builds/smack/docs/latest/documentation/providers.html
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class GeolocPacketProvider implements PacketExtensionProvider, IQProvider {
	@Override
	public PacketExtension parseExtension(final XmlPullParser parser)
	throws Exception {
		final Geoloc loc = new Geoloc();

		for (;;) {
			switch (parser.next()) {
			case XmlPullParser.START_TAG:
				final String name = parser.getName();

				if (name.equals("lat")) {
					loc.setLat(Double.parseDouble(parser.nextText().trim()));
					break;
				}
				if (name.equals("lon")) {
					loc.setLon(Double.parseDouble(parser.nextText().trim()));
					break;
				}

				if (name.equals("text")) {
					loc.setText(parser.nextText());
					break;
				}
				if (name.equals("building")) {
					loc.setBuilding(parser.nextText());
					break;
				}
				if (name.equals("area")) {
					loc.setArea(parser.nextText());
					break;
				}
				if (name.equals("postalcode")) {
					loc.setPostalCode(parser.nextText());
					break;
				}
				if (name.equals("street")) {
					loc.setStreet(parser.nextText());
					break;
				}
				if (name.equals("locality")) {
					loc.setLocality(parser.nextText());
					break;
				}
				if (name.equals("region")) {
					loc.setRegion(parser.nextText());
					break;
				}
				if (name.equals("country")) {
					loc.setCountry(parser.nextText());
					break;
				}
				if (name.equals("timestamp")) {
					loc.setTimestamp(parser.nextText());
					break;
				}
				if (name.equals("accuracy")) {
					loc.setAccuracy(Double
							.parseDouble(parser.nextText().trim()));
					break;
				}

				System.out.println("Unknown geoloc-type " + name);
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
				if (parser.getName().equals("geoloc")) {
					return loc;
				}
			}
		}
	}

	@Override
	public IQ parseIQ(final XmlPullParser parser) throws Exception {
		return ((Geoloc) parseExtension(parser)).getIQ();
	}
}
