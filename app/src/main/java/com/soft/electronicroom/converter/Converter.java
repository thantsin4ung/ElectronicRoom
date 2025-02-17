package com.soft.electronicroom.converter;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.Date;


public class Converter {

    @TypeConverter
    public static Date fromTimeStamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimeStamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
