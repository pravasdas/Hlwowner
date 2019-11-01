package oditek.com.hlw;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.List;

public class RateUsAdapter extends RecyclerView.Adapter<oditek.com.hlw.RateUsAdapter.ViewHolder> {
    private Context mContext;
    List<RateUsModel> list;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rate_us_rowlayout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public RateUsAdapter(Context mContext, List<RateUsModel> list) {
        this.mContext = mContext;
        this.list = list;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RateUsModel rateUsModel = list.get(position);
        final String checkbox = rateUsModel.getCheckbox();

        holder.chkAccept.setText(checkbox);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CheckBox chkAccept;
        public CardView mCardView;

        public ViewHolder(View itemView) {
            super(itemView);
            chkAccept = itemView.findViewById(R.id.chkAccept);

            mCardView = (CardView) itemView.findViewById(R.id.card_view);
        }

    }


}
