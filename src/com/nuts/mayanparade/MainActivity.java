package com.nuts.mayanparade;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_view);
		
		findViewById(R.id.main_view_btn_visor).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						launchRAViewer();
					}
				});
		findViewById(R.id.main_view_btn_gallery).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						launchGallery();
					}
				});
		findViewById(R.id.main_view_btn_map).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						launchMap();
					}
				});
		findViewById(R.id.main_view_btn_friends).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						launchFriendList();
					}
				});
		findViewById(R.id.main_view_btn_settings).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						launchSettings();
					}
				});
	}
	
	protected void launchRAViewer()
	{
		Intent nextAct = new Intent(getBaseContext(),VisorActivity.class);
		startActivity(nextAct);
	}
	
	protected void launchGallery()
	{
		Intent nextAct = new Intent(getBaseContext(),GalleryActivity.class);
		startActivity(nextAct);
	}
	
	protected void launchMap()
	{
		Intent nextAct = new Intent(getBaseContext(),MapActivity.class);
		startActivity(nextAct);
	}
	
	protected void launchFriendList()
	{
		/*Intent nextAct = new Intent(getBaseContext(),FriendsActivity.class);
		startActivity(nextAct);*/
	}
	
	protected void launchSettings()
	{
		Intent nextAct = new Intent(getBaseContext(),SettingsActivity.class);
		startActivity(nextAct);
	}
}
