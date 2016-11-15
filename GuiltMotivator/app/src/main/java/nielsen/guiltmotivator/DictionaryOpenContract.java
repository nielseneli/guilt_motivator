package nielsen.guiltmotivator;

import android.provider.BaseColumns;

public final class DictionaryOpenContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DictionaryOpenContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_TASK = "task";
        public static final String COLUMN_NAME_ISCHECKED = "isChecked";
    }
}