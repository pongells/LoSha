/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.tasks;

import android.os.AsyncTask;
import android.os.Messenger;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.XMPPConnectionManager;

/**
 * Task to register the user.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class RegisterTask extends AsyncTask<String, String, String> {
	private static final String ACTION_OK = "success";
	private final LoShaService service;
	private final XMPPConnectionManager connectionManager;
	private final Messenger to;

	/**
	 * 
	 */
	public RegisterTask(final LoShaService service, final Messenger to) {
		this.service = service;
		connectionManager = service.getConnectionManager();
		this.to = to;
	}

	@Override
	protected String doInBackground(final String... params) {
		if (connectionManager.isConnected()) {
			return connectionManager.register(params[0], params[1], params[2], params[3]);
		} else {
			final String connected = connectionManager.connect();
			if (connected.equals(ACTION_OK)) {
				return connectionManager.register(params[0], params[1], params[2], params[3]);
			} else {
				return connected;
			}
		}
	}

	@Override
	protected void onPostExecute(final String result) {
		super.onPostExecute(result);
		service.sendMessage(to, LoShaService.REGISTER_ANSWER, result);
	}

	@Override
	protected void onPreExecute() {
		Utils.log("-> Creating account..");
		super.onPreExecute();
	}
}
