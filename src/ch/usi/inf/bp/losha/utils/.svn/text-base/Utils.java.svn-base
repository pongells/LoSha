/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.utils;

import java.io.StringReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Utility class.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class Utils {
	private final static String TAG = "asmack";

	private static XmlPullParserFactory factory;
	private static XmlPullParser parser;

	static {
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			parser = factory.newPullParser();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns an hash of the text or (on exception) the text without spaces
	 * 
	 * @param text
	 * @return hash or nonce
	 */
	public static String getHash(final String text) {
		String hash;
		try {
			final MessageDigest m = MessageDigest.getInstance("MD5");
			final byte[] data = text.getBytes();
			m.update(data, 0, data.length);
			final BigInteger i = new BigInteger(1, m.digest());
			hash = String.format("%1$032X", i);
		} catch (final NoSuchAlgorithmException e) {
			hash = text.replaceAll(" ", "");
		}
		return hash;
	}

	public static XmlPullParser getParser(final String xmlExtensionString) {
		try {
			parser.setInput(new StringReader(xmlExtensionString));
			parser.next();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return parser;
	}

	public static void log(final String string) {
		Log.v(TAG, string);
	}

	public static String toDisableTags(final String aText) {
		return aText.replace("<", "&lt;").replace(">", "&gt;");
	}

	public static String toXMLTags(final String aText) {
		return aText.replace("&lt;", "<").replace("&gt;", ">");
	}

	public static void colorAllTargets(final ViewGroup view, final String tag, final int color) {
		final int childs = view.getChildCount();
		for (int i=0;i<childs;i++) {
			final View v = view.getChildAt(i);
			if (v instanceof ViewGroup) {
				colorAllTargets((ViewGroup) v, tag, color);
			}
			if (v.getTag() != null && v.getTag().equals(tag)) {
				v.setBackgroundColor(color);
			}
		}
	}
}
