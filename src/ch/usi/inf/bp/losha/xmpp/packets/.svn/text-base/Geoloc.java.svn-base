/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.packets;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.PacketExtension;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import ch.usi.inf.bp.losha.libs.iso8601dateparser.ISO8601DateParser;
import ch.usi.inf.bp.losha.utils.Utils;

import com.ocpsoft.pretty.time.PrettyTime;

/**
 * GeoLoc (XEP-0080) payload for Items
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class Geoloc implements PacketExtension {
	private double lat;
	private double lon;
	private String text; // THE USER JID and SOURCE NODE
	private String building; // Black Building
	private String area; // USI
	private String street; // Via yada Yada
	private String postalCode; // 6900
	private String locality; // Lugano
	private String region; // Ticino
	private String country; // Switzerland
	private String timestamp; // 2004-02-19T21:12Z
	private double accuracy; // Horizontal GPS error in meters

	public Geoloc() {}

	public Geoloc(final double lat, final double lon) {
		setLat(lat);
		setLon(lon);
	}

	public Geoloc(final Address address, final Location location) {
		if (location.getLatitude() == 0 || location.getLongitude() == 0) {
			setLat(address.getLatitude());
			setLon(address.getLongitude());
		} else {
			setLat(location.getLatitude());
			setLon(location.getLongitude());
		}
		setAccuracy(location.getAccuracy());
		setArea(address.getSubLocality());
		setBuilding(address.getPremises());
		setStreet(address.getThoroughfare());
		setPostalCode(address.getPostalCode());
		setCountry(address.getCountryName());
		setLocality(address.getLocality());
		setRegion(address.getAdminArea());
		final String timestamp = ISO8601DateParser.toString(new Date(location.getTime()));
		setTimestamp(timestamp);
	}

	public Geoloc(final Location location) {
		final Bundle b = location.getExtras();

		setLat(location.getLatitude());
		setLon(location.getLongitude());
		setAccuracy(location.getAccuracy());

		setArea(b.getString("area"));
		setBuilding(b.getString("building"));
		setStreet(b.getString("street"));
		setPostalCode(b.getString("postalCode"));
		setCountry(b.getString("country"));
		setLocality(b.getString("locality"));
		setRegion(b.getString("region"));

		final String timestamp = ISO8601DateParser.toString(new Date(location.getTime()));
		setTimestamp(timestamp);
	}

	public double getAccuracy() {
		return accuracy;
	}

	public String getArea() {
		return area;
	}

	public String getBuilding() {
		return building;
	}

	public String getCountry() {
		return country;
	}

	@Override
	public String getElementName() {
		return "geoloc";
	}

	private String getGeoLoc() {
		String xml;
		xml = "<" + getElementName() + " xmlns='" + getNamespace() + "'>";

		if (lat != 0d) {
			xml += "<lat>" + lat + "</lat>";
		}
		if (lon != 0d) {
			xml += "<lon>" + lon + "</lon>";
		}

		if (text != null) {
			xml += "<text>" + text + "</text>";
		}
		if (building != null) {
			xml += "<building>" + building + "</building>";
		}
		if (area != null) {
			xml += "<area>" + area + "</area>";
		}
		if (postalCode != null) {
			xml += "<postalcode>" + postalCode + "</postalcode>";
		}
		if (street != null) {
			xml += "<street>" + street + "</street>";
		}
		if (locality != null) {
			xml += "<locality>" + locality + "</locality>";
		}
		if (region != null) {
			xml += "<region>" + region + "</region>";
		}
		if (country != null) {
			xml += "<country>" + country + "</country>";
		}
		if (timestamp != null) {
			xml += "<timestamp>" + timestamp + "</timestamp>";
		}
		if (accuracy != 0d) {
			xml += "<accuracy>" + accuracy + "</accuracy>";
		}

		xml += "</" + getElementName() + ">";
		return xml;
	}

	public IQ getIQ() {
		final IQ geoLocIQ = new IQ() {
			@Override
			public String getChildElementXML() {
				return Geoloc.this.getGeoLoc();
			}
		};
		geoLocIQ.setType(Type.RESULT);
		return geoLocIQ;
	}

	public double getLat() {
		return lat;
	}

	public String getLocality() {
		return locality;
	}

	public double getLon() {
		return lon;
	}

	@Override
	public String getNamespace() {
		return "http://jabber.org/protocol/geoloc";
	}

	public String getPostalCode() {
		return postalCode;
	}

	public String getRegion() {
		return region;
	}

	public String getStreet() {
		return street;
	}

	public String getSummary() {
		String location = "";
		if (building != null) {
			location += building + "\n";
		}
		if (area != null) {
			location += area + "\n";
		}
		if (street != null) {
			location += street + "\n";
		}
		if (postalCode != null) {
			location += postalCode + " ";
		}
		if (locality != null) {
			location += locality + "\n";
		}
		if (region != null) {
			location += region + ", ";
		}
		if (country != null) {
			location += country;
		} else {
			location += "Lat: "+lat +"\nLon: "+lon;
		}
		return location;
	}

	//text containing user jid and source node
	//"user,nodeid"
	public String getText() {
		return text;
	}
	public String getUserJID() {
		if (text.contains(",")) {
			return text.split(",")[0];
		} else {
			//backward compatibility
			return text;
		}
	}
	public String getSourceNodeId() {
		if (text.contains(",")) {
			return text.split(",")[1];
		} else {
			return null;
		}
	}

	public String getTimestamp() {
		if (timestamp == null) {
			return "";
		}
		try {
			final Date time = ISO8601DateParser.parse(timestamp);
			final PrettyTime p = new PrettyTime(new Locale("en"));
			return "Last seen "+p.format(time);
			//return new SimpleDateFormat("d MMM yyyy, HH:mm:ss Z").format(time);
		} catch (final ParseException e) {
			Utils.log("-> Error: cannot parse timestamp");
			return timestamp;
		}
	}

	public void setAccuracy(final double accuracy) {
		this.accuracy = accuracy;
	}

	public void setArea(final String area) {
		this.area = area;
	}

	public void setBuilding(final String building) {
		this.building = building;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	public void setLat(final double lat) {
		this.lat = lat;
	}

	public void setLocality(final String locality) {
		this.locality = locality;
	}

	public void setLon(final double lon) {
		this.lon = lon;
	}

	public void setPostalCode(final String postalCode) {
		this.postalCode = postalCode;
	}

	public void setRegion(final String region) {
		this.region = region;
	}

	public void setStreet(final String street) {
		this.street = street;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public void setTimestamp(final String timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		String location = "";
		if (building != null) {
			location += building + ", ";
		}
		if (area != null) {
			location += area + ", ";
		}
		if (street != null) {
			location += street + ", ";
		}
		if (postalCode != null) {
			location += postalCode + ", ";
		}
		if (locality != null) {
			location += locality + ", ";
		}
		if (region != null) {
			location += region + ", ";
		}
		if (country != null) {
			location += country;
		}
		return location;
	}

	@Override
	public String toXML() {
		return getGeoLoc();
	}

	/**
	 * Convert into a Location object
	 * @return
	 * 		Location Object
	 */
	public Location getLocation() {
		final Location loc = new Location("losha");
		loc.setAccuracy((float) accuracy);

		loc.setLatitude(lat);
		loc.setLongitude(lon);

		long time;
		try {
			if (timestamp != null && !timestamp.equals("")) {
				time = ISO8601DateParser.parse(timestamp).getTime();
				loc.setTime(time);
			}
		} catch (final Exception e) {
			Utils.log("-> Me: error parsing location timestamp");
		}

		final Bundle extras = new Bundle();
		extras.putString("text", text);
		extras.putString("building", building);
		extras.putString("area", area);
		extras.putString("street", street);
		extras.putString("postalCode", postalCode);
		extras.putString("locality", locality);
		extras.putString("region", region);
		extras.putString("country", country);
		loc.setExtras(extras);

		return loc;
	}
}