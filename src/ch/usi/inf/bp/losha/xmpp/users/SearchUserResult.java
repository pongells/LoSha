/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.users;

/**
 * A parsed search result
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class SearchUserResult {
	private final String jid;
	private final String name;
	private final String user;
	private final String email;

	/**
	 * Create a new result item
	 */
	public SearchUserResult(final String jid, final String name,
			final String user, final String email) {
		this.jid = jid;
		this.name = name;
		this.user = user;
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public String getJid() {
		return jid;
	}

	public String getName() {
		return name;
	}

	public String getUser() {
		return user;
	}

	@Override
	public String toString() {
		return jid;
	}

}
