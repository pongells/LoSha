/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.profile;

import java.io.ByteArrayOutputStream;

import org.jivesoftware.smackx.packet.VCard;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import ch.usi.inf.bp.losha.MainActivity;
import ch.usi.inf.bp.losha.R;
import ch.usi.inf.bp.losha.map.MyMapActivity;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.utils.SectionLabel;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.packets.Geoloc;
import ch.usi.inf.bp.losha.xmpp.users.Me;

/**
 * Profile Activity
 * Shows the currently connected user's profile with name, email,
 * avatar (avatar selection on click), current location, link to location on map,
 * setter for fake location and a button to manually send the current location.
 * 
 * Menu: toggle sharing, change avatar
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class ProfileActivity extends Activity {
	private final Messenger mMessenger = new Messenger(new IncomingHandler());
	private LoShaService service;
	private Me me;

	private TextView userText;
	private TextView email;
	private TextView location;
	private TextView locationTime;
	private ToggleButton toggle;
	private ImageButton fakeLocButton;
	private EditText fakeLatitude;
	private EditText fakeLongitude;

	private ImageView refreshLocationButton;

	private boolean result;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.profile);

		((TextView) findViewById(R.id.title_text)).setText(getTitle());
		((LinearLayout) findViewById(R.id.titleBar)).setBackgroundColor(Preferences.getTitleBackgroundColor());
		Utils.colorAllTargets((ScrollView) findViewById(R.id.scrollView), "section", Preferences.getSectionBackgroundColor());

		userText = (TextView) findViewById(R.id.friend_name);
		email = (TextView) findViewById(R.id.friend_email);
		location = (TextView) findViewById(R.id.friend_location);
		locationTime = (TextView) findViewById(R.id.friend_location_time);

		toggle = (ToggleButton) findViewById(R.id.toggle_fake_button);
		fakeLocButton = (ImageButton) findViewById(R.id.fake_location_button);
		fakeLatitude = (EditText) findViewById(R.id.fake_latitude);
		fakeLongitude = (EditText) findViewById(R.id.fake_longitude);

		final SectionLabel locationTitle = (SectionLabel) findViewById(R.id.location_title);
		refreshLocationButton = (ImageView) locationTitle.findViewById(R.id.refreshButton);
	}

	@Override
	protected void onStart() {
		super.onStart();
		service = LoShaService.getService();
		service.registerClient(this, mMessenger);
		me = service.getMe();

		if (!me.hasVCard()) {
			loadUserInfo();
		} else {
			refreshUserInfo();
		}

		final Geoloc geoloc = me.getLocation();
		if (geoloc != null) {
			location.setText(geoloc.getSummary());
			locationTime.setText(geoloc.getTimestamp());
			if (!result) {
				fakeLatitude.setText("" + geoloc.getLat());
				fakeLongitude.setText("" + geoloc.getLon());
			}
		}
	}

	private final void loadUserInfo() {
		toggleRefresh(true);
		service.getUserProfile(me.getUser(), mMessenger);
	}

	private void toggleRefresh(final boolean refreshing) {
		findViewById(R.id.btn_title_refresh).setVisibility(
				refreshing ? View.GONE : View.VISIBLE);
		findViewById(R.id.title_refresh_progress).setVisibility(
				refreshing ? View.VISIBLE : View.GONE);
	}

	private void refreshUserInfo() {
		if (me == null || me.getUser() == null) {
			return;
		}

		userText.setText(me.getName() + " (" + me.getUser().split("@")[0] + ")");
		email.setText(me.getEmail());
		setProfilePicture(me.getAvatar());

		final SectionLabel locationTitle = (SectionLabel) findViewById(R.id.location_title);
		if (!Preferences.isDeliveryEnabled()) {
			locationTitle.setText(locationTitle.getText() + " (not sharing)");
		} else {
			locationTitle.setText(getString(R.string.location_title));
		}

		final Geoloc geoloc = me.getLocation();
		if (geoloc != null) {
			location.setText(geoloc.getSummary());
			locationTime.setText(geoloc.getTimestamp());
		} else {
			location.setText("Not available");
		}

		if (me.isUsingFakeLocation()) {
			toggle.setChecked(true);
			toggleFakeLocationInput(false);
		}

		toggleRefresh(false);
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0 && resultCode == 0 && data != null) {
			result = true;
			final int lat = data.getIntExtra("lat", 0);
			final int lon = data.getIntExtra("lon", 0);
			fakeLatitude.setText("" + lat / 1E6);
			fakeLongitude.setText("" + lon / 1E6);
		}

		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				final Uri selectedImageUri = data.getData();
				final int imageId = getImageID(selectedImageUri);
				if (imageId == -1) {
					Toast.makeText(this, "Error: Please use the default Gallery app.", Toast.LENGTH_LONG).show();
				} else {
					final Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(this.getContentResolver(),
							imageId, MediaStore.Images.Thumbnails.MICRO_KIND, null);
					Preferences.setProfilePictureID(imageId);

					final ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bitmap.compress(CompressFormat.PNG, 0, bos);
					final byte[] bitmapdata = bos.toByteArray();
					service.getXMPPManager().setVCardPicture(bitmapdata);

					setProfilePicture(bitmap);
				}
			}
		}
	}

	private void setProfilePicture(final Bitmap bitmap) {
		if (bitmap != null) {
			final ImageView image = (ImageView) findViewById(R.id.userPicture);
			image.setImageBitmap(bitmap);
		}
	}


	public int getImageID(final Uri uri) {
		final String[] projection = { MediaStore.Images.Media._ID };
		final Cursor cursor = managedQuery(uri, projection, null, null, null);
		if(cursor!=null) {
			cursor.moveToFirst();
			return cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
		} else {
			return -1;
		}
	}

	private void setFakeLocation() {
		final String fakeLat = fakeLatitude.getText().toString();
		final String fakeLon = fakeLongitude.getText().toString();
		if (fakeLat != null && !fakeLat.equals("") && fakeLon != null
				&& !fakeLon.equals("")) {

			final double fakeLatDouble = new Double(fakeLat);
			final double fakeLonDouble = new Double(fakeLon);

			me.setFakeLocation(fakeLatDouble, fakeLonDouble);
		}
	}

	private void toggleFakeLocationInput(final boolean enabled) {
		if (enabled) {
			refreshLocationButton.setVisibility(View.VISIBLE);
		} else {
			refreshLocationButton.setVisibility(View.GONE);
		}
		fakeLatitude.setEnabled(enabled);
		fakeLatitude.setFocusable(enabled);
		fakeLatitude.setFocusableInTouchMode(enabled);
		fakeLongitude.setEnabled(enabled);
		fakeLongitude.setFocusable(enabled);
		fakeLongitude.setFocusableInTouchMode(enabled);
		fakeLocButton.setEnabled(enabled);
	}

	// ================== CLICK EVENTS ==================

	public void onSelectUserPictureClick(final View target) {
		final Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
	}

	public void onToggleFakeLocationClick(final View target) {
		final ToggleButton btn = (ToggleButton) target;
		if (btn.isChecked()) {
			me.setUseFakeLocation(true);
			setFakeLocation();
			toggleFakeLocationInput(false);
			refreshUserInfo();
		} else {
			me.setUseFakeLocation(false);
			toggleFakeLocationInput(true);
		}
	}

	public void onHomeClick(final View target) {
		final Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	public void onLocationClick(final View target) {
		final Intent mapIntent = new Intent(this, MyMapActivity.class);
		startActivity(mapIntent);
	}

	public void onRefreshClick(final View target) {
		loadUserInfo();
	}

	public void onRefreshSectionClick(final View target) {
		toggleRefresh(true);
		service.setLastKnownLocation();
		Toast.makeText(this, "Refreshing location..", Toast.LENGTH_SHORT).show();
	}

	public void onSelectFakeLocationClick(final View target) {
		final Intent mapIntent = new Intent(this, MyMapActivity.class);
		mapIntent.putExtra("select", true);
		startActivityForResult(mapIntent, 0);
	}

	public void onSendLocationClick(final View target) {
		me.sendCurrentLocation();
		Toast.makeText(this, getString(R.string.location_sent), Toast.LENGTH_SHORT).show();
	}

	// ================== MENU ==================

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.profile, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		if (Preferences.isDeliveryEnabled()) {
			menu.findItem(R.id.start_location_sharing).setVisible(false).setEnabled(false);
			menu.findItem(R.id.stop_location_sharing).setVisible(true).setEnabled(true);
		} else {
			menu.findItem(R.id.stop_location_sharing).setVisible(false).setEnabled(false);
			menu.findItem(R.id.start_location_sharing).setVisible(true).setEnabled(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.start_location_sharing:
			service.startLocationDelivery();
			Preferences.setDeliveryEnabled(true);
			Preferences.commit();
			Toast.makeText(this, getString(R.string.sharing_enabled), Toast.LENGTH_SHORT).show();
			refreshUserInfo();
			break;
		case R.id.stop_location_sharing:
			service.stopLocationDelivery();
			Preferences.setDeliveryEnabled(false);
			Preferences.commit();
			Toast.makeText(this, getString(R.string.sharing_disabled), Toast.LENGTH_SHORT).show();
			refreshUserInfo();
			break;
		case R.id.set_avatar:
			onSelectUserPictureClick(null);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	// ================== SERVICE RELATED ==================

	/**
	 * Handler of incoming messages from service
	 * used to refresh view on events
	 */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case LoShaService.USER_PROFILE:
				final VCard result = (VCard) msg.obj;
				if (result != null) {
					me.setVCard(result);
					refreshUserInfo();
				}
				break;
			case LoShaService.LOCATION:
				refreshUserInfo();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}


}