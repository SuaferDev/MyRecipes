package com.suafer.myrecipes.activity

import android.app.Activity
import android.app.Dialog
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
import android.view.animation.AnimationUtils
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
import androidx.lifecycle.ViewModelProvider
import com.suafer.myrecipes.R
import com.suafer.myrecipes.app.PopupWindowError
import com.suafer.myrecipes.app.UserData
import com.suafer.myrecipes.app.Viewer
import com.suafer.myrecipes.app.Viewer.Companion.setDefaultEdit
import com.suafer.myrecipes.app.Viewer.Companion.showErrorEdit
import com.suafer.myrecipes.database.MyRecipesDataBase
import com.suafer.myrecipes.database.User
import com.suafer.myrecipes.dialog.LoadingDialog
import com.suafer.myrecipes.viewmodel.MainViewModel
import com.suafer.myrecipes.viewmodel.MainViewModelFactory

/**
 * Разработано для DSR в рамках практики 2024
 * Никишин Дмитрий
 */

class EntranceActivity : AppCompatActivity() {

    private lateinit var editTextLogin : EditText
    private lateinit var editTextPassword : EditText
    private lateinit var checkRemember : CheckBox

    private lateinit var dialogLoading: LoadingDialog

    private lateinit var dataBase : MyRecipesDataBase
    private lateinit var popupError : PopupWindowError

    private lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_entrance)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setWindowFlag(this)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = ContextCompat.getColor(this, R.color.none)

        init()
    }

    private fun setWindowFlag(activity: Activity) {
        val win = activity.window
        val winParams = win.attributes
        winParams.flags = winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        win.attributes = winParams
    }

    private fun init(){
        viewModel = ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
        popupError = PopupWindowError(this)
        dataBase = MyRecipesDataBase.get(this)
        dialogLoading = LoadingDialog(this)

        findViewById<LinearLayout>(R.id.linear).startAnimation(AnimationUtils.loadAnimation(this,
            R.anim.main_animation
        ))

        editTextLogin = findViewById(R.id.edit_text_login)
        editTextPassword = findViewById(R.id.edit_text_password)
        checkRemember = findViewById(R.id.check_remember)

        val imageGoogle = findViewById<ImageView>(R.id.image_google)

        val textForgotPassword = findViewById<TextView>(R.id.text_forgot_password)
        findViewById<TextView>(R.id.text_enter).setOnClickListener {
            Viewer.closeKeyboard(this, editTextLogin); Viewer.closeKeyboard(this, editTextPassword)

            if(ifEmpty()){
                showErrorEdit(this, editTextLogin, editTextPassword); popupError.show(string(R.string.error_empty))
                return@setOnClickListener
            }
            if(!checkInternet()){ popupError.show(string(R.string.error_internet)); return@setOnClickListener }

            val login = editTextLogin.text.toString()
            val password = editTextPassword.text.toString()

            dialogLoading.show()
            viewModel.getUser(login, password, dataBase)
            viewModel.resultLive.observe(this) { user ->
                if(user != null){
                    UserData.instance.setId(user.id!!)
                    val i = Intent(this@EntranceActivity, SearchActivity::class.java)
                    startActivity(i)
                    finish()
                }else{ showErrorEdit(this@EntranceActivity, editTextLogin, editTextPassword); popupError.show(string(
                    R.string.error_password
                )) }
            }
        }

        editTextLogin.addTextChangedListener(generateWatcher(editTextLogin))
        editTextPassword.addTextChangedListener(generateWatcher(editTextPassword))

        findViewById<TextView>(R.id.text_create_account).setOnClickListener { createCreateDialog() }
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
                    viewModel.insertUser(user, dialog, dataBase)
                }else{
                    linearMessage.visibility = View.VISIBLE
                    textMessage.text = string(R.string.error_password)
                    val animName = AnimationUtils.loadAnimation(applicationContext,
                        R.anim.anim_move_top
                    )
                    linearMessage.startAnimation(animName)
                }
            }
        }
        dialog.show()
    }

    private fun generateWatcher(editText: EditText) : TextWatcher{
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) { setDefaultEdit(editText, this@EntranceActivity) }
            override fun afterTextChanged(editable: Editable) {}
        }
        return textWatcher
    }
}