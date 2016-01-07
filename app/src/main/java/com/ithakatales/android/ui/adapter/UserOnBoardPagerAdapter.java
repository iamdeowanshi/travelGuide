package com.ithakatales.android.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ithakatales.android.R;
import com.ithakatales.android.app.di.Injector;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Farhan Ali
 */
public class UserOnBoardPagerAdapter extends PagerAdapter {

    @Inject Context context;
    @Inject LayoutInflater inflater;

    private List<Integer> slideResources;
    private OnBoardClickListener onBoardClickListener;

    public UserOnBoardPagerAdapter(List<Integer> slideResources) {
        Injector.instance().inject(this);
        this.slideResources = slideResources;
    }

    public void setOnBoardClickListener(OnBoardClickListener onBoardClickListener) {
        this.onBoardClickListener = onBoardClickListener;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        /*View view = (View) object;

        ImageView imageView = (ImageView) view.findViewById(R.id.image_view);
        Drawable drawable = imageView.getDrawable();
        if(drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bitmapDrawable.getBitmap().recycle();
        }*/

        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return slideResources.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View pageView = inflater.inflate(R.layout.page_item_user_onboard, view, false);

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

        private int position;

        public ViewHolder(View pageView) {
            ButterKnife.bind(this, pageView);
        }

        public void bindData(int position) {
            this.position = position;

            Picasso.with(context).load(slideResources.get(position)).into(imageView);
        }

        @OnClick(R.id.button_close)
        void onCloseClick() {
            if (onBoardClickListener != null) {
                onBoardClickListener.onCloseClicked(position);
            }
        }

        @OnClick(R.id.button_next)
        void onNextClick() {
            if (onBoardClickListener != null) {
                onBoardClickListener.onNextClicked(position);
            }
        }
    }

    public static interface OnBoardClickListener {

        void onCloseClicked(int position);

        void onNextClicked(int position);

    }

}
