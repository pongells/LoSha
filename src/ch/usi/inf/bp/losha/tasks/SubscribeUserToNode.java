/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.tasks;

import android.os.AsyncTask;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationNode;
import ch.usi.inf.bp.losha.xmpp.users.Friend;

/**
 * Task to subscribe an user to a node (group).
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class SubscribeUserToNode extends AsyncTask<LocationNode, Void, Void> {
	private final LoShaService service;
	private final Friend friend;

	public SubscribeUserToNode(final Friend friend) {
		this.service = LoShaService.getService();
		this.friend = friend;
	}

	@Override
	protected Void doInBackground(final LocationNode... nodes) {
		friend.subscribeTo(nodes[0], nodes[1]);
		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		super.onPostExecute(result);
		service.notifyFriendUpdate();
	}
}
