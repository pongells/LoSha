/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.nodes;

import java.util.ArrayList;
import java.util.HashMap;

import ch.usi.inf.bp.losha.utils.Utils;

/**
 * Repository of my location nodes.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class LocationNodesRepository {
	// title, node
	private final HashMap<String, LocationNode> nodes;

	public LocationNodesRepository() {
		nodes = new HashMap<String, LocationNode>();
	}

	public void addNode(final String title, final LocationNode node) {
		nodes.put(title, node);
		Utils.log("-> Node added: " + title);
	}

	public void deleteNode(final String title) {
		nodes.remove(title);
		Utils.log("-> Node removed: " + title);
	}

	public LocationNode getNode(final String title) {
		return nodes.get(title);
	}

	public ArrayList<LocationNode> getNodeList() {
		return new ArrayList<LocationNode>(nodes.values());
	}

	public ArrayList<String> getNodeListKeys() {
		return new ArrayList<String>(nodes.keySet());
	}

	public boolean hasGroup(final String title) {
		return nodes.containsKey(title);
	}

}
