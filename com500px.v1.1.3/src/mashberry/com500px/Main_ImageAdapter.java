package mashberry.com500px;

import mashberry.com500px.util.ImageLoader;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/*******************************************************************************
 * 
 * 그리드 뷰의 아답터
 * 
 *******************************************************************************/
public class Main_ImageAdapter extends BaseAdapter {
	GridView_FrameLayout gridImageView;
	private ImageLoader imgLoader;
	
	int space;
	int width;
   
	Main_ImageAdapter(Context context, int gridWidth){
		imgLoader = new ImageLoader(context);
		space = (int) context.getResources().getDimension(R.dimen.main_grid_space);
//		width = (Var.screenWidth - (space*Var.number_of_thumb_image)) / Var.number_of_thumb_image;
		width = (gridWidth - (space*Var.number_of_thumb_image)) / Var.number_of_thumb_image;
	}
	
    public int getCount() {
        return Var.imageSmall_urlArr.size();
    }

    public String getItem(int position) {
        return Var.imageSmall_urlArr.get(position);
    }

    public long getItemId(int position) {
        return Var.imageSmall_urlArr.get(position).hashCode();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
        	gridImageView = new GridView_FrameLayout(parent.getContext(), position);
            gridImageView.setLayoutParams(
            		new GridView.LayoutParams(getViewWidth(), getViewHeight()));	// x, y
        } else {
            gridImageView = (GridView_FrameLayout)convertView;
        }
        
        gridImageView.setImage(position);
        gridImageView.setIcon(position);
        gridImageView.setText(position);
        
        return gridImageView;
    }

	int getViewHeight(){
		return width;
	}
	int getViewWidth(){
		return width;
	}

	/*******************************************************************************
     * 
     * 그리드 뷰에서의 레이아웃 정의
     * 
     *******************************************************************************/
    public class GridView_FrameLayout extends FrameLayout {
    	private ImageView mImage;
    	private ImageView mIcon;
    	private TextView mTitle;
    	private TextView mUserName;
    	private TextView mPhotoRating;

    	public GridView_FrameLayout(Context context, int position){
    		super(context);
    		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
    		inflater.inflate(R.layout.main_gridview, this, true);

    		mImage			= (ImageView) findViewById(R.id.image_view);
    		mImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
    		imgLoader.DisplayImage(Var.imageSmall_urlArr.get(position), mImage);
    		mIcon			= (ImageView) findViewById(R.id.image_pimg);
    		mIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
    		imgLoader.DisplayImage(Var.user_userpic_urlArr.get(position), mIcon);
    		
    		mTitle			= (TextView) findViewById(R.id.image_title);
    		mUserName		= (TextView) findViewById(R.id.image_pid);
    		mPhotoRating	= (TextView) findViewById(R.id.image_rating);
    	}
    	public void setText(int position){
    		mTitle.setText(Var.nameArr.get(position));
    		mUserName.setText(Var.user_fullnameArr.get(position));
    		mPhotoRating.setText(Var.ratingArr.get(position));
    	}
    	public void setImage(int position){
    		imgLoader.DisplayImage(Var.imageSmall_urlArr.get(position), mImage);
    	}
    	public void setIcon(int position){
    		imgLoader.DisplayImage(Var.user_userpic_urlArr.get(position), mIcon);
    	}
    }
}