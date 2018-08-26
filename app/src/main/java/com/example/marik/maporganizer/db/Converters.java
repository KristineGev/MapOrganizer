package com.example.marik.maporganizer.db;

import android.arch.persistence.room.TypeConverter;
import android.location.Address;
import android.location.Location;

import com.google.android.gms.location.places.AddPlaceRequest;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Converters {

    @TypeConverter
    public static UUID toUUID(String value) {
        return value == null ? null : UUID.fromString(value);
    }

    @TypeConverter
    public static String toString(UUID value) {
        return value == null ? null : value.toString();
    }


    @TypeConverter
    public static Date toDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long toLong(Date value) {
        return value == null ? null : value.getTime();

    }

    @TypeConverter
    public static String fromLocation(Location location) {
        if (location==null) {
            return(null);
        }

        return(String.format(Locale.US, "%f,%f", location.getLatitude(),
                location.getLongitude()));
    }

    @TypeConverter
    public static Location toLocation(String latlon) {
        if (latlon==null) {
            return(null);
        }
        String[] pieces=latlon.split(",");
        Location result=new Location("");

        result.setLatitude(Double.parseDouble(pieces[0]));
        result.setLongitude(Double.parseDouble(pieces[1]));

        return(result);
    }}