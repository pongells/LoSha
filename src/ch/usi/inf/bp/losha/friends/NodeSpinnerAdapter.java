/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.friends;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ch.usi.inf.bp.losha.R;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationNode;

/**
 * Spinner adapter to show different nodes (groups) in the spinner.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class NodeSpinnerAdapter extends ArrayAdapter<LocationNode> {
	private final int spinnerResource;
	private final ArrayList<LocationNode> nodeList;
	private final Context context;

	/**
	 * @param context
	 * @param textViewResourceId
	 */
	public NodeSpinnerAdapter(final Context context, final int spinnerResource,
			final ArrayList<LocationNode> nodeList) {
		super(context, spinnerResource);
		this.spinnerResource = spinnerResource;
		this.nodeList = nodeList;
		this.context = context;
		setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	}

	@Override
	public int getCount() {
		return nodeList.size() + 1;
	}

	@Override
	public View getDropDownView(final int position, final View convertView,
			final ViewGroup parent) {
		return this.getView(position, convertView, parent,
				android.R.layout.simple_spinner_dropdown_item);
	}

	@Override
	public LocationNode getItem(final int position) {
		if (position == 0) {
			return null;
		} else {
			return nodeList.get(position - 1);
		}
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		return this.getView(position, convertView, parent, spinnerResource);
	}

	private View getView(final int position, View convertView,
			final ViewGroup parent, final int resource) {
		// view template
		if (convertView == null) {
			final LayoutInflater layoutInflater = (LayoutInflater) getContext()
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(resource, parent, false);
		}
		final TextView nodeText = (TextView) convertView
		.findViewById(android.R.id.text1);

		// populate
		final LocationNode node = getItem(position);
		if (node == null) {
			nodeText.setText(context.getString(R.string.friends_group_blocked));
		} else {
			nodeText.setText(node.getNodeTitle());
		}

		return convertView;
	}
}
