package com.ithakatales.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseFragment;
import com.ithakatales.android.data.model.Attraction;
import com.ithakatales.android.data.repository.AttractionRepository;
import com.ithakatales.android.download.TourDownloader;
import com.ithakatales.android.ui.actions.TourAction;
import com.ithakatales.android.ui.adapter.MyToursExpandableListAdapter;
import com.ithakatales.android.util.Bakery;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * @author Farhan Ali
 */
public class MyToursFragment extends BaseFragment implements MyToursExpandableListAdapter.TourActionClickListener {

    @Inject AttractionRepository attractionRepo;

    @Inject TourDownloader tourDownloader;

    @Inject Bakery bakery;

    @Bind(R.id.list_my_tours) ExpandableListView listMyTours;

    private MyToursExpandableListAdapter adapter;
    private List<Attraction> attractions = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mytours, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        attractions = attractionRepo.readAll();
        adapter = new MyToursExpandableListAdapter(attractions);
        adapter.setTourActionClickListener(this);
        listMyTours.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onTourActionClick(Attraction attraction, int tourAction) {
        switch (tourAction) {
            case TourAction.START:
                bakery.toastShort("Under development");
                break;
            case TourAction.RETRY:
                tourDownloader.retryDownload(attraction);
                break;
            case TourAction.UPDATE:
                tourDownloader.update(attraction);
                break;
        }
    }

    public void updateAdapter() {
        adapter.updateProgressMap();
        adapter.notifyDataSetChanged();
    }

}
