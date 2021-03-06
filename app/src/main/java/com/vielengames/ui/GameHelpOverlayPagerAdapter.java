package com.vielengames.ui;

import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vielengames.R;
import com.vielengames.utils.ViewUtils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class GameHelpOverlayPagerAdapter extends PagerAdapter {

    private static final int[] imageResArray = {
            R.drawable.game_help_overlay_1,
            R.drawable.game_help_overlay_2,
            R.drawable.game_help_overlay_3
    };

    public interface OnPageClickListener {

        void onPageClick();
    }

    private final OnPageClickListener listener;

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View page = inflater.inflate(R.layout.game_help_overlay_page, container, false);
        page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPageClick();
            }
        });
        Resources r = container.getResources();
        ViewUtils.setImage(imageResArray[position], page, R.id.game_help_overlay_image);
        ViewUtils.setText(r.getStringArray(R.array.game_help_overlay_info)[position], page, R.id.game_help_overlay_info);
        container.addView(page);
        return page;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
