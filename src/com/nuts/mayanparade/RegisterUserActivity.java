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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
						addUserDB();
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
	
	public void addUserDB()
	{
		HttpClient webClient = new DefaultHttpClient();
		HttpPost webPost = new HttpPost("http://www.nuts.mx/pakales/home/addUser");
		
		try
		{
			EditText etName = (EditText)findViewById(R.id.crear_view_txt_name);
			EditText etLastN = (EditText)findViewById(R.id.crear_view_txt_lastname);
			EditText etEmail = (EditText)findViewById(R.id.crear_view_txt_mail);
			EditText etPass = (EditText)findViewById(R.id.crear_view_txt_pass);
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
		showLoginView();
	}
	
	public void showLoginView()
	{
		//Intent nextAct = new Intent(getBaseContext(),LoginActivity.class);
		finish();
		//startActivity(nextAct);
	}
}
