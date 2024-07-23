package com.suafer.myrecipes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database (entities = [User::class, Recipe::class, Step::class], version = 1)
abstract class MyRecipesDataBase : RoomDatabase() {

    abstract fun dao() : MyRecipesDAO

    companion object{
        fun get(context : Context) : MyRecipesDataBase{
            return Room.databaseBuilder(
                context.applicationContext,
                MyRecipesDataBase::class.java,
                "my_recipes.db"
                ).build()
        }
    }

}