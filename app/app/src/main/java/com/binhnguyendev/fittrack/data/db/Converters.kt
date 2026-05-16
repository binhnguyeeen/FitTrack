package com.binhnguyendev.fittrack.data.db

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromActivityKind(kind: ActivityKind): String = kind.name

    @TypeConverter
    fun toActivityKind(value: String): ActivityKind =
        runCatching { ActivityKind.valueOf(value) }.getOrDefault(ActivityKind.ROUTINE)
}
