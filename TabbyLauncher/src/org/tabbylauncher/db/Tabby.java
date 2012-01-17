package org.tabbylauncher.db;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Tabby {

	public static final String AUTHORITY = "org.tabbylauncher.Tabby";
	
	private Tabby(){};
	
	public static final class Applications implements BaseColumns {
        // This class cannot be instantiated
        private Applications() {}

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/notes");

        /**
         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
         */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/application";   

        /**
         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
         */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/application";  

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "modified DESC";

        /**
         * The application package used as primary key
         * <P>Type: TEXT</P>
         */
        public static final String PACKAGE = "package";

        /**
         * The note itself
         * <P>Type: TEXT</P>
         */
        public static final String TAG = "tag";

        /**
         * Choosen Color
         * <P>Type: INTEGER </P>
         */
        public static final String COLOR = "color";

        /**
         * Self reference name
         * */
		public static final String THAT = "application";

      
    }
	
}
