package com.ithakatales.android.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.ithakatales.android.R;
import com.ithakatales.android.app.base.BaseFragment;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * @author Farhan Ali
 */
public class MyToursFragment extends BaseFragment {

    @Bind(R.id.list_my_tours) ExpandableListView listMyTours;

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

        /*MyToursExpandableListAdapter adapter = new MyToursExpandableListAdapter(tourDownloadRepo.readAll());
        listMyTours.setAdapter(adapter);*/
    }

}
