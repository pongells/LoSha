/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.tasks;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Messenger;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.XMPPFriendsManager;
import ch.usi.inf.bp.losha.xmpp.users.SearchUserResult;

/**
 * Task to find an user
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class FindUserTask extends
AsyncTask<String, String, ArrayList<SearchUserResult>> {
	private final LoShaService service;
	private final XMPPFriendsManager friendsManager;
	private final Messenger to;

	/**
	 * 
	 */
	public FindUserTask(final LoShaService service, final Messenger to) {
		this.service = service;
		this.friendsManager = service.getXMPPManager().getFriendsManager();
		this.to = to;
	}

	@Override
	protected ArrayList<SearchUserResult> doInBackground(final String... params) {
		return friendsManager.findUsers(params[0]);
	}

	@Override
	protected void onPostExecute(final ArrayList<SearchUserResult> result) {
		super.onPostExecute(result);
		service.sendMessage(to, LoShaService.FOUND_USERS, result);
	}

	@Override
	protected void onPreExecute() {
		Utils.log("-> Searching user..");
		super.onPreExecute();
	}
}
