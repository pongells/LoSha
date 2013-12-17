/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.users;

import java.util.Date;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smackx.packet.VCard;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import ch.usi.inf.bp.losha.libs.iso8601dateparser.ISO8601DateParser;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.tasks.GeocodeLocationTask;
import ch.usi.inf.bp.losha.xmpp.XMPPManager;
import ch.usi.inf.bp.losha.xmpp.packets.Geoloc;

/**
 * Object representing the current connected user and all his data.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class Me implements User {
	private final XMPPManager xmpp;
	private AccountManager accountManager;
	private VCard vCard;
	private String email;
	private String name;
	private Bitmap avatar;

	private Geoloc location;

	private boolean useFakeLocation;

	public Me(final XMPPManager xmpp) {
		this.xmpp = xmpp;
	}

	public String getEmail() {
		return email;
	}

	public Geoloc getLocation() {
		return location;
	}

	public Location getMapLocation() {
		if (location == null) {
			return null;
		}
		return location.getLocation();
	}

	public String getName() {
		return name;
	}

	public String getUser() {
		return xmpp.getUserJID();
	}

	public String getBareJID() {
		return getUser().split("/")[0];
	}

	public String getUsername() {
		return getUser().split("@")[0];
	}

	public boolean hasVCard() {
		return vCard != null;
	}

	public void init() {
		accountManager = xmpp.getAccountManager();

		try {
			setName(accountManager.getAccountAttribute("name"));
			setEmail(accountManager.getAccountAttribute("email"));
		} catch (final IllegalStateException e) {
			xmpp.getConnectionManager().disconnect();
			return;
		}

		if (Preferences.isUsingFakeLocation()) {
			useFakeLocation = true;
			final double fakeLat = Preferences.getFakeLat();
			final double fakeLon = Preferences.getFakeLon();
			setFakeLocation(fakeLat, fakeLon);
		}
	}

	public boolean isUsingFakeLocation() {
		return useFakeLocation;
	}

	public void sendCurrentLocation() {
		if (xmpp != null && xmpp.getConnectionManager().isLoggedIn()) {
			if (useFakeLocation) {
				sendFakeLocation();
			} else {
				sendLocation();
			}
		}
	}

	private void sendFakeLocation() {
		// make the current location "fresh"
		final String timestamp = ISO8601DateParser.toString(new Date(System
				.currentTimeMillis()));
		location.setTimestamp(timestamp);
		sendLocation();
	}

	private void sendLocation() {
		if (location != null) {
			xmpp.getNodesManager().sendGeoloc(location);
		}
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public void setFakeLocation(final double fakeLat, final double fakeLon) {
		Preferences.setFakeLocation(fakeLat, fakeLon);

		final Location fakeLoc = new Location("losha");
		fakeLoc.setLatitude(fakeLat);
		fakeLoc.setLongitude(fakeLon);
		fakeLoc.setTime(System.currentTimeMillis());

		loadLocationData(fakeLoc);
	}

	@Override
	public void loadLocationData(final Location location) {
		new GeocodeLocationTask(this).execute(location);
	}
	@Override
	public void setLocation(final Geoloc geoloc) {
		location = geoloc;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setUseFakeLocation(final boolean useFake) {
		Preferences.useFakeLocation(useFake);
		useFakeLocation = useFake;
	}

	public void setAvatar(final Bitmap bitmap) {
		this.avatar = bitmap;
	}

	public Bitmap getAvatar() {
		return avatar;
	}

	public void setVCard(final VCard vCard) {
		this.vCard = vCard;
		final byte[] avatarByte = vCard.getAvatar();
		if (avatarByte != null) {
			setAvatar(BitmapFactory.decodeByteArray(avatarByte, 0, avatarByte.length));
		}
		setEmail(vCard.getEmailHome());
		setName(vCard.getFirstName());
	}
}
