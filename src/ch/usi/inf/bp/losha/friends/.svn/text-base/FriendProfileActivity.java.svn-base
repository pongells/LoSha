/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.friends;

import java.util.ArrayList;

import org.jivesoftware.smack.packet.RosterPacket.ItemType;
import org.jivesoftware.smackx.packet.VCard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import ch.usi.inf.bp.losha.MainActivity;
import ch.usi.inf.bp.losha.R;
import ch.usi.inf.bp.losha.map.MyMapActivity;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationNode;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationNodesRepository;
import ch.usi.inf.bp.losha.xmpp.packets.Geoloc;
import ch.usi.inf.bp.losha.xmpp.packets.LocationSharingRule;
import ch.usi.inf.bp.losha.xmpp.users.Friend;

/**
 * Friend Profile Activity
 * Shows a friend profile with name, username, email, avatar,
 * last known location and timestamp, link to location on map,
 * and group membership.
 * 
 * If the friend is requesting friendship, it shows a confirm button.
 * 
 * Menu: block/delete friend
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class FriendProfileActivity extends Activity {
	private final Messenger mMessenger = new Messenger(new IncomingHandler());
	private LoShaService service;
	private Friend friend;
	private ArrayList<LocationNode> nodeList;
	private LocationNodesRepository nodeRepo;
	private LocationNode currentNode;

	private TextView userText;
	private TextView email;
	private TextView location;
	private TextView locationTime;
	private ImageButton locationButton;
	private Spinner nodeSpinner;
	private TextView nodeDesc;
	private LinearLayout acceptFriendship;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.friend_profile);

		((TextView) findViewById(R.id.title_text)).setText(getTitle());
		((LinearLayout) findViewById(R.id.titleBar)).setBackgroundColor(Preferences.getTitleBackgroundColor());
		Utils.colorAllTargets((ScrollView) findViewById(R.id.scrollView), "section", Preferences.getSectionBackgroundColor());

		userText = (TextView) findViewById(R.id.friend_name);
		email = (TextView) findViewById(R.id.friend_email);
		location = (TextView) findViewById(R.id.friend_location);
		locationTime = (TextView) findViewById(R.id.friend_location_time);
		locationButton = (ImageButton) findViewById(R.id.location_button);
		nodeSpinner = (Spinner) findViewById(R.id.friend_node_spinner);
		nodeDesc = (TextView) findViewById(R.id.friend_node_desc);
	}

	@Override
	protected void onStart() {
		super.onStart();
		service = LoShaService.getService();
		service.registerClient(this, mMessenger);

		nodeRepo = service.getNodesManager().getNodes();
		nodeList = nodeRepo.getNodeList();

		final Bundle extras = getIntent().getExtras();
		friend = service.getFriendsManager().getFriends().getFriend(extras.getString("user"));

		//populate view
		if (!friend.hasVCard()) {
			loadFriendInfo();
		} else {
			refreshUserInfo();
		}

		// requesting friendship
		if (friend.getType().equals(ItemType.from)
				&& friend.getStatus() == null) {
			acceptFriendship = (LinearLayout) findViewById(R.id.accept_friendship);
			acceptFriendship.setVisibility(View.VISIBLE);
		}

		// if we are not friends
		// disable location and group selection
		if (!friend.getType().equals(ItemType.both)) {
			nodeSpinner.setEnabled(false);
			nodeDesc.setEnabled(false);
			location.setEnabled(false);
			locationButton.setEnabled(false);
		}

		initGroupSelectionSpinner();
	}

	private void initGroupSelectionSpinner() {
		final NodeSpinnerAdapter adapter = new NodeSpinnerAdapter(this,
				android.R.layout.simple_spinner_item, nodeList);
		nodeSpinner.setAdapter(adapter);

		currentNode = nodeRepo.getNode(friend.getGroup());

		final int selectedGroup = nodeList.indexOf(currentNode);
		if (selectedGroup == -1) {
			nodeSpinner.setSelection(0);
		} else {
			nodeSpinner.setSelection(selectedGroup + 1);
		}

		setNodeDesc(currentNode);

		nodeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			private boolean initialized = false;

			@Override
			public void onItemSelected(final AdapterView<?> spinner,
					final View view, final int pos, final long id) {
				if (initialized) {
					if (pos == 0) {
						friend.blockAsync(currentNode);
						FriendProfileActivity.this.setNodeDesc(null);
					} else {
						final LocationNode newNode = nodeList.get(pos - 1);
						FriendProfileActivity.this.setNodeDesc(newNode);

						// subscribe to new node, remove old
						// subscriptions, change roster group
						friend.subscribeToAsync(newNode, currentNode);

						currentNode = newNode;
					}
				}
				initialized = true;
			}

			@Override
			public void onNothingSelected(final AdapterView<?> arg0) {}
		});
	}

	private final void loadFriendInfo() {
		toggleRefresh(true);
		service.getUserProfile(friend.getUser(), mMessenger);
	}

	private void toggleRefresh(final boolean refreshing) {
		findViewById(R.id.btn_title_refresh).setVisibility(
				refreshing ? View.GONE : View.VISIBLE);
		findViewById(R.id.title_refresh_progress).setVisibility(
				refreshing ? View.VISIBLE : View.GONE);
	}

	public void onRefreshSectionClick(final View target) {
		toggleRefresh(true);
		service.getNodesManager().refreshLocationAsync(friend);
		Toast.makeText(this, "Refreshing location..", Toast.LENGTH_SHORT).show();
	}

	private void refreshUserInfo() {
		if (friend == null) {
			toggleRefresh(false);
			return;
		}

		userText.setText(friend.getName() + " ("
				+ friend.getUser().split("@")[0] + ")");
		email.setText(friend.getEmail());

		setProfilePicture(friend.getAvatar());

		final Geoloc geoloc = friend.getLocation();
		if (geoloc != null) {
			location.setText(geoloc.getSummary());
			locationTime.setText(geoloc.getTimestamp());
			locationButton.setVisibility(View.VISIBLE);
		} else {
			locationButton.setVisibility(View.GONE);
			location.setText("Not available..");
		}

		toggleRefresh(false);
	}

	private void setProfilePicture(final Bitmap bitmap) {
		if (bitmap != null) {
			final ImageView image = (ImageView) findViewById(R.id.userPicture);
			image.setImageBitmap(bitmap);
		}
	}

	public void setNodeDesc(final LocationNode node) {
		if (node != null) {
			final LocationSharingRule rule = node.getLocationSharingRule();
			nodeDesc.setText(getString(R.string.select_group_desc, rule.getGranularity().getDesc()));
		} else {
			nodeDesc.setText("This friend will not receive location updates.");
		}
	}

	// ================== CLICK EVENTS ==================

	public void onHomeClick(final View target) {
		final Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	public void onAcceptFriendshipClick(final View target) {
		service.getXMPPManager().sendPresenceSubscribe(friend.getUser());
		acceptFriendship.setVisibility(View.GONE);
		Toast.makeText(this, getString(R.string.friend_added), Toast.LENGTH_LONG).show();
	}

	public void onLocationClick(final View target) {
		final Intent mapIntent = new Intent(this, MyMapActivity.class);
		mapIntent.putExtra("user", friend.getUser());
		startActivity(mapIntent);
	}

	public void onRefreshClick(final View target) {
		service.getNodesManager().refreshLocationAsync(friend);
		loadFriendInfo();
	}

	// ================== MENU ==================

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.friend, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete_friend:
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.remove_friend_title)
			.setMessage(R.string.remove_friend_mex)
			.setPositiveButton(R.string.yes,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					service.getFriendsManager().removeFriend(friend.getUser());
					FriendProfileActivity.this.finish();
				}
			}).setNegativeButton(R.string.no, null).show();
			break;
		case R.id.block_friend:
			if (friend.getType().equals(ItemType.both)) {
				friend.blockAsync(currentNode);
			}
			finish();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	// ================== ACTIVITY CYCLE ==================
	@Override
	protected void onStop() {
		super.onStop();
		clean();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		clean();
	}

	private void clean() {
		service = null;
		nodeRepo = null;
		nodeList = null;
		friend = null;
		currentNode = null;
	}

	// ================== SERVICE RELATED ==================
	/**
	 * Handler of incoming messages from service
	 * used to update view on events
	 */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case LoShaService.USER_PROFILE:
				final VCard result = (VCard) msg.obj;
				if (result != null) {
					friend.setVCard(result);
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