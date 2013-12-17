/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.map;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smackx.packet.VCard;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import ch.usi.inf.bp.losha.MainActivity;
import ch.usi.inf.bp.losha.R;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.xmpp.packets.Geoloc;
import ch.usi.inf.bp.losha.xmpp.users.Friend;
import ch.usi.inf.bp.losha.xmpp.users.FriendsRepository;
import ch.usi.inf.bp.losha.xmpp.users.Me;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * The map.
 * This activity is used both to show the friends' badges,
 * select an area for the area filter and a location for the fake location.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class MyMapActivity extends MapActivity {
	private final Messenger mMessenger = new Messenger(new IncomingHandler());
	private LoShaService service;

	private FriendsRepository friends;

	private List<Overlay> overlays;
	private ServiceLocationOverlay myLocationOverlay;
	private SelectLocationOverlay selectOverlay;
	private FriendsItemizedOverlay itemizedOverlay;

	private MapController mapController;
	private MapView map;
	private Intent intent;

	private boolean selectArea;
	private int radius;

	private ArrayList<Friend> currentFriends;
	private Me me;
	
	@Override
	public void onCreate(final Bundle bundle) {
		super.onCreate(bundle);
		this.setContentView(R.layout.my_map);

		((TextView) findViewById(R.id.title_text)).setText(getTitle());
		((LinearLayout) findViewById(R.id.titleBar)).setBackgroundColor(Preferences.getTitleBackgroundColor());

		intent = getIntent();

		map = (MapView) findViewById(R.id.mapview);
		map.setBuiltInZoomControls(true);

		mapController = map.getController();
		mapController.setZoom(14);

		overlays = map.getOverlays();
	}

	@Override
	protected void onStart() {
		super.onStart();

		service = LoShaService.getService();
		service.registerClient(this, mMessenger);
		
		friends = service.getFriendsManager().getFriends();
		currentFriends = friends.getCurrentFriendsWithKnownLocation();
		
		// show my location (default)
		me = service.getMe();
		myLocationOverlay = new ServiceLocationOverlay(this, map, me);
		overlays.add(myLocationOverlay);

		// show friends locations (colored badge representing granularity)
		final Drawable drawable = getResources().getDrawable(R.drawable.marker2);
		itemizedOverlay = new FriendsItemizedOverlay(drawable, map, currentFriends, this);
		overlays.add(itemizedOverlay);
		
		myLocationOverlay.enableMyLocation();
		if (!selectArea) {
			myLocationOverlay.enableCompass();
		}
		final Location myloc = me.getMapLocation();
		if (myloc != null) {
			myLocationOverlay.onLocationChanged(me.getMapLocation());
		}
		
		//load avatar
		for (final Friend f : currentFriends) {
			if (!f.hasVCard()) {
				service.getUserProfile(f.getUser(), mMessenger);
			}
		}
		
	}
	
	@Override
	public void onResume() {
		super.onResume();

		//Select Fake Location
		final boolean selectLocation = intent.getBooleanExtra("select", false);
		if (selectLocation) {
			selectOverlay = new SelectLocationOverlay(this, false);
			overlays.add(selectOverlay);
			((LinearLayout) findViewById(R.id.bottom_bar)).setVisibility(View.VISIBLE);
			Toast.makeText(this, "Tap on the map to place a marker", Toast.LENGTH_LONG).show();
		}

		//Select Area
		selectArea = intent.getBooleanExtra("select_area", false);
		if (selectArea) {
			selectOverlay = new SelectLocationOverlay(this, true);
			overlays.add(selectOverlay);
			((LinearLayout) findViewById(R.id.bottom_bar)).setVisibility(View.VISIBLE);
			final SeekBar seekBar = (SeekBar)findViewById(R.id.seekbar);
			seekBar.setVisibility(View.VISIBLE);
			seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				@Override
				public void onStopTrackingTouch(final SeekBar seekBar) {}

				@Override
				public void onStartTrackingTouch(final SeekBar seekBar) {
					final Projection proj = map.getProjection();
					final GeoPoint topLeft = proj.fromPixels(0, 0);
					final GeoPoint topRight = proj.fromPixels(map.getWidth()-1, 0);

					final double leftLat = topLeft.getLatitudeE6()/1E6;
					final double leftLon = topLeft.getLongitudeE6()/1E6;
					final double rightLat = topRight.getLatitudeE6()/1E6;
					final double  rightLon = topRight.getLongitudeE6()/1E6;

					final float[] results = new float[6];
					Location.distanceBetween(leftLat, leftLon, rightLat, rightLon, results);
					final int visibleDistanceMeters = (int) results[0];

					seekBar.setMax(visibleDistanceMeters/2);
				}

				@Override
				public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
					radius = progress;
					map.invalidate();
				}
			});
			Toast.makeText(this, "Tap on the map to place a marker and use the scrollbar to select the distance", Toast.LENGTH_LONG).show();
		}

		//center friend
		final String user = intent.getStringExtra("user");
		if (user != null) {
			centerUser(user);
		}
		
		updateMap();
	}

	public void onLocationClick(final View target) {
		final GeoPoint myLoc = myLocationOverlay.getMyLocation();
		if (myLoc != null)
			mapController.animateTo(myLoc);
		else 
			Toast.makeText(this, "Cannot get a fix. Check your settings.", Toast.LENGTH_SHORT).show();
	}

	public void onSelectLocationClick(final View target) {
		final GeoPoint point = selectOverlay.getPoint();
		if (point != null) {
			intent.putExtra("lat", point.getLatitudeE6());
			intent.putExtra("lon", point.getLongitudeE6());

			if (selectArea) {
				intent.putExtra("rad", radius);
			}

			this.setResult(0, intent);
			finish();
		}
	}

	public int getRadius() {
		return radius;
	}

	private void centerUser(final String user) {
		final Friend friend = friends.getFriend(user);
		final Geoloc geoloc = friend.getLocation();

		if (geoloc != null) {
			final int lat = (int) (geoloc.getLat() * 1E6);
			final int lon = (int) (geoloc.getLon() * 1E6);

			final GeoPoint point = new GeoPoint(lat, lon);
			mapController.animateTo(point);
		}
	}

	public void onHomeClick(final View target) {
		final Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	// ================== ACTIVITY CYCLE ==================

	@Override
	public void onPause() {
		super.onPause();
		service = null;
		myLocationOverlay.disableMyLocation();
		if (!selectArea) {
			myLocationOverlay.disableCompass();
		}
	}

	private void updateMap() {
		itemizedOverlay.rePopulate();
		map.invalidate();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	// ================== SERVICE RELATED ==================
	/**
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case LoShaService.LOCATION:
				updateMap();
				break;
			case LoShaService.USER_PROFILE:
				final VCard result = (VCard) msg.obj;
				if (result != null) {
					final Friend f = friends.getFriend(result.getFrom());
					if (f != null) {
						f.setVCard(result);
					}
					updateMap();
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}
}
