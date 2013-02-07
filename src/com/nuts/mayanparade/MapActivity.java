package com.nuts.mayanparade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
//import org.simpleframework.xml.Serializer;
//import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity  
{
	private GoogleMap _map;
	private List<Region> _reg;
	private Region _regTemp;
	private List<Float> _lonTmp;
	private List<Float> _latTmp;
	
	private OptionsTask _optTask = null;
	
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
		
		_lonTmp = new ArrayList<Float>();
		_latTmp = new ArrayList<Float>();
		
		FragmentManager fmng = getFragmentManager();
		MapFragment mapf = (MapFragment)fmng.findFragmentById(R.id.map_view_map);
		if(mapf!=null)
		{
			_map = mapf.getMap();
			LatLng markCoord = new LatLng(19.5, -98.877376);
			CameraPosition camPos = new CameraPosition.Builder().target(markCoord).zoom(14).build();
			_map.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
		}
		
		_optTask = new OptionsTask();
		_optTask.execute((Void) null);
	}
	
	public void setMapLocation(Region reg)
	{
		LatLng markCoord = new LatLng(reg.getLatitude(), reg.getLongitude());

		_map.clear();
		_map.addMarker(new MarkerOptions().position(markCoord).title(reg.getName())
                .snippet("Aca")
            .icon(BitmapDescriptorFactory.defaultMarker(
            		BitmapDescriptorFactory.HUE_AZURE)));
		
		CameraPosition camPos = new CameraPosition.Builder().target(markCoord).zoom(16).build();
		_map.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
	}
	
	private void fillCountryList()
	{
		//_reg = preg.getRegions();
		int pakalCount = _reg.size();
		LinearLayout lay = (LinearLayout)findViewById(R.id.map_view_options_layout);
		
		LinearLayout layOption[] = new LinearLayout[pakalCount];
		TextView tvOption[] = new TextView[pakalCount];
		TextView tvNum[] = new TextView[pakalCount];
		ImageView ivArrow[] = new ImageView[pakalCount];
		
		_lonTmp.clear();
		_latTmp.clear();
		
		for(int c=0; c<pakalCount; c++)
		{
			layOption[c] = new LinearLayout(this);
			tvOption[c] = new TextView(this);
			tvNum[c] = new TextView(this);
			ivArrow[c] = new ImageView(this);
			
			tvOption[c].setText(_reg.get(c).getName());
			tvNum[c].setText("N/M");
			ivArrow[c].setImageResource(R.drawable.map_view_pinkarrow);
			
			LinearLayout.LayoutParams layParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			layParams.setMargins(10, 10, 50, 10);
			layOption[c].addView(tvOption[c], layParams);
			layOption[c].addView(tvNum[c], layParams);
			layOption[c].addView(ivArrow[c], layParams);
			lay.addView(layOption[c], new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			_latTmp.add(_reg.get(c).getLatitude());
			_lonTmp.add(_reg.get(c).getLongitude());
			_regTemp = _reg.get(c);
			
			layOption[c].setId(c+1);
			layOption[c].setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							setMapLocation(_reg.get(v.getId()-1)); //_latTmp.get(v.getId()-1),_lonTmp.get(v.getId()-1));
						}
					});
		}
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class OptionsTask extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(Void... params)
		{
			try
			{
				HttpClient webClient = new DefaultHttpClient();
				HttpPost webPost = new HttpPost("http://www.proyectoskafe.com/pakales/android/regions");
				
				webPost.setEntity(null);
				HttpResponse response = null;
				try
				{
					response = webClient.execute(webPost);
				}
				catch (UnsupportedEncodingException e)
				{
					Log.i("Ver",">>>>>>>Error Cod: "+e.getMessage());
					return false;
				}
				catch (ClientProtocolException e)
				{
					Log.i("Ver",">>>>>>Error Tx: "+e.getMessage());
					return false;
				}
				catch (IOException e)
				{
					Log.i("Ver",">>>>>>Error Cnx: "+e.getMessage());
					return false;
				}
				catch (Exception e) {
					Log.i("Ver",">>>>>>>>Error: wnk:"+e.toString());
					return false;
				}
				
				InputStream resStream = response.getEntity().getContent();
				InputStreamReader srdr = new InputStreamReader(resStream);
				BufferedReader brdr = new BufferedReader(srdr);
				StringBuilder sbuild = new StringBuilder();
				String sdata = null;
				
				while((sdata = brdr.readLine()) != null)
				{
					if(!sdata.equals("\n"))
					sbuild.append(sdata);
				}
	
				Serializer xmlRdr = new Persister();
				PakalesRegion regions;
				try {
					regions = xmlRdr.read(PakalesRegion.class, sbuild.toString());
					_reg = regions.getRegions();
				} catch (Exception e) {
					Log.i("VER",">>>>>>>>>Error: "+e.getMessage());
					return false;
				}
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
			fillCountryList();
		}
		
		@Override
		protected void onCancelled()
		{
		}
	}
}
