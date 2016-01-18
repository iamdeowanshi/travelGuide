package com.ithakatales.android.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ithakatales.android.R;
import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Image;
import com.ithakatales.android.util.DisplayUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Farhan Ali
 */
public class GalleryPagerAdapter extends PagerAdapter {

    @Inject Context context;
    @Inject LayoutInflater inflater;
    @Inject DisplayUtil displayUtil;

    private List<Image> images;
    private boolean isLoadFromUrl;

    private NavigationClickListener navigationClickListener;

    public GalleryPagerAdapter(List<Image> images, boolean isLoadFromUrl) {
        Injector.instance().inject(this);
        this.images = images;
        this.isLoadFromUrl = isLoadFromUrl;
    }

    public void setNavigationClickListener(NavigationClickListener navigationClickListener) {
        this.navigationClickListener = navigationClickListener;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View pageView = inflater.inflate(R.layout.page_item_gallery, view, false);

        ViewHolder viewHolder = new ViewHolder(pageView);
        viewHolder.bindData(position);

        view.addView(pageView, 0);

        return pageView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public class ViewHolder {
        @Bind(R.id.image_view) ImageView imageView;
        @Bind(R.id.text_caption) TextView textCaption;
        @Bind(R.id.button_next) Button buttonNext;
        @Bind(R.id.button_previous) Button buttonPrevious;

        int position;

        public ViewHolder(View pageView) {
            ButterKnife.bind(this, pageView);
        }

        public void bindData(int position) {
            this.position = position;
            Image image = images.get(position);
            textCaption.setText(image.getCaption());

            Picasso picasso = Picasso.with(context);
            RequestCreator requestCreator = isLoadFromUrl
                    ? picasso.load(image.getUrl())
                    : picasso.load(new File(image.getPath()));
            requestCreator.resize(displayUtil.getWidth(), 0)
                    .into(imageView);

            validateNavigationButton(position);
        }

        @OnClick(R.id.button_next)
        void onNextClick() {
            if (navigationClickListener != null) {
                navigationClickListener.onNextClicked(position);
            }
        }

        @OnClick(R.id.button_previous)
        void onPreviousClick() {
            if (navigationClickListener != null) {
                navigationClickListener.onPreviousClick(position);
            }
        }

        private void validateNavigationButton(int position) {
            int buttonNextVisibility = (position >= images.size() - 1) ? View.GONE : View.VISIBLE;
            buttonNext.setVisibility(buttonNextVisibility);

            int buttonPreviousVisibility = (position <= 0) ? View.GONE : View.VISIBLE;
            buttonPrevious.setVisibility(buttonPreviousVisibility);
        }
    }

    public static interface NavigationClickListener {

        void onPreviousClick(int position);

        void onNextClicked(int position);

    }

}
