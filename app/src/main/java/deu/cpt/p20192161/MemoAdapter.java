/*
* 개인프로젝트 - MemoAdapter.java
* 개발자 : 컴퓨터공학과 20192161 황도균
* 20192161@office.deu.ac.kr
*/

package deu.cpt.p20192161;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import deu.cpt.p20192161.R;

public class MemoAdapter extends ArrayAdapter<memo_item> {
    private final Context context;
    private final ArrayList<memo_item> memoItems;
    // context는 어떤 활동이나 애플리케이션에서 어댑터를 사용하는지 알려주는 역할
    // memoItems는 어댑터가 표시할 데이터
    public MemoAdapter(Context context, ArrayList<memo_item> memoItems) {
        super(context, R.layout.memo_listview, memoItems); // 어댑터 초기화
        this.context = context;
        this.memoItems = memoItems;
    }

    @NonNull
    @Override // 각 항목에 대한 뷰를 생성하고 데이터를 설정하는 역할
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // LayoutInflater는 memo_listview 레이아웃 파일에서 뷰를 생성
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // rowview에서 찾아와서 각각의 textview에 대한 참조를 가져옴
        View rowView = inflater.inflate(R.layout.memo_listview, parent, false);

        TextView titleTextView = rowView.findViewById(R.id.title_lv);
        TextView dateTextView = rowView.findViewById(R.id.date_lv);
        ImageView imageView = rowView.findViewById(R.id.imageView);
        // memoItems에서 현재 위치의 memo_item 객체를 가져와서 해당 항목의 데이터로 업데이트
        memo_item memoItem = memoItems.get(position);

        titleTextView.setText(memoItem.getTitle());
        dateTextView.setText(memoItem.getWriteDate());
        // Glide를 사용하여 이미지를 로드하고 ImageView에 표시
        Glide.with(context)
                .load(memoItem.getImageUrl())  // memoItem에서 이미지 URL 가져오기
                .into(imageView);

        return rowView;
    }
}
