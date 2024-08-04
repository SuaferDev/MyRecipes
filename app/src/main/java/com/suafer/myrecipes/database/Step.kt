package com.suafer.myrecipes.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "steps",
    foreignKeys = [ForeignKey(
        entity = Recipe::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("recipeId"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("recipeId")]
)
data class Step(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    @ColumnInfo(name = "image")
    var image: String?,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "time")
    var time: Double,

    @ColumnInfo(name = "recipeId")
    var recipeId: Int
)