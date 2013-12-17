/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.packets;

import org.jivesoftware.smack.packet.PacketExtension;

import ch.usi.inf.bp.losha.xmpp.nodes.LocationGranularity;

/**
 * SharingRule
 * <rule>
 * 	<granularity level="XXX" radius="XXX"/>
 * 	<geoloc>XXX</geoloc>
 * 	<time from="12:12:.." to="13:13:.."/>
 * 	[<proximity>] //could be extended to do so..
 * </rule>
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class LocationSharingRule implements PacketExtension {
	private LocationGranularity granularity;
	private Geoloc geoLoc;
	private String fromTime;
	private String toTime;
	private int radius;

	public LocationSharingRule() {}

	public LocationSharingRule(final LocationGranularity granularity) {
		if (!granularity.equals(LocationGranularity.CUSTOM)) {
			setGranularity(granularity);
		} else {
			setGranularity(LocationGranularity.BEST);
		}
	}

	public LocationSharingRule(final LocationGranularity granularity, final int radius) {
		if (!granularity.equals(LocationGranularity.CUSTOM)) {
			setGranularity(granularity);
		} else if (radius == 0) {
			setGranularity(LocationGranularity.BEST);
		} else {
			setGranularity(granularity);
			setRadius(radius);
		}
	}

	@Override
	public String getElementName() {
		return "rule";
	}
	@Override
	public String getNamespace() {
		return "ch:usi:inf:losha";
	}

	public void setGranularity(final LocationGranularity granularity) {
		this.granularity = granularity;
	}
	public LocationGranularity getGranularity() {
		return granularity;
	}

	public int getRadius() {
		return radius;
	}
	public void setRadius(final int radius) {
		this.radius = radius;
	}

	public void setGeoLoc(final Geoloc geoLoc) {
		this.geoLoc = geoLoc;
	}
	public Geoloc getGeoLoc() {
		return geoLoc;
	}

	public void setFromTime(final String fromTime) {
		this.fromTime = fromTime;
	}
	public String getFromTime() {
		return fromTime;
	}

	public void setToTime(final String toTime) {
		this.toTime = toTime;
	}
	public String getToTime() {
		return toTime;
	}

	public boolean hasTime() {
		return toTime != null || fromTime != null;
	}

	public boolean hasGeoLoc() {
		return geoLoc != null;
	}

	@Override
	public String toXML() {
		String xml;
		xml = "<"+getElementName()+" xmlns='"+ getNamespace() +"'>";

		if (granularity.equals(LocationGranularity.CUSTOM)) {
			xml += "<granularity level='" + granularity.getText() + "' radius='"+radius+"'/>";
		} else {
			xml += "<granularity level='" + granularity.getText() + "'/>";
		}

		if (hasGeoLoc()) {
			xml += geoLoc.toXML();
		}
		if (hasTime()) {
			xml += "<time from='"+fromTime+"' to='"+toTime+"'/>";
		}

		xml += "</"+getElementName()+">";
		return xml;
	}


}