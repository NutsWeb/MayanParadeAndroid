package com.nuts.mayanparade;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class GalleryActivity extends Activity 
{
	ImageView myFullScreen;
	Gallery myGallery;
	ImageAdapter IAdapter;
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.gallery_view);
		myFullScreen = (ImageView) findViewById(R.id.myFullScreen);
		
		// Bind the gallery defined in the main.xml
		// Apply a new (customized) ImageAdapter to it.

		myGallery = (Gallery) findViewById(R.id.myGallery);

		IAdapter = new ImageAdapter(this);
		myGallery.setAdapter(IAdapter);

		myGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				myFullScreen.setImageResource(IAdapter.getImageFull(position));
	
			}

			public void onNothingSelected(AdapterView<?> parent) {

			}

		});
	}// onCreate

	public class ImageAdapter extends BaseAdapter {
		/** The parent context */
		private Context myContext;
		// Put some images to project-folder: /res/drawable/
		// format: jpg, gif, png, bmp, ...

		private int[] myImageIds = {R.drawable.image1, R.drawable.image2,
				       R.drawable.image3 ,R.drawable.image3};

		/** Simple Constructor saving the 'parent' context. */
		public ImageAdapter(Context c) {
			this.myContext = c;
		}

		// inherited abstract methods - must be implemented
		// Returns count of images, and individual IDs
		public int getCount() {
			return this.myImageIds.length;
		}
		
		public int getImageFull(int position){
			return this.myImageIds[position];
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}
		// Returns a new ImageView to be displayed,
		public View getView(int position, View convertView, 
				ViewGroup parent) {

			// Get a View to display image data 					
			ImageView iv = new ImageView(this.myContext);
			iv.setImageResource(this.myImageIds[position]);

			// Image should be scaled somehow
			//iv.setScaleType(ImageView.ScaleType.CENTER);
			//iv.setScaleType(ImageView.ScaleType.CENTER_CROP);			
			//iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			//iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
			//iv.setScaleType(ImageView.ScaleType.FIT_XY);
			iv.setScaleType(ImageView.ScaleType.FIT_END);

			// Set the Width & Height of the individual images
			iv.setLayoutParams(new Gallery.LayoutParams(95, 70));

			return iv;
		}
	}// ImageAdapter
}// AndDemoUI

