/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.tasks;

import android.os.AsyncTask;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.users.Friend;

/**
 * Task to reload a friend's location asynchronously
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class LoadFriendLocation extends AsyncTask<Friend, Void, Void> {
	private final LoShaService service;

	public LoadFriendLocation() {
		this.service = LoShaService.getService();
	}

	@Override
	protected Void doInBackground(final Friend... friend) {
		service.getNodesManager().refreshLocation(friend[0]);
		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		super.onPostExecute(result);
		Utils.log("-> friend location updated..");
		//no need for this - is done inside refreshLocation()
		//if (service != null) service.notifyLocationUpdated();
	}
}
