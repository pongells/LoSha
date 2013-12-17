/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.tasks;

import android.os.AsyncTask;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.utils.Utils;

/**
 * Task to reload (all) locations asynchronously
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class LoadFriendsLocation extends AsyncTask<Void, Void, Void> {
	private final LoShaService service;

	public LoadFriendsLocation() {
		this.service = LoShaService.getService();
	}

	@Override
	protected Void doInBackground(final Void... nill) {
		service.getNodesManager().refreshLocations();
		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		super.onPostExecute(result);
		Utils.log("-> friends' locations updated..");
		//no need for this - is done inside refreshLocations()
		//if (service != null) service.notifyLocationUpdated();
	}
}
