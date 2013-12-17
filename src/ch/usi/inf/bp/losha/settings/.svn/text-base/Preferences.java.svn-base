/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.settings;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import ch.usi.inf.bp.losha.R;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationGranularity;

/**
 * Preferences class used to store/retrieve temp./stored preferences.
 * Extends Application as a commodity.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class Preferences extends Application {
	public static final String PREFS_NAME = "preferences";
	private static SharedPreferences settings;
	private static SharedPreferences.Editor editor;

	private static Context context;
	private static Resources res;

	private static boolean sessionInit;

	public static Context getAppContext() {
		return context;
	}

	@Override
	@SuppressWarnings("static-access")
	public void onCreate() {
		Utils.log("-> Preferences created!");
		this.context = getApplicationContext();
		this.res = context.getResources();
		this.settings = getSharedPreferences(PREFS_NAME, 0);
		this.editor = settings.edit();
		super.onCreate();
	}

	public static void setUsername(final String user) {
		editor.putString("user", user);
		editor.commit();
	}
	public static String getUsername() {
		return settings.getString("user", null);
	}

	public static void setPassword(final String pass) {
		editor.putString("pass", pass);
		editor.commit();
	}
	public static String getPassword() {
		return settings.getString("pass", null);
	}

	public static void setSessionInit(final boolean init) {
		sessionInit = init;
	}
	public static boolean isSessionInitiated() {
		return sessionInit;
	}

	public static void clearLoginData() {
		editor.remove("user");
		editor.remove("pass");
		setAutoLogin(false);
		editor.commit();
	}

	public static void clearPreferences() {
		editor.clear();
		editor.commit();
	}

	public static float getFakeLat() {
		return settings.getFloat("fake_lat", 46f);
	}

	public static float getFakeLon() {
		return settings.getFloat("fake_lon", 8.59f);
	}

	public static int getLocationDeliveryDelay() {
		return settings.getInt("loc_delivery_delay", 600);
	}

	public static int getLocationRefreshDelay() {
		return settings.getInt("loc_refresh_delay", 60);
	}

	public static SharedPreferences getPref() {
		return settings;
	}

	public static void setAutoLogin(final boolean auto) {
		editor.putBoolean("autoLogin", auto);
		editor.commit();
	}
	public static boolean isAutoLoginEnabled() {
		return settings.getBoolean("autoLogin", false);
	}

	public static String getServerAddress() {
		//USI: 195.176.180.56
		//me: fusio9.dyndns.org
		return settings.getString("server_address", "195.176.180.56");
	}

	public static String getServerName() {
		//USI: loshaserver
		//me: pongells.local
		return settings.getString("server_name", "loshaserver");
	}

	public static int getServerPort() {
		return settings.getInt("server_port", 5222);
	}

	public static boolean isUsingFakeLocation() {
		return settings.getBoolean("use_fake_loc", false);
	}

	public static void setFakeLocation(final double fakeLat,
			final double fakeLon) {
		editor.putFloat("fake_lat", (float) fakeLat);
		editor.putFloat("fake_lon", (float) fakeLon);
		editor.commit();
	}

	public static void setLocationDeliveryDelay(final int delay) {
		editor.putInt("loc_delivery_delay", delay);
		editor.commit();
	}

	public static void setLocationRefreshDelay(final int delay) {
		editor.putInt("loc_refresh_delay", delay);
		editor.commit();
	}

	public static void setPref(final String key, final String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public static void setServerAddress(final String address) {
		editor.putString("server_address", address);
	}

	public static void setServerName(final String name) {
		editor.putString("server_name", name);
	}

	public static void setServerPort(final int port) {
		editor.putInt("server_port", port);
	}

	public static void useFakeLocation(final boolean useFake) {
		editor.putBoolean("use_fake_loc", useFake);
		editor.commit();
	}

	public static void setProfilePictureID(final int id) {
		editor.putInt("profilePicture", id);
		editor.commit();
	}
	public static int getProfilePictureID() {
		return settings.getInt("profilePicture", -1);
	}


	public static boolean isPushEnabled() {
		return settings.getBoolean("push", true);
	}
	public static void setPushEnabled(final boolean enabled) {
		editor.putBoolean("push", enabled);
	}

	public static boolean isDeliveryEnabled() {
		return settings.getBoolean("delivery", true);
	}
	public static void setDeliveryEnabled(final boolean enabled) {
		editor.putBoolean("delivery", enabled);
	}

	public static boolean isKeepHistoryEnabled() {
		return settings.getBoolean("keepHistory", true);
	}
	public static void setKeepHistoryEnabled(final boolean enabled) {
		editor.putBoolean("keepHistory", enabled);
	}

	public static boolean isSendDataUsageEnabled() {
		return settings.getBoolean("sendDataUsage", true);
	}
	public static void setSendDataUsageEnabled(final boolean enabled) {
		editor.putBoolean("sendDataUsage", enabled);
	}

	public static int getTitleBackgroundColor() {
		return settings.getInt("titleBackgroundColor", res.getColor(R.color.title_background));
	}
	public static void setTitleBackgroundColor(final int color) {
		editor.putInt("titleBackgroundColor", color);
	}

	public static int getSectionBackgroundColor() {
		return settings.getInt("sectionBackgroundColor", res.getColor(R.color.label_background1));
	}
	public static void setSectionBackgroundColor(final int color) {
		editor.putInt("sectionBackgroundColor", color);
	}

	public static int getOfflineColor() {
		return settings.getInt("offlineColor", res.getColor(R.color.presence_offline));
	}
	public static void setOfflineColor(final int color) {
		editor.putInt("offlineColor", color);
	}

	public static int getNotSharingColor() {
		return settings.getInt("notSharingColor", res.getColor(R.color.presence_neither));
	}
	public static void setNotSharingColor(final int color) {
		editor.putInt("notSharingColor", color);
	}

	public static int getNotReceivingColor() {
		return settings.getInt("notReceivingColor", res.getColor(R.color.presence_nopush));
	}
	public static void setNotReceivingColor(final int color) {
		editor.putInt("notReceivingColor", color);
	}

	public static int getNotSendingColor() {
		return settings.getInt("notSendingColor", res.getColor(R.color.presence_nosend));
	}
	public static void setNotSendingColor(final int color) {
		editor.putInt("notSendingColor", color);
	}

	public static int getOnlineColor() {
		return settings.getInt("onlineColor", res.getColor(R.color.presence_online));
	}
	public static void setOnlineColor(final int color) {
		editor.putInt("onlineColor", color);
	}


	public static int getMarkerColor(LocationGranularity granularity) {
		int color = getCustomGranularityColor();
		switch (granularity) {
			case BEST:
				color = getBestGranularityColor();
				break;
			case CITY:
				color = getCityGranularityColor();
				break;
			case COUNTRY:
				color = getCountryGranularityColor();
				break;
			case CUSTOM:
				color = getCustomGranularityColor();
				break;
			case STATE:
				color = getStateGranularityColor();
				break;
		}
		return color;
	}
	
	
	public static int getCustomGranularityColor() {
		return settings.getInt("customGranularityColor", res.getColor(R.color.granularity_custom));
	}
	public static void setCustomGranularityColor(final int color) {
		editor.putInt("customGranularityColor", color);
	}

	public static int getCountryGranularityColor() {
		return settings.getInt("countryGranularityColor", res.getColor(R.color.granularity_country));
	}
	public static void setCountryGranularityColor(final int color) {
		editor.putInt("countryGranularityColor", color);
	}

	public static int getStateGranularityColor() {
		return settings.getInt("stateGranularityColor", res.getColor(R.color.granularity_state));
	}
	public static void setStateGranularityColor(final int color) {
		editor.putInt("stateGranularityColor", color);
	}

	public static int getCityGranularityColor() {
		return settings.getInt("cityGranularityColor", res.getColor(R.color.granularity_city));
	}
	public static void setCityGranularityColor(final int color) {
		editor.putInt("cityGranularityColor", color);
	}

	public static int getBestGranularityColor() {
		return settings.getInt("bestGranularityColor", res.getColor(R.color.granularity_best));
	}
	public static void setBestGranularityColor(final int color) {
		editor.putInt("bestGranularityColor", color);
	}

	public static void commit() {
		editor.commit();
	}
}
