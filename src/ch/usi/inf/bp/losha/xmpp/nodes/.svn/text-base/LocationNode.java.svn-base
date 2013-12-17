/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.nodes;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.FormField;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.LeafNode;

import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.packets.LocationSharingRule;
import ch.usi.inf.bp.losha.xmpp.packets.providers.LocationSharingRuleProvider;

/**
 * Client representation of a location node
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class LocationNode {
	private final LeafNode node;
	private String nodeID;
	private String nodeTitle;
	private LocationSharingRule nodeRule;

	public LocationNode(final LeafNode node) {
		this.node = node;

		try {
			final ConfigureForm config = node.getNodeConfiguration();
			final FormField description = new FormField("pubsub#description");
			description.setType("text-single");
			config.addField(description);
			nodeID = node.getId();
			nodeTitle = config.getTitle();
			final String nodeRuleXML = config.getField("pubsub#description").getValues().next();
			final LocationSharingRuleProvider p = new LocationSharingRuleProvider();
			nodeRule = (LocationSharingRule) p.parseExtension(Utils.getParser(nodeRuleXML));
		} catch (final Exception e) {
			Utils.log("-> Error: cannot parse rule.");
		}
	}

	public LocationSharingRule getLocationSharingRule() {
		return nodeRule;
	}

	public LeafNode getNode() {
		return node;
	}

	public String getNodeID() {
		return nodeID;
	}

	public String getNodeTitle() {
		return nodeTitle;
	}

	public void setLocationSharingRule(final LocationSharingRule nodeRule) {
		this.nodeRule = nodeRule;

		final ConfigureForm configForm = new ConfigureForm(FormType.submit);
		final FormField description = new FormField("pubsub#description");
		description.setType("text-single");
		configForm.addField(description);
		configForm.setAnswer("pubsub#description", Utils.toDisableTags(nodeRule.toXML()));

		try {
			node.sendConfigurationForm(configForm);
		} catch (final XMPPException e) {
			Utils.log("-> Error: cannot update node configuration.");
		}
	}
}
