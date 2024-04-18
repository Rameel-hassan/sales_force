package com.ibrahim.salesforce.offline;

import androidx.room.TypeConverter;

public class Converters {

    @TypeConverter
    public static Double toDouble(String str){
        return (Double.parseDouble(str));
    }

    @TypeConverter
    public static String fromDouble(Double doub){
        return String.valueOf(doub);
    }
}
