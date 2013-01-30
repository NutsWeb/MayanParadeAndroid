package com.nuts.mayanparade;

import java.util.Timer;
import java.util.TimerTask;

import com.qualcomm.QCAR.QCAR;
import com.qualcomm.QCARUnityPlayer.QCARPlayerActivity;
import com.qualcomm.QCARUnityPlayer.QCARUnityPlayer;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;

public class VisorActivity extends QCARPlayerActivity 
{
	private QCARUnityPlayer mQCARView = null;

	private Timer mViewFinderTimer = null;
	      
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
       super.onCreate(savedInstanceState);
   }
  
   @Override
   public void onResume() 
   {
      super.onResume();
     
      if (mQCARView == null) {
             //search the QCAR view
             mViewFinderTimer = new Timer();
             mViewFinderTimer.scheduleAtFixedRate(new QCARViewFinderTask(), 1000, 1000);
      }
   }
  
   @Override
   public void onPause() 
   {
      super.onPause();
      if (mViewFinderTimer != null) {
             mViewFinderTimer.cancel();
             mViewFinderTimer = null;
      }
   }

	class QCARViewFinderTask extends TimerTask 
	{	
		public void run() 
	  {
		  runOnUiThread(new Runnable() 
		  {
			  public void run() 
			  {
			      if (!QCAR.isInitialized()) return; //wait for QCAR init
				  if (mQCARView != null) return;//already found, no need to search
				                     
				  //else search
				  View rootView = findViewById(android.R.id.content);
				  QCARUnityPlayer qcarView = findQCARView(rootView);
				               
				  //if QCAR view has been found, add some android view/widget on top
			      if (qcarView != null)
			      {
			          ViewGroup qcarParentView = (ViewGroup)(qcarView.getParent());
			          View myView = getLayoutInflater().inflate(R.layout.visor_view, null);
			          qcarParentView.addView(myView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			          mQCARView = qcarView;
			      }
		  	  }
		  });
	   } 

		private QCARUnityPlayer findQCARView(View view)
		{
			if (view instanceof QCARUnityPlayer) {
			   return (QCARUnityPlayer)view;
			}
				if (view instanceof ViewGroup) {
			         ViewGroup vg = (ViewGroup)view;
			         for (int i = 0; i < vg.getChildCount(); ++i) {
			               QCARUnityPlayer foundView = findQCARView(vg.getChildAt(i));
			               if (foundView != null)
			                      return foundView;
				}
			}
				return null;
		}
	}
}
