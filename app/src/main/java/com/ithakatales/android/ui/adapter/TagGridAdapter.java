package com.ithakatales.android.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ithakatales.android.R;
import com.ithakatales.android.app.di.Injector;
import com.ithakatales.android.data.model.IconMap;
import com.ithakatales.android.data.model.Tag;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Farhan Ali
 */
public class TagGridAdapter extends BaseAdapter {

    @Inject LayoutInflater inflater;

    private List<Tag> tags;

    public TagGridAdapter(List<Tag> tags) {
        Injector.instance().inject(this);
        this.tags = tags;
    }

    @Override
    public int getCount() {
        return tags.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item_tag, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.bindData(tags.get(position));

        return convertView;
    }

    public class ViewHolder {
        @Bind(R.id.icon_tag) ImageView iconTag;
        @Bind(R.id.text_tag_name) TextView textTagName;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        void bindData(Tag tag) {
            iconTag.setImageResource(IconMap.tourTags.get(tag.getId()));
            textTagName.setText(tag.getName());
        }
    }

}
