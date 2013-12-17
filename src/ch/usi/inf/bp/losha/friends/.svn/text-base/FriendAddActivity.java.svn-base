/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.friends;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import ch.usi.inf.bp.losha.MainActivity;
import ch.usi.inf.bp.losha.R;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.users.SearchUserResult;

/**
 * Friend Add Activity
 * Shows a screen to search/add a friend.
 * The XMPP server must implement the advanced search protocol.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class FriendAddActivity extends Activity {
	private final Messenger mMessenger = new Messenger(new IncomingHandler());
	private LoShaService service;

	private  ArrayList<SearchUserResult> users;

	private TextView userNotFound;
	private TextView searchField;
	private ListView list;
	private ProgressDialog findProgressDialog;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.friend_search);

		((TextView) findViewById(R.id.title_text)).setText(getTitle());
		((LinearLayout) findViewById(R.id.titleBar)).setBackgroundColor(Preferences.getTitleBackgroundColor());
		Utils.colorAllTargets((ViewGroup) findViewById(R.id.mainLayout), "section", Preferences.getSectionBackgroundColor());

		searchField = (TextView) findViewById(R.id.search_friend);
		userNotFound = (TextView) findViewById(R.id.user_not_found_text);
		list = (ListView) findViewById(R.id.search_result_list);
	}

	@Override
	protected void onStart() {
		super.onStart();
		service = LoShaService.getService();
		service.registerClient(this, mMessenger);

		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent,
					final View view, final int position, final long id) {
				final String user = users.get(position).getJid();
				final String name = users.get(position).getName();
				if (service.getMe().getBareJID().equals(user)) {
					Toast.makeText(FriendAddActivity.this,
							getString(R.string.add_yourself),
							Toast.LENGTH_LONG).show();
				} else {
					service.getFriendsManager().addFriend(user, name);
					Toast.makeText(FriendAddActivity.this,
							getString(R.string.requested_friendship, name),
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	public void onHomeClick(final View target) {
		final Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	public void onSearchClick(final View target) {
		final InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
		final String toSearch = searchField.getText().toString();
		doFindUsers(toSearch);
	}

	private final void doFindUsers(final String toSearch) {
		findProgressDialog = ProgressDialog.show(FriendAddActivity.this,
				this.getString(R.string.friend_search_title),
				this.getString(R.string.friend_search_text), true, true,
				new OnCancelListener() {
			@Override
			public void onCancel(final DialogInterface dialog) {
				service.disconnect();
			}
		});
		service.findUsers(toSearch, mMessenger);
	}

	// ================== ACTIVITY CYCLE ==================

	@Override
	protected void onStop() {
		super.onStop();
		service = null;
		clean();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		service = null;
		clean();
	}

	private void clean() {
		service = null;
		if (findProgressDialog != null && findProgressDialog.isShowing()) {
			findProgressDialog.dismiss();
		}
	}

	// ================== SERVICE RELATED ==================
	/**
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler {
		@Override
		@SuppressWarnings("unchecked")
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case LoShaService.FOUND_USERS:
				findProgressDialog.dismiss();
				users = (ArrayList<SearchUserResult>) msg.obj;
				populateList(users);
				break;
			}
		}
	}

	private void populateList(final ArrayList<SearchUserResult> users) {
		if (!users.isEmpty()) {
			userNotFound.setVisibility(View.GONE);
			list.setAdapter(new SearchResultAdapter(users,
					FriendAddActivity.this));
		} else {
			list.setAdapter(null);
			userNotFound.setVisibility(View.VISIBLE);
		}
	}
}
