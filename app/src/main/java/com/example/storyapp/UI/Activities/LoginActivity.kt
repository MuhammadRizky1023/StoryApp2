package com.example.storyapp.UI

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.example.storyapp.Preference.StoryUserPreference
import com.example.storyapp.R
import com.example.storyapp.UI.ViewModel.LoginViewModel
import com.example.storyapp.UI.ViewModel.RepositoryViewModel
import com.example.storyapp.UI.ViewModel.StoryUserViewModel
import com.example.storyapp.UI.ViewModel.ViewModelUserFactory
import com.example.storyapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels{
        RepositoryViewModel(this)
    }
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "login")
    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            loginBinding= ActivityLoginBinding.inflate(layoutInflater)
            setContentView(loginBinding.root)
            setLoginNow()
            animationIsActive()
            setThemeView()
            preferenceObserve()
            showPassword()
            toRegister()

        }
        private  fun preferenceObserve(){
            val preferences = StoryUserPreference.getUserPreference(dataStore)
            val storyUserViewModel =
                ViewModelProvider(this, ViewModelUserFactory(preferences))[StoryUserViewModel::class.java]
            storyUserViewModel.getUserLoginAuth().observe(this) { state ->
                if (state) {
                    val login = Intent(this, ListStoryActivity::class.java)
                    startActivity(login)
                    finish()
                }
            }

            loginViewModel.message.observe(this) {
                val user = loginViewModel.userlogin.value
                checkResponseLoginUser(it, user?.loginResult?.token, storyUserViewModel)
            }


            loginViewModel.isLoading.observe(this) {
                loadingIsActive(it)
            }

        }
        private fun setLoginNow() {
            loginBinding.btnLogin.setOnClickListener {
                loginBinding.edEmailLogin.clearFocus()
                loginBinding.edPasswordLogin.clearFocus()
                checkUserData()
            }
        }


       private fun checkUserData(){
           if (dataIsValid()) {
             dataVerify()
           }
       }

        private  fun dataVerify(){
            val userLogin = LoginRequest(
                loginBinding.edEmailLogin.text.toString().trim(),
                loginBinding.edPasswordLogin.text.toString().trim()
            )
            loginViewModel.getResponseLoginUser(userLogin)
        }

           private fun toRegister(){
               loginBinding.tvRegister.setOnClickListener {
                   val intent = Intent(this, RegisterActivity::class.java)
                   startActivity(intent)
               }
           }

           private  fun showPassword(){
               loginBinding.cbSeePassword.setOnClickListener {
                   if (loginBinding.cbSeePassword.isChecked) {
                       loginBinding.edPasswordLogin.transformationMethod = HideReturnsTransformationMethod.getInstance()
                   } else {
                       loginBinding.edPasswordLogin.transformationMethod = PasswordTransformationMethod.getInstance()
                   }
               }
           }

        private fun dataIsValid(): Boolean {
            return loginBinding.edEmailLogin.userEmailIsValid &&  loginBinding.edPasswordLogin.userPasswordIsValid
        }

        private fun checkResponseLoginUser(
            message: String,
            token: String?,
            viewModel: StoryUserViewModel
        ) {
            if (message.contains("Login as")) {
                loginSuccessfully()
                viewModel.saveUserLoginAuth(true)
                if (token != null) viewModel.saveTokenAuth(token)
                viewModel.saveUserName(loginViewModel.userlogin.value?.loginResult?.name.toString())
            } else {
                when (message) {
                    "unauthorized" -> {
                        unauthorized()
                        loginBinding.edEmailLogin.apply {
                            setText("")
                            requestFocus()
                        }
                        loginBinding.edPasswordLogin.setText("")

                    }
                    "timeout" -> {
                       timeOut()
                    }
                    else -> {
                       errorMessage(message)
                    }
                }
            }
        }

       private  fun loginSuccessfully(){
           AlertDialog.Builder(this).apply {
               setTitle(getString(R.string.information))
               setMessage(getString(R.string.login_successfully))
               setPositiveButton(getString(R.string.continue_)) { _, _ ->
                   val intent = Intent(this@LoginActivity, ListStoryActivity::class.java)
                   intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                   startActivity(intent)
                   finish()
               }
               create()
               show()
           }
       }

        private  fun unauthorized(){
            Toast.makeText(this, getString(R.string.this_account_unauthorized), Toast.LENGTH_SHORT)
                .show()

        }

        private  fun errorMessage(massage: String){
            Toast.makeText(
                this,
                "${getString(R.string.error_message)} $massage",
                Toast.LENGTH_SHORT
            )
                .show()
        }

        private  fun timeOut(){
            Toast.makeText(this, getString(R.string.timeout_time), Toast.LENGTH_SHORT)
                .show()
        }



        private fun loadingIsActive(loadingIsActive: Boolean) {
            loginBinding.loadingProgressBar.visibility = if (loadingIsActive) View.VISIBLE else View.GONE
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

        private fun animationIsActive() {
            val titleLogin = ObjectAnimator.ofFloat(loginBinding.tvTitleLogin, View.ALPHA, 1f).setDuration(500)
            val emailTextViewLogin = ObjectAnimator.ofFloat(loginBinding.tvEmail, View.ALPHA, 1f).setDuration(500)
            val emailEditTextLayoutLogin = ObjectAnimator.ofFloat(loginBinding.tilEmail, View.ALPHA, 1f).setDuration(500)
            val passwordTextViewLogin = ObjectAnimator.ofFloat(loginBinding.tvPassword, View.ALPHA, 1f).setDuration(500)
            val passwordEditTextLayoutLogin = ObjectAnimator.ofFloat(loginBinding.tilPassword, View.ALPHA, 1f).setDuration(500)
            val seePassword= ObjectAnimator.ofFloat(loginBinding.cbSeePassword, View.ALPHA, 1f).setDuration(500)
            val buttonLogin = ObjectAnimator.ofFloat(loginBinding.btnLogin, View.ALPHA, 1f).setDuration(500)
            val donTHaveAnAccount= ObjectAnimator.ofFloat(loginBinding.tvDonTHaveAnAccount, View.ALPHA, 1f).setDuration(500)
            val textViewRegister= ObjectAnimator.ofFloat(loginBinding.tvRegister, View.ALPHA, 1f).setDuration(500)
            AnimatorSet().apply {
                playSequentially(
                    titleLogin,
                    emailTextViewLogin,
                    emailEditTextLayoutLogin,
                    passwordTextViewLogin,
                    passwordEditTextLayoutLogin,
                    seePassword,
                    buttonLogin,
                    donTHaveAnAccount,
                    textViewRegister
                )
                startDelay = 500
            }.start()
        }

}