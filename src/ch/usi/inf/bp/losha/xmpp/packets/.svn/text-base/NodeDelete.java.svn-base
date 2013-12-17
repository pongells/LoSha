/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.packets;

import org.jivesoftware.smack.packet.IQ;

/**
 * Packet used to notify the Component we have deleted a node.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class NodeDelete extends IQ {
	private final String nodeID;

	/**
	 * Inform Location Component that the node has been deleted on the pubsub service.
	 * (the pubsub service informs the component only in case of new nodes)
	 */
	public NodeDelete(final String nodeID) {
		this.nodeID = nodeID;
	}

	@Override
	public String getChildElementXML() {
		String xml;
		xml = "<" + getElementName() + " xmlns='" + getNamespace() + "'>";
		xml += "<node>" + nodeID + "</node>";
		xml += "</" + getElementName() + ">";
		return xml;
	}

	public String getElementName() {
		return "delete";
	}

	public String getNamespace() {
		return "ch:usi:inf:losha";
	}

	public String getNodeID() {
		return nodeID;
	}
}
