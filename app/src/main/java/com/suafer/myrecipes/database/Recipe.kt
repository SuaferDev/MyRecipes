package com.suafer.myrecipes.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipes",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("userId")]
)
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    @ColumnInfo(name = "data")
    var data: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "image")
    var image: ByteArray?,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "type")
    var type: String,

    @ColumnInfo(name = "time")
    var time: Int,

    @ColumnInfo(name = "calories")
    var calories: Int,

    @ColumnInfo(name = "ingredients")
    var ingredients: String,

    @ColumnInfo(name = "userId")
    var userId: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Recipe

        if (id != other.id) return false
        if (data != other.data) return false
        if (name != other.name) return false
        if (type != other.type) return false
        if (time != other.time) return false
        if (calories != other.calories) return false
        if (ingredients != other.ingredients) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false
        if (userId != other.userId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id ?: 0
        result = 31 * result + data.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + time
        result = 31 * result + calories
        result = 31 * result + ingredients.hashCode()
        result = 31 * result + (image?.contentHashCode() ?: 0)
        result = 31 * result + userId
        return result
    }
}