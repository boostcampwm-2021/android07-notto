package com.gojol.notto.model.database.label

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Label(
    @ColumnInfo(name = "order") val order: Int,
    @ColumnInfo(name = "name") val name: String,
    @PrimaryKey(autoGenerate = true) var labelId: Int = 0
) : Parcelable
