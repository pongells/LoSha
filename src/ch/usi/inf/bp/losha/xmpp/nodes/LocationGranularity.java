/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.nodes;


/**
 * Enumerator of Granularity, each one has a tag and a title.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public enum LocationGranularity {
	BEST("best", "Best Accuracy"),
	CITY("city", "City Level"),
	STATE("state", "State Level"),
	COUNTRY("country", "Country Level"),
	CUSTOM("custom", "Custom Radius");

	public static LocationGranularity fromString(final String text) {
		if (text != null) {
			for (final LocationGranularity b : LocationGranularity.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		throw new IllegalArgumentException("No constant with text " + text
				+ " found");
	}

	private String text;
	private String desc;

	LocationGranularity(final String text, final String desc) {
		this.text = text;
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public String getText() {
		return text;
	}
}
