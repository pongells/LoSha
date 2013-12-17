/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.users;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.packet.RosterPacket.ItemStatus;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;

import android.location.Location;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.XMPPNodesManager;
import ch.usi.inf.bp.losha.xmpp.users.Friend.Type;

/**
 * Repository of friends, it is similar to the Smack Roster,
 * just more advanced.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class FriendsRepository {

	private final Roster roster;
	private final HashMap<String, Friend> friends;
	private final XMPPNodesManager nodesManager;

	public FriendsRepository(final Roster roster, final XMPPNodesManager nodesManager) {
		this.roster = roster;
		this.nodesManager = nodesManager;
		friends = new HashMap<String, Friend>();
	}

	public void addFriend(final String user) {
		Utils.log("-> Friend added: " + user);
		final RosterEntry buddy = roster.getEntry(user);
		final Friend friend = new Friend(roster, buddy, nodesManager);
		friends.put(buddy.getUser(), friend);
	}

	private void addNewFriend(final ArrayList<Friend> friendsToReturn, final Friend friend) {
		if (friend.getType().equals(ItemType.both)) {
			friendsToReturn.add(friend);
		}
	}

	private void addPending(final ArrayList<Friend> friendsToReturn,
			final Friend friend) {
		final ItemType type = friend.getType();
		final ItemStatus status = friend.getStatus();
		if (type.equals(ItemType.to) || type.equals(ItemType.none)
				|| type.equals(ItemType.from) && status != null) {
			friendsToReturn.add(friend);
		}
	}

	private void addRequesting(final ArrayList<Friend> friendsToReturn,
			final Friend friend) {
		final ItemType type = friend.getType();
		final ItemStatus status = friend.getStatus();
		if (type.equals(ItemType.from) && status == null) {
			friendsToReturn.add(friend);
		}
	}

	public ArrayList<Friend> getCurrentFriends() {
		final ArrayList<Friend> toReturn = new ArrayList<Friend>();
		final Collection<RosterEntry> entries = roster.getEntries();
		for (final RosterEntry entry : entries) {
			final Friend friend = friends.get(entry.getUser());
			if (friend != null && friend.getType().equals(ItemType.both)) {
				toReturn.add(friend);
			}
		}
		return toReturn;
	}

	public ArrayList<Friend> getCurrentFriendsWithKnownLocation() {
		final ArrayList<Friend> toReturn = new ArrayList<Friend>();
		final Collection<RosterEntry> entries = roster.getEntries();
		for (final RosterEntry entry : entries) {
			final Friend friend = friends.get(entry.getUser());
			//current friends
			if (friend != null && friend.getType().equals(ItemType.both)) {
				final Location location = friend.getMapLocation();
				//known location
				if (location != null && location.getLatitude() != 0) {
					toReturn.add(friend);
				}
			}
		}
		return toReturn;
	}

	/**
	 * Get the stored friend
	 * @param user
	 * 			the friend bare JID
	 * @return
	 * 			the friend
	 */
	public Friend getFriend(final String user) {
		return friends.get(user);
	}

	public ArrayList<Friend> getFriends(final Type type) {
		final ArrayList<Friend> toReturn = new ArrayList<Friend>();
		final Collection<RosterEntry> entries = roster.getUnfiledEntries();
		for (final RosterEntry entry : entries) {
			final Friend friend = friends.get(entry.getUser());
			if (friend != null && !friend.getUser().contains("pubsub.")) {
				switch (type) {
				case ACTUAL:
					break;
				case NEW:
					addNewFriend(toReturn, friend);
					break;
				case PENDING:
					addPending(toReturn, friend);
					break;
				case REQUESTING:
					addRequesting(toReturn, friend);
					break;
				}
			}
		}
		return toReturn;
	}

	public ArrayList<Friend> getFriendsInGroup(final String group) {
		final ArrayList<Friend> toReturn = new ArrayList<Friend>();
		final Collection<RosterEntry> entries = roster.getGroup(group).getEntries();
		for (final RosterEntry entry : entries) {
			toReturn.add(friends.get(entry.getUser()));
		}
		return toReturn;
	}

	public Collection<RosterGroup> getGroups() {
		return roster.getGroups();
	}

	// refresh
	public void reloadRoster() {
		roster.reload();
	}

	public void removeFriend(final String user) {
		Utils.log("-> LoshaRoster: removing " + user);
		friends.remove(user);
	}
}
