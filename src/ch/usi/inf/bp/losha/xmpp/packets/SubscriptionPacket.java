/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.packets;

import org.jivesoftware.smack.packet.IQ;

import ch.usi.inf.bp.losha.xmpp.nodes.SubscriptionAction;

/**
 * Subscription Packet used to subscribe/remove users from nodes
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class SubscriptionPacket extends IQ {
	private String userJID;
	private String nodeID;
	private SubscriptionAction action;

	public SubscriptionPacket() {
	}

	public SubscriptionPacket(final String userJID, final String nodeID,
			final SubscriptionAction action) {
		setUserJID(userJID);
		setNodeID(nodeID);
		setAction(action);
		setType(Type.RESULT);
	}

	public SubscriptionAction getAction() {
		return action;
	}

	@Override
	public String getChildElementXML() {
		String xml;
		xml = "<" + getElementName() + " xmlns='" + getNamespace() + "'>";

		xml += "<jid>" + userJID + "</jid>";
		xml += "<node>" + nodeID + "</node>";
		xml += "<action>" + action.getText() + "</action>";

		xml += "</" + getElementName() + ">";
		return xml;
	}

	public String getElementName() {
		return "subscription";
	}

	public String getNamespace() {
		return "ch:usi:inf:losha";
	}

	public String getNodeID() {
		return nodeID;
	}

	public String getUserJID() {
		return userJID;
	}

	public void setAction(final SubscriptionAction action) {
		this.action = action;
	}

	public void setNodeID(final String nodeID) {
		this.nodeID = nodeID;
	}

	public void setUserJID(final String userJID) {
		this.userJID = userJID;
	}

}