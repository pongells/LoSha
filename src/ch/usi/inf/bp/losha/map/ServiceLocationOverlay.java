/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.map;

import android.content.Context;
import android.location.Location;
import ch.usi.inf.bp.losha.xmpp.users.Me;

import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

/**
 * A very simple Overlay to show my position using the default style,
 * this picks the location from the one set inside the service, therefore
 * a fake location will result as your real location.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class ServiceLocationOverlay extends MyLocationOverlay {
	private final Me me;
	public ServiceLocationOverlay(final Context context, final MapView mapView, final Me me) {
		super(context, mapView);
		this.me = me;
	}

	@Override
	public synchronized void onLocationChanged(final Location location) {
		final Location current = me.getMapLocation();
		if (current != null) {
			super.onLocationChanged(current);
		}
	}
}
