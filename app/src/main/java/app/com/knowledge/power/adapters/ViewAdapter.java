package app.com.knowledge.power.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import app.com.knowledge.power.R;

public class ViewAdapter extends PagerAdapter {

    private final Context context;
    private final ArrayList<Integer> imagesList = new ArrayList<>();

    public ViewAdapter(Context context) {
        this.context = context;
        imagesList.add(R.drawable.bg1);
        imagesList.add(R.drawable.bg2);
        imagesList.add(R.drawable.bg3);
        imagesList.add(R.drawable.bg5);
        imagesList.add(R.drawable.bg4);
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE
        );
        View view = layoutInflater.inflate(R.layout.viewpager_item, null);
        ImageView imageView = view.findViewById(R.id.viewPagerImage);
        imageView.setImageDrawable(ContextCompat.getDrawable(context, imagesList.get(position)));

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}