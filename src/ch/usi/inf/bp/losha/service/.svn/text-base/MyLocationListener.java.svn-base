/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.service;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.users.Me;

/**
 * Implementation of Android's LocationListener.
 * Used to grab my current location and store it.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class MyLocationListener implements LocationListener {
	private Location currentLocation;
	private final Me me;

	public MyLocationListener(final LoShaService service) {
		me = service.getMe();
	}

	public Location getLocation() {
		return currentLocation;
	}

	@Override
	public void onLocationChanged(final Location location) {
		currentLocation = location;
		if (location != null && !me.isUsingFakeLocation()) {
			me.loadLocationData(location);
		}
	}

	@Override
	public void onProviderDisabled(final String provider) {
		Utils.log("-> Provider disabled (" + provider + ")");
	}

	@Override
	public void onProviderEnabled(final String provider) {
		Utils.log("-> Provider enabled (" + provider + ")");
	}

	@Override
	public void onStatusChanged(final String provider, final int status,
			final Bundle extras) {
		Utils.log("-> Status changed: " + provider + " (status: " + status+ ")");
	}
}
