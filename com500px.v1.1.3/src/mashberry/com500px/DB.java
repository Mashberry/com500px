package mashberry.com500px;


/*
 *  수정내용
 *  
 *  날짜		- 내용
 *  131217	- 카테고리 추가
 *  		- 배열 string.xml 로 이동
 *  
 */

public class DB {
	public static final int progressMax				= 1000;
	
	public static final String Consumer_Sercet		= "2zuwVGSAoFbletAxF886OpJUmtGznY58RQ5x28Bt";
	public static final String Consumer_Key_Url		= "https://www.facebook.com/note.php";
	
	 /* 인증을 위한 주소 */
	public static final String Site_Url				= "https://api.500px.com";
	public static final String Request_Token_Url	= "/v1/oauth/request_token";
	public static final String Access_Token_Url		= "/v1/oauth/access_token";
	public static final String Authorize_Url		= "/v1/oauth/authorize";
	
	/* 사진 가져오기 */
	public static final String Get_Photo_Url		= "https://api.500px.com/v1/photos";
}