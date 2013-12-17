/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.packets;

import org.jivesoftware.smack.packet.IQ;

/**
 * Usage Info packet used to send info to the server
 * This could be extended to send any kind of information just by adding
 * XML tags.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class UsageInfo extends IQ {
	private String jid;

	/**
	 * Usage info
	 */
	public UsageInfo(final String jid) {
		this.jid = jid;
	}

	@Override
	public String getChildElementXML() {
		String xml;
		xml = "<" + getElementName() + " xmlns='" + getNamespace() + "'>";
		xml += "<loc user='"+jid+"'/>";
		xml += "</" + getElementName() + ">";
		return xml;
	}

	public String getElementName() {
		return "usage";
	}

	public String getNamespace() {
		return "ch:usi:inf:losha";
	}

	public void setJID(final String jid) {
		this.jid = jid;
	}
	public String getJID() {
		return jid;
	}
}
