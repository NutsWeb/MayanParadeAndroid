package com.nuts.mayanparade;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.*;

public class MapActivity extends Activity  
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.map_view);	
		
		findViewById(R.id.map_view_home).setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				finish();
			}
		});
	}
}
