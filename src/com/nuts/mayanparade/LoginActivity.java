package com.nuts.mayanparade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login_view);

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						//if (id == R.id.login || id == EditorInfo.IME_NULL) {
						if (id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		
		//Login listener
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		//Add user listeners
		findViewById(R.id.button1).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showCreateAccount();
					}
				});
		findViewById(R.id.add_usr_btn).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						addUserDB();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login_view, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

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
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	//Add user callbacks
	public void showCreateAccount()
	{
		RelativeLayout logForm = (RelativeLayout)findViewById(R.id.login_form);
		logForm.setVisibility(View.GONE);
		RelativeLayout layAdd = (RelativeLayout)findViewById(R.id.add_usr_layer);
		layAdd.setVisibility(View.VISIBLE);
	}
	
	public void addUserDB()
	{
		HttpClient webClient = new DefaultHttpClient();
		HttpPost webPost = new HttpPost("http://www.nuts.mx/pakales/home/addUser");
		
		try
		{
			List<NameValuePair> data = new ArrayList<NameValuePair>(5);
			data.add(new BasicNameValuePair("name", "dummy2"));
			data.add(new BasicNameValuePair("lastname", "dumm2"));
			data.add(new BasicNameValuePair("email", "dummy@test.com"));
			data.add(new BasicNameValuePair("password", "abcd2"));
			data.add(new BasicNameValuePair("type", "site"));
			webPost.setEntity(new UrlEncodedFormEntity(data));
			
			HttpResponse response = webClient.execute(webPost);
			InputStream istrm = response.getEntity().getContent();
			InputStreamReader srdr = new InputStreamReader(istrm);
			BufferedReader brdr = new BufferedReader(srdr);
			StringBuilder sbuild = new StringBuilder();
			String sdata = null;
			
			while((sdata = brdr.readLine()) != null)
				sbuild.append(sdata+";");
			
			Log.i("Versión","AU>>>>>>>>>"+sbuild.toString());
		}
		catch(ClientProtocolException e)
		{
			Log.i("Versión","E>>>>>>>>>>Error protocolo");
		}
		catch(IOException e)
		{
			Log.i("Versión","E>>>>>>>>>>Error IO");
		}
		
		RelativeLayout layAdd = (RelativeLayout)findViewById(R.id.add_usr_layer);
		layAdd.setVisibility(View.GONE);
		RelativeLayout logForm = (RelativeLayout)findViewById(R.id.login_form);
		logForm.setVisibility(View.VISIBLE);
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {

			HttpClient webClient = new DefaultHttpClient();
			HttpPost webPost = new HttpPost("http://www.nuts.mx/pakales/account/login");
			
			try
			{
				List<NameValuePair> data = new ArrayList<NameValuePair>(2);
				EditText etUser = (EditText)findViewById(R.id.email);
				EditText etPass = (EditText)findViewById(R.id.password);
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
	}
}
