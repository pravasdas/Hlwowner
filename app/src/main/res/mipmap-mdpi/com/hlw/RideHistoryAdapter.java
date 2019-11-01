package oditek.com.hlw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class RideHistoryAdapter extends ArrayAdapter<RideHistoryModel> implements Filterable {

    Context mContext;
    int layoutResourceId;
    TextView txtNoRcrd;
    private ArrayList<RideHistoryModel> list;
    private List<RideHistoryModel> listFiltered;

    public RideHistoryAdapter(Context mContext, int layoutResourceId, ArrayList<RideHistoryModel> list) {

        super(mContext, layoutResourceId, list);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.list = list;
        this.txtNoRcrd = txtNoRcrd;
        this.listFiltered = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
        }
        RideHistoryModel objectItem = listFiltered.get(position);

        Typeface typeFaceLight = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-Regular.ttf");
        Typeface typeFaceBold = Typeface.createFromAsset(getContext().getAssets(), "fonts/Poppins-SemiBold.ttf");


        TextView locName = convertView.findViewById(R.id.tv_locName);
        TextView locAdd = convertView.findViewById(R.id.tv_locAddress);

        locName.setTypeface(typeFaceBold);
        locAdd.setTypeface(typeFaceLight);
        locName.setText(objectItem.getLocName());
        locAdd.setText(objectItem.getLocAddress());

        return convertView;

    }
    @Override
    public int getCount() {
        return listFiltered.size();
    }

    @Override
    public RideHistoryModel getItem(int position) {
        return listFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listFiltered = list;
                } else {
                    List<RideHistoryModel> filteredList = new ArrayList<>();
                    for (RideHistoryModel row : list) {
                        if (row.getLocName().toLowerCase().contains(charString.toLowerCase()) || row.getLocAddress().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    listFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = listFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                try {
                    listFiltered = (ArrayList<RideHistoryModel>) filterResults.values;
                    notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        };
    }

}
