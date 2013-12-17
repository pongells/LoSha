/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.nodes;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import ch.usi.inf.bp.losha.MainActivity;
import ch.usi.inf.bp.losha.R;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationNodesRepository;

/**
 * Nodes List Activity
 * Shows a list of nodes (groups): each one can have zero, one or two icons representing
 * time and area filters (if an icon is shown, the node has the corresponding filter set).
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class NodesListActivity extends ListActivity {
	private final Messenger mMessenger = new Messenger(new IncomingHandler());
	private LoShaService service;
	private NodesListAdapter adapter;
	private LocationNodesRepository nodes;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.node_list);

		((TextView) findViewById(R.id.title_text)).setText(getTitle());
		((LinearLayout) findViewById(R.id.titleBar)).setBackgroundColor(Preferences.getTitleBackgroundColor());
	}

	@Override
	protected void onStart() {
		super.onStart();
		service = LoShaService.getService();
		service.registerClient(this, mMessenger);
		nodes = service.getNodesManager().getNodes();
		initList();
	}

	private void initList() {
		adapter = new NodesListAdapter(nodes.getNodeList(), this);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(final ListView l, final View v, final int position, final long id) {
		super.onListItemClick(l, v, position, id);

		final Intent intent = new Intent(this, EditNodeActivity.class);
		intent.putExtra("node_title", nodes.getNodeList().get(position).getNodeTitle());
		startActivity(intent);
	}

	public void onCreateClick(final View target) {
		final Intent intent = new Intent(this, CreateNodeActivity.class);
		startActivity(intent);
	}

	public void onHomeClick(final View target) {
		final Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	// ================== ACTIVITY CYCLE ==================

	@Override
	protected void onStop() {
		super.onStop();
		service = null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		service = null;
	}

	// ================== SERVICE RELATED ==================
	/**
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			default:
				super.handleMessage(msg);
			}
		}
	}
}
