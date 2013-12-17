/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ch.usi.inf.bp.losha.about.AboutActivity;
import ch.usi.inf.bp.losha.connect.LoginActivity;
import ch.usi.inf.bp.losha.friends.FriendsListActivity;
import ch.usi.inf.bp.losha.map.MyMapActivity;
import ch.usi.inf.bp.losha.nodes.NodesListActivity;
import ch.usi.inf.bp.losha.profile.ProfileActivity;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.settings.SettingsActivity;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.XMPPConnectionManager;

/**
 * Main Activity
 * Shows a view following the Dashboard pattern.
 * It is responsible for starting and destroying the service.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class MainActivity extends Activity {
	private static final String ACTION_OK = "success";
	private final Messenger mMessenger = new Messenger(new IncomingHandler());
	private LoShaService service;
	private Intent serviceIntent;
	private boolean connected = false;
	private ImageButton btnConnect;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		serviceIntent = new Intent(MainActivity.this,
				LoShaService.class);
		this.setContentView(R.layout.main);

		((TextView) findViewById(R.id.title_text)).setText(getTitle());

		btnConnect = (ImageButton) findViewById(R.id.btnConnect);

		// start service and register main to it (bind)
		startService(serviceIntent);
		bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		((LinearLayout) findViewById(R.id.titleBar)).setBackgroundColor(Preferences.getTitleBackgroundColor());
		final LinearLayout bottom = (LinearLayout) findViewById(R.id.bottomBar);
		if (bottom!=null) {
			bottom.setBackgroundColor(Preferences.getTitleBackgroundColor());
		}
		service = LoShaService.getService();
	}

	// ================== CLICK EVENTS ==================

	public void onToggleConnectionButtonClick(final View target) {
		if (connected) {
			doDisconnect();
		} else {
			doConnect();
		}
	}

	public void onFriendsButtonClick(final View target) {
		if (connected) {
			startActivity(new Intent(this, FriendsListActivity.class));
		} else {
			doAskToConnect();
		}
	}

	public void onGroupsButtonClick(final View target) {
		if (connected) {
			if (service.isComponentOnline()) {
				startActivity(new Intent(this, NodesListActivity.class));
			} else {
				Toast.makeText(this, getString(R.string.location_component_offline), Toast.LENGTH_LONG).show();
			}
		} else {
			doAskToConnect();
		}
	}

	public void onHelpButtonClick(final View target) {
		startActivity(new Intent(this, AboutActivity.class));
	}

	public void onMapButtonClick(final View target) {
		if (connected) {
			startActivity(new Intent(this, MyMapActivity.class));
		} else {
			doAskToConnect();
		}
	}

	public void onSettingsButtonClick(final View target) {
		startActivity(new Intent(this, SettingsActivity.class));
	}

	public void onProfileButtonClick(final View target) {
		if (connected) {
			startActivity(new Intent(this, ProfileActivity.class));
		} else {
			doAskToConnect();
		}
	}

	private void doConnect() {
		startActivity(new Intent(this, LoginActivity.class));
	}

	private void doDisconnect() {
		service.disconnect();
	}

	private void doExit() {
		stopService(serviceIntent);
		finish();
	}

	private void doAskToConnect() {
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle(R.string.not_connected_title)
		.setMessage(R.string.not_connected_mex)
		.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				MainActivity.this.doConnect();
			}
		}).setNegativeButton(R.string.no, null).show();
	}


	// ================== MENU ==================

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		if (connected) {
			menu.findItem(R.id.connect).setVisible(false).setEnabled(false);
			menu.findItem(R.id.logout).setVisible(true).setEnabled(true);
		} else {
			menu.findItem(R.id.logout).setVisible(false).setEnabled(false);
			menu.findItem(R.id.connect).setVisible(true).setEnabled(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.connect:
			doConnect();
			break;
		case R.id.logout:
			doDisconnect();
			break;
		case R.id.exit:
			doExit();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	// ================== SERVICE RELATED ==================

	/**
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case LoShaService.DISCONNECTED:
				connected = false;
				btnConnect.setImageResource(R.drawable.fdisconnected);
				break;
			case LoShaService.LOGGED:
				connected = true;
				btnConnect.setImageResource(R.drawable.fconnected);
				break;
			case LoShaService.LOGIN_ANSWER:
				final String result = (String) msg.obj;
				if (result != null) {
					if (result.equals(ACTION_OK)) {
						Preferences.setSessionInit(true);
						Toast.makeText(
								MainActivity.this,
								MainActivity.this.getString(R.string.connected),
								Toast.LENGTH_SHORT).show();
					} else {
						Preferences.setAutoLogin(false);

						Toast.makeText(
								MainActivity.this,
								MainActivity.this.getString(R.string.error)
								+ ": " + msg.obj.toString(),
								Toast.LENGTH_LONG).show();
					}
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	/**
	 * ServiceConnection - used to register MainActivity to the client Need to
	 * use this because we cannot be sure the service is (already) running. No
	 * need for this with other activities.
	 */
	private final ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(final ComponentName arg0, final IBinder binder) {
			try {
				final Message msg = Message.obtain(null, LoShaService.REGISTER_CLIENT);
				msg.replyTo = mMessenger;
				msg.obj = MainActivity.this;
				new Messenger(binder).send(msg);
				service = LoShaService.getService();

				final XMPPConnectionManager conn = service.getConnectionManager();
				if (conn != null) {
					connected = conn.isConnected();
				}

				if (connected) {
					btnConnect.setImageResource(R.drawable.fconnected);
				} else {
					if (Preferences.isAutoLoginEnabled()) {
						Toast.makeText(MainActivity.this, getString(R.string.login_text), Toast.LENGTH_LONG).show();
						if (conn == null) {
							service.initXMPPManager();
						}
						service.login(Preferences.getUsername(), Preferences.getPassword(), mMessenger);
					}
				}
			} catch (final RemoteException e) {
				// service crashed - should never happen
			}
			Utils.log("Main registered!");
		}

		@Override
		public void onServiceDisconnected(final ComponentName name) {
		}
	};

	// ================== CYCLE ==================

	@Override
	protected void onStop() {
		super.onStop();
		service = null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		service = null;
		unbindService(mConnection);
		Utils.log("-> LoSha destroyed");
	}
}