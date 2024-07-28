package com.suafer.myrecipes

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.suafer.myrecipes.app.PopupWindowError
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
    private lateinit var checkRemember : CheckBox
    private lateinit var textEnter : TextView

    private lateinit var dialogLoading: Dialog

    private lateinit var dataBase : MyRecipesDataBase
    private lateinit var popupError : PopupWindowError

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setWindowFlag(this)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = ContextCompat.getColor(this, R.color.none)

        popupError = PopupWindowError(this)
        dataBase = MyRecipesDataBase.get(this)

        init()
    }

    private fun setWindowFlag(activity: Activity) {
        val win = activity.window
        val winParams = win.attributes
        winParams.flags = winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        win.attributes = winParams
    }

    private fun init(){

        findViewById<LinearLayout>(R.id.linear).startAnimation(AnimationUtils.loadAnimation(this, R.anim.main_animation))

        editTextLogin = findViewById(R.id.edit_text_login)
        editTextPassword = findViewById(R.id.edit_text_password)
        checkRemember = findViewById(R.id.check_remember)

        textEnter = findViewById(R.id.text_enter)
        val imageGoogle = findViewById<ImageView>(R.id.image_google)

        val textForgotPassword = findViewById<TextView>(R.id.text_forgot_password)
        val textCreateAccount = findViewById<TextView>(R.id.text_create_account)

        textEnter.setOnClickListener {
            closeKeyboard(this, editTextLogin)
            closeKeyboard(this, editTextPassword)

            if(ifEmpty()){
                showError(); popupError.show(string(R.string.error_empty))
                return@setOnClickListener
            }
            if(!checkInternet()){ popupError.show(string(R.string.error_internet)); return@setOnClickListener }

            val login = editTextLogin.text.toString()
            val password = editTextPassword.text.toString()

            var user : User?
            createLoadingDialog()
            lifecycleScope.launch(Dispatchers.IO) {
                user = dataBase.dao().getUser(login, password)
                withContext(Dispatchers.Main) {
                    dialogLoading.dismiss()
                    if(user != null){
                        UserData.instance.setId(user!!.id!!)
                        val i = Intent(this@MainActivity, SearchActivity::class.java)
                        startActivity(i)
                        finish()
                    }else{
                        showError(); popupError.show(string(R.string.error_password))
                    }
                }
            }
        }

        editTextLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) { setDefault(editTextLogin) }
            override fun afterTextChanged(editable: Editable) {}
        })

        editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) { setDefault(editTextPassword) }
            override fun afterTextChanged(editable: Editable) {}
        })

        textCreateAccount.setOnClickListener { createCreateDialog() }
    }

    private fun ifEmpty() : Boolean{ return editTextLogin.text.isEmpty() && editTextPassword.text.isEmpty() }

    private fun string(int : Int) : String{ return getString(int) }

    private fun checkInternet(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun createCreateDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_create)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.attributes.windowAnimations = R.style.DialogAnimationTop
        dialog.window!!.statusBarColor = ContextCompat.getColor(this, R.color.background)
        dialog.window!!.navigationBarColor = ContextCompat.getColor(this, R.color.white)
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
                }else{
                    linearMessage.visibility = View.VISIBLE
                    textMessage.text = string(R.string.error_password)
                    val animName = AnimationUtils.loadAnimation(applicationContext, R.anim.anim_move_top)
                    linearMessage.startAnimation(animName)
                }
            }
        }



        dialog.show()
    }

    private fun createLoadingDialog() { /** Создание диалога загрузки **/
        dialogLoading = Dialog(this)
        dialogLoading.setContentView(R.layout.loading_dialog)
        dialogLoading.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialogLoading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogLoading.window!!.attributes.windowAnimations = R.style.DialogLoadingAnim
        dialogLoading.setCancelable(false)

        val imageLoading = dialogLoading.findViewById<ImageView>(R.id.imageLoading)

        val rotateAnimation = RotateAnimation(0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.duration = 1000
        rotateAnimation.repeatCount = Animation.INFINITE

        imageLoading.startAnimation(rotateAnimation)

        dialogLoading.show()
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
        editTextLogin.setBackgroundResource(R.drawable.corner_red); editTextLogin.setTextColor(getColor(R.color.red))
        editTextPassword.setBackgroundResource(R.drawable.corner_red); editTextPassword.setTextColor(getColor(R.color.red))
    }

    private fun setDefault(editText : EditText){
        editText.setBackgroundResource(R.drawable.corner_main_dark)
        editText.setTextColor(getColor(R.color.black))
    }

    fun closeKeyboard(context: Context, editText: EditText) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }
}