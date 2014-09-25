package mashberry.com500px;

import java.util.ArrayList;
import java.util.HashMap;

import mashberry.com500px.util.Api_Parser;
import mashberry.com500px.util.ImageLoader;
import mashberry.com500px.util.UrlPost;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mocoplex.adlib.AdlibActivity;
import com.mocoplex.adlib.AdlibConfig;
import com.mocoplex.adlib.AdlibManager.AdlibVersionCheckingListener;

public class Main extends AdlibActivity implements OnClickListener {
	final String TAG	= getClass().getSimpleName();
	Main mContext;
	
	ProgressDialog progressDialog;
	GridView main_gridview;
	GridView sub_gridview;
	Button photo_btn;
	Button category_btn;
	Button menu_btn;
	Button refresh_btn;
	
	String detail_result;
    String feature;
    int category			= 0;
    int featureNo			= 0;
    int categoryNo			= 0;
    int pageNo				= 0;
    int backButtonCount		= 0;
    int handlerNo			= 0;
    boolean notScroll		= false;
    
    int IMAGE_NO_FOR_PAGE	= 21;
    	
	/*******************************************************************************
     * 
     * 뷰 생성
     * 
     *******************************************************************************/
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mContext	= this;
		
		Log.i(TAG, "onCreate");
    	
		getScreenSize(getResources().getConfiguration().orientation);
		
		Var.categoryArr				= new ArrayList<String>();
		Var.idArr					= new ArrayList<String>();
		Var.imageSmall_urlArr		= new ArrayList<String>();
		Var.imageLarge_urlArr		= new ArrayList<String>();
		Var.nameArr					= new ArrayList<String>();
		Var.ratingArr				= new ArrayList<String>();
		Var.user_firstnameArr		= new ArrayList<String>();
		Var.user_fullnameArr		= new ArrayList<String>();
		Var.user_lastnameArr		= new ArrayList<String>();
		Var.user_upgrade_statusArr	= new ArrayList<String>();
		Var.user_usernameArr		= new ArrayList<String>();
		Var.user_userpic_urlArr		= new ArrayList<String>();
		
		Var.progressBar	= (ProgressBar)findViewById(R.id.progressBar);
		Var.progressBar.setMax(DB.progressMax);
		
		photo_btn		= (Button)findViewById(R.id.photo_btn);
    	category_btn	= (Button)findViewById(R.id.category_btn);
    	menu_btn		= (Button)findViewById(R.id.menu_btn);
    	refresh_btn		= (Button)findViewById(R.id.refresh_btn);
    	btn_setting(photo_btn);
    	btn_setting(category_btn);
    	btn_setting(menu_btn);
    	btn_setting(refresh_btn);
    	
		main_gridview	= (GridView) findViewById(R.id.main_grid);
		main_gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				backButtonCount = 0;
				Intent intent = new Intent(Main.this, Detail.class);
				intent.putExtra("position", position);
				startActivity(intent);
			}
		});
		main_gridview.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(sub_gridview.getVisibility() == View.VISIBLE){
					sub_gridview.setVisibility(View.GONE);
				}
				return false;
			}
		});
		main_gridview.setOnScrollListener(new OnScrollListener() {
			@Override public void onScrollStateChanged(AbsListView view, int scrollState) {}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if(totalItemCount != 0){
					if((IMAGE_NO_FOR_PAGE*pageNo) == totalItemCount){
						if (totalItemCount-(firstVisibleItem + visibleItemCount) <= IMAGE_NO_FOR_PAGE) {
							pageNo++;
							handlerNo	= 1;
							setMainInit(featureNo, categoryNo, pageNo, handlerNo);
						}
					}
				}
			}
		});
		sub_gridview 	= (GridView) findViewById(R.id.sub_grid);
		sub_gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				backButtonCount = 0;
				setVarInit();
				sub_gridview.setVisibility(View.GONE);
				progressDialog = ProgressDialog.show(Main.this, null, "Loading...", true, true);

				if(Var.Main_Btn == "photo"){
					featureNo	= position;
					pageNo		= 1;
					handlerNo	= 0;
					setMainInit(featureNo, categoryNo, pageNo, handlerNo);
				}else{
					categoryNo	= position;
					pageNo		= 1;
					handlerNo	= 0;
					setMainInit(featureNo, categoryNo, pageNo, handlerNo);
				}
			}
		});
	    
		// 광고 초기화
        initAds();
        this.setAdsContainer(R.id.ads);
        
		progressDialog				= ProgressDialog.show(Main.this, null, "Loading...", true, true);
		Var.photoFeatureArr 		= getResources().getStringArray(R.array.photo_feature);
		Var.photoFeature2Arr		= getResources().getStringArray(R.array.photo_feature_2);
		Var.photoCategoryNameArr	= getResources().getStringArray(R.array.photo_category_name);
		Var.photoCategoryNoArr		= getResources().getIntArray(R.array.photo_category_no);
		
		Var.Get_Photo_Category		= new HashMap<Integer, String>();
		
		for(int i=0 ; i<Var.photoCategoryNameArr.length ; i++){
			Var.Get_Photo_Category.put(Var.photoCategoryNoArr[i], Var.photoCategoryNameArr[i]);
		}
		
		featureNo	= 0;
		categoryNo	= 0;
		pageNo		= 1; // 1 이상의 숫자
		handlerNo	= 0;
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				if(getConsumerKey()){
					startHandler.sendEmptyMessage(0);
				}else{
					startHandler.sendEmptyMessage(1);
				}				
			}
		});
		thread.start();
	}

	/*******************************************************************************
	 * 
	 *	스크린 사이즈 가져오기
	 *
	 ******************************************************************************/
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	@SuppressWarnings({ "deprecation" })
	void getScreenSize(int cofig){
		Var.screen	= getWindowManager().getDefaultDisplay();
		Point size	= new Point();
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Var.screen.getSize(size);
			
			Var.screenWidth		= size.x;
			Var.screenHeight	= size.y;
		}else{
			Var.screenWidth		= Var.screen.getWidth();
			Var.screenHeight	= Var.screen.getHeight();
		}
		
		if(cofig == Configuration.ORIENTATION_LANDSCAPE){
//			Log.i(TAG, "Configuration.ORIENTATION_LANDSCAPE");
			if(Var.screenWidth < 1024) {
				Var.number_of_thumb_image = 3;
			}else if(Var.screenWidth <= 1920) {
				Var.number_of_thumb_image = 4;
			}else if(Var.screenWidth <= 2560) {
				Var.number_of_thumb_image = 5;
			}else if(Var.screenWidth > 2560) {
				Var.number_of_thumb_image = 6;
			}
		}else{
//			Log.i(TAG, "Configuration.ORIENTATION_PORTRAIT");
			if(Var.screenHeight < 1024) {
				Var.number_of_thumb_image = 2;
			}else if(Var.screenHeight <= 1920) {
				Var.number_of_thumb_image = 3;
			}else if(Var.screenHeight <= 2560) {
				Var.number_of_thumb_image = 4;
			}else if(Var.screenHeight > 2560) {
				Var.number_of_thumb_image = 5;
			}
		}
		
		IMAGE_NO_FOR_PAGE	= Var.number_of_thumb_image * 7;
		
//		Log.i(TAG, "Var.screenWidth " + Var.screenWidth);
//		Log.i(TAG, "Var.screenHeight " + Var.screenHeight);
//		Log.i(TAG, "Var.number_of_thumb_image " + Var.number_of_thumb_image);
	}
	
	/*******************************************************************************
	 * 
	 *	시작 핸들러
	 *
	 ******************************************************************************/
	Handler startHandler = new Handler(){
		public void handleMessage(Message msg) {
			int id = msg.what;
			
			if(id == 0){
				setMainInit(featureNo, categoryNo, pageNo, handlerNo);
			}else if(id == 1){
				Toast.makeText(mContext, "Did not bring the key. Please try again", Toast.LENGTH_LONG).show();
			}
		};
	};

	/*******************************************************************************
	 * 
	 *	로그인 동작
	 *
	 ******************************************************************************/
	boolean getConsumerKey(){
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
    	postParameters.add(new BasicNameValuePair("note_id",	"149798815082562"));
    	
    	try {
    	    String response		= UrlPost.executeHttpPost(DB.Consumer_Key_Url, postParameters);
    	    String res			= response.toString();
    	    String resultStart	= "#500px#";
    	    String resultEnd	= "#/500px#";
    	    String result		= null;
    	    
    	    res = res.replaceAll("\\s+", "");

    	    try{
    	    	result = res.substring(res.indexOf(resultStart)+resultStart.length(), res.indexOf(resultEnd));
    	    }catch(Exception e){
    	    	e.printStackTrace();
    	    }
    	    
    	    Var.consumer_key = result;
    	    
    	    if(result.equals("") || result == null){								// 성공 메시지일 경우
    	    	return false;
    	    }else{																	// 성공이 아닐 경우
    	    	return true;
    	    }
    	} catch (Exception e) {
    		e.printStackTrace();
    		return false;
    	}
	}
	
	/*******************************************************************************
     * 
     * 변수 및 뷰에 값 얻기
     * 
     *******************************************************************************/
	void setMainInit(int fNo, int cNo, final int pNo, final int handlerNo){
		feature		= Var.photoFeatureArr[fNo];
		category	= Var.photoCategoryNoArr[cNo];

		Var.progressBar.setVisibility(View.VISIBLE);
		progressBar_process(10);
		
		Thread t	= new Thread(new Runnable() {
			@Override
			public void run() {
				detail_result = Api_Parser.get_first_detail(feature, Var.number_of_thumb_image,Var.Get_Photo_Category.get(category), pNo, IMAGE_NO_FOR_PAGE);
				handler.sendEmptyMessage(handlerNo);
			}
		});
		t.start();
		t = null;
	}
	
	/*******************************************************************************
     * 
     * 핸들러 정의
     * 
     *******************************************************************************/
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(detail_result == "not response"){
				Toast.makeText(mContext, "Server is not response :(\nTry to Refresh", Toast.LENGTH_LONG).show();
				notScroll = true;
			}else if(detail_result == "no results" && Var.imageSmall_urlArr.size()==0){
				Toast.makeText(mContext, "This category has no results", Toast.LENGTH_LONG).show();
			}
			
			if(msg.what == 0){
				main_gridview.setNumColumns(Var.number_of_thumb_image);
				main_gridview.setAdapter(new Main_ImageAdapter(mContext, main_gridview.getWidth()));
			}else if(msg.what == 1){
				new Main_ImageAdapter(mContext, main_gridview.getWidth());
			}
			
			progressBar_process(100);
			photo_btn.setText(Var.photoFeature2Arr[featureNo]);
			category_btn.setText(Var.Get_Photo_Category.get(category));

			if((progressDialog != null) && progressDialog.isShowing()){
				progressDialog.dismiss();
			}

			progressBar_process(0);
			Var.progressBar.setVisibility(View.GONE);
		}
	};

	/*******************************************************************************
     * 
     * 프로그레스 바 진행(i는 100까지)
     * 
     *******************************************************************************/
	public static void progressBar_process(int i){
		try{
			Var.progressBar.setProgress(DB.progressMax/100*i);
		}catch(Exception e){
			Var.progressBar.setProgress(0);
			Var.progressBar.setVisibility(View.GONE);
//			Log.e("Main", "progressBar_process e " + e);
		}
	}
	
	/*******************************************************************************
     * 
     * 버튼 세팅
     * 
     *******************************************************************************/
	private void btn_setting(final Button btn){
		btn.setOnClickListener(mContext);
		btn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN){
					btn.setBackgroundColor(Color.BLACK);
				}else{
					btn.setBackgroundColor(Color.TRANSPARENT);
				}
				return false;
			}
		});
	}
	
	/*******************************************************************************
     * 
     * 버튼 클릭 시
     * 
     *******************************************************************************/
	private void setVarInit(){
		Var.categoryArr.clear();
		Var.idArr.clear();
		Var.imageSmall_urlArr.clear();
		Var.imageLarge_urlArr.clear();
		Var.nameArr.clear();
		Var.ratingArr.clear();
	    Var.user_firstnameArr.clear();
	    Var.user_fullnameArr.clear();
	    Var.user_lastnameArr.clear();
	    Var.user_upgrade_statusArr.clear();
	    Var.user_usernameArr.clear();
	    Var.user_userpic_urlArr.clear();
	}
	
	/*******************************************************************************
     * 
     * 메뉴 버튼 클릭 시
     * 
     *******************************************************************************/
	private void pop_select(String str){
		if(Var.Main_Btn!=str || sub_gridview.getVisibility()==View.GONE){
			Var.Main_Btn				= str;
			sub_gridview.setVisibility(View.VISIBLE);
			Var.category_text_padding	= (int) getResources().getDimension(R.dimen.category_text_padding);
			sub_gridview.setAdapter(new Main_CategoryAdapter());
		}else{
			sub_gridview.setVisibility(View.GONE);
		}
	}

	/*******************************************************************************
     * 
     * 버튼 클릭 시
     * 
     *******************************************************************************/
	public void onClick(View v){
		backButtonCount = 0;
		
		switch(v.getId()){
		case R.id.photo_btn:
			pop_select("photo");
			break;
			
		case R.id.category_btn:
			pop_select("category");
			break;
			
		case R.id.menu_btn:
			if(sub_gridview.getVisibility() == View.VISIBLE){
				sub_gridview.setVisibility(View.GONE);
			}
			
			Toast.makeText(mContext, "Not Yet :(\nComing Soon :)", Toast.LENGTH_LONG).show();
			break;

		case R.id.refresh_btn:
			if(sub_gridview.getVisibility() == View.VISIBLE){
				sub_gridview.setVisibility(View.GONE);
			}
			
			if(Var.imageSmall_urlArr.size() > 0){
				if(notScroll){
					notScroll		= false;
					handlerNo		= 1;
					setMainInit(featureNo, categoryNo, pageNo, handlerNo);
				}else{
					int position	= main_gridview.getFirstVisiblePosition();
					main_gridview.setAdapter(new Main_ImageAdapter(mContext, main_gridview.getWidth()));
					main_gridview.setSelection(position);
				}
			}else if(Var.imageSmall_urlArr.size() == 0){
				progressDialog	= ProgressDialog.show(mContext, null, "Loading...", true, true);
				Var.progressBar.setVisibility(View.VISIBLE);
				progressBar_process(10);
				pageNo			= 1;
				handlerNo		= 0;
				setMainInit(featureNo, categoryNo, pageNo, handlerNo);
				break;
			}
		}
	}
	
	/*******************************************************************************
     * 
     * 뒤로가기
     * 
     *******************************************************************************/
	@Override
	public void onBackPressed() {
		if(sub_gridview.getVisibility() == View.VISIBLE){
			sub_gridview.setVisibility(View.GONE);
		}else{
			backButtonCount++;															// 뒤로가기 버튼에 1을 더해 카운트를 늘린다.
	
			if(backButtonCount == 1){													// 카운트가 1일 때
				Toast.makeText(mContext, "Press once more to quit", Toast.LENGTH_LONG).show();
			}else if(backButtonCount == 2){												// 카운트가 2일 때
				synchronized(this){
					ImageLoader imageLoader = new ImageLoader(mContext);
					imageLoader.clearCache();
					finish();
					Process.killProcess(Process.myPid());
				}
			}
		}
	}

	/*******************************************************************************
     * 
     * 상태 변환할 때
     * 
     *******************************************************************************/
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    
	    // Checks the orientation of the screen
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
		    getScreenSize(newConfig.orientation);
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
		    getScreenSize(newConfig.orientation);
	    }
	}
	
	/*******************************************************************************
     * 
     * 광고 초기화
     * 
     *******************************************************************************/
    protected void initAds() {
    	// 광고 스케줄링 설정을 위해 아래 내용을 프로그램 실행시 한번만 실행합니다. (처음 실행되는 activity에서 한번만 호출해주세요.)
    	
        // 광고 subview 의 패키지 경로를 설정합니다. (실제로 작성된 패키지 경로로 수정해주세요.)
    	// 쓰지 않을 광고플랫폼은 삭제해주세요.
        AdlibConfig.getInstance().bindPlatform("ADMOB",	"mashberry.com500px.ads.SubAdlibAdViewAdmob");
        AdlibConfig.getInstance().bindPlatform("CAULY",	"mashberry.com500px.ads.SubAdlibAdViewCauly");
        // 쓰지 않을 플랫폼은 JAR 파일 및 test.adlib.project.ads 경로에서 삭제하면 최종 바이너리 크기를 줄일 수 있습니다.
        
        // adlib 에서 발급받은 api 키를 입력합니다.
        // http://adlib.mocoplex.com/m/media.aspx

        AdlibConfig.getInstance().setAdlibKey(getResources().getString(R.string.adlibID));
                
        // 광고 타겟팅을 위한 사용자 정보를 입력합니다. (옵션)
        // gender(M/F/0), age(10/20/30/40/0), lat(위도), lon(경도)
        AdlibConfig.getInstance().setAdInfo("0", "0", "31.111", "127.111");
                
        // 클라이언트 버전관리를 위한 리스너 추가 (adlib - 1.1)
        // 서버에서 클라이언트 버전을 관리하여 사용자에게 업데이트를 안내할 수 있습니다. (옵션)
        this.setVersionCheckingListner(new AdlibVersionCheckingListener(){
			@Override
			public void gotCurrentVersion(String ver) {
				// 서버에서 설정한 버전정보를 수신했습니다.
				// 기존 클라이언트 버전을 확인하여 적절한 작업을 수행하세요.
				PackageInfo i;
				double current;
				try {
					i		= mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
					current	= Double.parseDouble(i.versionName);
					
					double newVersion = Double.parseDouble(ver);				
					if(current >= newVersion){
						return;
					}else{
						new AlertDialog.Builder(mContext)
					    .setTitle("Updated versions")
					    .setMessage("Would you like to update the program?")
							    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
							      public void onClick(DialogInterface dialog, int whichButton) {
							    	  Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
							    	  marketLaunch.setData(Uri.parse("market://details?id=mashberry.com500px"));
							    	  startActivity(marketLaunch);
							    	  dialog.dismiss();
							      }
							    })	    
							    .setNegativeButton("no", new DialogInterface.OnClickListener() {
							      public void onClick(DialogInterface dialog, int whichButton) {
							    	  dialog.dismiss();
							      }
							    })	    
					    .show();
					}
				} catch (NameNotFoundException e) {
//					Log.e("Main", "gotCurrentVersion e " + e);
				}
			}
        });
    }
    
	/*******************************************************************************
     * 
     * Json을 이용한 Post로 값 가져오기
     * 
     *******************************************************************************/
/*	protected void accessJson() {
        Thread t = new Thread(){
        public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();
                String url = DB.Get_Photo_Url;//+Access_Token_Url+Authorize_Url;
                
                try{
                    HttpPost post = new HttpPost(url);
                    json.put("x_auth_mode", "client_auth");
                    json.put("x_auth_username", id);
                    json.put("x_auth_password", pwd);
                    StringEntity se = new StringEntity( "JSON: " + json.toString());  
                    se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);
                    Checking response;
                    if(response!=null){
                        InputStream in = response.getEntity().getContent(); //Get the data in the entity
                        result = convertStreamToString(in);
                        Log.i("Main","accessJson===================================================");
                        Log.i("Main","accessJson   " +result);
                    }
                }
                catch(Exception e){
                	Log.i("Main","Exception   " + e);
                }
                Looper.loop(); //Loop in the message queue
            }
        };
        t.start();      
    }*/
}