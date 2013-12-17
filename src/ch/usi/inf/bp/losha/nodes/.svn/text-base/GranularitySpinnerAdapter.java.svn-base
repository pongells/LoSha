/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.nodes;

import ch.usi.inf.bp.losha.xmpp.nodes.LocationGranularity;
import android.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * A simple Adapter to show different granularities inside a Spinner.
 *
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class GranularitySpinnerAdapter extends
ArrayAdapter<LocationGranularity> {
	private final int spinnerResource;

	/**
	 * @param context
	 * @param textViewResourceId
	 */
	public GranularitySpinnerAdapter(final Context context,
			final int spinnerResource) {
		super(context, spinnerResource);
		this.spinnerResource = spinnerResource;
		setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	@Override
	public int getCount() {
		return LocationGranularity.values().length;
	}

	@Override
	public View getDropDownView(final int position, final View convertView,
			final ViewGroup parent) {
		return this.getView(position, convertView, parent,
				android.R.layout.simple_spinner_dropdown_item);
	}

	@Override
	public LocationGranularity getItem(final int position) {
		return LocationGranularity.values()[position];
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		return this.getView(position, convertView, parent, spinnerResource);
	}

	private View getView(final int position, View convertView,
			final ViewGroup parent, final int resource) {
		// Inflate a view template
		if (convertView == null) {
			final LayoutInflater layoutInflater = (LayoutInflater) getContext()
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(resource, parent, false);
		}
		final TextView granularityText = (TextView) convertView
		.findViewById(R.id.text1);

		// Populate template
		final LocationGranularity granularity = getItem(position);
		granularityText.setText(granularity.getDesc());

		return convertView;
	}
}
