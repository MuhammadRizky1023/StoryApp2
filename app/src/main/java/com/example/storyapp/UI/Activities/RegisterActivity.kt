package com.example.storyapp.UI

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.Model.LoginRequest
import com.example.storyapp.Model.RequestRegister
import com.example.storyapp.Preference.StoryUserPreference
import com.example.storyapp.R
import com.example.storyapp.UI.ViewModel.*
import com.example.storyapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var registerBinding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by viewModels{
        RepositoryViewModel(this)
    }
    private val loginViewModel: LoginViewModel by viewModels{
        RepositoryViewModel(this)
    }
    lateinit var name: String
    private lateinit var email: String
    private lateinit var pass: String
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "register")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)
        setupActionBar()
        setRegisterNow()
        animationIsActive()
        observe()
        preferenceObserve()
        showPassword()
        setThemeView()
    }

    private fun setThemeView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private  fun preferenceObserve(){

        val preferences = StoryUserPreference.getUserPreference(dataStore)
        val storyUserViewModel =
            ViewModelProvider(this, ViewModelUserFactory(preferences))[StoryUserViewModel::class.java]

        storyUserViewModel.getUserLoginAuth().observe(this) { model ->
                storyUserViewModel.saveTokenAuth("")
                storyUserViewModel.saveUserName("")

        }

        loginViewModel.userlogin.observe(this) {
            storyUserViewModel.saveUserLoginAuth(true)
            storyUserViewModel.saveTokenAuth(it.loginResult.token)
            storyUserViewModel.saveUserName(it.loginResult.name)

        }
    }


    private  fun observe(){
        registerViewModel.messages.observe(this) {
            checkResponseRegisterUser(it)
        }

        registerViewModel.isLoading.observe(this) {
            loadingIsActive(it)
        }


        loginViewModel.isLoading.observe(this) {
            loadingIsActive(it)
        }

    }

    private fun setRegisterNow() {
        registerBinding.btnRegister.setOnClickListener {
            registerBinding.apply {
                edEmailRegister.clearFocus()
                edNameRegister.clearFocus()
                edPasswordRegister.clearFocus()
            }
            checkUserData()

        }
    }

    private  fun checkUserData(){
        if (dataIsValid()) {
            name = registerBinding.edNameRegister.text.toString().trim()
            email = registerBinding.edEmailRegister.text.toString().trim()
            pass = registerBinding.edPasswordRegister.text.toString().trim()
            val register = RequestRegister(
                name,
                email,
                pass
            )
            registerViewModel.getResponseRegisterUser(register)
        }
    }

    private  fun showPassword(){
        registerBinding.cbSeePassword.setOnClickListener {
            if (registerBinding.cbSeePassword.isChecked) {
                registerBinding.edPasswordRegister.transformationMethod = HideReturnsTransformationMethod.getInstance()

            } else {
                registerBinding.edPasswordRegister.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }




    private fun checkResponseRegisterUser(message: String) {
        if (message== "User created") {
            userWasCreated()
            val user = LoginRequest(
                email,
                pass
            )
            loginViewModel.getResponseLoginUser(user)
        } else {
            when (message) {
                "Bad Request" -> {
                    showBadRequest()
                    registerBinding.edEmailRegister.apply {
                        setText("")
                        requestFocus()
                    }
                }
                "timeout" -> {
                   showTimeOut()
                }
                else -> {
                    showErrorMessage()
                }
            }
        }
    }

    private  fun userWasCreated(){
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.information))
            setMessage(getString(R.string.the_user_created))
            setPositiveButton(getString(R.string.continue_)) { _, _ ->
                val intent = Intent(this@RegisterActivity, ListStoryActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }
    private fun showBadRequest(){

        Toast.makeText(this, getString(R.string.email_already), Toast.LENGTH_LONG).show()

    }

    private  fun showErrorMessage(){
        Toast.makeText(this, getString(R.string.timeout_time), Toast.LENGTH_LONG)
            .show()
    }



    private fun showTimeOut(){
        Toast.makeText(this, getString(R.string.timeout_time), Toast.LENGTH_LONG).show()
    }

    private fun dataIsValid(): Boolean {
        return registerBinding.edNameRegister.userNameIsValid && registerBinding.edEmailRegister.userEmailIsValid &&
                registerBinding.edPasswordRegister.userPasswordIsValid
    }


    private fun loadingIsActive(isLoading: Boolean) {
        registerBinding.loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    @SuppressLint("RestrictedApi")
    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private fun animationIsActive() {
        val titleRegister = ObjectAnimator.ofFloat(registerBinding.tvTitle, View.ALPHA, 1f).setDuration(500)
        val nameRegister = ObjectAnimator.ofFloat(registerBinding.tvName, View.ALPHA, 1f).setDuration(500)
        val nameTextViewRegister = ObjectAnimator.ofFloat(registerBinding.tilName, View.ALPHA, 1f).setDuration(500)
        val emailTextViewRegister = ObjectAnimator.ofFloat(registerBinding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayoutRegister = ObjectAnimator.ofFloat(registerBinding.tilEmail, View.ALPHA, 1f).setDuration(500)
        val passwordTextViewRegister = ObjectAnimator.ofFloat(registerBinding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayoutRegister = ObjectAnimator.ofFloat(registerBinding.tilPassword, View.ALPHA, 1f).setDuration(500)
        val seePassword= ObjectAnimator.ofFloat(registerBinding.cbSeePassword, View.ALPHA, 1f).setDuration(500)
        val buttonRegister = ObjectAnimator.ofFloat(registerBinding.btnRegister, View.ALPHA, 1f).setDuration(500)
        AnimatorSet().apply {
            playSequentially(
                titleRegister,
                nameRegister,
                nameTextViewRegister,
                emailTextViewRegister,
                emailEditTextLayoutRegister,
                passwordTextViewRegister,
                passwordEditTextLayoutRegister,
                seePassword,
                buttonRegister,
            )
            startDelay = 500
        }.start()
    }


}