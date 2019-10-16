package edu.monash.smarter;

import android.provider.BaseColumns;

public abstract class DBStructure {

    public static abstract class tableEntry implements BaseColumns {
        public static final String TABLE_NAME = "usage";
        public static final String COLUMN_RESID = "resid";
        public static final String COLUMN_USAGE_DATE = "usageDate";
        public static final String COLUMN_USAGE_HOUR = "usageHour";
        public static final String COLUMN_FRIDGE_USAGE = "fridgeUsage";
        public static final String COLUMN_AIR_CONDITIONER_USAGE = "airConditionerUsage";
        public static final String COLUMN_WASHING_MACHINE_USAGE = "washingMachineUsage";
        public static final String COLUMN_TEMPERATURE = "temperature";
    }

}