package com.suafer.myrecipes.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity (tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null,

    @ColumnInfo(name = "login")
    var login : String,

    @ColumnInfo(name = "password")
    var password : String,

    @ColumnInfo(name = "name")
    var name : String,

)