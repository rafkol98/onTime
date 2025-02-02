package com.example.ontime.AutoSuggestClasses;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.ontime.MapRelatedClasses.PlaceAPI;

import java.util.ArrayList;


public class PlaceAutoSuggestAdapter extends ArrayAdapter implements Filterable {

    ArrayList<String> results;

    int resource;
    Context context;

    PlaceAPI placeAPI = new PlaceAPI();

    public PlaceAutoSuggestAdapter(Context context, int resId) {
        super(context,resId);
        this.context = context;
        this.resource = resId;

    }

    /**
     *
     * @return
     */
    @Override
    public int getCount() {
        return results.size();
    }

    /**
     *
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    /**
     *
     * @return
     */
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            /**
             *
             * @param constraint
             * @return
             */
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint!= null) {
                    results = placeAPI.autoComplete(constraint.toString());

                    filterResults.values=results;
                    filterResults.count = results.size();
                }
                return filterResults;
            }

            /**
             *
             * @param constraint
             * @param results
             */
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results!=null && results.count>0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

}
