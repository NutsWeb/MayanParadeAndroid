package com.nuts.mayanparade;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.facebook.Session;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends Activity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings_view);	
		
		findViewById(R.id.settings_view_home).setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				finish();
			}
		});
		
		findViewById(R.id.settings_view_cuenta).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				LogoutTask lot = new LogoutTask();
				lot.execute((Void)null);
				finishSession();
			}
		});
		
		findViewById(R.id.settings_view_fb).setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Session actSess = Session.getActiveSession();
				actSess.closeAndClearTokenInformation();
				finishSession();
			}
		});
	}
	
	private void finishSession()
	{
		//Close user
		SharedPreferences mPrefs = getSharedPreferences("PrefFile",MODE_PRIVATE);
	    SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString("login", null);
        editor.commit(); 
        //Go to login
		Intent newActivity = new Intent(getBaseContext(), LoginActivity.class);
		//finish(); //Settings
		//finish(); //Main menu
		newActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(newActivity);
	}
	
	public class LogoutTask extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(Void... params)
		{
			try
			{
				HttpClient webClient = new DefaultHttpClient();
				HttpPost webPost = new HttpPost("http://www.proyectoskafe.com/pakales/acount/logout");
				HttpResponse resp = webClient.execute(webPost);
				
				InputStream resStream = resp.getEntity().getContent();
				InputStreamReader srdr = new InputStreamReader(resStream);
				BufferedReader brdr = new BufferedReader(srdr);
				StringBuilder sbuild = new StringBuilder();
				String sdata = null;
				
				while((sdata = brdr.readLine()) != null)
				{
					if(!sdata.equals("\n"))
						sbuild.append(sdata);
				}
			}
			catch(Exception e)
			{
				return false;
			}
			return true;
		}
	}
}
