/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.nodes;

/**
 * Enum used to subscribe a friend to one of my nodes,
 * as it turns out we don't need this to unsubscribe (block) someone.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public enum SubscriptionAction {
	SUBSCRIBE("subscribe"),
	REMOVE("remove");

	public static SubscriptionAction fromString(final String text) {
		if (text != null) {
			for (final SubscriptionAction b : SubscriptionAction.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		throw new IllegalArgumentException("No constant with text " + text
				+ " found");
	}

	private String text;

	SubscriptionAction(final String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
