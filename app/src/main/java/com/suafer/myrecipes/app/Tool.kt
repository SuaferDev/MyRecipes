package com.suafer.myrecipes.app


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.suafer.myrecipes.database.Recipe
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Tool {
    companion object{

        /** Сортировки **/
        fun sortAZ(recipes: List<Recipe>): List<Recipe> { return recipes.sortedBy { it.name } }

        fun sortZA(recipes: List<Recipe>): List<Recipe> { return recipes.sortedByDescending  { it.name } }

        fun sortNew(recipes: List<Recipe>): List<Recipe> { return recipes.sortedBy  { it.data } }

        fun sortOld(recipes: List<Recipe>): List<Recipe> { return recipes.sortedByDescending { it.data } }

        fun getTime(): String {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
            return currentDateTime.format(formatter)
        }

        fun findByName(recipes: List<Recipe>, query: String): List<Recipe> {
            return recipes.filter { recipe ->
                recipe.name.contains(query, ignoreCase = true) ||
                        recipe.type.contains(query, ignoreCase = true) ||
                        recipe.ingredients.contains(query, ignoreCase = true)
            }
        }

        /** Работа с изображениями **/
        fun saveImage(bitmap: Bitmap, name: String, context: Context) {
            val file = File(context.filesDir, "$name.png")
            var fos: FileOutputStream? = null
            try {
                fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                fos?.close()
            }
        }

        fun getImage(name: String, context: Context): Bitmap? {
            val file = File(context.filesDir, "$name.png")
            return if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null
            }
        }

        fun deleteImage(imageName: String, context : Context): Boolean {
            val file = File(context.filesDir, "$imageName.png")
            return if (file.exists()) {
                file.delete()
            } else {
                false
            }
        }
    }
}