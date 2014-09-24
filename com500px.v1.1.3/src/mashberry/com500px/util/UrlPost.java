package mashberry.com500px.util;

/*----------------------------------------------------------------------------------
 *
 *	Program	: Url Post
 *	Author	: Jin hyun-seok
 *	Date	: 2012-02-20
 *	Path	: /src/com.naggama/UrlPost.java
 *	Comment	: Url Post 기능
 *
 ---------------------------------------------------------------------------------*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class UrlPost {
    public static final int			HTTP_TIMEOUT = 10 * 1000;						// milliseconds
    public static DefaultHttpClient	mHttpClient;

	/*******************************************************************************
	 * 
	 *	연결시에 타임아웃 시간을 정해줌
	 *
	 ******************************************************************************/
    public static DefaultHttpClient getHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new DefaultHttpClient();
            final HttpParams params = mHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
            ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);
        }
        return mHttpClient;
    }
	
	/*******************************************************************************
	 * 
	 *	url에 포스팅할 내용을 담아 스트링으로 리턴
	 *
	 ******************************************************************************/
    public static String executeHttpPost(String url, ArrayList<NameValuePair> postParameters) throws Exception {
        BufferedReader in = null;
        try {
            UrlEncodedFormEntity	formEntity	= new UrlEncodedFormEntity(postParameters, "UTF-8");
        	DefaultHttpClient		client		= getHttpClient();
            HttpPost				request		= new HttpPost(url);
            request.setEntity(formEntity);
            HttpResponse			response	= client.execute(request);
            StringBuffer			sb			= new StringBuffer("");
            String					line		= "";
            String					NL			= System.getProperty("line.separator");
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            String result = sb.toString();
            return result;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}