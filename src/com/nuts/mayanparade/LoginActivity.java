package com.nuts.mayanparade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.*;
import com.facebook.widget.LoginButton;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */

public class LoginActivity extends Activity 
{
	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	//private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;

	private UiLifecycleHelper uiHelper;

	/*private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;*/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login_view);
		
		//Fb setup
		uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    
	    SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
	    String access_token = mPrefs.getString("access_token", null);

	    Session session = Session.getActiveSession();
	    if (session == null) {
	        session = new Session(getApplicationContext());

	        // Check if there is an existing token to be migrated 
	        if(access_token != null) {                              
	            // Clear the token info
	            SharedPreferences.Editor editor = mPrefs.edit();
	            editor.putString("access_token", null);
	            editor.commit();    
	            // Create an AccessToken object for importing
	            // just pass in the access token and take the
	            // defaults on other values
	            AccessToken accessToken = AccessToken.createFromExistingAccessToken(
	                                        access_token,
	                                        null, null, null, null);

	            // statusCallback: Session.StatusCallback implementation
	            session.open(accessToken, callback);
	            Session.setActiveSession(session);
	        }
	    }

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.login_view_txt_email);
		mEmailView.setText(mEmail);

		Log.i("Ver","Agregando el listener de campos");
		mPasswordView = (EditText) findViewById(R.id.login_view_txt_pass);
		/*mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});*/

		//mLoginFormView = findViewById(R.id.login);
		//mLoginStatusView = findViewById(R.id.login_status);
		//mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		//Login listener
		findViewById(R.id.login_view_btn_acept).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		//Add user listeners
		findViewById(R.id.login_view_btn_cc).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showCreateAccount();
					}
				});

		//Facebook Listeners
		LoginButton authButton = (LoginButton) findViewById(R.id.login_view_btn_fb);
		authButton.setReadPermissions(Arrays.asList("email"));
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		/*if (mAuthTask != null) {
			return;
		}*/

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 2) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@") && (!mEmail.contains("."))) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			//mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			//mAuthTask = new UserLoginTask();
			//mAuthTask.execute((Void) null);
		}
	}

	//Add user callbacks
	public void showCreateAccount()
	{
		Intent nextAct = new Intent(getBaseContext(), RegisterUserActivity.class);
		startActivity(nextAct);
	}
	
	/**
	 * Facebook functions
	 */
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) 
	    {
	        Log.i("Fb", "Logged in...");
	        //Fb Login
	    }
	    else if (state.isClosed())
	        Log.i("Fb", "Logged out...");
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	  super.onActivityResult(requestCode, resultCode, data);
	  uiHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}*/
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	/*public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {

			HttpClient webClient = new DefaultHttpClient();
			HttpPost webPost = new HttpPost("http://www.nuts.mx/pakales/account/login");
			
			try
			{
				List<NameValuePair> data = new ArrayList<NameValuePair>(2);
				EditText etUser = (EditText)findViewById(R.id.login_view_txt_email);
				EditText etPass = (EditText)findViewById(R.id.login_view_txt_pass);
				data.add(new BasicNameValuePair("username", etUser.getText().toString()));
				data.add(new BasicNameValuePair("password", etPass.getText().toString()));
				webPost.setEntity(new UrlEncodedFormEntity(data));
				
				HttpResponse response = webClient.execute(webPost);
				InputStream resStream = response.getEntity().getContent();
				InputStreamReader srdr = new InputStreamReader(resStream);
				BufferedReader brdr = new BufferedReader(srdr);
				StringBuilder sbuild = new StringBuilder();
				String sdata = null;

				while((sdata = brdr.readLine()) != null)
					sbuild.append(sdata+"\n");
				
				if(!sbuild.toString().equals("Ok"))
					return false;
			}
			catch (ClientProtocolException e)
			{
				Log.i("Versión","E>>>>>>>>>>Error protocolo");
				return false;
			}
			catch (IOException e)
			{
				Log.i("Versión","E>>>>>>>>>>Error IO");
				return false;
			}
			
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				Intent nextAct = new Intent(getBaseContext(),MapActivity.class);
				startActivity(nextAct);
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}*/
}
