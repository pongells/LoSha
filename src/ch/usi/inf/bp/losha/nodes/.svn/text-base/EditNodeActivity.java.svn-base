/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.nodes;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import ch.usi.inf.bp.losha.R;
import ch.usi.inf.bp.losha.libs.datetimepicker.DateTimePicker;
import ch.usi.inf.bp.losha.libs.iso8601dateparser.ISO8601DateParser;
import ch.usi.inf.bp.losha.map.MyMapActivity;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.utils.CheckboxSetting;
import ch.usi.inf.bp.losha.utils.SectionLabel;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationGranularity;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationNode;
import ch.usi.inf.bp.losha.xmpp.packets.Geoloc;
import ch.usi.inf.bp.losha.xmpp.packets.LocationSharingRule;

import com.google.android.maps.GeoPoint;

/**
 * Edit Node Activity
 * Shows a view to configure advanced node (group) settings.
 * E.g.: granularity, area filter, location filter.
 * 
 * Menu: remove group
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class EditNodeActivity extends Activity {
	private LoShaService service;
	private LocationNode node;

	private TextView metersView;
	private TextView granDesc;
	private Spinner granularitySpinner;
	private LinearLayout layoutGranularityCustom;
	private LocationGranularity selectedGranularity;

	private TextView latitude;
	private TextView longitude;
	private TextView radius;

	private TextView fromTime;
	private TextView toTime;
	private SectionLabel nodeTitle;

	private CheckboxSetting area;
	private LinearLayout areaFilter;
	private CheckboxSetting time;
	private LinearLayout timeFilter;

	private String now;
	private String future;

	private LocationSharingRule currentRule;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_node);

		((TextView) findViewById(R.id.title_text)).setText(getTitle());
		((LinearLayout) findViewById(R.id.titleBar)).setBackgroundColor(Preferences.getTitleBackgroundColor());
		Utils.colorAllTargets((ScrollView) findViewById(R.id.scrollView), "section", Preferences.getSectionBackgroundColor());

		//default starting time (now)
		now = ISO8601DateParser.toString(new Date());
		//by default is enabled jump 10years into the future
		future = ISO8601DateParser.toString(new Date(System.currentTimeMillis()+315569259747l));

		layoutGranularityCustom = (LinearLayout) findViewById(R.id.granularity_custom_radius);
		metersView = (TextView) findViewById(R.id.granularity_custom_meters);
		granDesc = (TextView) findViewById(R.id.create_node_gran_desc);
		granularitySpinner = (Spinner) findViewById(R.id.granularity_spinner);

		latitude = (TextView) findViewById(R.id.latitude);
		longitude = (TextView) findViewById(R.id.longitude);
		radius = (TextView) findViewById(R.id.radius_dist);

		fromTime = (TextView) findViewById(R.id.fromTime);
		toTime = (TextView) findViewById(R.id.toTime);

		nodeTitle = (SectionLabel) findViewById(R.id.node_title);

		area = (CheckboxSetting) findViewById(R.id.areaFilterCheck);
		areaFilter = (LinearLayout) findViewById(R.id.areaFilter);
		time = (CheckboxSetting) findViewById(R.id.timeFilterCheck);
		timeFilter = (LinearLayout) findViewById(R.id.timeFilter);
	}

	@Override
	protected void onStart() {
		super.onStart();
		service = LoShaService.getService();
		final String title = getIntent().getStringExtra("node_title");
		node = service.getNodesManager().getNodes().getNode(title);

		currentRule = node.getLocationSharingRule();

		nodeTitle.setText(title);

		initGranularityListener();
		initRadiusListener();

		if (currentRule.hasGeoLoc()) {
			area.setChecked(true);
			areaFilter.setVisibility(View.VISIBLE);
			final Geoloc geoloc = currentRule.getGeoLoc();
			latitude.setText(geoloc.getLat()+"");
			longitude.setText(geoloc.getLon()+"");
			radius.setText((int)geoloc.getAccuracy()+"");
		}
		area.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (area.isChecked()) {
					areaFilter.setVisibility(View.GONE);
				} else {
					areaFilter.setVisibility(View.VISIBLE);
				}
				area.getCheckbox().toggle();
			}
		});

		if (currentRule.hasTime()) {
			time.setChecked(true);
			timeFilter.setVisibility(View.VISIBLE);
			fromTime.setText(currentRule.getFromTime());
			toTime.setText(currentRule.getToTime());
		}
		time.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (time.isChecked()) {
					timeFilter.setVisibility(View.GONE);
				} else {
					timeFilter.setVisibility(View.VISIBLE);
				}
				time.getCheckbox().toggle();
			}
		});

		fromTime.setHint(now);
		fromTime.setFocusable(false);
		toTime.setHint(future);
		toTime.setFocusable(false);

		Utils.log("Selected node: "+node.getNodeTitle());
	}

	private void toggleSaving(final boolean refreshing) {
		findViewById(R.id.title_refresh_progress).setVisibility(
				refreshing ? View.VISIBLE : View.GONE);
	}

	public void onSaveClick(final View target) {
		toggleSaving(true);
		final String meters = metersView.getText().toString();
		int metersInt = 0;
		if (selectedGranularity.equals(LocationGranularity.CUSTOM)) {
			if (meters == null || meters.equals("")) {
				metersView.setError(getText(R.string.err_not_empty));
				return;
			} else {
				try {
					final int temp = Integer.parseInt(meters);
					if (temp < 0) {
						metersView.setError(getText(R.string.err_not_positive_integer));
						return;
					} else {
						metersInt = temp;
					}
				} catch (final NumberFormatException e) {
					metersView.setError(getText(R.string.err_not_positive_integer));
					return;
				}
			}
		}
		if (metersInt == 0 || metersInt != 0 && metersView.getError() == null) {
			final LocationSharingRule rule = new LocationSharingRule(selectedGranularity, metersInt);

			if (time.isChecked()) {
				final String from = fromTime.getText().toString();
				final String to = toTime.getText().toString();
				if (from != null && !from.equals("")) {
					rule.setFromTime(from);
				} else {
					rule.setFromTime(now);
				}
				if (to != null && !to.equals("")) {
					rule.setToTime(to);
				} else {
					rule.setToTime(future);
				}
			}

			if (area.isChecked()) {
				final String lat = latitude.getText().toString();
				final String lon = longitude.getText().toString();
				final String rad = radius.getText().toString();
				final GeoPoint point = getValidPoint(lat, lon);
				if (point != null) {
					if (isValidInt(rad, radius)) {
						final Geoloc geoloc = new Geoloc((point.getLatitudeE6() / 1E6),
								(point.getLongitudeE6() / 1E6));
						geoloc.setAccuracy(Integer.parseInt(rad));

						rule.setGeoLoc(geoloc);
					} else {
						return;
					}
				} else {
					return;
				}
			}

			node.setLocationSharingRule(rule);
			service.getNodesManager().sendNodeUpdate(node.getNodeID());

			toggleSaving(false);

			finish();
		}
	}

	private boolean isValidInt(final String meters, final TextView radius2) {
		if (meters == null || meters.equals("")) {
			radius2.setError(getText(R.string.err_not_empty));
			return false;
		} else {
			try {
				final int temp = Integer.parseInt(meters);
				if (temp < 0) {
					radius2.setError(getText(R.string.err_not_positive_integer));
					return false;
				} else {
					return true;
				}
			} catch (final NumberFormatException e) {
				radius2.setError(getText(R.string.err_not_positive_integer));
				return false;
			}
		}
	}

	private GeoPoint getValidPoint(final String lat, final String lon) {
		if (lat == null || lat.equals("") || lon == null || lon.equals("")) {
			latitude.setError(getText(R.string.err_not_empty));
			longitude.setError(getText(R.string.err_not_empty));
			return null;
		} else {
			try {
				final double tempLat = Double.parseDouble(lat);
				final double tempLon = Double.parseDouble(lon);
				return new GeoPoint((int) (tempLat * 1E6), (int) (tempLon*1E6));
			} catch (final NumberFormatException e) {
				latitude.setError(getText(R.string.err_nan));
				longitude.setError(getText(R.string.err_nan));
				return null;
			}
		}
	}

	private void initGranularityListener() {
		final GranularitySpinnerAdapter granAdapter = new GranularitySpinnerAdapter(this, android.R.layout.simple_spinner_item);
		granularitySpinner.setAdapter(granAdapter);

		granularitySpinner.setSelection(currentRule.getGranularity().ordinal());
		if (currentRule.getGranularity().equals(LocationGranularity.CUSTOM)) {
			metersView.setText(currentRule.getRadius()+"");
		}

		granularitySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> spinner,
					final View view, final int pos, final long id) {
				selectedGranularity = LocationGranularity.values()[pos];

				if (selectedGranularity.equals(LocationGranularity.CUSTOM)) {
					layoutGranularityCustom.setVisibility(View.VISIBLE);
					granDesc.setText(EditNodeActivity.this
							.getString(R.string.create_node_gran_meters));
				} else {
					layoutGranularityCustom.setVisibility(View.GONE);
					granDesc.setText(EditNodeActivity.this.getString(
							R.string.create_node_gran_desc, selectedGranularity.getDesc()));
				}
			}

			@Override
			public void onNothingSelected(final AdapterView<?> arg0) {}
		});

	}

	private void initRadiusListener() {
		metersView.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(final Editable s) {}

			@Override
			public void beforeTextChanged(final CharSequence s,
					final int start, final int count, final int after) {}

			@Override
			public void onTextChanged(final CharSequence s, final int start,
					final int before, final int count) {
				granDesc.setText(EditNodeActivity.this.getString(
						R.string.create_node_gran_desc_custom,
						selectedGranularity.getDesc(), metersView.getText().toString()));
			}
		});
	}

	public void onSelectLocationClick(final View target) {
		final Intent mapIntent = new Intent(this, MyMapActivity.class);
		mapIntent.putExtra("select_area", true);
		startActivityForResult(mapIntent, 0);
	}

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0 && resultCode == 0 && data != null) {
			final int lat = data.getIntExtra("lat", 0);
			final int lon = data.getIntExtra("lon", 0);
			final int rad = data.getIntExtra("rad", 0);
			latitude.setText("" + lat / 1E6);
			longitude.setText("" + lon / 1E6);
			radius.setText(rad+"");
		}
	}

	public void onSelectFromTime(final View target) {
		showDateTimeDialog(fromTime);
	}

	public void onSelectToTime(final View target) {
		showDateTimeDialog(toTime);
	}

	private void showDateTimeDialog(final TextView target) {
		final Dialog mDateTimeDialog = new Dialog(this);
		final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.date_time_dialog, null);
		final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView.findViewById(R.id.DateTimePicker);

		((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				final String year = addZero(mDateTimePicker.get(Calendar.YEAR));
				final String month = addZero((mDateTimePicker.get(Calendar.MONTH)+1));
				final String day = addZero(mDateTimePicker.get(Calendar.DAY_OF_MONTH));

				final String hour = addZero(mDateTimePicker.get(Calendar.HOUR_OF_DAY));
				final String min = addZero(mDateTimePicker.get(Calendar.MINUTE));

				int off = mDateTimePicker.get(Calendar.DST_OFFSET) + mDateTimePicker.get(Calendar.ZONE_OFFSET);
				String sign = "+"; if (off < 0) { sign = "-"; off = off*-1; }

				final String date = year+"-"+month+"-"+day+"T";
				final String time = hour+":"+min+":00"+sign+getOffset(off);

				target.setText(date+time);
				mDateTimeDialog.dismiss();
			}
		});

		((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				mDateTimeDialog.cancel();
			}
		});
		((Button) mDateTimeDialogView.findViewById(R.id.ResetDateTime)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				mDateTimePicker.reset();
			}
		});
		mDateTimePicker.setIs24HourView(true);
		mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mDateTimeDialog.setContentView(mDateTimeDialogView);
		mDateTimeDialog.show();
	}

	private String addZero(final int val) {
		return val > 9 ? val+"" : "0"+val;
	}

	public String getOffset(long elapsedTime) {
		final String format = String.format("%%0%dd", 2);
		elapsedTime = elapsedTime / 1000;
		final String minutes = String.format(format, elapsedTime % 3600 / 60);
		final String hours = String.format(format, elapsedTime / 3600);
		final String time =  hours + ":" + minutes;
		return time;
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


	// ================== MENU ==================

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_node, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.delete_node:
			new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setTitle(R.string.delete_node)
			.setMessage(R.string.delete_node_mex)
			.setPositiveButton(R.string.yes,
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					service.getNodesManager().deleteNode(node);
					EditNodeActivity.this.finish();
				}
			}).setNegativeButton(R.string.no, null).show();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
