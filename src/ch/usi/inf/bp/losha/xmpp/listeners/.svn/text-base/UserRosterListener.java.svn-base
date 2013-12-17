/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.listeners;

import java.util.Collection;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.XMPPFriendsManager;
import ch.usi.inf.bp.losha.xmpp.users.Friend;
import ch.usi.inf.bp.losha.xmpp.users.FriendsRepository;

/**
 * Listen for changes in the XMPP default Roster (contacts)
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class UserRosterListener implements RosterListener {
	private final FriendsRepository friendRepo;
	private final Roster roster;
	private final LoShaService service;

	public UserRosterListener(final Roster roster, final XMPPFriendsManager friendsManager) {
		this.service = LoShaService.getService();
		this.friendRepo = friendsManager.getFriends();
		this.roster = roster;
	}

	@Override
	public void entriesAdded(final Collection<String> addresses) {
		Utils.log("-> Entries added: " + addresses);
		for (final String user : addresses) {
			// we are friends with the pubsub service (receive and send
			// messages)
			// no need to store it as a "real" friend
			if (!user.contains("pubsub.")) {
				friendRepo.addFriend(user);
				service.notifyFriendUpdate();
			}
		}
	}

	@Override
	public void entriesDeleted(final Collection<String> addresses) {
		Utils.log("->  Entries deleted: " + addresses);
		for (final String user : addresses) {
			friendRepo.removeFriend(user);
			service.notifyFriendUpdate();
		}
	}

	@Override
	public void entriesUpdated(final Collection<String> addresses) {
		Utils.log("->  Entries updated: " + addresses);
		for (final String user : addresses) {
			final Friend friend = friendRepo.getFriend(user);
			final RosterEntry entry = roster.getEntry(user);
			if (friend != null && entry != null) {
				friend.setEntry(entry);
				service.notifyFriendUpdate();
			}
		}
	}

	@Override
	public void presenceChanged(final Presence presence) {
		final String fromBare = presence.getFrom().split("/")[0];
		final Friend friend = friendRepo.getFriend(fromBare);
		friend.setPresence(presence);
		Utils.log("-> Updated presence of "+fromBare+": "+
				(presence.isAvailable() ? "available" : "not available")+","+
				presence.getMode()+","+presence.getStatus());
		service.notifyFriendUpdate();
	}
}
