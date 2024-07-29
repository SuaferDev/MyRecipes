package com.suafer.myrecipes.app

import com.suafer.myrecipes.database.Recipe
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Tool {
    companion object{
        fun sortAZ(recipes: List<Recipe>): List<Recipe> {
            return recipes.sortedBy { it.name }
        }

        fun sortZA(recipes: List<Recipe>): List<Recipe> {
            return recipes.sortedByDescending  { it.name }
        }

        fun sortNew(recipes: List<Recipe>): List<Recipe> {
            return recipes.sortedBy  { it.data }
        }

        fun sortOld(recipes: List<Recipe>): List<Recipe> {
            return recipes.sortedByDescending  { it.data }
        }

        fun getTime(): String {
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
            return currentDateTime.format(formatter)
        }

        fun findByName(recipes: List<Recipe>, query: String): List<Recipe> {
            return recipes.filter { it.name.contains(query, ignoreCase = true) }
        }
    }
}