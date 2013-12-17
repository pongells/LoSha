/**
 * University of Lugano - Faculty of Informatics
 * Bachelor Project - Ubiquitous Computing Group
 * An experimental Location Sharing platform (LoSha)
 */
package ch.usi.inf.bp.losha.connect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
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
 * Login Activity
 * Shows a simple login screen with username, password and
 * checkboxes for auto login and keeping data in Preferences.
 * 
 * Menu: register
 * 
 * @author Stefano Pongelli <stefano.pongelli@usi.ch>
 */
public class LoginActivity extends Activity {
	private static final String ACTION_OK = "success";
	private final Messenger mMessenger = new Messenger(new IncomingHandler());
	private LoShaService service;
	private TextView usernameView;
	private TextView passwordView;
	private CheckBox rememberLogin;
	private ProgressDialog loginProgressDialog;
	private CheckBox autoLogin;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login);

		//fill colors and title
		((TextView) findViewById(R.id.title_text)).setText(getTitle());
		((LinearLayout) findViewById(R.id.titleBar)).setBackgroundColor(Preferences.getTitleBackgroundColor());
		Utils.colorAllTargets((ScrollView) findViewById(R.id.scrollView), "section", Preferences.getSectionBackgroundColor());

		//grab views
		usernameView = (TextView) findViewById(R.id.username);
		passwordView = (TextView) findViewById(R.id.password);
		rememberLogin = (CheckBox) findViewById(R.id.rememberLogin);
		autoLogin = (CheckBox) findViewById(R.id.autoLogin);
	}

	//called with autologin after registration
	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			service = LoShaService.getService();

			final String user = data.getStringExtra("user");
			final String pass = data.getStringExtra("pass");

			doLogin(user, pass);
		}
	}

	//init fields
	@Override
	protected void onResume() {
		super.onResume();
		service = LoShaService.getService();

		final String user = Preferences.getUsername();
		final String pass = Preferences.getPassword();
		if (user != null && pass != null) {
			usernameView.setText(user);
			passwordView.setText(pass);
			rememberLogin.setChecked(true);
			autoLogin.setEnabled(true);

			if (Preferences.isAutoLoginEnabled()) {
				autoLogin.setChecked(true);
			}
		}
	}

	// ================== ON CLICK ==================

	public void onHomeClick(final View target) {
		final Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	public void onRegisterClick(final View target) {
		final Intent intent = new Intent(this, RegisterActivity.class);
		startActivityForResult(intent, 1);
	}

	public void onRememberLoginClick(final View target) {
		if (rememberLogin.isChecked()) {
			autoLogin.setEnabled(true);
		} else {
			autoLogin.setEnabled(false);
			autoLogin.setChecked(false);
		}
	}

	public void onAutoLoginClick(final View target) {
		if(!autoLogin.isChecked()) {
			Preferences.setAutoLogin(false);
		}
	}

	public void onLoginClick(final View target) {
		final String username = usernameView.getText().toString();
		final String password = passwordView.getText().toString();
		if (username == null || username.equals("")) {
			usernameView.setError(this.getString(R.string.err_not_empty));
		}
		if (password == null || password.equals("")) {
			passwordView.setError(this.getString(R.string.err_not_empty));
		}
		if (usernameView.getError() == null
				&& passwordView.getError() == null) {
			doLogin(username, password);
		}
	}

	private final void doLogin(final String user, final String pass) {
		loginProgressDialog = ProgressDialog.show(LoginActivity.this,
				this.getString(R.string.login_title),
				this.getString(R.string.login_text), true, true,
				new OnCancelListener() {
			@Override
			public void onCancel(final DialogInterface dialog) {
				service.disconnect();
			}
		});
		service.login(user, pass, mMessenger);
	}

	private void storeLoginInfo() {
		final String username = usernameView.getText().toString();
		final String password = passwordView.getText().toString();
		if (username != null && !username.equals("") &&
				password != null && !password.equals("")) {
			Preferences.setUsername(username);
			Preferences.setPassword(password);
			Preferences.setAutoLogin(autoLogin.isChecked());
		}
	}

	private void clearLoginInfo() {
		Preferences.clearLoginData();
	}

	// ================== MENU ==================

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.login, menu);
		return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.register:
			final Intent intent = new Intent(this, RegisterActivity.class);
			startActivityForResult(intent, 1);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
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
		if (loginProgressDialog != null && loginProgressDialog.isShowing()) {
			loginProgressDialog.dismiss();
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
			case LoShaService.LOGIN_ANSWER:
				loginProgressDialog.dismiss();
				final String result = (String) msg.obj;
				if (result != null) {
					if (result.equals(ACTION_OK)) {
						Preferences.setSessionInit(true);
						if (rememberLogin.isChecked()) {
							storeLoginInfo();
						} else {
							clearLoginInfo();
						}
						finish();
					} else {
						Preferences.setAutoLogin(false);
						findViewById(R.id.login_view).startAnimation(
								AnimationUtils.loadAnimation(LoginActivity.this, R.anim.shake));
						Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.error)
								+ ": " + msg.obj.toString(), Toast.LENGTH_LONG).show();
					}
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}
}
