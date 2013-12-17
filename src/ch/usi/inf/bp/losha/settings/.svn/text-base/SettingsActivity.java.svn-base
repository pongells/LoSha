/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import ch.usi.inf.bp.losha.MainActivity;
import ch.usi.inf.bp.losha.R;
import ch.usi.inf.bp.losha.utils.CheckboxSetting;
import ch.usi.inf.bp.losha.utils.ColorSetting;
import ch.usi.inf.bp.losha.utils.Utils;

/**
 * Settings Activity
 * Shows a list of customized settings - from server info to colors selection.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class SettingsActivity extends Activity {
	private EditText serverAddress;
	private EditText serverPort;
	private EditText serverName;

	private EditText locRefreshDelay;
	private EditText locDeliveryDelay;

	private CheckboxSetting push;
	private CheckboxSetting delivery;
	private CheckboxSetting dataUsage;
	private CheckboxSetting keepHistory;

	private ColorSetting titleBackground;
	private ColorSetting sectionBackground;

	private ColorSetting offlineColor;
	private ColorSetting notSharingColor;
	private ColorSetting notReceivingColor;
	private ColorSetting notSendingColor;
	private ColorSetting onlineColor;

	private ColorSetting granularityCustomColor;
	private ColorSetting granularityCountryColor;
	private ColorSetting granularityStateColor;
	private ColorSetting granularityCityColor;
	private ColorSetting granularityBestColor;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.settings);

		((TextView) findViewById(R.id.title_text)).setText(getTitle());

		//server settings
		serverAddress = (EditText) findViewById(R.id.server_address);
		serverPort = (EditText) findViewById(R.id.server_port);
		serverName = (EditText) findViewById(R.id.server_name);

		//location refresh settings
		locRefreshDelay = (EditText) findViewById(R.id.loc_refresh_delay);
		locDeliveryDelay = (EditText) findViewById(R.id.loc_delivery_delay);

		//checks
		push = (CheckboxSetting) findViewById(R.id.push_notif);
		delivery = (CheckboxSetting) findViewById(R.id.send_loc);
		dataUsage = (CheckboxSetting) findViewById(R.id.stats_send);
		keepHistory = (CheckboxSetting) findViewById(R.id.store_history);

		//colors
		titleBackground = (ColorSetting) findViewById(R.id.title_background);
		sectionBackground = (ColorSetting) findViewById(R.id.section_background);

		offlineColor = (ColorSetting) findViewById(R.id.friend_offline_color);
		notSharingColor = (ColorSetting) findViewById(R.id.friend_not_receiving_nor_sending_color);
		notReceivingColor = (ColorSetting) findViewById(R.id.friend_not_receiving_color);
		notSendingColor = (ColorSetting) findViewById(R.id.friend_not_sending_color);
		onlineColor = (ColorSetting) findViewById(R.id.friend_online_color);

		granularityCustomColor = (ColorSetting) findViewById(R.id.location_custom);
		granularityCountryColor = (ColorSetting) findViewById(R.id.location_country);
		granularityStateColor = (ColorSetting) findViewById(R.id.location_state);
		granularityCityColor = (ColorSetting) findViewById(R.id.location_city);
		granularityBestColor = (ColorSetting) findViewById(R.id.location_best);
	}

	@Override
	protected void onStart() {
		super.onStart();
		((LinearLayout) findViewById(R.id.titleBar)).setBackgroundColor(Preferences.getTitleBackgroundColor());
		final ScrollView main = (ScrollView) findViewById(R.id.settings);
		final int sectionBackgroundColor = Preferences.getSectionBackgroundColor();
		Utils.colorAllTargets(main, "section", sectionBackgroundColor);
		sectionBackground.setColor(sectionBackgroundColor);
		sectionBackground.setTarget(main, "section");

		final String serverAddressVal = Preferences.getServerAddress();
		serverAddress.setText(serverAddressVal);

		final String serverNameVal = Preferences.getServerName();
		serverName.setText(serverNameVal);

		final int serverPortVal = Preferences.getServerPort();
		serverPort.setText(serverPortVal + "");

		final int locRefreshDelayVal = Preferences.getLocationRefreshDelay();
		locRefreshDelay.setText(locRefreshDelayVal + "");

		final int locDeliveryDelayVal = Preferences.getLocationDeliveryDelay();
		locDeliveryDelay.setText(locDeliveryDelayVal + "");

		final boolean pushEnabled = Preferences.isPushEnabled();
		push.setChecked(pushEnabled);

		final boolean deliveryEnabled = Preferences.isDeliveryEnabled();
		delivery.setChecked(deliveryEnabled);

		final boolean sendDataUsageEnabled = Preferences.isSendDataUsageEnabled();
		dataUsage.setChecked(sendDataUsageEnabled);

		final boolean keepHistoryEnabled = Preferences.isKeepHistoryEnabled();
		keepHistory.setChecked(keepHistoryEnabled);

		final int titleBackgroundColor = Preferences.getTitleBackgroundColor();
		titleBackground.setColor(titleBackgroundColor);
		titleBackground.setTarget((findViewById(R.id.titleBar)));

		offlineColor.setColor(Preferences.getOfflineColor());
		notSharingColor.setColor(Preferences.getNotSharingColor());
		notReceivingColor.setColor(Preferences.getNotReceivingColor());
		notSendingColor.setColor(Preferences.getNotSendingColor());
		onlineColor.setColor(Preferences.getOnlineColor());

		granularityCustomColor.setColor(Preferences.getCustomGranularityColor());
		granularityCountryColor.setColor(Preferences.getCountryGranularityColor());
		granularityStateColor.setColor(Preferences.getStateGranularityColor());
		granularityCityColor.setColor(Preferences.getCityGranularityColor());
		granularityBestColor.setColor(Preferences.getBestGranularityColor());
	}

	// ================== CHECK VALID INT ==================
	private boolean checkIntegerValue(final EditText from) {
		if (from.getText() == null || from.getText().toString().equals("")) {
			from.setError(getText(R.string.err_not_empty));
		} else {
			try {
				final int temp = Integer.parseInt(from.getText().toString());
				if (temp < 0) {
					from.setError(getText(R.string.err_not_positive_integer));
				} else {
					return true;
				}
			} catch (final NumberFormatException e) {
				from.setError(getText(R.string.err_not_positive_integer));
			}
		}
		return false;
	}
	private int getInt(final EditText from) {
		final int temp = Integer.parseInt(from.getText().toString());
		return temp;
	}

	// ================== CHECK VALID STRING ==================
	private boolean checkStringValue(final EditText from) {
		final String text = from.getText().toString();
		if (text == null || text.equals("")) {
			from.setError(getText(R.string.err_not_empty));
		} else {
			return true;
		}
		return false;
	}
	private String getString(final EditText from) {
		return from.getText().toString();
	}

	// ================== ON SAVE CLICK ==================
	public void onSaveClick(final View target) {
		if (checkStringValue(serverAddress) &&
				checkStringValue(serverName) &&
				checkIntegerValue(serverPort) &&
				checkIntegerValue(locRefreshDelay) &&
				checkIntegerValue(locDeliveryDelay)) {

			Preferences.setServerAddress(getString(serverAddress));
			Preferences.setServerName(getString(serverName));
			Preferences.setServerPort(getInt(serverPort));

			Preferences.setLocationRefreshDelay(getInt(locRefreshDelay));
			Preferences.setLocationDeliveryDelay(getInt(locDeliveryDelay));

			Preferences.setDeliveryEnabled(delivery.isChecked());
			Preferences.setPushEnabled(push.isChecked());
			Preferences.setSendDataUsageEnabled(dataUsage.isChecked());
			Preferences.setKeepHistoryEnabled(keepHistory.isChecked());

			Preferences.setTitleBackgroundColor(titleBackground.getColor());
			Preferences.setSectionBackgroundColor(sectionBackground.getColor());

			Preferences.setOfflineColor(offlineColor.getColor());
			Preferences.setNotSharingColor(notSharingColor.getColor());
			Preferences.setNotReceivingColor(notReceivingColor.getColor());
			Preferences.setNotSendingColor(notSendingColor.getColor());
			Preferences.setOnlineColor(onlineColor.getColor());

			Preferences.setCustomGranularityColor(granularityCustomColor.getColor());
			Preferences.setCountryGranularityColor(granularityCountryColor.getColor());
			Preferences.setStateGranularityColor(granularityStateColor.getColor());
			Preferences.setCityGranularityColor(granularityCityColor.getColor());
			Preferences.setBestGranularityColor(granularityBestColor.getColor());

			Preferences.commit();
			Toast.makeText(this, this.getString(R.string.saved), Toast.LENGTH_LONG)
			.show();
		}
	}

	public void onHomeClick(final View target) {
		final Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	// ================== MENU ==================
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.reset:
			findViewById(R.id.settings).startAnimation(
					AnimationUtils.loadAnimation(this, R.anim.shake));
			Preferences.clearPreferences();
			onStart();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
