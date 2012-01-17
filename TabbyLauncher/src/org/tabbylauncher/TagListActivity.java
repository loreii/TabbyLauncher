package org.tabbylauncher;

import java.util.ArrayList;

import org.w3c.dom.Node;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TagListActivity extends ListActivity  implements OnItemClickListener{

	private static ArrayList<ApplicationInfo> mApplications = new ArrayList<ApplicationInfo>();

	private static class EfficientAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public EfficientAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		/**
		 * The number of items in the list is determined by the number of speeches
		 * in our array.
		 *
		 * @see android.widget.ListAdapter#getCount()
		 */
		public int getCount() {
			return mApplications.size();
		}

		/**
		 * Since the data comes from an array, just returning the index is
		 * sufficent to get at the data. If we were using a more complex data
		 * structure, we would return whatever object represents one row in the
		 * list.
		 *
		 * @see android.widget.ListAdapter#getItem(int)
		 */
		public Object getItem(int position) {
			return position;
		}

		/**
		 * Use the array index as a unique id.
		 *
		 * @see android.widget.ListAdapter#getItemId(int)
		 */
		public long getItemId(int position) {
			return position;
		}

		/**
		 * Make a view to hold each row.
		 *
		 * @see android.widget.ListAdapter#getView(int, android.view.View,
		 *      android.view.ViewGroup)
		 */
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.tag_item_view, null);

				// Creates a ViewHolder and store references to the two children views
				// we want to bind data to.
				holder = new ViewHolder();
				holder.title 	   = (TextView)  convertView.findViewById(R.id.tag_item_text);
				holder.icon 	   = (ImageView) convertView.findViewById(R.id.tag_item_image);
				holder.color 	   = (Button)    convertView.findViewById(R.id.tag_item_color);

				convertView.setTag(holder);
			} else {
				// Get the ViewHolder back to get fast access to the TextView
				// and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}

			// Bind the data efficiently with the holder.
			final ApplicationInfo node = mApplications.get(position);
			try{
				holder.title.setText(node.title);
				holder.icon.setImageDrawable(node.icon);
				holder.color.setBackgroundColor(node.color);
			}catch (Exception e) {
				// TODO: handle exception
			}
			
			holder.color.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					node.color=ColorUtils.getNextColor(node.color);
					holder.color.setBackgroundColor(node.color);
					//mUri = getContentResolver().insert(intent.getData(), null); help me there!!
				}
			});

			return convertView;
		}



		static class ViewHolder {
			Button    color;
			TextView  title;
			ImageView icon;
		}
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.tag_list_view);

		ListView shot = getListView();
		shot.setOnItemClickListener(this);



		//		Bundle bundle = getIntent().getExtras();
		//		
		//		try{
		//			ArrayList<Parcelable> tmp = bundle.getBundle("tabby").getParcelableArrayList("applications");
		//		
		//		}catch(Exception e){
		//			Log.e("VF","error reading boundle");
		//		}           


		mApplications = Rotor.mApplications;

		setListAdapter(new EfficientAdapter(this));


	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub

	}



}
