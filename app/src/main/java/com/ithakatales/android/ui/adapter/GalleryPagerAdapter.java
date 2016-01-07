package com.ithakatales.android.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ithakatales.android.R;
import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.Image;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Farhan Ali
 */
public class GalleryPagerAdapter extends PagerAdapter {

    @Inject Context context;
    @Inject LayoutInflater inflater;

    private List<Image> images;
    private boolean isLoadFromUrl;

    public GalleryPagerAdapter(List<Image> images, boolean isLoadFromUrl) {
        Injector.instance().inject(this);
        this.images = images;
        this.isLoadFromUrl = isLoadFromUrl;
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
        viewHolder.bindData(images.get(position));

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

        public ViewHolder(View pageView) {
            ButterKnife.bind(this, pageView);
        }

        public void bindData(Image image) {
            textCaption.setText(image.getCaption());

            if (isLoadFromUrl) {
                Picasso.with(context).load(image.getUrl()).into(imageView);
                return;
            }

            Picasso.with(context).load(new File(image.getPath())).into(imageView);
        }
    }

}
