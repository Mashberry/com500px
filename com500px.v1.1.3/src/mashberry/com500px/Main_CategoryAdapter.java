package mashberry.com500px;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class Main_CategoryAdapter extends BaseAdapter {
	TextView gridTextView;

    public int getCount() {
		if(Var.Main_Btn == "photo"){
			return Var.photoFeature2Arr.length;
		}else {
			return Var.photoCategoryNameArr.length;
		}
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
        	gridTextView = new TextView(parent.getContext());						// context 값을 뷰로 설정
        	gridTextView.setTextColor(0xffffffff);
        	gridTextView.setTextSize(20);
        	gridTextView.setGravity(Gravity.CENTER_VERTICAL);
            gridTextView.setLayoutParams(new GridView.LayoutParams(
            				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));	// 이미지 크기 설정  (x, y)
            gridTextView.setPadding(Var.category_text_padding, Var.category_text_padding, Var.category_text_padding, Var.category_text_padding);
        } else {
            gridTextView = (TextView)convertView;
        }

    	if(Var.Main_Btn == "photo"){
    		gridTextView.setText(Var.photoFeature2Arr[position]);
		}else {
			gridTextView.setText(Var.photoCategoryNameArr[position]);
		}
    	
        return gridTextView;
    }

    @Override
	public Object getItem(int position) {
		return position;
	}
    public long getItemId(int position) {
        return position;
    }
}