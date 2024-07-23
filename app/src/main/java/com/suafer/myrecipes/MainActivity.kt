package com.suafer.myrecipes

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.suafer.myrecipes.app.UserData
import com.suafer.myrecipes.database.MyRecipesDataBase
import com.suafer.myrecipes.database.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Разработано для DSR в рамках практики 2024
 * Никишин Дмитрий
 */


class MainActivity : AppCompatActivity() {

    private lateinit var editTextLogin : EditText
    private lateinit var editTextPassword : EditText
    private lateinit var textEnter : TextView

    private lateinit var textMessage : TextView
    private lateinit var linearMessage : LinearLayout

    private lateinit var dataBase : MyRecipesDataBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR

        dataBase = MyRecipesDataBase.get(this)

        init()
    }

    private fun init(){

        linearMessage = findViewById(R.id.linearMessage)
        textMessage = findViewById(R.id.textMessage)
        findViewById<LinearLayout>(R.id.linear).startAnimation(AnimationUtils.loadAnimation(this, R.anim.main_animation))

        editTextLogin = findViewById(R.id.edit_text_login)
        editTextPassword = findViewById(R.id.edit_text_password)

        textEnter = findViewById(R.id.text_enter)
        val imageGoogle = findViewById<ImageView>(R.id.image_google)

        val textForgotPassword = findViewById<TextView>(R.id.text_forgot_password)
        val textCreateAccount = findViewById<TextView>(R.id.text_create_account)

        textEnter.setOnClickListener {
            //val user = User(null, "test@mail.ru", "1234567r", "Jon")
            if(ifEmpty()){
                showErrorMessage(string(R.string.error_empty)); return@setOnClickListener
            }
            if(!checkInternet()){
                showErrorMessage(string(R.string.error_internet)); return@setOnClickListener
            }
            val login = editTextLogin.text.toString()
            val password = editTextPassword.text.toString()

            var user : User?
            lifecycleScope.launch(Dispatchers.IO) {
                user = dataBase.dao().getUser(login, password)
                withContext(Dispatchers.Main) {
                    if(user != null){
                        UserData.instance.setId(user!!.id!!)
                        val i = Intent(this@MainActivity, RecipesActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                }
            }
            /*
            if (user == null){
                var us : List<User>? = null
                Thread{
                    us = dataBase.dao().getAllUsers()
                }.start()
                Toast.makeText(this, us?.size.toString(), Toast.LENGTH_LONG).show()
                var bol: User?
                lifecycleScope.launch(Dispatchers.IO) {
                    bol = dataBase.dao().getUser("test", "12345")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, (bol == null).toString(), Toast.LENGTH_LONG).show()

                    }
                }
                /*
                if(dataBase.dao().ifUser(login)){
                    showErrorMessage(string(R.string.error_password))
                }else{
                    showErrorMessage(string(R.string.error_email))
                }
                return@setOnClickListener*/
            }else{
                Toast.makeText(this, "+", Toast.LENGTH_LONG).show()
                //val i = Intent(this, RecipesActivity::class.java)
                //startActivity(i)
                //finish()
            }*/
        }

        textCreateAccount.setOnClickListener { createGameCreateDialog() }
    }

    private fun ifEmpty() : Boolean{
        return editTextLogin.text.isEmpty() && editTextPassword.text.isEmpty()
    }

    private fun string(int : Int) : String{ return getString(int) }

    private fun showErrorMessage(str : String){
        linearMessage.visibility = View.VISIBLE
        textMessage.text = str
        val animName = AnimationUtils.loadAnimation(applicationContext, R.anim.anim_move_top)
        linearMessage.startAnimation(animName)
    }

    private fun checkInternet(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun createDialogCreate(){

    }

    private fun createGameCreateDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_create)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimationTop
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background)
        dialog.setCancelable(true)

        val editTextLogin = dialog.findViewById<EditText>(R.id.edit_text_login)
        val editTextPassword = dialog.findViewById<EditText>(R.id.edit_text_password)
        val editTextPasswordConfirm = dialog.findViewById<EditText>(R.id.edit_text_password_confirm)
        val editTextName = dialog.findViewById<EditText>(R.id.edit_text_name)
        val textEnter = dialog.findViewById<TextView>(R.id.text_enter)

        val linearMessage = dialog.findViewById<LinearLayout>(R.id.linearMessage)
        val textMessage = dialog.findViewById<TextView>(R.id.textMessage)

        textEnter.setOnClickListener {
            val login = editTextLogin.text.toString()
            val password = editTextPassword.text.toString()
            val passwordConfirm = editTextPasswordConfirm.text.toString()
            val name = editTextName.text.toString()
            if(login.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty() || name.isEmpty()){
            }else{
                if(editTextPassword.text.toString() == editTextPasswordConfirm.text.toString()){
                    val user = User(null, login, password, name)
                    addUser(user, dialog)
                    /*
                    linearMessage.visibility = View.VISIBLE
                    textMessage.text = string(R.string.error_email_use)
                    val animName = AnimationUtils.loadAnimation(applicationContext, R.anim.anim_move_top)
                    linearMessage.startAnimation(animName)
                     */
                }else{
                    linearMessage.visibility = View.VISIBLE
                    textMessage.text = string(R.string.error_password_incorect)
                    val animName = AnimationUtils.loadAnimation(applicationContext, R.anim.anim_move_top)
                    linearMessage.startAnimation(animName)
                }
            }
        }

        dialog.show()
    }

    private fun addUser(user : User, dialog : Dialog){
        lifecycleScope.launch(Dispatchers.IO) {
            dataBase.dao().insertUser(user)
            withContext(Dispatchers.Main) {
                dialog.dismiss()
            }
        }
    }

    private fun showWait(){
        textEnter.setBackgroundResource(R.drawable.background_gray)
        editTextLogin.setBackgroundResource(R.drawable.corner_gray)
        editTextPassword.setBackgroundResource(R.drawable.corner_gray)
    }

    private fun showError(){
        editTextLogin.setTextColor(getColor(R.color.red))
        editTextPassword.setTextColor(getColor(R.color.red))
    }

    private fun showDef(){
        textEnter.setBackgroundResource(R.drawable.background_gray)
        editTextLogin.setTextColor(getColor(R.color.black))
        editTextPassword.setTextColor(getColor(R.color.black))
    }
}