/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.about;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import ch.usi.inf.bp.losha.MainActivity;
import ch.usi.inf.bp.losha.R;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.utils.SectionLabel;
import ch.usi.inf.bp.losha.utils.Utils;

/**
 * About Activity
 * Shows an about screen with clickable links.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 * 
 */
public class AboutActivity extends Activity {
	private WebView webview;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.about);

		((TextView) findViewById(R.id.title_text)).setText(getTitle());
		((LinearLayout) findViewById(R.id.titleBar)).setBackgroundColor(Preferences.getTitleBackgroundColor());
		Utils.colorAllTargets((ScrollView) findViewById(R.id.scrollView), "section", Preferences.getSectionBackgroundColor());
		
		final ImageView usiLogo = (ImageView) findViewById(R.id.usi);
		usiLogo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				final Intent browserIntent = new Intent("android.intent.action.VIEW",
						Uri.parse("http://www.inf.usi.ch"));
				startActivity(browserIntent);
			}
		});
 
		final SectionLabel version = (SectionLabel) findViewById(R.id.version);
		final String revision = getString(R.string.version);
		version.setExtraText("v"+revision.split(" ")[1]);
		
		final TextView author = (TextView) findViewById(R.id.author_name);
		author.setLinkTextColor(getResources().getColor(R.color.link));
		Linkify.addLinks(author, Linkify.ALL);

		final TextView professor = (TextView) findViewById(R.id.professor_name);
		professor.setLinkTextColor(getResources().getColor(R.color.link));
		Linkify.addLinks(professor, Linkify.ALL);

		final TextView supervisor = (TextView) findViewById(R.id.supervisor_name);
		supervisor.setLinkTextColor(getResources().getColor(R.color.link));
		Linkify.addLinks(supervisor, Linkify.ALL);
		
		webview = (WebView) findViewById(R.id.webview);
		webview.loadUrl("file:///android_asset/libraries");
		webview.getSettings().setDefaultFontSize(14);
	}

	public void onHomeClick(final View target) {
		final Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
}
