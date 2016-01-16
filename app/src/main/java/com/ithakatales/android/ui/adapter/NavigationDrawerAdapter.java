package com.ithakatales.android.ui.adapter;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ithakatales.android.R;
import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.City;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Farhan Ali
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {

    @Inject LayoutInflater inflater;

    private List<City> cities;
    private RecyclerItemClickListener<City> itemClickListener;

    private TextView lastSelectedText;

    public NavigationDrawerAdapter(List<City> cities, RecyclerItemClickListener<City> itemClickListener) {
        Injector.instance().inject(this);
        this.cities = cities;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_drawer, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.text_title) TextView textTitle;

        private City city;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onItemClick(city);
                    if (lastSelectedText != null) {
                        lastSelectedText.setTypeface(null, Typeface.NORMAL);
                        textTitle.setTypeface(null, Typeface.BOLD);
                    }

                    lastSelectedText = textTitle;
                }
            });
        }

        public void bindData(int position) {
            if (position == 0 && lastSelectedText == null) {
                lastSelectedText = textTitle;
                lastSelectedText.setTypeface(null, Typeface.BOLD);
            }

            this.city = cities.get(position);
            textTitle.setText(city.getName());
        }
    }

}
