package pl.konarzewski.uboze.database.entity

import androidx.room.*
import org.joda.time.DateTime
import pl.konarzewski.uboze.database.conventer.DateTimeConverter

@Entity
@TypeConverters(DateTimeConverter::class)
data class Image(
    @PrimaryKey val path: String,
    @ColumnInfo(name = "repetition_number") val rep_no: Int?,
    @ColumnInfo(name = "last_repetition_date") val last_rep_date: DateTime?,
    @ColumnInfo(name = "last_modified") val last_modified: Long,
    @ColumnInfo(name = "is_active") val isActive: Boolean
)
