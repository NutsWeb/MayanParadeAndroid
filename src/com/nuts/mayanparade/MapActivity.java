package com.nuts.mayanparade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.R.bool;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
	private List<Pakal> _paks;
	
	private int _regInd;
	private int _sregInd;
	private boolean _resetView;
	private SubRegion _subrSelected;
	
	private OptionsTask _optTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_view);
		_reg = null;
		((TextView)findViewById(R.id.map_view_pais)).setText("");
		
		findViewById(R.id.map_view_home).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		
		findViewById(R.id.map_view_regresarpais).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				resetView();
			}
		});
		
		FragmentManager fmng = getFragmentManager();
		MapFragment mapf = (MapFragment)fmng.findFragmentById(R.id.map_view_map);
		if(mapf!=null)
		{
			_map = mapf.getMap();
			LatLng markCoord = new LatLng(19.5, -98.877376);
			CameraPosition camPos = new CameraPosition.Builder().target(markCoord).zoom(3).build();
			_map.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
		}

		_resetView = false;
		_optTask = new OptionsTask();
		_optTask.execute((Void) null);
	}
	
	public void resetView()
	{
		if(_resetView)
			return;
		
		if(_reg == null)
		{
			_optTask = new OptionsTask();
			_optTask.execute((Void) null);
		}
		else
			fillRegionList();
		
		if(_map!=null)
		{
			_map.clear();
			LatLng markCoord = new LatLng(19.5, -98.877376);
			CameraPosition camPos = new CameraPosition.Builder().target(markCoord).zoom(3).build();
			_map.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
		}
		((TextView)findViewById(R.id.map_view_pais)).setText("");
		_resetView = true;
	}
	
	public void setMapLocation(Region reg)
	{
		LatLng markCoord = new LatLng(reg.getLatitude(), reg.getLongitude());

		_map.clear();
		List<SubRegion> sreg = reg.getSubegions();
		
		for (SubRegion subr : sreg) {
			_map.addMarker(new MarkerOptions()
				.position(new LatLng(subr.getLatitude(), subr.getLongitude()))
				.title(subr.getName())
	            .icon(BitmapDescriptorFactory.defaultMarker(
	            		BitmapDescriptorFactory.HUE_AZURE)));
		}
		
		
		((TextView)findViewById(R.id.map_view_pais)).setText(reg.getName());
		CameraPosition camPos = new CameraPosition.Builder().target(markCoord).zoom(6).build();
		_map.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
		
		fillSubRegionList(reg);
	}
	
	public void setPakalesLocation()
	{
		LatLng markCoord = new LatLng(_subrSelected.getLatitude(), _subrSelected.getLongitude());

		_map.clear();
		for (Pakal pakInfo : _paks)
		{
			_map.addMarker(new MarkerOptions()
			.position(new LatLng(pakInfo.getLatitude(), pakInfo.getLongitude()))
			.title(pakInfo.getName())
			.snippet(pakInfo.getArtist().getName())
	        .icon(BitmapDescriptorFactory.defaultMarker(
	            		BitmapDescriptorFactory.HUE_AZURE)));
		}

		CameraPosition camPos = new CameraPosition.Builder().target(markCoord).zoom(11).build();
 		_map.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
	}
	
	public void getSubRegionPakales(SubRegion sreg)
	{
		_subrSelected = sreg;
		SubRegionPakalesTask srTask = new SubRegionPakalesTask();
		srTask.execute((Void) null);
	}
	
	private void fillRegionList()
	{
		int pakalCount = _reg.size();
		LinearLayout lay = (LinearLayout)findViewById(R.id.map_view_options_layout);
		ImageView headLine = new ImageView(this);
		
		lay.removeAllViews();
		
		LinearLayout layOption[] = new LinearLayout[pakalCount];
		TextView tvOption[] = new TextView[pakalCount];
		TextView tvNum[] = new TextView[pakalCount];
		ImageView ivArrow[] = new ImageView[pakalCount];
		ImageView ivLine[] = new ImageView[pakalCount];
		
		headLine.setImageResource(R.drawable.map_view_greenline);
		lay.addView(headLine, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		for(int c=0; c<pakalCount; c++)
		{
			layOption[c] = new LinearLayout(this);
			tvOption[c] = new TextView(this);
			tvNum[c] = new TextView(this);
			ivArrow[c] = new ImageView(this);
			ivLine[c] = new ImageView(this);
			
			tvOption[c].setText(_reg.get(c).getName());
			tvNum[c].setText("N/M");
			ivArrow[c].setImageResource(R.drawable.map_view_greenarrow);
			ivLine[c].setImageResource(R.drawable.map_view_greenline);
			
			LinearLayout.LayoutParams layParams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams layParams2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams layParams3 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layParams1.setMargins(10, 10, 30, 10);
			layParams1.weight = 300;
			layOption[c].addView(tvOption[c], layParams1);
			layParams2.setMargins(10, 10, 10, 10);
			layParams2.weight = 20;
			layOption[c].addView(tvNum[c], layParams2);
			layParams3.setMargins(10, 10, 10, 10);
			layOption[c].addView(ivArrow[c], layParams3);
			lay.addView(layOption[c], new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			lay.addView(ivLine[c], new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
			layOption[c].setId(c+1);
			_regInd = c;
			_reg.get(c).setIndex(c);
			layOption[c].setOnClickListener(
					new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							setMapLocation(_reg.get(v.getId()-1));
						}
					});
		}
	}
	
	private void fillSubRegionList(Region reg)
	{
		List<SubRegion> sreg = reg.getSubegions();
		int sregCount = sreg.size();
		LinearLayout lay = (LinearLayout)findViewById(R.id.map_view_options_layout);
		ImageView headLine = new ImageView(this);
		
		lay.removeAllViews();
		_resetView = false;
		
		LinearLayout layOption[] = new LinearLayout[sregCount];
		TextView tvOption[] = new TextView[sregCount];
		TextView tvNum[] = new TextView[sregCount];
		ImageView ivArrow[] = new ImageView[sregCount];
		ImageView ivLine[] = new ImageView[sregCount];
		
		_regInd = reg.getIndex();

		lay.addView(headLine, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		for(int c=0; c<sregCount; c++)
		{
			layOption[c] = new LinearLayout(this);
			tvOption[c] = new TextView(this);
			tvNum[c] = new TextView(this);
			ivArrow[c] = new ImageView(this);
			ivLine[c] = new ImageView(this);
			
			tvOption[c].setText(sreg.get(c).getName());
			tvNum[c].setText("N/M");
			ivArrow[c].setImageResource(R.drawable.map_view_pinkarrow);
			ivLine[c].setImageResource(R.drawable.map_view_pinkline);
			
			LinearLayout.LayoutParams layParams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams layParams2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams layParams3 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layParams1.setMargins(10, 10, 20, 10);
			layParams1.weight = 300;
			layOption[c].addView(tvOption[c], layParams1);
			layParams2.setMargins(10, 10, 10, 10);
			layParams2.weight = 20;
			layOption[c].addView(tvNum[c], layParams2);
			layParams3.setMargins(10, 10, 10, 10);
			layOption[c].addView(ivArrow[c], layParams3);
			lay.addView(layOption[c], new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			lay.addView(ivLine[c], new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
			layOption[c].setId(c+1);
			_sregInd = c;
			sreg.get(c).setIndex(c);
			layOption[c].setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							getSubRegionPakales(_reg.get(_regInd).getSubegions().get(v.getId()-1));
						}
					});
		}
	}
	
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
					Log.i("Ver",">>>>>>>>Error: Ukw:"+e.toString());
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
					Log.i("VER",">>>>>>>>>Error XML: "+e.getMessage());
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
			if(success)
				fillRegionList();
		}
		
		@Override
		protected void onCancelled()
		{
		}
	}
	
	public class SubRegionPakalesTask extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(Void... params)
		{
			try
			{
				HttpClient webClient = new DefaultHttpClient();
				HttpPost webPost = new HttpPost("http://www.proyectoskafe.com/pakales/android/regionPakales");
				
				webPost.setEntity(null);
				HttpResponse response = null;
				try
				{
					List<NameValuePair> data = new ArrayList<NameValuePair>(1);
					data.add(new BasicNameValuePair("regionid",String.valueOf(_subrSelected.getId())));
					webPost.setEntity(new UrlEncodedFormEntity(data));
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
				Pakales pakales;
				try {
					pakales = xmlRdr.read(Pakales.class, sbuild.toString());
					_paks = pakales.getPakales();
				} catch (Exception e) {
					Log.i("VER",">>>>>>>>>Error XML: "+e.getMessage());
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
			if(success)
				setPakalesLocation();
		}
		
		@Override
		protected void onCancelled()
		{
		}
	}
}
