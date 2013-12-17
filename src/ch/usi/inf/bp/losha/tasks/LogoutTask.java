/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.tasks;

import android.os.AsyncTask;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.xmpp.XMPPConnectionManager;
import ch.usi.inf.bp.losha.xmpp.XMPPManager;

/**
 * Task to logout from the server.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class LogoutTask extends AsyncTask<Void, Void, Void> {
	private XMPPManager xmpp;
	private XMPPConnectionManager cm;

	public LogoutTask(final LoShaService service) {
		xmpp = service.getXMPPManager();
	}
	public LogoutTask(final XMPPConnectionManager cm) {
		this.cm = cm;
	}

	@Override
	protected Void doInBackground(final Void... params) {
		if (cm == null) {
			cm = xmpp.getConnectionManager();
		}
		if (cm != null) {
			cm.disconnect();
		}
		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

}
