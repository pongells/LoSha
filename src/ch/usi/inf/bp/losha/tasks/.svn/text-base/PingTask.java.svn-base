/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.tasks;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;

import android.os.AsyncTask;
import ch.usi.inf.bp.losha.utils.Utils;

/**
 * Task to ping the server (actually send a pong).
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class PingTask extends AsyncTask<Void, Void, Void> {
	private final Connection conn;

	/**
	 * 
	 */
	public PingTask(final Connection conn) {
		this.conn = conn;
	}

	@Override
	protected Void doInBackground(final Void... params) {
		final IQ pong = new IQ() {
			@Override
			public String getChildElementXML() {
				return "<ping xmlns='urn:xmpp:ping'/>";
			}
		};
		pong.setType(Type.GET);
		pong.setTo(conn.getServiceName());
		conn.sendPacket(pong);
		return null;
	}

	@Override
	protected void onPostExecute(final Void result) {
		super.onPostExecute(result);
		Utils.log("-> Pong!");
	}

	@Override
	protected void onPreExecute() {
		Utils.log("-> Ping?");
		super.onPreExecute();
	}
}
