package org.tabbylauncher.fragment;

import org.tabbylauncher.R;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;

public class AllContacts extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

	
    SimpleCursorAdapter mAdapter;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        mAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.image_text_element, null,
				new String[] { ContactsContract.Contacts.DISPLAY_NAME },
				new int[] { R.id.element },
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		setListAdapter(mAdapter);

		getLoaderManager().initLoader(0, null, this);        
    }
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getActivity(), ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor data) {
		// TODO Auto-generated method stub
		mAdapter.swapCursor(data);

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		mAdapter.swapCursor(null);

	}
    
    

}
