/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp;

import java.util.ArrayList;
import java.util.Iterator;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ReportedData.Row;
import org.jivesoftware.smackx.search.UserSearchManager;

import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.listeners.UserRosterListener;
import ch.usi.inf.bp.losha.xmpp.users.FriendsRepository;
import ch.usi.inf.bp.losha.xmpp.users.SearchUserResult;

/**
 * This class is used to manage friends, i.e. parse the Roster
 * and pass entries to the FriendsRepository
 * Also used to add, find, and remove friends.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class XMPPFriendsManager {
	private final Roster roster;
	private final FriendsRepository friends;
	private final UserSearchManager searchManager;
	private final String searchService;

	public XMPPFriendsManager(final XMPPManager xmpp, final XMPPNodesManager nodesManager, final Roster roster, final String searchService) {
		this.roster = roster;
		this.searchService = searchService;

		final XMPPConnection connection = xmpp.getConnectionManager().getConnection();
		this.searchManager = new UserSearchManager(connection);

		friends = new FriendsRepository(roster, nodesManager);
		roster.addRosterListener(new UserRosterListener(roster, this));
	}

	public boolean addFriend(final String friendJID, final String friendName) {
		try {
			roster.createEntry(friendJID, friendName, null);
		} catch (final XMPPException e) {
			Utils.log("-> Error: failed to add friend " + e);
		}
		return true;
	}

	// Find users using the standard XEP-0055: Jabber Search
	// of course this was not documented..
	public ArrayList<SearchUserResult> findUsers(final String toSearch) {
		final ArrayList<SearchUserResult> results = new ArrayList<SearchUserResult>();

		try {
			final Form searchForm = searchManager.getSearchForm(searchService);

			final Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("search", toSearch);
			answerForm.setAnswer("Name", true);
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("Email", true);

			final ReportedData data = searchManager.getSearchResults(
					answerForm, searchService);

			Row next;
			SearchUserResult res;
			final Iterator<Row> it = data.getRows();
			while (it.hasNext()) {
				next = it.next();

				res = new SearchUserResult(next.getValues("jid").next().toString(),
						next.getValues("Name").next().toString(),
						next.getValues("Username").next().toString(),
						next.getValues("Email").next().toString());

				if (!res.getUser().equals("admin")) {
					results.add(res);
				}
			}
		} catch (final XMPPException e) {
			e.printStackTrace();
		} catch (final IllegalStateException ex) {
			ex.printStackTrace();
		}
		return results;
	}

	public Roster getRoster() {
		return roster;
	}

	public FriendsRepository getFriends() {
		return friends;
	}

	public void removeFriend(final String friendJID) {
		try {
			roster.removeEntry(roster.getEntry(friendJID));
		} catch (final XMPPException e1) {
			Utils.log("-> Error: cannot remove entry " + e1);
		} catch (final NullPointerException e) {
			Utils.log("-> Error: entry not found");
		}
	}

}
