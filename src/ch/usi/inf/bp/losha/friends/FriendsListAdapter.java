package ch.usi.inf.bp.losha.friends;

/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
import java.util.ArrayList;

import org.jivesoftware.smack.packet.Presence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import ch.usi.inf.bp.losha.R;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.xmpp.packets.Geoloc;
import ch.usi.inf.bp.losha.xmpp.users.Friend;

/**
 * Custom list adapter to show friends with locations
 * and status on the left.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class FriendsListAdapter extends BaseAdapter {
	// holder view
	protected static class FriendView {
		protected TextView alias;
		protected TextView user;
		protected TextView extra;
		protected LinearLayout status;
	}

	private final Context context;

	private final ArrayList<Friend> rosterGroupList;

	public FriendsListAdapter(final ArrayList<Friend> rosterGroupList, final Context context) {
		this.context = context;
		this.rosterGroupList = rosterGroupList;
	}

	@Override
	public int getCount() {
		return rosterGroupList.size();
	}

	@Override
	public Friend getItem(final int position) {
		return rosterGroupList.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		View rowView = convertView;
		FriendView friendView = null;

		if (rowView == null) {
			rowView = LayoutInflater.from(context).inflate(R.layout.friend_list_item,
					parent, false);

			friendView = new FriendView();
			friendView.status = (LinearLayout) rowView
			.findViewById(R.id.status_icon);
			friendView.user = (TextView) rowView.findViewById(R.id.user);
			friendView.extra = (TextView) rowView.findViewById(R.id.extra);

			rowView.setTag(friendView);
		} else {
			friendView = (FriendView) rowView.getTag();
		}

		final Friend currentFriend = rosterGroupList.get(position);

		final String user = currentFriend.getUser().split("@")[0];
		final String name = currentFriend.getName();
		final Geoloc location = currentFriend.getLocation();

		if (name != null && !name.equals("")) {
			friendView.user.setText(name + " (" + user + ")");
		} else {
			friendView.user.setText(user);
		}

		Presence presence = currentFriend.getPresence();
		if (presence == null) {
			presence = currentFriend.getPresenceFromRoster();
		}

		int presence_color;

		if (!presence.isAvailable()) {
			presence_color = Preferences.getOfflineColor();
		} else {
			presence_color = Preferences.getOnlineColor();
		}

		if (presence.getMode() != null) {
			switch (presence.getMode()) {
			case away:
				presence_color = Preferences.getNotSendingColor();
				break;
			case dnd:
				presence_color = Preferences.getNotReceivingColor();
				break;
			case xa:
				presence_color = Preferences.getNotSharingColor();
				break;
			default:
				presence_color = Preferences.getOnlineColor();
				break;
			}
		}

		//friendView.status.setImageResource(iconToDiaplay);
		friendView.status.setBackgroundColor(presence_color);

		if (location != null) {
			friendView.extra.setVisibility(View.VISIBLE);
			friendView.extra.setText(location.getSummary());
		} else {
			friendView.extra.setVisibility(View.GONE);
		}

		return rowView;
	}
}