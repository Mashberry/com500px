package mashberry.com500px.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import mashberry.com500px.DB;
import mashberry.com500px.Main;
import mashberry.com500px.Var;

import org.json.JSONArray;
import org.json.JSONObject;

public class Api_Parser {
	public final static String TAG = "Api_Parser";
	
	/*******************************************************************************
     * 
     * 처음 사진 정보 가져오기(메인의 그리드 뷰)
     * 
     *******************************************************************************/
	public static String get_first_detail(final String url_feature,
			final int url_image_size, final String url_category, final int url_page, int image_no){
		String returnStr				= "success";
        String urlStr					= DB.Get_Photo_Url
										+ "?feature="		+ url_feature
										+ "&image_size="	+ url_image_size
										+ "&only="			+ getCategoryName(url_category) 
										+ "&page="			+ url_page
										+ "&consumer_key="	+ Var.consumer_key
										+ "&rpp="			+ image_no;
        
		try{
			URL url						= new URL(urlStr);
    		URLConnection uc			= url.openConnection();
    		HttpURLConnection httpConn	= (HttpURLConnection) uc;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setConnectTimeout(10000);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            int response				= httpConn.getResponseCode();
            Main.progressBar_process(50);
            
            if (response == HttpURLConnection.HTTP_OK) {
				InputStream in			= httpConn.getInputStream();
                String smallImageOpt	= "3";													// 비트맵으로 변환 시 옵션에 들어갈 숫자(8이면 1/8의 크기로 줄여줌)
                String largeImageOpt	= "1";
                String userPictureOpt	= "8";
				String result			= convertStreamToString(in);
				JSONObject jObject		= new JSONObject(result);
				JSONArray jsonArray		= jObject.getJSONArray("photos");
				Main.progressBar_process(75);

				if(jsonArray.length() == 0){
					returnStr				= "no results";
				}else{
					for(int i=0 ; i<jsonArray.length() ; i++){
						Var.categoryArr.add(jsonArray.getJSONObject(i).getString("category"));
						Var.idArr.add(jsonArray.getJSONObject(i).getString("id"));
						String smallImage	= jsonArray.getJSONObject(i).getString("image_url");
						String largeImage	= largeImageOpt + smallImage.substring(0, smallImage.lastIndexOf(".jpg")-1)+"4.jpg";
						Var.imageSmall_urlArr.add(smallImageOpt + smallImage);
						Var.imageLarge_urlArr.add(largeImage);
						Var.nameArr.add(jsonArray.getJSONObject(i).getString("name"));
						Var.ratingArr.add(jsonArray.getJSONObject(i).getString("rating"));
					    
					    JSONObject jsonuser	= jsonArray.getJSONObject(i).getJSONObject("user");
					    Var.user_firstnameArr.add(jsonuser.getString("firstname"));
					    Var.user_fullnameArr.add(jsonuser.getString("fullname"));
					    Var.user_lastnameArr.add(jsonuser.getString("lastname"));
					    Var.user_upgrade_statusArr.add(jsonuser.getString("upgrade_status"));
					    Var.user_usernameArr.add(jsonuser.getString("username"));
					    Var.user_userpic_urlArr.add(userPictureOpt + jsonuser.getString("userpic_url"));
					    
					    Main.progressBar_process(75+(15*i/jsonArray.length()));
					}
				}

//				Log.i("Main", "urlStr   " +urlStr);
//				Log.i("Main", "url_feature   " +url_feature);
//				Log.i("Main", "categoryArr   " +Var.categoryArr);
//				Log.i("Main", "idArr   " +Var.idArr);
//				Log.i("Main", "imageLarge_urlArr   " +Var.imageLarge_urlArr);
//				Log.i("Main", "nameArr   " +Var.nameArr);
//				Log.i("Main", "ratingArr   " +Var.ratingArr);
//				Log.i("Main", "user_firstnameArr   " +Var.user_firstnameArr);
//				Log.i("Main", "user_fullnameArr   " +Var.user_fullnameArr);
//				Log.i("Main", "user_lastnameArr   " +Var.user_lastnameArr);
//				Log.i("Main", "user_upgrade_statusArr   " +Var.user_upgrade_statusArr);
//				Log.i("Main", "user_usernameArr   " +Var.user_usernameArr);
//				Log.i("Main", "user_userpic_urlArr   " +Var.user_userpic_urlArr);
			} else {
				returnStr = "not response";
				return returnStr;
			}
		}catch(Exception e){
			e.printStackTrace();
			returnStr = "not response";
			return returnStr;
		}
		return returnStr;
	}


	/*******************************************************************************
     * 
     * 두번째 사진 정보 가져오기(클릭한 사진의 정보)
     * 
     *******************************************************************************/
	public static String get_second_detail(final int position,
			final String string, final int url_image_size, final int comments, final int comments_page){
		String returnStr				= "success";
		String urlStr					= DB.Get_Photo_Url
										+ "/"				+ string
										+ "?image_size="	+ url_image_size
										/*+ "&comments="	+ comments
										+ "&comments_page="	+ comments_page*/
										+ "&consumer_key="	+ Var.consumer_key;
		try{
			URL url						= new URL(urlStr);	
    		URLConnection uc			= url.openConnection();
    		HttpURLConnection httpConn	= (HttpURLConnection) uc;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            httpConn.setConnectTimeout(10000);
            int response				= httpConn.getResponseCode();
            
            if (response == HttpURLConnection.HTTP_OK) {
            	InputStream in			= httpConn.getInputStream();
            	String userPictureOpt	= "8";
				String result			= convertStreamToString(in);
				JSONObject jObject 		= new JSONObject(result);
				JSONObject jsonObject	= jObject.getJSONObject("photo");
				
//				Log.i(TAG, "jsonObject " + jsonObject);

				Var.detail_nameMap.put(position, jsonObject.getString("name"));
				Var.detail_locationMap.put(position, jsonObject.getString("location"));
				Var.detail_ratingMap.put(position, jsonObject.getString("rating"));
				Var.detail_times_viewedMap.put(position, jsonObject.getString("times_viewed"));
				Var.detail_votesMap.put(position, jsonObject.getString("votes_count"));
				Var.detail_favoritesMap.put(position, jsonObject.getString("favorites_count"));
				Var.detail_descriptionMap.put(position, jsonObject.getString("description"));
				Var.detail_cameraMap.put(position, jsonObject.getString("camera"));
				Var.detail_lensMap.put(position, jsonObject.getString("lens"));
				Var.detail_focal_lengthMap.put(position, jsonObject.getString("focal_length"));
				Var.detail_isoMap.put(position, jsonObject.getString("iso"));
				Var.detail_shutter_speedMap.put(position, jsonObject.getString("shutter_speed"));
				Var.detail_apertureMap.put(position, jsonObject.getString("aperture"));
				Var.detail_categoryMap.put(position, jsonObject.getString("category"));
				Var.detail_uploadedMap.put(position, jsonObject.getString("hi_res_uploaded"));
				Var.detail_takenMap.put(position, jsonObject.getString("taken_at"));
				Var.detail_licenseTypeMap.put(position, jsonObject.getString("license_type"));

			    JSONObject jsonuser = jsonObject.getJSONObject("user");
				Var.detail_user_nameMap.put(position, jsonuser.getString("fullname"));
				Var.detail_userpicMap.put(position, userPictureOpt + jsonuser.getString("userpic_url"));
				
				// 코멘트 부분 받는 부분 (사용 못함. 수정해야함.)
				/*JSONArray jsonArray = jObject.getJSONArray("comments");
				for(int i=0 ; i<jsonArray.length() ; i++){
					Var.comment_user_id.add(jsonArray.getJSONObject(i).getString("user_id"));
					Var.comment_body.add(jsonArray.getJSONObject(i).getString("body"));
					
				    JSONObject jsonuser = jsonArray.getJSONObject(i).getJSONObject("user");
				    Var.comment_fullname.add(jsonuser.getString("fullname"));
				    Var.comment_userpic_url.add(jsonuser.getString("userpic_url"));
				}*/
				
/*				Log.i("Main", "feature   " +feature);
				Log.i("Main", "filters   " +filters);
				Log.i("Main", "categoryArr   " +Var.categoryArr);
				Log.i("Main", "idArr   " +Var.idArr);
				Log.i("Main", "image_urlArr   " +Var.image_urlArr);
				Log.i("Main", "nameArr   " +Var.nameArr);
				Log.i("Main", "ratingArr   " +Var.ratingArr);
				Log.i("Main", "user_firstnameArr   " +Var.user_firstnameArr);
				Log.i("Main", "user_fullnameArr   " +Var.user_fullnameArr);
				Log.i("Main", "user_lastnameArr   " +Var.user_lastnameArr);
				Log.i("Main", "user_upgrade_statusArr   " +Var.user_upgrade_statusArr);
				Log.i("Main", "user_usernameArr   " +Var.user_usernameArr);
				Log.i("Main", "user_userpic_urlArr   " +Var.user_userpic_urlArr);*/
            } else {
				returnStr = "not response";
				return returnStr;
			}
		}catch(Exception e){
			e.printStackTrace();
			returnStr = "not response";
			return returnStr;
		}
		return returnStr;
	}
	
	/*******************************************************************************
     * 
     * 스트림을 스트링으로 변환
     * 
     *******************************************************************************/
	private static String convertStreamToString(InputStream is) {
		BufferedReader reader	= new BufferedReader(new InputStreamReader(is));
		StringBuilder sb		= new StringBuilder();
		String line				= null;
		
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/*******************************************************************************
     * 
     * 카테고리 명에 공백이 있는 부분을 url 문자로 변경
     * 
     *******************************************************************************/
	public static String getCategoryName(String str){
		String pattern	= " ";
		String rep		= "%20";
		
        if (str==null || str.equals("")) return "";

        int s				= 0;
        int e				= 0;
        StringBuffer result = new StringBuffer();

        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(rep);
            s = e + pattern.length();
        }
        
        result.append(str.substring(s));
        str	= result.toString();
        
        if(str.equals("All")){
        	str = "";
        }
        
        return str;
    }
}