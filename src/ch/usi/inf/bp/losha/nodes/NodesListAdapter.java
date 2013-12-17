/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.nodes;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ch.usi.inf.bp.losha.R;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationGranularity;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationNode;
import ch.usi.inf.bp.losha.xmpp.packets.LocationSharingRule;

/**
 * The adapter used to show the nodes inside the list.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class NodesListAdapter extends BaseAdapter {
	// holder view
	protected static class NodeView {
		protected TextView nodeTitle;
		protected TextView nodeGranularity;
		protected ImageView time;
		protected ImageView area;
	}

	private final Context context;

	private final ArrayList<LocationNode> nodes;

	public NodesListAdapter(final ArrayList<LocationNode> nodes,
			final Context context) {
		this.context = context;
		this.nodes = nodes;
	}

	@Override
	public int getCount() {
		return nodes.size();
	}

	@Override
	public LocationNode getItem(final int position) {
		return nodes.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public View getView(final int position, final View convertView,
			final ViewGroup parent) {
		View rowView = convertView;
		NodeView nodeView = null;

		if (rowView == null) {
			rowView = LayoutInflater.from(context).inflate(
					R.layout.node_list_item, parent, false);

			nodeView = new NodeView();
			nodeView.nodeTitle = (TextView) rowView.findViewById(R.id.node_title);
			nodeView.nodeGranularity = (TextView) rowView.findViewById(R.id.node_granularity);

			nodeView.time = (ImageView) rowView.findViewById(R.id.time_icon);
			nodeView.area = (ImageView) rowView.findViewById(R.id.location_icon);

			rowView.setTag(nodeView);
		} else {
			nodeView = (NodeView) rowView.getTag();
		}

		final LocationNode currentNode = nodes.get(position);
		final String title = currentNode.getNodeTitle();

		final LocationSharingRule rule = currentNode.getLocationSharingRule();
		final LocationGranularity granularity = rule.getGranularity();
		final String granularityDesc = granularity.getDesc();
		String radius = "";
		if (granularity.equals(LocationGranularity.CUSTOM)) {
			radius = " ("+rule.getRadius() +"m)";
		}

		nodeView.nodeTitle.setText(title);
		nodeView.nodeGranularity.setText(granularityDesc+radius);

		if (rule.hasGeoLoc()) {
			nodeView.area.setVisibility(View.VISIBLE);
		} else {
			nodeView.area.setVisibility(View.GONE);
		}
		if (rule.hasTime()) {
			nodeView.time.setVisibility(View.VISIBLE);
		} else {
			nodeView.time.setVisibility(View.GONE);
		}

		return rowView;
	}
}