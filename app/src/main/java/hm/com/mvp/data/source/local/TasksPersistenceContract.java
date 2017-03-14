package hm.com.mvp.data.source.local;

import android.provider.BaseColumns;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/10
 */
class TasksPersistenceContract {
    private TasksPersistenceContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "task";
        public static final String COLUMN_NAME_ENTRY_ID = "entryid";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_COMPLETED = "completed";
    }
}
