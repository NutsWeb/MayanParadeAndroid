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
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class RegisterUserActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.crear_view);
		
		findViewById(R.id.crear_view_btn_acept).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						verifyInfo();
					}
				});
		findViewById(R.id.crear_view_btn_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showLoginView();
					}
				});
	}
	
	public void verifyInfo()
	{
		EditText etName = (EditText)findViewById(R.id.crear_view_txt_name);
		EditText etEmail = (EditText)findViewById(R.id.crear_view_txt_mail);
		EditText etPass1 = (EditText)findViewById(R.id.crear_view_txt_pass);
		EditText etPass2 = (EditText)findViewById(R.id.crear_view_txt_pass2);
		CheckBox cbTerms = (CheckBox)findViewById(R.id.crear_view_cb_terms);
		
		String txtName = etName.getText().toString();
		String txtEmail = etEmail.getText().toString();
		String txtPass1 = etPass1.getText().toString();
		String txtPass2 = etPass2.getText().toString();
		
		View focusView = null;
		
		Log.i("Ver",">>>>>>>>Buscando errores en los campos");
		if(TextUtils.isEmpty(txtName))
		{
			ErrorMessage(getString(R.string.error_field_required),etName);
			focusView = etName;
		}
		else if(TextUtils.isEmpty(txtEmail))
		{
			ErrorMessage(getString(R.string.error_field_required), etEmail);
			focusView = etEmail;
		}
		else if(!txtEmail.contains("@") || !txtEmail.contains("."))
		{
			ErrorMessage(getString(R.string.error_invalid_email), etEmail);
			focusView = etEmail;
		}
		else if(txtPass1.compareTo(txtPass2) != 0)
		{
			ErrorMessage(getString(R.string.error_incorrect_password), etPass1);
			focusView = etPass1;
		}
		else if(TextUtils.isEmpty(txtPass1))
		{
			ErrorMessage(getString(R.string.error_field_required), etPass1);
			focusView = etPass1;
		}
		else if(txtPass1.length() < 3)
		{
			ErrorMessage(getString(R.string.error_invalid_password), etPass1);
			focusView = etPass1;
		}
		else if(!cbTerms.isChecked())
		{
			ErrorMessage("Debe aceptar los términos y condiciones", cbTerms);
			focusView = cbTerms;
		}
		
		if(focusView != null)
		{
			Log.i("Ver",">>>>>>>>>Hay error");
			focusView.requestFocus();
			return;
		}
		Log.i("Ver",">>>>>>>>>No hubo errores");
		
		showProgress(true);
		addUserDB();
	}
	
	protected void addUserDB()
	{
		EditText etName = (EditText)findViewById(R.id.crear_view_txt_name);
		EditText etLastN = (EditText)findViewById(R.id.crear_view_txt_lastname);
		EditText etEmail = (EditText)findViewById(R.id.crear_view_txt_mail);
		EditText etPass = (EditText)findViewById(R.id.crear_view_txt_pass);
		
		HttpClient webClient = new DefaultHttpClient();
		HttpPost webPost = new HttpPost("http://www.nuts.mx/pakales/home/addUser");
		
		try
		{
			List<NameValuePair> data = new ArrayList<NameValuePair>(5);
			data.add(new BasicNameValuePair("name", etName.getText().toString()));
			data.add(new BasicNameValuePair("lastname", etLastN.getText().toString()));
			data.add(new BasicNameValuePair("email", etEmail.getText().toString()));
			data.add(new BasicNameValuePair("password", etPass.getText().toString()));
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
		}
		catch(ClientProtocolException e)
		{
			ErrorMessage(getString(R.string.error_transmission), etName);
			etName.requestFocus();
			showProgress(false);
			return;
		}
		catch(IOException e)
		{
			ErrorMessage(getString(R.string.error_conection), etName);
			etName.requestFocus();
			showProgress(false);
			return;
		}
		showProgress(false);
		showLoginView();
	}
	
	public void showLoginView()
	{
		finish();
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		final View llay1 = (View)findViewById(R.id.crea_view_input_layout);
		final View llay2 = (View)findViewById(R.id.crea_view_title_layout);
		final View llay3 = (View)findViewById(R.id.crea_view_btns_layout);
		final View llay4 = (View)findViewById(R.id.crea_view_terms_layout);
		
		final View creaStatusView = (View)findViewById(R.id.crea_view_status_layout);
		
		//Button btn_accept = (Button)findViewById(R.id.crear_view_btn_acept);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			creaStatusView.setVisibility(View.VISIBLE);
			creaStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							creaStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			llay1.setVisibility(View.VISIBLE);
			llay2.setVisibility(View.VISIBLE);
			llay3.setVisibility(View.VISIBLE);
			llay4.setVisibility(View.VISIBLE);
			//btn_accept.setVisibility(View.VISIBLE);
			llay1.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							llay1.setVisibility(show ? View.GONE : View.VISIBLE);
							llay2.setVisibility(show ? View.GONE : View.VISIBLE);
							llay3.setVisibility(show ? View.GONE : View.VISIBLE);
							llay4.setVisibility(show ? View.GONE : View.VISIBLE);
							//btn_accept.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			creaStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			llay1.setVisibility(show ? View.GONE : View.VISIBLE);
			llay2.setVisibility(show ? View.GONE : View.VISIBLE);
			llay3.setVisibility(show ? View.GONE : View.VISIBLE);
			llay4.setVisibility(show ? View.GONE : View.VISIBLE);
			//btn_accept.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	//Sets the color for error text's
	protected void ErrorMessage(String errMsg, EditText etField)
	{
		int ecolor = R.color.error_text_color;
		ForegroundColorSpan fgcspan = new ForegroundColorSpan(ecolor);
		SpannableStringBuilder ssbuilder = new SpannableStringBuilder(errMsg);
		ssbuilder.setSpan(fgcspan, 0, errMsg.length(), 0);
		etField.setError(ssbuilder);
	}
	
	//Sets the color for error text's
	protected void ErrorMessage(String errMsg, Button btnField)
	{
		int ecolor = R.color.error_text_color;
		ForegroundColorSpan fgcspan = new ForegroundColorSpan(ecolor);
		SpannableStringBuilder ssbuilder = new SpannableStringBuilder(errMsg);
		ssbuilder.setSpan(fgcspan, 0, errMsg.length(), 0);
		btnField.setError(ssbuilder);
	}
	
	//Sets the color for error text's
	protected void ErrorMessage(String errMsg, CheckBox cbField)
	{
		int ecolor = R.color.error_text_color;
		ForegroundColorSpan fgcspan = new ForegroundColorSpan(ecolor);
		SpannableStringBuilder ssbuilder = new SpannableStringBuilder(errMsg);
		ssbuilder.setSpan(fgcspan, 0, errMsg.length(), 0);
		cbField.setError(ssbuilder);
	}
}
