/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.tasks;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.packets.Geoloc;
import ch.usi.inf.bp.losha.xmpp.users.User;

/**
 * Task to ask Google Servers for details of a given Location
 * and uses the data to fill my location.
 * 
 * @param location
 * 		a location containing some data
 * @return
 * 		a Geoloc object filled with informations
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class GeocodeLocationTask extends AsyncTask<Location, String, Geoloc> {
	private final Geocoder gc;
	private final LoShaService service;
	private final User user;

	public GeocodeLocationTask(final User user) {
		final Context context = Preferences.getAppContext();
		this.gc = new Geocoder(context, Locale.getDefault());
		this.service = LoShaService.getService();
		this.user = user;
	}

	@Override
	protected Geoloc doInBackground(final Location... locations) {
		final Location location = locations[0];
		if (location.getLatitude() == 0d || location.getLongitude() == 0d) {
			return doGeocodeLocation(location);
		} else {
			return doReverseGeocodeLocation(location);
		}
	}

	private Geoloc doGeocodeLocation(final Location location) {
		try {
			final Bundle b = location.getExtras();
			final List<Address> addresses = gc.getFromLocationName(getLocationName(b), 1);

			if (addresses.size() > 0) {
				final Address address = addresses.get(0);
				return new Geoloc(address, location);
			}

		} catch (final IOException e) {
			Utils.log("--> Problem geocoding coordinates.. (no network?)");
		}

		return new Geoloc(location);
	}

	private Geoloc doReverseGeocodeLocation(final Location location) {
		final double lat = location.getLatitude();
		final double lon = location.getLongitude();

		try {
			final List<Address> addresses = gc.getFromLocation(lat, lon, 1);

			if (addresses.size() > 0) {
				final Address address = addresses.get(0);
				return new Geoloc(address, location);
			}

		} catch (final IOException e) {
			Utils.log("-> Problem getting location address.. (no network?)");
		}

		return new Geoloc(lat, lon);
	}

	private String getLocationName(final Bundle b) {
		final String building = b.getString("building");
		final String area = b.getString("area");
		final String street = b.getString("street");
		final String postalCode = b.getString("postalCode");
		final String locality = b.getString("locality");
		final String region = b.getString("region");
		final String country = b.getString("country");

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
	protected void onPostExecute(final Geoloc result) {
		super.onPostExecute(result);
		//can be that the (reverse)geocoding of location fails
		//e.g. using the emulator..
		if (result.getLat() == 0d || result.getLon() == 0d) {
			user.setLocation(null);
		} else {
			user.setLocation(result);
		}
		if (service != null) {
			service.notifyLocationUpdated();
		}
	}
}
