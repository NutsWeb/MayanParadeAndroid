package com.nuts.mayanparade;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class FriendsActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendlist_view);
		
		fillFriendList();
		
		findViewById(R.id.friends_view_home).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						finish();
					}
				});
	}
	
	private void fillFriendList()
	{
		int usrCount = 4;

		LinearLayout lay = (LinearLayout)findViewById(R.id.friends_view_list_layout);
		ImageView headLine = (ImageView)findViewById(R.id.friends_view_head_line);
		
		LinearLayout layFriends[] = new LinearLayout[usrCount];
		ImageView usrImg[] = new ImageView[usrCount];
		TextView usrName[] = new TextView[usrCount];
		TextView pakCount[] = new TextView[usrCount];
		ImageView addImg[] = new ImageView[usrCount];
		ImageView line[] = new ImageView[usrCount];
		
		lay.removeAllViews();
		lay.addView(headLine);
		for(int c=0; c<usrCount; c++)
		{
			layFriends[c] = new LinearLayout(this);
			usrImg[c] = new ImageView(this);
			usrName[c] = new TextView(this);
			pakCount[c] = new TextView(this);
			addImg[c] = new ImageView(this);
			line[c] = new ImageView(this);

			usrImg[c].setImageResource(R.drawable.friends_view_cuadropic);
			usrName[c].setText("Nombre del amigo");
			pakCount[c].setText("M/N");
			addImg[c].setImageResource(R.drawable.friends_view_masverde);
			line[c].setImageResource(R.drawable.friends_view_linearosa);
			
			LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layParams.setMargins(10, 10, 10, 10);
			layFriends[c].addView(usrImg[c],layParams);
			layFriends[c].addView(usrName[c],layParams);
			layFriends[c].addView(pakCount[c],layParams);
			layFriends[c].addView(addImg[c],layParams);
			lay.addView(layFriends[c]);
			lay.addView(line[c]);
		}
	}
	
	public class FriendsTask extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(Void... params)
		{
			try
			{
				HttpClient webClient = new DefaultHttpClient();
				HttpPost webPost = new HttpPost("http://www.proyectoskafe.com/pakales/android/regionPakales");
				
				HttpResponse resp = webClient.execute(webPost);
			}
			catch (ClientProtocolException e)
			{
				Log.i("Ver",">>>>>>Error Tx");
				return false;
			}
			catch (IOException e)
			{
				Log.i("Ver",">>>>>>Error Cnx");
				return false;
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(final Boolean success)
		{
		}
		
		@Override
		protected void onCancelled()
		{
		}
	}
}
