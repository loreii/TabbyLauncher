/**
 * 
 */
package org.tabbylauncher.adapter;

import java.util.ArrayList;

import org.tabbylauncher.ApplicationInfo;
import org.tabbylauncher.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author fabiobenedetti
 *
 */
public class ApplicationsAdapter extends ArrayAdapter<ApplicationInfo> {

    private Rect mOldBounds = new Rect();
    private ArrayList<ApplicationInfo> mApplications;
    private Fragment mFragment;

    public ApplicationsAdapter(Context context, ArrayList<ApplicationInfo> apps,
    		Fragment fragment) {
        super(context, 0, apps);
        mFragment = fragment;
        mApplications = apps;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ApplicationInfo info = mApplications.get(position);

        if (convertView == null) {
            final LayoutInflater inflater = mFragment.getActivity().getLayoutInflater();
            convertView = inflater.inflate(R.layout.application, parent, false);
        }

        Drawable icon = info.icon;

        if (!info.filtered) {
            //final Resources resources = getContext().getResources();
            int width = 42;//(int) resources.getDimension(android.R.dimen.app_icon_size);
            int height = 42;//(int) resources.getDimension(android.R.dimen.app_icon_size);

            final int iconWidth = icon.getIntrinsicWidth();
            final int iconHeight = icon.getIntrinsicHeight();

            if (icon instanceof PaintDrawable) {
                PaintDrawable painter = (PaintDrawable) icon;
                painter.setIntrinsicWidth(width);
                painter.setIntrinsicHeight(height);
            }

            if (width > 0 && height > 0 && (width < iconWidth || height < iconHeight)) {
                final float ratio = (float) iconWidth / iconHeight;

                if (iconWidth > iconHeight) {
                    height = (int) (width / ratio);
                } else if (iconHeight > iconWidth) {
                    width = (int) (height * ratio);
                }

                final Bitmap.Config c =
                        icon.getOpacity() != PixelFormat.OPAQUE ?
                            Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
                final Bitmap thumb = Bitmap.createBitmap(width, height, c);
                final Canvas canvas = new Canvas(thumb);
                canvas.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, 0));
                // Copy the old bounds to restore them later
                // If we were to do oldBounds = icon.getBounds(),
                // the call to setBounds() that follows would
                // change the same instance and we would lose the
                // old bounds
                mOldBounds.set(icon.getBounds());
                icon.setBounds(0, 0, width, height);
                icon.draw(canvas);
                icon.setBounds(mOldBounds);
                icon = info.icon = new BitmapDrawable(thumb);
                info.filtered = true;
            }
        }

        final TextView textView = (TextView) convertView.findViewById(R.id.label);
        textView.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
       Log.i("", "-------> title "+info.title);
        textView.setText(info.title);

        return convertView;
    }

}
