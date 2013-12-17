/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.connect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import ch.usi.inf.bp.losha.MainActivity;
import ch.usi.inf.bp.losha.R;
import ch.usi.inf.bp.losha.service.LoShaService;
import ch.usi.inf.bp.losha.settings.Preferences;
import ch.usi.inf.bp.losha.utils.Utils;

/**
 * Register Activity
 * Shows a simple registration form with username, password,
 * full name and email. All fields are mandatory.
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class RegisterActivity extends Activity {
	private static final String ACTION_OK = "success";
	private final Messenger mMessenger = new Messenger(new IncomingHandler());
	private LoShaService service;

	private TextView usernameView;
	private TextView passwordView;
	private TextView passwordViewAgain;
	private TextView nameView;
	private TextView emailView;

	private ProgressDialog registerProgressDialog;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.register);

		((TextView) findViewById(R.id.title_text)).setText(getTitle());
		((LinearLayout) findViewById(R.id.titleBar)).setBackgroundColor(Preferences.getTitleBackgroundColor());
		Utils.colorAllTargets((ScrollView) findViewById(R.id.scrollView), "section", Preferences.getSectionBackgroundColor());

		usernameView = (TextView) findViewById(R.id.reg_username);
		passwordView = (TextView) findViewById(R.id.reg_password);
		passwordViewAgain = (TextView) findViewById(R.id.reg_password_again);
		nameView = (TextView) findViewById(R.id.reg_name);
		emailView = (TextView) findViewById(R.id.reg_email);
	}

	@Override
	protected void onResume() {
		super.onResume();
		service = LoShaService.getService();
	}

	public void onHomeClick(final View target) {
		final Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	public void onRegisterClick(final View target) {
		final String username = usernameView.getText().toString();
		final String password = passwordView.getText().toString();
		final String passwordAgain = passwordViewAgain.getText().toString();
		final String name = nameView.getText().toString();
		final String email = emailView.getText().toString();

		if (username != null && !username.equals("") &&
				password != null && !password.equals("") &&
				name != null && !name.equals("") &&
				email != null && !email.equals("")) {

			if (password.equals(passwordAgain)) {
				doRegister(username, password, name, email);
			} else {
				// passwrod mismatch!
				passwordView.setError(this.getString(R.string.err_pass_mismatch));
				Toast.makeText(RegisterActivity.this, this.getString(R.string.error) + ": " +
						this.getString(R.string.err_pass_mismatch), Toast.LENGTH_LONG).show();
			}
		} else {
			// missing info
			Toast.makeText(RegisterActivity.this, this.getString(R.string.error) + ": " +
					this.getString(R.string.err_missing), Toast.LENGTH_LONG).show();
		}
	}

	private final void doRegister(final String user, final String pass,
			final String name, final String email) {
		registerProgressDialog = ProgressDialog.show(RegisterActivity.this,
				this.getString(R.string.register_title),
				this.getString(R.string.reg_text), true, true);

		service.register(user, pass, name, email, mMessenger);
	}

	// ================== ACTIVITY CYCLE ==================

	@Override
	protected void onPause() {
		super.onPause();
		clean();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		clean();
	}

	private void clean() {
		service = null;
		if (registerProgressDialog != null
				&& registerProgressDialog.isShowing()) {
			registerProgressDialog.dismiss();
		}
	}

	// ================== SERVICE RELATED ==================
	/**
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case LoShaService.REGISTER_ANSWER:
				registerProgressDialog.dismiss();
				final String result = (String) msg.obj;
				if (result != null) {
					if (result.equals(ACTION_OK)) {
						final Intent intent = new Intent();
						intent.putExtra("user", usernameView.getText().toString());
						intent.putExtra("pass", passwordView.getText().toString());
						RegisterActivity.this.setResult(Activity.RESULT_OK, intent);
						finish();
					} else {
						passwordView.setText("");
						passwordViewAgain.setText("");
						Toast.makeText(
								RegisterActivity.this,
								RegisterActivity.this.getString(R.string.error)
								+ ": " + msg.obj.toString(),
								Toast.LENGTH_LONG).show();
					}
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

}
