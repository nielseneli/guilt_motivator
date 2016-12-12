package nielsen.guiltmotivator;

import android.provider.BaseColumns;

/**
 * Contract of the columns in the contact table.
 *
 */

public class ContactDbContract{

    private ContactDbContract(){}

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_NAME_TASK_ID = "taskId";
        public static final String COLUMN_NAME_CONTACT_NAME = "contactName";
        public static final String COLUMN_NAME_CONTACT_ADDRESS = "contactAddress";
        public static final String COLUMN_NAME_CONTACT_METHOD = "contactMethod";
    }
}
