package pl.konarzewski.uboze.conventer

import androidx.room.TypeConverter
import org.joda.time.DateTime

object DateTimeConverter {
    @TypeConverter
    fun toDateTime(dateTimeLong: Long?): DateTime? {
        return dateTimeLong?.let { DateTime(it) }
    }

    @TypeConverter
    fun fromDateTime(dateTime: DateTime?): Long? {
        return if (dateTime == null) null else dateTime.millis
    }
}