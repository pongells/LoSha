/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.packets;

import org.jivesoftware.smack.packet.IQ;

/**
 * InitPacket to let the Component know we are connected.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 * 
 */
public class InitPacket extends IQ {
	private final boolean firstLogin;

	/**
	 * Connection packet to inform Component of user Login or first login ever.
	 */
	public InitPacket() {
		firstLogin = false;
	}

	public InitPacket(final boolean firstLogin) {
		this.firstLogin = firstLogin;
	}

	@Override
	public String getChildElementXML() {
		String xml;
		if (!firstLogin) {
			xml = "<" + getElementName() + " xmlns='" + getNamespace() + "' />";
		} else {
			xml = "<" + getElementName() + " xmlns='" + getNamespace() + "' >";
			xml += "<first>" + firstLogin + "</first>";
			xml += "</" + getElementName() + ">";
		}
		return xml;
	}

	public String getElementName() {
		return "init";
	}

	public String getNamespace() {
		return "ch:usi:inf:losha";
	}

	public boolean isFirstLogin() {
		return firstLogin;
	}
}
