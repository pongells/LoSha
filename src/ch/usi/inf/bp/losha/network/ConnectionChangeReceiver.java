/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.utils.Utils;

/**
 * Listen for android.net.conn.CONNECTIVITY_CHANGE broadcasts
 * in order to reconnect if needed
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {
	private final LoShaService service;
	private Handler reconnectionHandler;
	private NetworkInfo activeNetInfo;

	public ConnectionChangeReceiver() {
		this.service = LoShaService.getService();
	}

	public void register() {
		final IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
		Preferences.getAppContext().registerReceiver(this, intentFilter);
	}

	public void unRegister() {
		Preferences.getAppContext().unregisterReceiver(this);
	}

	@Override
	public void onReceive(final Context context, final Intent intent) {
		final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		activeNetInfo = connectivityManager.getActiveNetworkInfo();

		if (Preferences.isSessionInitiated() && reconnectionHandler == null) {
			reconnectionHandler = new Handler();
			reconnectionHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (activeNetInfo != null) {
						Utils.log("--> Connectivity changed ("+activeNetInfo.getTypeName()+")");
						service.disconnect();
						service.relogin();
					} else {
						Utils.log("--> No network");
						service.disconnect();
					}
					activeNetInfo = null;
					reconnectionHandler = null;
				}
			}, 10000);
		}

	}
}