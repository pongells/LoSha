/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.tasks;

import org.jivesoftware.smackx.packet.VCard;

import android.os.AsyncTask;
import android.os.Messenger;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.xmpp.XMPPManager;

/**
 * Task to load an user profile.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class UserProfileTask extends AsyncTask<String, Void, VCard> {
	private final XMPPManager xmpp;
	private final LoShaService service;
	private final Messenger to;
	private String user;

	public UserProfileTask(final LoShaService service,
			final Messenger to) {
		this.service = service;
		xmpp = service.getXMPPManager();
		this.to = to;
	}

	@Override
	protected VCard doInBackground(final String... params) {
		this.user = params[0];
		return xmpp.getVCard(user);
	}

	@Override
	protected void onPostExecute(final VCard result) {
		super.onPostExecute(result);
		result.setFrom(user);
		service.sendMessage(to, LoShaService.USER_PROFILE, result);
	}
}
