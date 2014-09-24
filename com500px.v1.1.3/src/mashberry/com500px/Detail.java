package mashberry.com500px;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import mashberry.com500px.util.Api_Parser;
import mashberry.com500px.util.ImageLoader;
import mashberry.com500px.util.RecycleUtils;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Detail extends Activity {
	Detail mContext;
	private Detail_Gallery mGallery;
	private ImageLoader imgLoader;
	
	ScrollView info_scroll_layout;
	TextView detail_name;
	ImageView detail_userpic;
	TextView detail_user_name;
	TextView detail_location;
	TextView detail_rating;
	TextView detail_times_viewed;
	TextView detail_votes;
	TextView detail_favorites;
	TextView detail_description;
	TextView detail_camera;
	TextView detail_lens;
	TextView detail_focal_length;
	TextView detail_iso;
	TextView detail_shutter_speed;
	TextView detail_aperture;
	TextView detail_category;
	TextView detail_uploaded;
	TextView detail_taken;
	TextView detail_copyright;
	TextView detail_licenseType;
	Button detail_info_save_btn;
	Button detail_info_share_btn;
	Button detail_info_close_btn;
	ProgressBar save_progressbar;
	ProgressBar share_progressbar;
	ProgressBar info_progressbar;
	Dialog infoDialog;

	String saveFileName;
	String preFileName;
	boolean saveSameFile	= false;
	boolean shareMode		= false;
	
	String detail_result;
	int comments			= 1;
	int comments_page		= 1;
	
	static int GETINFO		= 0;
		
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGallery.onGalleryTouchEvent(event);
		
		if(GETINFO == 1){
			getInfo();
			GETINFO = 0;
		}
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		mContext		= this;
		Intent intent	= getIntent();
		int position	= intent.getIntExtra("position", 0);

		imgLoader		= new ImageLoader(this);
		mGallery		= new Detail_Gallery(this, R.id.iv);
		mGallery.setPaddingWidth(5);
		mGallery.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, Var.imageLarge_urlArr) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent){
				if (convertView == null) {
					LayoutInflater inflater	= getLayoutInflater();
					convertView				= inflater.inflate(R.layout.detail_image, parent, false);
				}
				
				Var.pb	= (ProgressBar) convertView.findViewById(R.id.progressbar);
				
				if(Var.pb != null){
					Var.pb.setVisibility(View.VISIBLE);
				}
				
				ImageView iv	= (ImageView) convertView.findViewById(R.id.iv);
				
				try{
					imgLoader.DisplayImage(Var.imageLarge_urlArr.get(position), iv);
				}catch(Exception e){
//					Log.e("Detail", "getView e " + e);
					onBackPressed();
				}
				
				return convertView;
			}
		}, position);

		final LinearLayout layout				= new LinearLayout(getApplicationContext());
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setBackgroundColor(Color.BLACK);
		LinearLayout.LayoutParams layoutParams	= new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);

		layoutParams.weight						= 1.0f;
		layout.addView(mGallery, layoutParams);
		setContentView(layout);
	}
	
	public void infoDialog(){
	    infoDialog				= new Dialog(mContext, R.style.FullScreen);
	    infoDialog.setContentView(R.layout.detail_image_info);
	    infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
	    infoDialog.show();
	    
	    info_scroll_layout		= (ScrollView)infoDialog.findViewById(R.id.info_scroll_layout);
	    detail_name				= (TextView)infoDialog.findViewById(R.id.detail_name);
		detail_userpic			= (ImageView)infoDialog.findViewById(R.id.detail_userpic);
		detail_user_name		= (TextView)infoDialog.findViewById(R.id.detail_user_name);
		detail_location			= (TextView)infoDialog.findViewById(R.id.detail_location);
		detail_rating			= (TextView)infoDialog.findViewById(R.id.detail_rating);
		detail_times_viewed		= (TextView)infoDialog.findViewById(R.id.detail_times_viewed);
		detail_votes			= (TextView)infoDialog.findViewById(R.id.detail_votes);
		detail_favorites		= (TextView)infoDialog.findViewById(R.id.detail_favorites);
		detail_description		= (TextView)infoDialog.findViewById(R.id.detail_description);
		detail_camera			= (TextView)infoDialog.findViewById(R.id.detail_camera);
		detail_lens				= (TextView)infoDialog.findViewById(R.id.detail_lens);
		detail_focal_length		= (TextView)infoDialog.findViewById(R.id.detail_focal_length);
		detail_iso				= (TextView)infoDialog.findViewById(R.id.detail_iso);
		detail_shutter_speed	= (TextView)infoDialog.findViewById(R.id.detail_shutter_speed);
		detail_aperture			= (TextView)infoDialog.findViewById(R.id.detail_aperture);
		detail_category			= (TextView)infoDialog.findViewById(R.id.detail_category);
		detail_uploaded			= (TextView)infoDialog.findViewById(R.id.detail_uploaded);
		detail_taken			= (TextView)infoDialog.findViewById(R.id.detail_taken);
		detail_copyright		= (TextView)infoDialog.findViewById(R.id.detail_copyright);
		detail_licenseType		= (TextView)infoDialog.findViewById(R.id.detail_licenseType);
		detail_info_save_btn	= (Button) infoDialog.findViewById(R.id.detail_info_save_btn);
		save_progressbar 		= (ProgressBar) infoDialog.findViewById(R.id.save_progressbar);
		detail_info_share_btn	= (Button) infoDialog.findViewById(R.id.detail_info_share_btn);
		share_progressbar 		= (ProgressBar) infoDialog.findViewById(R.id.share_progressbar);
		detail_info_close_btn	= (Button) infoDialog.findViewById(R.id.detail_info_close_btn);
	    info_progressbar		= (ProgressBar)infoDialog.findViewById(R.id.info_progressbar);

		btn_setting(detail_info_save_btn);
		detail_info_save_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v.getId() == R.id.detail_info_save_btn){
					shareMode = false;
					save_progressbar.setVisibility(View.VISIBLE);
					detail_info_save_btn.setVisibility(View.GONE);
					share_progressbar.setVisibility(View.VISIBLE);
					detail_info_share_btn.setVisibility(View.GONE);
					externalImageSave();
				}
			}
		});

		btn_setting(detail_info_share_btn);
		detail_info_share_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v.getId() == R.id.detail_info_share_btn){
					shareMode = true;
					save_progressbar.setVisibility(View.VISIBLE);
					detail_info_save_btn.setVisibility(View.GONE);
					share_progressbar.setVisibility(View.VISIBLE);
					detail_info_share_btn.setVisibility(View.GONE);
					externalImageSave();
				}
			}
		});
		
		btn_setting(detail_info_close_btn);
		detail_info_close_btn.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		infoDialog.dismiss();
				shareMode		= false;
        		saveSameFile	= false;
        	}
        });
	}

	public void externalImageSave(){
		final String dir		= Environment.getExternalStorageDirectory().toString()
								+ "/data/com500px/";
		long now				= System.currentTimeMillis();
        Date date				= new Date(now);
        SimpleDateFormat sdfNow	= new SimpleDateFormat("yyyyMMddHHmmss");
        String strNow			= sdfNow.format(date);
        final String fileName	= strNow + ".png";
        
		try{
        	File directory		= new File(dir);
        	
        	if(!directory.isDirectory()){
        		directory.mkdir();
        	}

        	if(!directory.isDirectory()){
        		directory.mkdirs();
        	}
    	} catch (Exception e) {
//			Log.e("Detail", "externalImageSave e " + e);
        	return;
    	}
    	
    	new Thread(new Runnable() {
			@Override
			public void run() {
				saveFileName	= dir + fileName;
				
				if(!saveSameFile){
			    	boolean b	= DownloadImage(Var.imageLarge_urlArr.get(Detail_Gallery.mCurrentPosition).substring(1), saveFileName);
			    	
			    	if(b){
		        		saveSameFile	= true;
		        		preFileName		= saveFileName;
			    		handler1.sendEmptyMessage(1);
			    	}else{
			    		handler1.sendEmptyMessage(0);
			    	}
				}else{
		    		if(shareMode){
						shareImage();
					}
					handler1.sendEmptyMessage(2);
				}
			}
		}).start();
	}
	Handler handler1 = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int i = msg.what;
			
			if(i == 0){
	        	Toast.makeText(mContext, "Failed to save.", Toast.LENGTH_SHORT).show();
			}else if(i == 1){
		    	Toast.makeText(mContext, "Has been saved! Look at the gallery.", Toast.LENGTH_SHORT).show();

		    	MyMediaConnectorClient client	= new MyMediaConnectorClient(saveFileName);
		    	MediaScannerConnection scanner	= new MediaScannerConnection(mContext, client);
		    	client.setScanner(scanner);
		    	scanner.connect();
		    	
		    	if(shareMode){
			    	shareImage();
		    	}
			}else if(i == 2){
		    	Toast.makeText(mContext, "Previously saved picture.", Toast.LENGTH_SHORT).show();
			}

			save_progressbar.setVisibility(View.GONE);
			detail_info_save_btn.setVisibility(View.VISIBLE);
			share_progressbar.setVisibility(View.GONE);
			detail_info_share_btn.setVisibility(View.VISIBLE);
		};
	};
	
	private class MyMediaConnectorClient implements MediaScannerConnectionClient {
		String _fisier;
		MediaScannerConnection MEDIA_SCANNER_CONNECTION;

		public MyMediaConnectorClient(String nume) {
		    _fisier = nume;
		}
	
		public void setScanner(MediaScannerConnection msc){
		    MEDIA_SCANNER_CONNECTION = msc;
		}
	
		@Override
		public void onMediaScannerConnected() {
		    MEDIA_SCANNER_CONNECTION.scanFile(_fisier, null);
		}
	
		@Override
		public void onScanCompleted(String path, Uri uri) {
		    if(path.equals(_fisier))
		        MEDIA_SCANNER_CONNECTION.disconnect();
		}
	}
	
	void shareImage(){
		String appName		= getString(R.string.appName);
		String titleText	= appName + "_" + Var.nameArr.get(Detail_Gallery.mCurrentPosition).substring(1);
		Uri screenshotUri	= Uri.parse("file://"+preFileName);
		Intent shareIntent	= new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
        shareIntent.putExtra(Intent.EXTRA_TEXT, titleText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, titleText);
        startActivity(Intent.createChooser(shareIntent, "Share"));
	}
	
	boolean DownloadImage(String Url, String FileName) {
		URL imageurl;
		int Read;
		try {
			imageurl				= new URL(Url);
	    	URLConnection conn		= imageurl.openConnection();
			int len					= conn.getContentLength();
			byte[] raster			= new byte[len];
			InputStream is			= conn.getInputStream();
			FileOutputStream fos	= new FileOutputStream(new File(FileName));
			
			for (;;) {
				Read = is.read(raster);
				
				if (Read <= 0) {
					break;
				}
				
				fos.write(raster,0, Read);
			}
			
			is.close();
			fos.flush();
			fos.close();
		} catch (Exception e) {
//			Log.e("Detail", "DownloadImage e " + e);
			return false;
		}
		return true;
	}
	
	public static void progressBarGone(){
		if(Var.pb != null){
			Var.pb.setVisibility(View.GONE);
			Var.pb = null;
		}
	}
	
	private void btn_setting(final Button btn){
		btn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN){
					btn.setBackgroundColor(Color.DKGRAY);
				}else{
					btn.setBackgroundColor(Color.TRANSPARENT);
				}
				return false;
			}
		});
	}
	
	public void getInfo(){
		infoDialog();
		info_scroll_layout.setVisibility(View.GONE);
		info_progressbar.setVisibility(View.VISIBLE);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				detail_result = Api_Parser.get_second_detail(Detail_Gallery.mCurrentPosition,
						Var.idArr.get(Detail_Gallery.mCurrentPosition),
						Var.number_of_thumb_image, comments, comments_page);
				handler.sendEmptyMessage(0);
			}
		}).start();
	}
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(detail_result == "not response"){
				infoDialog.dismiss();
				Toast.makeText(Detail.this, "Server is not response :(", Toast.LENGTH_LONG).show();
			}else{
				info_scroll_layout.setVisibility(View.VISIBLE);
				info_progressbar.setVisibility(View.GONE);
				int position = Detail_Gallery.mCurrentPosition;
				detail_name.setText(null2space(Var.detail_nameMap.get(position)));

				try{
//					Image_Downloader.download(Var.detail_userpicMap.get(position), detail_userpic);
					imgLoader.DisplayImage(Var.detail_userpicMap.get(position), detail_userpic);
				}catch(Exception e){
//					Log.e("Detail", "handleMessage e " + e);
					onBackPressed();
				}
				
				detail_user_name.setText(null2space(Var.detail_user_nameMap.get(position)));
				detail_location.setText(null2space(Var.detail_locationMap.get(position)));
				detail_rating.setText(null2space(Var.detail_ratingMap.get(position)));
				detail_times_viewed.setText(null2space(Var.detail_times_viewedMap.get(position)));
				detail_votes.setText(null2space(Var.detail_votesMap.get(position)));
				detail_favorites.setText(null2space(Var.detail_favoritesMap.get(position)));
				detail_description.setText(null2space(Var.detail_descriptionMap.get(position)));
				detail_camera.setText(null2space(Var.detail_cameraMap.get(position)));
				detail_lens.setText(null2space(Var.detail_lensMap.get(position)));
				String focal_length		= null2space(Var.detail_focal_lengthMap.get(position));
				focal_length			= focal_length.equals("") ? focal_length : (focal_length+"mm");
				detail_focal_length.setText(focal_length);
				detail_iso.setText(null2space(Var.detail_isoMap.get(position)));
				String shutter_speed	= null2space(Var.detail_shutter_speedMap.get(position));
				shutter_speed			= shutter_speed.equals("") ? shutter_speed : (shutter_speed+"s");
				detail_shutter_speed.setText(shutter_speed);
				String aperture			= null2space(Var.detail_apertureMap.get(position));
				aperture				= aperture.equals("") ? aperture : "f/"+aperture;
				detail_aperture.setText(aperture);
				detail_category.setText(getCategoryName(null2space(Var.detail_categoryMap.get(position))));
				detail_uploaded.setText(null2space(Var.detail_uploadedMap.get(position)));
				detail_taken.setText(null2space(Var.detail_takenMap.get(position)));
				detail_copyright.setText(null2space(Var.detail_user_nameMap.get(position)));
				detail_licenseType.setText(getLicenseType(null2space(Var.detail_licenseTypeMap.get(position))));
				
			}
		};
	};

	private String getCategoryName(String str){
		String name						= "";
		int index						= Integer.parseInt(str);
		ArrayList<String> tempNameArr	= new ArrayList<String>(Arrays.asList(Var.photoCategoryNameArr));
		ArrayList<Integer> tempNoArr	= new ArrayList<Integer>();
		
		for(int i=0 ; i<Var.photoCategoryNoArr.length ; i++){
			tempNoArr.add(Var.photoCategoryNoArr[i]);
		}
		
		try{
			for(int i=0 ; i<tempNoArr.size()-1 ; i++){
				if(tempNoArr.get(i) == index){
					name = tempNameArr.get(i);
					break;
				}
			}
		}catch(Exception e){
		}
		
//		Log.i("Detail", "index " + index);
//		Log.i("Detail", "tempArr " + tempNameArr);
//		Log.i("Detail", "temp2Arr " + tempNoArr);
		
		tempNameArr.clear();
		tempNoArr.clear();
		tempNameArr	= null;
		tempNoArr	= null;
		
		return name;
	}

	private String getLicenseType(String i){
		String name					= "";
		String[] temp				= getResources().getStringArray(R.array.license_type);
		ArrayList<String> tempArr	= new ArrayList<String>(Arrays.asList(temp));
		
		try{
			name					= tempArr.get(Integer.parseInt(i));
		}catch(Exception e){
		}
		
		temp	= null;
		tempArr.clear();
		tempArr	= null;
		
		return name;
	}
		
	private String null2space(String s){
		String str;
		
		if(null == s){
			str = "";
		}else if(s.equals("null")){
			str = "";
		}else{
			str = s;
		}
		return str;
	}
	
	@Override
	public void onBackPressed() {
		finish();
		RecycleUtils.recursiveRecycle(getWindow().getDecorView());
		super.onBackPressed();
	}
}