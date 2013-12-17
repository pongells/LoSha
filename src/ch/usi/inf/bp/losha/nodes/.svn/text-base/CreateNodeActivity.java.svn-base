/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.nodes;

import org.jivesoftware.smackx.pubsub.LeafNode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import ch.usi.inf.bp.losha.MainActivity;
import ch.usi.inf.bp.losha.R;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.XMPPNodesManager;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationGranularity;
import ch.usi.inf.bp.losha.xmpp.packets.LocationSharingRule;

/**
 * Create Node Activity
 * Shows a view to create simple nodes (groups) with a title and a granularity.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class CreateNodeActivity extends Activity {
	private LoShaService service;
	private XMPPNodesManager nodesManager;

	private TextView titleView;
	private TextView metersView;
	private TextView granDesc;
	private Spinner granularitySpinner;
	private LinearLayout layoutGranularityCustom;
	private LocationGranularity selectedGranularity;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.create_node);

		((TextView) findViewById(R.id.title_text)).setText(getTitle());
		((LinearLayout) findViewById(R.id.titleBar)).setBackgroundColor(Preferences.getTitleBackgroundColor());
		Utils.colorAllTargets((ScrollView) findViewById(R.id.scrollView), "section", Preferences.getSectionBackgroundColor());

		titleView = (TextView) findViewById(R.id.node_title);
		layoutGranularityCustom = (LinearLayout) findViewById(R.id.granularity_custom_radius);
		metersView = (TextView) findViewById(R.id.granularity_custom_meters);
		granDesc = (TextView) findViewById(R.id.create_node_gran_desc);
		granularitySpinner = (Spinner) findViewById(R.id.granularity_spinner);
	}

	@Override
	protected void onStart() {
		super.onStart();
		service = LoShaService.getService();
		nodesManager = service.getNodesManager();

		initTitleListener();
		initGranularityListener();
		initRadiusListener();
	}

	private void initTitleListener() {
		titleView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(final TextView v,
					final int actionId, final KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_NULL) {
					final InputMethodManager imm = (InputMethodManager) CreateNodeActivity.this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(titleView.getWindowToken(), 0);
					return true;
				}
				return false;
			}
		});
	}

	private void initGranularityListener() {
		final GranularitySpinnerAdapter granAdapter = new GranularitySpinnerAdapter(
				this, android.R.layout.simple_spinner_item);
		granularitySpinner.setAdapter(granAdapter);

		granularitySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> spinner,
					final View view, final int pos, final long id) {
				selectedGranularity = LocationGranularity.values()[pos];

				if (selectedGranularity.equals(LocationGranularity.CUSTOM)) {
					layoutGranularityCustom.setVisibility(View.VISIBLE);
					granDesc.setText(CreateNodeActivity.this
							.getString(R.string.create_node_gran_meters));
				} else {
					layoutGranularityCustom.setVisibility(View.GONE);
					granDesc.setText(CreateNodeActivity.this.getString(
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
				granDesc.setText(CreateNodeActivity.this.getString(
						R.string.create_node_gran_desc_custom,
						selectedGranularity.getDesc(), metersView.getText().toString()));
			}
		});
	}

	public void onCreateClick(final View target) {
		final String title = titleView.getText().toString();
		if (title.equals("")
				|| title.equals(this.getString(R.string.friends_group_blocked))) {
			titleView.setError(this.getString(R.string.err_not_empty));
		}

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
		if (titleView.getError() == null) {
			final LocationSharingRule rule = new LocationSharingRule(selectedGranularity, metersInt);
			final LeafNode node = nodesManager.createLocationNode(title, rule);

			if (node != null) {
				nodesManager.storeNode(node);
			}
			finish();
		}
	}

	public void onHomeClick(final View target) {
		final Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	// ================== ACTIVITY CYCLE ==================

	@Override
	protected void onPause() {
		super.onPause();
		service = null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		service = null;
	}
}
