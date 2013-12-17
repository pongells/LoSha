/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.packets;

/**
 * Ping packet
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

public class Ping extends IQ implements IQProvider {

	@Override
	public String getChildElementXML() {
		return "<ping xmlns='urn:xmpp:ping' />";
	}

	public String getElementName() {
		return "ping";
	}

	public String getNamespace() {
		return "urn:xmpp:ping";
	}

	@Override
	public IQ parseIQ(final XmlPullParser parser) throws Exception {
		parser.nextTag();
		return new Ping();
	}

}