package com.nuts.mayanparade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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

import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.facebook.widget.FriendPickerFragment;
import com.facebook.widget.WebDialog;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.SyncStateContract.Constants;
import android.test.PerformanceTestCase;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class FriendsActivity extends Activity
{
	private List<User> _usrList = null;
	private List<User> _emailList = null;
	private List<GraphUser> _fbFriends = null;
	private View _friendListStatus;

	private int _countAux;
	private ImageView _ivAux;
	
	private UiLifecycleHelper uiHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friendlist_view);
		
		findViewById(R.id.friends_view_home).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		findViewById(R.id.friends_view_follow_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				setFriendList();
			}
		});
		
		findViewById(R.id.friends_view_fb_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				setAddByFacebook();
			}
		});
		
		findViewById(R.id.friends_view_mail_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				setAddByEmail();
			}
		});
		
		_friendListStatus = findViewById(R.id.friends_view_progress_layout);
		//Fb setup
		uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    		
		//flwBtn.setImageResource(R.drawable.btnrs_grupo_4);
		/*new Thread(new Runnable() {
			@Override
			public void run()
			{
				ImageView flwBtn = (ImageView)findViewById(R.id.friends_view_follow_btn);
				flwBtn.setImageResource(R.drawable.friends_view_btn_addfb);
				//flwBtn.setImageResource(R.drawable.btnrs_grupo_4);
			}
		}).start();*/

		showProgress(true);
		FriendsTask ft = new FriendsTask(true);
		ft.execute((Void) null);
	}
	
	private void fillFriendList()
	{
		ImageView headLine = new ImageView(this);
		
		Log.i("Ver",">>>>>>Entra a llenar lista de amigos");
		LinearLayout lay = (LinearLayout)findViewById(R.id.friends_view_list_layout);
		lay.removeAllViews();
		headLine.setImageResource(R.drawable.friends_view_linearosa);
		lay.addView(headLine);

		if((_usrList == null) || (_usrList.size() <= 0))
		{
			showProgress(false);
			return;
		}
		
		int usrCount = _usrList.size();

		LinearLayout layFriends[] = new LinearLayout[usrCount];
		ImageView usrImg[] = new ImageView[usrCount];
		ProgressBar imgPB[] = new ProgressBar[usrCount];
		TextView usrName[] = new TextView[usrCount];
		TextView pakCount[] = new TextView[usrCount];
		ImageView addImg[] = new ImageView[usrCount];
		ImageView line[] = new ImageView[usrCount];

		for(int c=0; c<usrCount; c++)
		{
			layFriends[c] = new LinearLayout(this);
			usrImg[c] = new ImageView(this);
			imgPB[c] = new ProgressBar(this);
			usrName[c] = new TextView(this);
			pakCount[c] = new TextView(this);
			addImg[c] = new ImageView(this);
			line[c] = new ImageView(this);

			usrImg[c].setImageResource(R.drawable.com_facebook_profile_default_icon);
			usrName[c].setText(_usrList.get(c).getName()+" "+_usrList.get(c).getLastName());
			pakCount[c].setText(_usrList.get(c).getEmail());
			addImg[c].setImageResource(R.drawable.friends_view_masverde);
			line[c].setImageResource(R.drawable.friends_view_linearosa);
			
			if((_usrList.get(c).getFacebookid() != null) && (!_usrList.get(c).getFacebookid().isEmpty()))
			{
				usrImg[c].setVisibility(View.GONE);
				LoadUrlImageTask imgTask = new LoadUrlImageTask(usrImg[c], pakCount[c], _usrList.get(c).getFacebookid(),imgPB[c],addImg[c]);
				imgTask.execute((Void) null);
			}
			else
				imgPB[c].setVisibility(View.GONE);
			pakCount[c].setText("M/N");
			
			LinearLayout.LayoutParams layParams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams layParams2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams layParams3 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			//LinearLayout.LayoutParams layParams4 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layParams1.setMargins(20, 10, 10, 10);
			layFriends[c].addView(usrImg[c],layParams1);
			layFriends[c].addView(imgPB[c],layParams1);
			layParams2.setMargins(20, 10, 10, 10);
			layParams2.weight = 100;
			layFriends[c].addView(usrName[c],layParams2);
			layParams3.setMargins(10, 10, 10, 10);
			layFriends[c].addView(pakCount[c],layParams3);
			//if somebody like remove button
			//layParams4.setMargins(20, 10, 30, 10);
			//layFriends[c].addView(addImg[c],layParams4);
			lay.addView(layFriends[c]);
			lay.addView(line[c]);
		}

		showProgress(false);
	}
	
	private void fillAddFacebookList()
	{
		LinearLayout lay = (LinearLayout)findViewById(R.id.friends_view_list_layout);
		lay.removeAllViews();
		
		if((_fbFriends == null) || (_fbFriends.size() <= 0))
		{
			showProgress(false);
			return;
		}
		
		int usrCount = _fbFriends.size();

		ImageView headLine = new ImageView(this);
		
		LinearLayout layFriends[] = new LinearLayout[usrCount];
		ImageView usrImg[] = new ImageView[usrCount];
		TextView usrName[] = new TextView[usrCount];
		TextView usrEmail[] = new TextView[usrCount];
		ImageView addImg[] = new ImageView[usrCount];
		ProgressBar imgPB[] = new ProgressBar[usrCount];
		ImageView line[] = new ImageView[usrCount];
		
		headLine.setImageResource(R.drawable.friends_view_lineaverde);
		lay.addView(headLine);
		for(int c=0; c<usrCount; c++)
		{
			layFriends[c] = new LinearLayout(this);
			usrImg[c] = new ImageView(this);
			usrName[c] = new TextView(this);
			usrEmail[c] = new TextView(this);
			addImg[c] = new ImageView(this);
			imgPB[c] = new ProgressBar(this);
			line[c] = new ImageView(this);

			usrName[c].setText(_fbFriends.get(c).getName());
			usrEmail[c].setText("correo");
			usrImg[c].setVisibility(View.GONE);
			addImg[c].setImageResource(R.drawable.friends_view_masverde);
			line[c].setImageResource(R.drawable.friends_view_lineaverde);

			LoadUrlImageTask imgTask = new LoadUrlImageTask(usrImg[c], usrEmail[c], _fbFriends.get(c).getId(),imgPB[c],addImg[c]);
			imgTask.execute((Void) null);

			LinearLayout.LayoutParams layParams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams layParams2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams layParams3 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams layParams4 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layParams1.setMargins(20, 10, 10, 10);
			layFriends[c].addView(usrImg[c],layParams1);
			layFriends[c].addView(imgPB[c],layParams1);
			layParams2.setMargins(20, 10, 10, 10);
			layParams2.weight = 100;
			layFriends[c].addView(usrName[c],layParams2);
			layParams3.setMargins(10, 10, 10, 10);
			layFriends[c].addView(usrEmail[c],layParams3);
			layParams4.setMargins(20, 10, 10, 10);
			layFriends[c].addView(addImg[c],layParams4);
			lay.addView(layFriends[c]);
			lay.addView(line[c]);
		}
		
		showProgress(false);
	}
	
	private void fillAddMailList()
	{
		LinearLayout lay = (LinearLayout)findViewById(R.id.friends_view_list_layout);
		ImageView headLine = new ImageView(this);
		
		lay.removeAllViews();
		headLine.setImageResource(R.drawable.friends_view_lineaverde);
		lay.addView(headLine);
		if((_emailList == null) || (_emailList.size() <= 0))
		{
			showProgress(false);
			return;
		}
		
		int usrCount = _emailList.size();
		
		LinearLayout layFriends[] = new LinearLayout[usrCount];
		ImageView usrImg[] = new ImageView[usrCount];
		ProgressBar imgPB[] = new ProgressBar[usrCount];
		TextView usrName[] = new TextView[usrCount];
		TextView pakCount[] = new TextView[usrCount];
		ImageView addImg[] = new ImageView[usrCount];
		ImageView line[] = new ImageView[usrCount];
		
		for(int c=0; c<usrCount; c++)
		{
			layFriends[c] = new LinearLayout(this);
			usrImg[c] = new ImageView(this);
			imgPB[c] = new ProgressBar(this);
			usrName[c] = new TextView(this);
			pakCount[c] = new TextView(this);
			addImg[c] = new ImageView(this);
			line[c] = new ImageView(this);

			usrImg[c].setImageResource(R.drawable.com_facebook_profile_default_icon);
			usrName[c].setText(_emailList.get(c).getName());
			pakCount[c].setText(_emailList.get(c).getEmail());
			addImg[c].setImageResource(R.drawable.friends_view_masverde);
			line[c].setImageResource(R.drawable.friends_view_lineaverde);

			if((_emailList.get(c).getFacebookid() != null) && (!_emailList.get(c).getFacebookid().isEmpty()))
			{
				usrImg[c].setVisibility(View.GONE);
				LoadUrlImageTask imgTask = new LoadUrlImageTask(usrImg[c], pakCount[c], _emailList.get(c).getFacebookid(),imgPB[c],addImg[c]);
				imgTask.execute((Void) null);
			}
			else
			{
				imgPB[c].setVisibility(View.GONE);
				addImg[c].setId(c+1);
				addImg[c].setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) {
						addFacebookFriendToList(_emailList.get(v.getId()-1).getEmail(), v);
					}
				});
			}

			LinearLayout.LayoutParams layParams1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams layParams2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams layParams3 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams layParams4 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			layParams1.setMargins(20, 10, 10, 10);
			layFriends[c].addView(usrImg[c],layParams1);
			layParams2.setMargins(20, 10, 10, 10);
			layParams2.weight = 100;
			layFriends[c].addView(usrName[c],layParams2);
			layParams3.setMargins(10, 10, 10, 10);
			layFriends[c].addView(pakCount[c],layParams3);
			layParams4.setMargins(20, 10, 10, 10);
			layFriends[c].addView(addImg[c],layParams4);
			lay.addView(layFriends[c]);
			lay.addView(line[c]);
		}
		
		showProgress(false);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		final View flView = (View)findViewById(R.id.friends_view_list);
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			_friendListStatus.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							_friendListStatus.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});
			flView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							flView.setVisibility(show ? View.GONE : View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			_friendListStatus.setVisibility(show ? View.VISIBLE : View.GONE);
			flView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	public void setFriendList()
	{
		/*new Thread(new Runnable() {
			@Override
			public void run()
			{
				ImageView imgvFL = (ImageView)findViewById(R.id.friends_view_follow_btn);
				imgvFL.setImageResource(R.drawable.btnrs_grupo_4);
				
				ImageView imgvFB = (ImageView)findViewById(R.id.friends_view_fb_btn);
				imgvFB.setImageResource(R.drawable.friends_view_btn_addfb);
				
				ImageView imgvEM = (ImageView)findViewById(R.id.friends_view_mail_btn);
				imgvEM.setImageResource(R.drawable.friends_view_btn_addcontact);
			}
		}).start();*/
		
		fillFriendList();
	}
	
	public void setAddByFacebook()
	{
		/*new Thread(new Runnable() {
			@Override
			public void run()
			{
				ImageView imgvFL = (ImageView)findViewById(R.id.friends_view_follow_btn);
				imgvFL.setImageResource(R.drawable.friends_view_btn_following);
				
				ImageView imgvFB = (ImageView)findViewById(R.id.friends_view_fb_btn);
				imgvFB.setImageResource(R.drawable.btnrs_grupo_3);
				
				ImageView imgvEM = (ImageView)findViewById(R.id.friends_view_mail_btn);
				imgvEM.setImageResource(R.drawable.friends_view_btn_addcontact);
			}
		}).start();*/

		LinearLayout lay = (LinearLayout)findViewById(R.id.friends_view_list_layout);
		ImageView headLine = new ImageView(this);
		lay.removeAllViews();
		headLine.setImageResource(R.drawable.friends_view_lineaverde);
		lay.addView(headLine);
		
		getFacebookFriendList();
	}
	
	public void setAddByEmail()
	{
		/*new Thread(new Runnable() {
			@Override
			public void run()
			{
				ImageView imgvFL = (ImageView)findViewById(R.id.friends_view_follow_btn);
				imgvFL.setImageResource(R.drawable.friends_view_btn_following);
				
				ImageView imgvFB = (ImageView)findViewById(R.id.friends_view_fb_btn);
				imgvFB.setImageResource(R.drawable.friends_view_btn_addfb);
				
				ImageView imgvEM = (ImageView)findViewById(R.id.friends_view_mail_btn);
				imgvEM.setImageResource(R.drawable.btnrs_grupo_2);
			}
		}).start();*/
		
		/*LinearLayout lay = (LinearLayout)findViewById(R.id.friends_view_list_layout);
		ImageView headLine = new ImageView(this);
		lay.removeAllViews();
		headLine.setImageResource(R.drawable.friends_view_lineaverde);
		lay.addView(headLine);*/
		
		if((_emailList == null) || (_emailList.size() <= 0))
		{
			showProgress(true);
			AddContactsTask act = new AddContactsTask();
			act.execute((Void) null);
		}
		else
			fillAddMailList();
	}
	
	public void addFacebookFriendToList(String usrEmail, View v)
	{
		AddFriendToListTask addf = new AddFriendToListTask(usrEmail,v);
		addf.execute((Void) null);
	}

	/**
	 * Facebook functions
	 */
	private Session.StatusCallback callback = new Session.StatusCallback()
	{
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception)
	{
		if((session!=null) && session.isOpened())
			getFacebookUserData(session);
	}
	
	private void getFacebookUserData(final Session session)
	{
		Request request = Request.newMeRequest(session,
				new Request.GraphUserCallback()
				{
					@Override
					public void onCompleted(GraphUser user, Response response)
					{
						if((user!=null) && (session==Session.getActiveSession()))
						{
							Log.i("Ver",">>>>>>>>>FB Nombre: "+user.getName()
									+" "+user.asMap().get("email"));
							//getFacebookFriendList();
						}
					}
				});
		request.executeAsync();
	}
	
	private void getFacebookFriendList()
	{
		Log.i("Ver",">>>>>>>>>Obtiene sesión de FB");
		Session actSess = Session.getActiveSession();
		
		Log.i("Ver",">>>>>>>>>Revisa si hay sesión abierta");
		if(actSess.getState().isOpened())
		{
			Log.i("Ver",">>>>>>>>>Prepara petición de lista de amigos");
			Request friendsReq = Request.newMyFriendsRequest(actSess,
				new GraphUserListCallback()
				{
					@Override
					public void onCompleted(List<GraphUser> users, Response response)
					{
						Session actSess = Session.getActiveSession();
						Log.i("Ver",">>>>>>>> Perms: "+actSess.getPermissions().toString());

						_fbFriends = users;
						//for(int c=0; c<users.size(); c++)
							/*Log.i("Ver",">>>>>>>>Resp: "+users.get(c).getName()
									+" "+users.get(c).getId());
									+" "+users.get(c).getLink()
									+" "+users.get(c).asMap().get("email"));*/
						
						fillAddFacebookList();
					}
				});
			Log.i("Ver",">>>>>>>>>Ejecuta lista de amigos");
			Bundle params = new Bundle();
			params.putString("fields", "id, name, email, picture");
			friendsReq.setParameters(params);
			friendsReq.executeAsync();
			Log.i("Ver",">>>>>>>>>Terminó ejecución");
		}
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	  super.onActivityResult(requestCode, resultCode, data);
	  uiHelper.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * End Facebook functions
	 */
	
	public class FriendsTask extends AsyncTask<Void, Void, Boolean>
	{
		private Boolean _makeList = true;
		
		public FriendsTask(Boolean list)
		{
			_makeList = list;
		}
		@Override
		protected Boolean doInBackground(Void... params)
		{
			try
			{
				HttpClient webClient = new DefaultHttpClient();
				HttpPost webPost = new HttpPost("http://www.proyectoskafe.com/pakales/android/getFriends");

				SharedPreferences mPrefs = getSharedPreferences("PrefFile",MODE_PRIVATE);
				List<NameValuePair> data = new ArrayList<NameValuePair>(1);
				Log.i("Ver",">>>>>>>>>Usuario: "+mPrefs.getString("login", null));
				data.add(new BasicNameValuePair("email",mPrefs.getString("login", null)));
				webPost.setEntity(new UrlEncodedFormEntity(data));
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
				
				Serializer xmlRdr = new Persister();
				Users usrs;
				try
				{
					usrs = xmlRdr.read(Users.class, sbuild.toString());
					_usrList = usrs.getUsers();
				}
				catch(Exception e)
				{
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
			if(success && _makeList)
			{
				fillFriendList();
			}
			else
			{
				showProgress(false);
			}
		}
		
		@Override
		protected void onCancelled()
		{
			showProgress(false);
		}
	}
	
	public class AddContactsTask extends AsyncTask<Void, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground(Void... params)
		{
			if(_usrList==null)
			{
				Log.i("Ver",">>>>>>>Error de conexión");
				return false;
			}
			try
			{
				Pattern emailPat = Patterns.EMAIL_ADDRESS; // API level 8+
				Account[] contacts = AccountManager.get(getApplicationContext()).getAccounts();
				List<NameValuePair> data = new ArrayList<NameValuePair>();
				int dataCount = 0;
				
				ContentResolver cr = getContentResolver();
				Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
		                null, null, null, null);
		        if (cur.getCount() > 0)
		        {
		        	data.add(new BasicNameValuePair("nemail", String.valueOf(cur.getCount())));
		        	while (cur.moveToNext())
		        	{
		        		String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
		        		
		        		Cursor emailCur = cr.query(
		        			ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
							null,
							ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
							new String[]{id}, null);
		        		if (emailCur.moveToNext())
		        		{
		        		    String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
		        		    data.add(new BasicNameValuePair("email"+String.valueOf(dataCount), email));
		        		    dataCount++;
		        	 	} 
		        	 	emailCur.close();
		        	}
		        }
		        
				HttpClient webClient = new DefaultHttpClient();
				HttpPost webPost = new HttpPost("http://www.proyectoskafe.com/pakales/android/getUsersByEmail");

				webPost.setEntity(new UrlEncodedFormEntity(data));
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
				
				Serializer xmlRdr = new Persister();
				Users usrs;
				try
				{
					usrs = xmlRdr.read(Users.class, sbuild.toString());
					_emailList = usrs.getUsers();
				}
				catch(Exception e)
				{
					Log.i("Ver",">>>>>>>>>Error XML: "+e.getMessage());
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
				fillAddMailList();
			else
				showProgress(false);
		}
		
		@Override
		protected void onCancelled()
		{
			showProgress(false);
		}
	}
	
	public class LoadUrlImageTask extends AsyncTask<Void, Void, Boolean>
	{
		private ImageView _ivFriend;
		private TextView _tvFriendEMail;
		private String _idFriend;
		private ProgressBar _pbImg;
		private ImageView _ivAdd;
		
		private User _usr;
		private Bitmap _bmp;
		
		public LoadUrlImageTask(ImageView iv, TextView tv, String id, ProgressBar pb, ImageView add)
		{
			_ivFriend = iv;
			_tvFriendEMail = tv;
			_idFriend = id;
			_pbImg = pb;
			_ivAdd = add;
		}
		
		@Override
		protected Boolean doInBackground(Void... params)
		{
			try
			{
				HttpClient webClient = new DefaultHttpClient();
				HttpPost webPost = new HttpPost("http://www.proyectoskafe.com/pakales/android/getUserByFacebook");

				List<NameValuePair> data = new ArrayList<NameValuePair>(1);
				data.add(new BasicNameValuePair("facebook_id", _idFriend));
				webPost.setEntity(new UrlEncodedFormEntity(data));
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
				
				Serializer xmlRdr = new Persister();
				_usr = xmlRdr.read(User.class, sbuild.toString());
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						_tvFriendEMail.setText(_usr.getEmail());
						_ivAdd.setOnClickListener(new OnClickListener()
						{
							@Override
							public void onClick(View v) {
								addFacebookFriendToList(_usr.getEmail(), _ivAdd);
							}
						});
					}
				});
			}
			catch (Exception e)
			{
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						_tvFriendEMail.setText("No registrado en la aplicación");
					}
				});
			}
			
			try
			{
				String imgUrl ="http://graph.facebook.com/"+_idFriend+"/picture?type=small";
				URL url=new URL(imgUrl);
			    _bmp=BitmapFactory.decodeStream(url.openConnection().getInputStream());
			    runOnUiThread(new Runnable()
			    {
			    	@Override
			    	public void run()
			    	{
			    		_ivFriend.setImageBitmap(_bmp);
			    		_pbImg.setVisibility(View.GONE);
			    		_ivFriend.setVisibility(View.VISIBLE);
			    	}
			    });
			}
			catch(Exception e)
			{
				Log.i("Ver",">>>>>>Error en imagen: "+e.getMessage());
				return false;
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(final Boolean sucsses)
		{
			if(!sucsses)
			{
				 runOnUiThread(new Runnable()
			    {
			    	public void run()
			    	{
			    		_ivFriend.setImageResource(R.drawable.friends_view_cuadropic);
			    		_pbImg.setVisibility(View.GONE);
			    		_ivFriend.setVisibility(View.VISIBLE);
			    	}
			    });
			}
		}
		
		@Override
		protected void onCancelled()
		{
			runOnUiThread(new Runnable()
		    {
		    	public void run()
		    	{
		    		_ivFriend.setImageResource(R.drawable.friends_view_cuadropic);
		    	}
		    });
		}
	}
	
	public class AddFriendToListTask extends AsyncTask<Void, Void, Boolean>
	{
		private String _friendEMail;
		private View _lay;
		
		public AddFriendToListTask(String femail, View v)
		{
			_friendEMail = femail;
			_lay = v;
		}
		
		@Override
		protected Boolean doInBackground(Void... params)
		{
			try
			{
				HttpClient webClient = new DefaultHttpClient();
				HttpPost webPost = new HttpPost("http://www.proyectoskafe.com/pakales/android/addFriend");

				SharedPreferences mPrefs = getSharedPreferences("PrefFile",MODE_PRIVATE);
				List<NameValuePair> data = new ArrayList<NameValuePair>(2);
				data.add(new BasicNameValuePair("email", mPrefs.getString("login",null)));
				data.add(new BasicNameValuePair("friend_mail", _friendEMail));
				webPost.setEntity(new UrlEncodedFormEntity(data));
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
				
				if(!sbuild.toString().contains("Ok"))
					return false;
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
		protected void onPostExecute(final Boolean sucsses)
		{
			if(sucsses)
			{
				runOnUiThread(new Runnable()
			    {
			    	public void run()
			    	{
			    		_lay.setVisibility(View.GONE);
			    		showProgress(true);
			    		FriendsTask ft = new FriendsTask(false);
			    		ft.execute((Void) null);
			    	}
			    });
			}
		}
	}
}
