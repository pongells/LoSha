/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.users;

import android.location.Location;
import ch.usi.inf.bp.losha.xmpp.packets.Geoloc;

/**
 * Utility interface to pass a generic User to the Geocoding task,
 * i.e. both Me and Friend have to request their location info.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public interface User {
	public void loadLocationData(Location location);
	public void setLocation(Geoloc result);
}
