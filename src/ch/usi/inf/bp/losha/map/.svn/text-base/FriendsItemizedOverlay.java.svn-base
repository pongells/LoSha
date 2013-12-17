/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.map;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import ch.usi.inf.bp.losha.friends.FriendProfileActivity;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationGranularity;
import ch.usi.inf.bp.losha.xmpp.packets.Geoloc;
import ch.usi.inf.bp.losha.xmpp.users.Friend;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;

/**
 * This BalloonItemizedOverlay shows friends' locations on the map,
 * the marker is generated on the fly and only if visible.
 *
 * Clicking on a marker will open a balloon with user informations,
 * clicking on the balloon will open the friend's profile.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class FriendsItemizedOverlay extends BalloonItemizedOverlay<OverlayItem> {
	private final ArrayList<Friend> currentFriends;
	private final Context context;

	public FriendsItemizedOverlay(final Drawable defaultMarker, final MapView mapView, final ArrayList<Friend> currentFriends, final Context context) {
		super(boundCenter(defaultMarker), mapView);
		this.context = context;
		this.currentFriends = currentFriends;

		for (final Friend f : currentFriends) {
			f.makeMarker();
		}
	}

	@Override
	protected OverlayItem createItem(final int i) {
		//hide open balloon (they don't move)
		hideBalloon();

		//create overlay
		final Friend friend = currentFriends.get(i);
		final Geoloc loc = friend.getLocation();
		final GeoPoint point = new GeoPoint((int)(loc.getLat()*1E6),(int)(loc.getLon()*1E6));
		final int granularityType = (int) loc.getAccuracy();
		final LocationGranularity granularity;
		String desc;
		if (granularityType > LocationGranularity.values().length) {
			granularity = LocationGranularity.CUSTOM;
			desc = granularity.getDesc() +" ("+granularityType+" meters)";
		} else {
			granularity = LocationGranularity.values()[granularityType];
			desc = granularity.getDesc();
		}

		final OverlayItem overlayItem = new OverlayItem(
				point,
				friend.getName(),
				loc.getSummary()+"\n"+
				loc.getTimestamp()+"\n"+
				desc);

		final Drawable marker = friend.getMarker();
		this.setBalloonBottomOffset(marker.getIntrinsicHeight()+22);
		overlayItem.setMarker(marker);

		return overlayItem;
	}

	@Override
	public int size() {
		return currentFriends.size();
	}

	@Override
	protected boolean onBalloonTap(final int index, final OverlayItem item) {
		final Intent intent = new Intent(context, FriendProfileActivity.class);
		intent.putExtra("user", currentFriends.get(index).getUser());
		context.startActivity(intent);
		return true;
	}

	public void rePopulate() {
		this.populate();
	}

}
