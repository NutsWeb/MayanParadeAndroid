package com.nuts.mayanparade;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemSelectedListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class GalleryActivity extends Activity 
{
	List<File> listOfFiles;
	Uri screenshotUri;
	ImageView myFullScreen;
	Gallery myGallery;
	ImageAdapter IAdapter;
	Boolean deleteFoto = false;
	int lastPosition = 0;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		listOfFiles = new ArrayList<File>();
		//Load files
		File path = new File(getExternalCacheDir(), "../files/Gallery");
		Log.i("Gallery", "Path >>>>>>>> " + path.getAbsolutePath() + " " + Environment.getExternalStorageDirectory().getAbsolutePath());
		if(path.isDirectory())
		{
			File[] files = path.listFiles(); 
			Log.i("Gallery", "Path >>>>>>>> " + files.length);
		    for (int i = 0; i < files.length; i++) 
		    {
		    	if (files[i].isFile() && MimeTypeMap.getFileExtensionFromUrl(files[i].getAbsolutePath()).equalsIgnoreCase("png"))
		    			//(MimeTypeMap.getFileExtensionFromUrl(files[i].getAbsolutePath()).equalsIgnoreCase("png") ||
		    			 //MimeTypeMap.getFileExtensionFromUrl(files[i].getAbsolutePath()).equalsIgnoreCase("jpg") ||
		    			 //MimeTypeMap.getFileExtensionFromUrl(files[i].getAbsolutePath()).equalsIgnoreCase("jpeg") ))
		    	{
		    		listOfFiles.add(files[i]);
		    		Log.i("Gallery", "Path >>>>>>>> " + files[i].getAbsolutePath());
		    	}
		    }
		}
		
		
		Log.i("Gallery", "Filter size >>>>>>>> " + listOfFiles.size());
	    // Setup
 		setContentView(R.layout.gallery_view);
 		myFullScreen = (ImageView) findViewById(R.id.myFullScreen);
 		
 		// Bind the gallery defined in the main.xml
 		// Apply a new (customized) ImageAdapter to it.
 		myGallery = (Gallery) findViewById(R.id.myGallery);
 		IAdapter = new ImageAdapter(this);
 		myGallery.setAdapter(IAdapter);

		// Events listerners
		myGallery.setOnItemSelectedListener(OnGallerySelected);
		findViewById(R.id.gallery_view_emailSend).setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				SharePicture ();
			}
		});
		
		findViewById(R.id.gallery_view_delete).setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				listOfFiles.get(lastPosition).delete();
				listOfFiles.remove(lastPosition);
			}
		});
		
		findViewById(R.id.gallery_view_home).setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				finish();
			}
		});
	}
	
	OnItemSelectedListener OnGallerySelected = new OnItemSelectedListener()
	{
		public void onItemSelected(AdapterView<?> parent, View v, int position, long id) 
		{
			screenshotUri = Uri.fromFile(IAdapter.getImageFull(position));
			myFullScreen.setImageURI(screenshotUri);
			lastPosition = position;
		}
		public void onNothingSelected(AdapterView<?> parent){ }
	};

	public void SharePicture( )
	{
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("image/*");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Mayan parade!");
		sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}
	
	public class ImageAdapter extends BaseAdapter 
	{
		/** The parent context */
		private Context myContext;
		// Put some images to project-folder: /res/drawable/
		// format: jpg, gif, png, bmp, ...

		/** Simple Constructor saving the 'parent' context. */
		public ImageAdapter(Context c) 
		{
			this.myContext = c;
		}

		// inherited abstract methods - must be implemented
		// Returns count of images, and individual IDs
		public int getCount() 
		{
			return listOfFiles.size();
		}

		public File getImageFull(int position)
		{
			return listOfFiles.get(position);
		}

		public Object getItem(int position)
		{
			return position;
		}

		public long getItemId(int position) 
		{
			return position;
		}
		// Returns a new ImageView to be displayed,
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// Get a View to display image data 					
			ImageView iv = new ImageView(this.myContext);
			iv.setImageURI(Uri.fromFile(listOfFiles.get(position)));
			// Image should be scaled somehow
			iv.setScaleType(ImageView.ScaleType.FIT_END);
			// Set the Width & Height of the individual images
			iv.setLayoutParams(new Gallery.LayoutParams(95, 70));
			return iv;
		}
	}
}

