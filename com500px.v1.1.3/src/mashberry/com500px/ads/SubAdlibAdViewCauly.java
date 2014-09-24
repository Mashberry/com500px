package mashberry.com500px.ads;

import mashberry.com500px.R;
import android.content.Context;
import android.util.AttributeSet;

import com.cauly.android.ad.AdListener;
import com.mocoplex.adlib.AdlibConfig;
import com.mocoplex.adlib.SubAdlibAdViewCore;

public class SubAdlibAdViewCauly extends SubAdlibAdViewCore  {
	
	protected com.cauly.android.ad.AdView ad;
	protected boolean bGotAd = false;

	public SubAdlibAdViewCauly(Context context) {
		this(context,null);
	}	
	
	public SubAdlibAdViewCauly(Context context, AttributeSet attrs) {
		super(context, attrs);

		String caulyID = getResources().getString(R.string.caulyID);
		
		com.cauly.android.ad.AdInfo ai = new com.cauly.android.ad.AdInfo();
		ai.initData(caulyID, "cpc",
				AdlibConfig.getInstance().getCaulyGender(),
				AdlibConfig.getInstance().getCaulyAge(),
				AdlibConfig.getInstance().getCaulyGPS(),
				"default",
				"yes",30,true);

		ad = new com.cauly.android.ad.AdView(this.getContext());
		ad.setAdListener(new AdListener(){
			
			@Override
			public void onCloseScreen() {
			}

			@Override
			public void onShowScreen() {
			}

//			@Override
//			public void onCloseInterstitialAd() {
//				
//			}

			@Override
			public void onFailedToReceiveAd(boolean arg0) {

				if(!bGotAd)
					failed();
			}

			@Override
			public void onReceiveAd() {
				if(ad.isChargeableAd())
				{
					bGotAd = true;
					gotAd();					
				}
				else
				{
					if(!bGotAd)
						failed();
				}				
			}
			
		});
		
		this.addView(ad);		
	}
		
	public void query()
	{
		ad.startLoading();
		gotAd();
	}
	
	public void clearAdView()
	{
		if(ad != null)
		{
			ad.stopLoading();			
		}
		
		super.clearAdView();
	}
	public void onDestroy()
	{
		if(ad != null)
		{
			ad.stopLoading();
			ad.destroy();
			ad = null;
		}
		super.onDestroy();
	}	
	public void onResume()
	{
		if(ad != null)			
			ad.startLoading();
		
		super.onResume();
	}
	public void onPause()
	{
		if(ad != null)
			ad.stopLoading();
		
		super.onPause();
	}	
}