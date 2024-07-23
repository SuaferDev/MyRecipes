package com.suafer.myrecipes.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MyRecipesDAO {

    /** Пользователь **/
    @Insert
    fun insertUser(user : User)

    @Query ("SELECT * FROM users")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM users WHERE login = :login AND password = :password")
    fun getUser(login: String, password: String): User?

    @Query("SELECT COUNT(*) FROM users WHERE login = :login")
    fun ifUser(login: String): Boolean

    @Query("UPDATE users SET password = :newPassword WHERE login = :login")
    fun updatePassword(login: String, newPassword: String): Int

    /** Рецепт **/
    @Insert
    fun insertRecipe(recipe: Recipe)
    @Query("SELECT * FROM recipes WHERE userId = :userId")
    fun getAllRecipes(userId: Int): MutableList<Recipe>
    @Query("DELETE FROM recipes WHERE id = :recipeId")
    fun deleteRecipe(recipeId: Int)
    @Update
    fun updateRecipe(recipe: Recipe)

    /** Шаги рецепта **/
    @Insert
    fun insertStep(step: Step)
    @Query("SELECT * FROM steps WHERE recipeId = :recipeId")
    fun getAllSteps(recipeId: Int): Flow<MutableList<Step>>
    @Query("DELETE FROM steps WHERE id = :stepId")
    fun deleteStep(stepId: Int)
    @Update
    fun updateStep(step: Step)

}