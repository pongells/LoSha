/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.xmpp.users;

import java.util.Collection;
import java.util.List;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket.ItemStatus;
import org.jivesoftware.smack.packet.RosterPacket.ItemType;
import org.jivesoftware.smackx.packet.VCard;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.Subscription;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.location.Location;
import android.util.DisplayMetrics;
//import ch.usi.inf.bp.losha.R;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.tasks.BlockUserTask;
import ch.usi.inf.bp.losha.tasks.GeocodeLocationTask;
import ch.usi.inf.bp.losha.tasks.SubscribeUserToNode;
import ch.usi.inf.bp.losha.utils.Utils;
import ch.usi.inf.bp.losha.xmpp.XMPPNodesManager;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationGranularity;
import ch.usi.inf.bp.losha.xmpp.nodes.LocationNode;
import ch.usi.inf.bp.losha.xmpp.nodes.SubscriptionAction;
import ch.usi.inf.bp.losha.xmpp.packets.Geoloc;

/**
 * Object representing a Friend and all his data.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class Friend implements User {
	private final Roster roster;
	private RosterEntry entry;
	private final XMPPNodesManager nodesManager;
	private VCard vCard;
	private String email;
	private Bitmap avatar;
	private Drawable marker;

	private String currentNodeId;
	private Geoloc location;
	private Presence presence;
	private final Context context;

	public static enum Type {
		ACTUAL, PENDING, REQUESTING, NEW;
	}
	
	public Friend(final Roster roster, final RosterEntry entry, final XMPPNodesManager nodesManager) {
		this.roster = roster;
		this.entry = entry;
		this.nodesManager = nodesManager;
		this.context = Preferences.getAppContext();
	}

	public String getEmail() {
		return email;
	}

	public String getGroup() {
		final Collection<RosterGroup> groups = entry.getGroups();
		if (!groups.isEmpty()) {
			return groups.iterator().next().getName();
		} else {
			return null;
		}
	}

	public Geoloc getLocation() {
		return location;
	}

	public Location getMapLocation() {
		final Location mapLocation = new Location("losha");
		if (location != null) {
			mapLocation.setLatitude(location.getLat());
			mapLocation.setLongitude(location.getLon());
		}
		return mapLocation;
	}

	public String getName() {
		return entry.getName();
	}

	public Presence getPresenceFromRoster() {
		return roster.getPresence(getUser());
	}
	public Presence getPresence() {
		return presence;
	}
	public void setPresence(final Presence presence) {
		this.presence = presence;
	}

	public ItemStatus getStatus() {
		return entry.getStatus();
	}

	public ItemType getType() {
		return entry.getType();
	}

	public String getUser() {
		return entry.getUser();
	}

	public boolean hasVCard() {
		return vCard != null;
	}

	public void removeSubscriptions(final LocationNode node) {
		final LeafNode pubsubNode = node.getNode();
		final List<Subscription> subs = nodesManager.getSubscriptions(node.getNodeID());

		for (final Subscription subscription : subs) {
			if (subscription.getJid().contains(getUser())) {
				nodesManager.removeSubscription(pubsubNode, subscription);
			}
		}
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public void setEntry(final RosterEntry entry) {
		this.entry = entry;
	}

	@Override
	public void setLocation(final Geoloc geoloc) {
		location = geoloc;
		//location can be null if for instance the source is running the app on the emulator
		//i.e. if the source geocoding fails and he sends only the coordinates to the server
		//in this case, if he puts me under some group different than "BEST" I will not receive anything
		if (location != null) {
			makeMarker();
			//log
			if (Preferences.isSendDataUsageEnabled()) {
				nodesManager.sendUsageLocationSeen(getUser());
			}
		}
	}

	public void setAvatar(final Bitmap bitmap) {
		this.avatar = bitmap;
	}

	public Bitmap getAvatar() {
		return avatar;
	}

	//set additional user data, e.g. avatar
	public void setVCard(final VCard vCard) {
		this.vCard = vCard;
		final byte[] avatarByte = vCard.getAvatar();
		if (avatarByte != null) {
			final DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			final Options myOptions = new Options();
			myOptions.inScaled = false;
			myOptions.inScreenDensity = metrics.densityDpi;
			myOptions.inTargetDensity = metrics.densityDpi;
			setAvatar(BitmapFactory.decodeByteArray(avatarByte, 0, avatarByte.length, myOptions));
			makeMarker();
		}
		email = vCard.getEmailHome();
		entry.setName(vCard.getFirstName());
	}

	public void subscribeToAsync(final LocationNode node, final LocationNode oldNode) {
		new SubscribeUserToNode(this).execute(node, oldNode);
	}

	public void subscribeTo(final LocationNode node, final LocationNode oldNode) {
		try {
			roster.createEntry(getUser(), getName(), new String[] { node.getNodeTitle() });
			if (oldNode != null) {
				removeSubscriptions(oldNode);
			}
			nodesManager.sendSubscriptionUpdate(getUser(), node.getNodeID(),
					SubscriptionAction.SUBSCRIBE);
		} catch (final XMPPException e) {
			Utils.log("failed to move friend " + e);
		}
	}

	public void blockAsync(final LocationNode oldNode) {
		new BlockUserTask(this).execute(oldNode);
	}

	public void block(final LocationNode oldNode) {
		final String blockedGroup = context.getString(R.string.friends_group_blocked);
		try {
			roster.createEntry(getUser(), getName(), new String[] { blockedGroup });
			if (oldNode != null) {
				removeSubscriptions(oldNode);
			}
		} catch (final XMPPException e) {
			Utils.log("failed to move friend " + e);
		}
	}

	@Override
	public void loadLocationData(final Location location) {
		new GeocodeLocationTask(this).execute(location);
	}

	public void makeMarker() {
		int granularityType = -1;
		LocationGranularity granularity;

		if (location != null) {
			granularityType = (int) location.getAccuracy();
		}
		if (granularityType > LocationGranularity.values().length || granularityType == -1) {
			granularity = LocationGranularity.CUSTOM;
		} else {
			granularity = LocationGranularity.values()[granularityType];
		}

		final int color = Preferences.getMarkerColor(granularity);

		Drawable drawable;
		if (avatar != null) {
			drawable = resize(40,40,new BitmapDrawable(context.getResources(), avatar));
		} else {
			drawable = context.getResources().getDrawable(R.drawable.avatar_not_found);
		}

		final Path path = new Path();
		path.moveTo(0, 0);
		path.lineTo(50, 50);
		path.lineTo(100, 0);
		path.close();
		final ShapeDrawable triangle = new ShapeDrawable(new PathShape(path, 100, 50));
		triangle.getPaint().setColor(color);

		final float[] outerR = new float[] { 12, 12, 12, 12, 4, 4, 4, 4 };
		final RectF   inset = new RectF(5, 5, 5, 1);
		final float[] innerR = new float[] { 12, 12, 12, 12, 12, 12, 12, 12 };
		final ShapeDrawable background = new ShapeDrawable(new RoundRectShape(outerR, inset, innerR));
		background.getPaint().setColor(color);

		final Drawable[] layers = new Drawable[3];
		layers[0] = triangle;
		layers[1] = drawable;
		layers[2] = background;
		final LayerDrawable layerDrawable = new LayerDrawable(layers);
		layerDrawable.setLayerInset(1, 5, 5, 5, 1);
		layerDrawable.setLayerInset(0, 0, drawable.getIntrinsicHeight()+5, 0, -20);

		layerDrawable.setBounds(
				-layerDrawable.getIntrinsicWidth() / 2,
				-layerDrawable.getIntrinsicHeight()-20, //up 20 (arrow)
				layerDrawable.getIntrinsicWidth() / 2,
				-20);

		this.marker = layerDrawable;
	}

	private BitmapDrawable resize(final float newWidth, final float newHeight, final BitmapDrawable bdImage) {
		final float scale = context.getResources().getDisplayMetrics().density;
		final Bitmap bitmapOrig = bdImage.getBitmap();
		final float scaleWidth = scale * newWidth / bitmapOrig.getWidth();
		final float scaleHeight =  scale * newHeight / bitmapOrig.getHeight();
		final Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		final Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrig, 0, 0, bitmapOrig.getWidth(), bitmapOrig.getHeight(), matrix, true);
		final BitmapDrawable bitmapDrawableResized = new BitmapDrawable(resizedBitmap);
		return bitmapDrawableResized;
	}

	public Drawable getMarker() {
		return marker;
	}

	/**
	 * @param sourceNodeId
	 */
	public void setCurrentNodeId(final String sourceNodeId) {
		Utils.log("--> Received location of "+getUser()+" from node "+sourceNodeId);
		this.currentNodeId = sourceNodeId;
	}
	public String getCurrentNodeId() {
		return currentNodeId;
	}
}
