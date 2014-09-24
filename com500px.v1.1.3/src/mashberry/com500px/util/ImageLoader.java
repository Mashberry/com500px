package mashberry.com500px.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

public class ImageLoader {
	MemoryCache memoryCache						= new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews	= Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;

	public ImageLoader(Context context) {
		fileCache		= new FileCache(context);
		executorService	= Executors.newFixedThreadPool(5);
	}

//	final int stub_id	= R.drawable.loading;
	final int stub_id	= android.R.color.transparent;

	public void DisplayImage(String url, ImageView imageView) {
		int imageOpt	= Integer.parseInt(url.substring(0, 1));
		url				= url.substring(1);
		imageViews.put(imageView, url);
		Bitmap bitmap	= memoryCache.get(url);
		
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else {
			queuePhoto(url, imageView, imageOpt);
			imageView.setImageResource(stub_id);
		}
	}

	private void queuePhoto(String url, ImageView imageView, int imageOpt) {
		PhotoToLoad p = new PhotoToLoad(url, imageView, imageOpt);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url, int imgOpt) {
		File 	f	= fileCache.getFile(url);

		// from SD cache
		Bitmap b	= decodeFile(f, imgOpt);
		
		if (b != null){
			return b;
		}

		// from web
		try {
			Bitmap bitmap			= null;
			URL imageUrl			= new URL(url);
			HttpURLConnection conn	= (HttpURLConnection) imageUrl.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is			= conn.getInputStream();
			OutputStream os			= new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			bitmap					= decodeFile(f, imgOpt);
			return bitmap;
		} catch (Throwable ex) {
			ex.printStackTrace();
			
			if (ex instanceof OutOfMemoryError){
				memoryCache.clear();
			}
			
			return null;
		}
	}

	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f, int imgOpt) {
		Bitmap bitmap;
		
		try {
			// decode image size
			BitmapFactory.Options o	= new BitmapFactory.Options();
			o.inJustDecodeBounds	= true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 720;
			int width_tmp			= o.outWidth, height_tmp = o.outHeight;
			int scale				= imgOpt;
			
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE){
					break;
				}
				
				width_tmp			/= 2;
				height_tmp			/= 2;
				scale				*= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2	= new BitmapFactory.Options();
			o2.inSampleSize				= scale;
			try{
				bitmap					= BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
			}catch(Exception e){
				e.printStackTrace();
				clearCache();
				bitmap					= BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
			}
			
			return bitmap;
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		public int imgOpt;

		public PhotoToLoad(String u, ImageView i, int imageOpt) {
			url			= u;
			imageView	= i;
			imgOpt		= imageOpt;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;
		Bitmap bmp;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad	= photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad)){
				return;
			}
			
			try{
				bmp			= getBitmap(photoToLoad.url, photoToLoad.imgOpt);
			}catch(Exception e){
				e.printStackTrace();
				bmp			= getBitmap(photoToLoad.url, photoToLoad.imgOpt);
			}
			
			memoryCache.put(photoToLoad.url, bmp);
			
			if (imageViewReused(photoToLoad)){
				return;
			}
			
			BitmapDisplayer bd	= new BitmapDisplayer(bmp, photoToLoad);
			Activity a			= (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		
		if (tag == null || !tag.equals(photoToLoad.url)){
			return true;
		}
		
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap		= b;
			photoToLoad	= p;
		}

		public void run() {
			if (imageViewReused(photoToLoad)){
				return;
			}
			
			if (bitmap != null){
				photoToLoad.imageView.setImageBitmap(bitmap);
			}else{
				photoToLoad.imageView.setImageResource(stub_id);
			}
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}
}