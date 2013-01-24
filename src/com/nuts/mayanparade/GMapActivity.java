package com.nuts.mayanparade;

import android.os.Bundle;

import com.google.android.maps.MapActivity;

public class GMapActivity extends MapActivity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.map_view);	
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
