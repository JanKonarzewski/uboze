package pl.konarzewski.uboze.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.joda.time.DateTime
import pl.konarzewski.uboze.database.conventer.DateTimeConverter

@Entity
@TypeConverters(
    DateTimeConverter::class)
data class Imige(
    @PrimaryKey val path: String,
    @ColumnInfo(name = "repetition_number") var rep_no: Int?,
    @ColumnInfo(name = "last_repetition_date") var last_rep_date: DateTime?

)
