package nielsen.guiltmotivator;

import android.provider.BaseColumns;

/**
 * This is the contract that contains information for the table of tasks.
 */
public final class TaskDbContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private TaskDbContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_TASK = "task";
        public static final String COLUMN_NAME_ISCHECKED = "isChecked";
        public static final String COLUMN_NAME_DUEDATE = "dueDate";
    }
}