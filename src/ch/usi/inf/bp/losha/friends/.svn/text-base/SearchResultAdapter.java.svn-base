package ch.usi.inf.bp.losha.friends;

/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import ch.usi.inf.bp.losha.xmpp.users.SearchUserResult;

/**
 * Adapter to show a list of results (SearchUserResult) to the user.
 * Each SearchUserResult contains informations about a specific user.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class SearchResultAdapter extends BaseAdapter {
	// holder view
	protected static class FriendView {
		protected TextView user;
		protected TextView info;
	}

	private final Context context;

	private final ArrayList<SearchUserResult> searchResults;

	public SearchResultAdapter(final ArrayList<SearchUserResult> searchResults,
			final Context context) {
		this.searchResults = searchResults;
		this.context = context;
	}

	@Override
	public int getCount() {
		return searchResults.size();
	}

	@Override
	public SearchUserResult getItem(final int position) {
		return searchResults.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		View rowView = convertView;
		FriendView friendView = null;

		if (rowView == null) {
			rowView = LayoutInflater.from(context).inflate(
					android.R.layout.simple_list_item_2, parent, false);

			friendView = new FriendView();
			friendView.user = (TextView) rowView
			.findViewById(android.R.id.text1);
			friendView.info = (TextView) rowView
			.findViewById(android.R.id.text2);

			rowView.setTag(friendView);
		} else {
			friendView = (FriendView) rowView.getTag();
		}

		final SearchUserResult currentUser = searchResults.get(position);
		final String user = currentUser.getUser();
		final String name = currentUser.getName();
		final String email = currentUser.getEmail();

		if (name != null && !name.equals("")) {
			friendView.user.setText(name + " (" + user + ")");
		} else {
			friendView.user.setText(user);
		}
		if (email != null && !email.equals("")) {
			friendView.info.setText(email);
		}
		return rowView;
	}
}