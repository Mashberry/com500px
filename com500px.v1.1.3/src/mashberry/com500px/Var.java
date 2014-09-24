package mashberry.com500px;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.Display;
import android.widget.ProgressBar;

public class Var {
	public static ArrayList<String> categoryArr				= new ArrayList<String>();
	public static ArrayList<String> idArr					= new ArrayList<String>();
	public static ArrayList<String> imageSmall_urlArr		= new ArrayList<String>();
	public static ArrayList<String> imageLarge_urlArr		= new ArrayList<String>();
	public static ArrayList<String> nameArr					= new ArrayList<String>();
	public static ArrayList<String> ratingArr				= new ArrayList<String>();
	public static ArrayList<String> user_firstnameArr		= new ArrayList<String>();
	public static ArrayList<String> user_fullnameArr		= new ArrayList<String>();
	public static ArrayList<String> user_lastnameArr		= new ArrayList<String>();
	public static ArrayList<String> user_upgrade_statusArr	= new ArrayList<String>();
	public static ArrayList<String> user_usernameArr		= new ArrayList<String>();
	public static ArrayList<String> user_userpic_urlArr		= new ArrayList<String>();
	
	public static HashMap<Integer, String> detail_nameMap			= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_userpicMap		= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_user_nameMap		= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_locationMap		= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_ratingMap			= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_times_viewedMap	= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_votesMap			= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_favoritesMap		= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_descriptionMap	= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_cameraMap			= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_lensMap			= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_focal_lengthMap	= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_isoMap			= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_shutter_speedMap	= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_apertureMap		= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_categoryMap		= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_uploadedMap		= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_takenMap			= new HashMap<Integer, String>();
	public static HashMap<Integer, String> detail_licenseTypeMap	= new HashMap<Integer, String>();
	
    public static String[] photoFeatureArr;
    public static String[] photoFeature2Arr;
    public static String[] photoCategoryNameArr;
    public static int[] photoCategoryNoArr;
    
    
	/*public static ArrayList<String> comment_user_id		= new ArrayList<String>();
	public static ArrayList<String> comment_body			= new ArrayList<String>();
	public static ArrayList<String> comment_fullname		= new ArrayList<String>();
	public static ArrayList<String> comment_userpic_url		= new ArrayList<String>();	*/
    
	public static HashMap<Integer, String> Get_Photo_Category = new HashMap<Integer, String>();
	
	public static String Main_Btn;
		
	public static ProgressBar pb;
	public static ProgressBar progressBar;
	
    public static Display screen;

    public static int category_text_padding;
	public static int addImageApadterCount;
	public static int number_of_thumb_image	= 2;
	public static int screenWidth			= 0;
	public static int screenHeight			= 0;
	
	public static String consumer_key;
}