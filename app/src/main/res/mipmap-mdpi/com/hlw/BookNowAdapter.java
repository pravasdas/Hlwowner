package oditek.com.hlw;

import android.content.Context;

import java.util.List;

public class BookNowAdapter extends oditek.com.hlw.RadioAdapter<oditek.com.hlw.BookNowModel> {

    public BookNowAdapter(Context context, List<oditek.com.hlw.BookNowModel> items){
        super(context, items);
    }

    @Override
    public void onBindViewHolder(oditek.com.hlw.RadioAdapter.ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        viewHolder.mText.setText(mItems.get(i).mName);
    }
}
