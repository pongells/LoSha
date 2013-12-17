/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.friends;

import java.util.ArrayList;
import java.util.Collection;

import org.jivesoftware.smack.RosterGroup;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ch.usi.inf.bp.losha.MainActivity;
import ch.usi.inf.bp.losha.R;
import ch.usi.inf.bp.losha.libs.mergeadapter.MergeAdapter;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.utils.SectionLabel;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationNodesRepository;
import ch.usi.inf.bp.losha.xmpp.users.Friend;
import ch.usi.inf.bp.losha.xmpp.users.Friend.Type;
import ch.usi.inf.bp.losha.xmpp.users.FriendsRepository;

/**
 * Friends List Activity
 * Shows a list of friends with respective locations,
 * and a list of notifications (pending/incoming friendship requests).
 * 
 * Using the Menu one can refresh the whole list or add a friend.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class FriendsListActivity extends ListActivity {
	private final Messenger mMessenger = new Messenger(new IncomingHandler());
	private LoShaService service;

	private LocationNodesRepository nodes;
	private FriendsRepository friendsRepo;

	private MergeAdapter adapter;
	private ImageButton notif;
	private TextView notifCount;

	private boolean notifications;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.friend_list);

		((TextView) findViewById(R.id.title_text)).setText(getTitle());
		((LinearLayout) findViewById(R.id.titleBar)).setBackgroundColor(Preferences.getTitleBackgroundColor());

		notif = (ImageButton) findViewById(R.id.btnNotif);

		notifCount = (TextView) findViewById(R.id.notif_num);
	}

	@Override
	protected void onStart() {
		super.onStart();
		service = LoShaService.getService();
		service.registerClient(this, mMessenger);
		friendsRepo = service.getFriendsManager().getFriends();
		nodes = service.getNodesManager().getNodes();
		notifications = false;

		initFriends();

		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent,
					final View view, final int position, final long id) {
				final MergeAdapter adapter = (MergeAdapter) parent.getAdapter();
				final Friend friend = (Friend) adapter.getAdapter(position).getItem((int) id);
				final Intent intent = new Intent(FriendsListActivity.this, FriendProfileActivity.class);
				intent.putExtra("user", friend.getUser());
				FriendsListActivity.this.startActivity(intent);
			}
		});
	}

	/**
	 * Populates the list with friends
	 */
	private void initFriends() {
		notif.setImageResource(R.drawable.title_mail);
		final ArrayList<Friend> requesting = friendsRepo.getFriends(Type.REQUESTING);
		final int total = requesting.size();
		if (total != 0) {
			notifCount.setVisibility(View.VISIBLE);
			final ShapeDrawable circle = new ShapeDrawable(new OvalShape());
			circle.getPaint().setColor(getResources().getColor(R.color.notification_circle));
			notifCount.setBackgroundDrawable(circle);
			notifCount.setText(total+"");
		}
		else {
			notifCount.setVisibility(View.GONE);
			notifCount.setText("");
		}

		adapter = new MergeAdapter();

		final Collection<RosterGroup> groups = friendsRepo.getGroups();
		// in groups
		for (final RosterGroup group : groups) {
			final String groupTitle = group.getName();
			if (nodes.hasGroup(groupTitle)) {
				// normal friends
				addToList(friendsRepo.getFriendsInGroup(groupTitle), groupTitle);
			} else {
				// blocked
				addToList(friendsRepo.getFriendsInGroup(groupTitle),
						this.getString(R.string.friends_group_blocked));
			}
		}

		// new contacts
		final ArrayList<Friend> newContacts = friendsRepo.getFriends(Type.NEW);
		if (!newContacts.isEmpty()) {
			addToList(newContacts, this.getString(R.string.friends_group_new));
		}

		// if nothing to display
		if (groups.isEmpty() && newContacts.isEmpty()) {
			adapter.addView(buildGroupLabel(
					this.getString(R.string.friends_group_empty)));
		}

		setListAdapter(adapter);
	}

	/**
	 * Populates the list with notifications
	 */
	private void initNotifications() {
		notif.setImageResource(R.drawable.title_friends);
		notifCount.setText("");
		notifCount.setVisibility(View.GONE);

		adapter = new MergeAdapter();

		// requesting friendship..
		final ArrayList<Friend> requesting = friendsRepo
		.getFriends(Type.REQUESTING);
		if (!requesting.isEmpty()) {
			addToList(requesting,
					this.getString(R.string.friends_group_notif_incoming));
		}
		// pending friendship requests..
		final ArrayList<Friend> pending = friendsRepo.getFriends(Type.PENDING);
		if (!pending.isEmpty()) {
			addToList(pending,
					this.getString(R.string.friends_group_notif_pending));
		}
		// if nothing to display
		if (requesting.isEmpty() && pending.isEmpty()) {
			adapter.addView(buildGroupLabel(
					this.getString(R.string.friends_group_notif_empty)));
		}

		setListAdapter(adapter);
	}

	private void addToList(final ArrayList<Friend> group, final String title) {
		adapter.addView(buildGroupLabel(title));
		adapter.addAdapter(new FriendsListAdapter(group, this));
	}

	private View buildGroupLabel(final String rosterGroup) {
		final SectionLabel label = new SectionLabel(this);
		label.setBackgroundColor(Preferences.getSectionBackgroundColor());
		label.setText(rosterGroup);
		return label;
	}

	@Override
	public void onBackPressed() {
		if (notifications) {
			notifications = false;
			notif.setImageResource(R.drawable.title_mail);
			initFriends();
		} else {
			super.onBackPressed();
		}
	}

	public void onHomeClick(final View target) {
		final Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}


	public void onSearchClick(final View target) {
		startActivityForResult(new Intent(this, FriendAddActivity.class), 1);
	}

	public void onNotifClick(final View target) {
		if (notifications) {
			notifications = false;
			initFriends();
		} else {
			notifications = true;
			initNotifications();
		}
	}

	// ================== MENU ==================

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.friends, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_friend:
			startActivityForResult(new Intent(this, FriendAddActivity.class), 1);
			break;
		case R.id.refresh_friends:
			Toast.makeText(this, "Refreshing..", Toast.LENGTH_SHORT).show();
			friendsRepo.reloadRoster();
			service.getNodesManager().refreshLocationsAsync();
			initFriends();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	// ================== ACTIVITY CYCLE ==================

	@Override
	protected void onStop() {
		super.onStop();
		service = null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		service = null;
	}

	// ================== SERVICE RELATED ==================
	/**
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case LoShaService.FRIEND_UPDATE:
				initFriends();
				break;
			case LoShaService.LOCATION:
				if (!notifications) {
					initFriends();
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}
}
