package com.nuts.mayanparade;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
		
		findViewById(R.id.settings_view_fb).setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				//Close user
				SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
			    SharedPreferences.Editor editor = mPrefs.edit();
		        editor.putString("login", null);
		        editor.commit(); 
		        //Go to login
				Intent newActivity = new Intent(getBaseContext(), LoginActivity.class);
				finish();
				startActivity(newActivity);
			}
		});
	}
}
