package com.example.storyapps.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapps.R
import com.example.storyapps.data.model.LoginResponse
import com.example.storyapps.data.network.ApiConfig
import com.example.storyapps.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity(), View.OnFocusChangeListener, View.OnKeyListener {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title1 = binding.txtTitleLogin1
        val title2 = binding.txtTitleLogin2

        val txt_email = binding.layoutEtEmail
        val txt_password = binding.layoutEtPassword

        val btn_login = binding.btnLogin
        val txt_tittle = binding.txtTitle
        val txt_regis = binding.txtRegister

        val title1Animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top)
        title1.startAnimation(title1Animation)
        val title2Animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top)
        title2.startAnimation(title2Animation)
        // animasi edit text
        val emailAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top)
        txt_email.startAnimation(emailAnimation)
        val passwordAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top)
        txt_password.startAnimation(passwordAnimation)

        // animasi button
        val buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)
        btn_login.startAnimation(buttonAnimation)
        val tittleAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)
        txt_tittle.startAnimation(tittleAnimation)
        val regisAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)
        txt_regis.startAnimation(regisAnimation)


        // Set visibility of views to VISIBLE
        txt_email.visibility = View.VISIBLE
        txt_password.visibility = View.VISIBLE
        btn_login.visibility = View.VISIBLE
        txt_tittle.visibility = View.VISIBLE
        txt_regis.visibility = View.VISIBLE
        title1.visibility = View.VISIBLE
        title2.visibility = View.VISIBLE

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        if (isLoggedIn()) {
            // Jika pengguna sudah login sebelumnya, langsung arahkan ke MainActivity
            goToMainActivity()
        }

        binding.etEmail.onFocusChangeListener = this
        binding.etPassword.onFocusChangeListener = this

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }

        binding.txtRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun loginUser(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiConfig.getApiService().login(email, password)
                withContext(Dispatchers.Main) {
                    handleLoginResponse(response)

                    // Jika login berhasil, dapatkan informasi pengguna dan token
                    if (!response.error) {
                        val username = response.loginResult.name
                        val token = response.loginResult.token

                        // Cetak informasi pengguna dan token ke terminal log
                        Log.d("LoginActivity", "User logged in: $username")
                        Log.d("LoginActivity", "Token: $token")

                        // Simpan informasi pengguna dan token di SharedPreferences
                        saveUserCredentials(username, token)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginActivity, "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleLoginResponse(response: LoginResponse) {
        if (!response.error) {
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

            // Simpan username dan status login setelah login berhasil
            val username =
                response.loginResult.name // Ganti dengan cara mengambil username dari response login
            val editor = sharedPreferences.edit()
            val token = response.loginResult.token

            sharedPreferences.edit().putString("token", token).apply()
            editor.putString("username", username)
            editor.putBoolean("is_logged_in", true) // Tandai bahwa pengguna sudah login
            editor.apply()

            // Cetak informasi pengguna dan token ke logcat
            Log.d("LoginActivity", "User logged in: $username")
            Log.d("LoginActivity", "Token: $token")

            goToMainActivity() // Panggil fungsi goToMainActivity setelah login berhasil
        } else {
            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish() // Tutup LoginActivity setelah memulai MainActivity
    }

    private fun isLoggedIn(): Boolean {
        // Periksa apakah pengguna sudah login sebelumnya
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when(view.id) {
                R.id.et_email -> {
                    if (!hasFocus) {
                        validationEmail()
                    } else {
                        binding.layoutEtEmail.error = null // Clear error when EditText gains focus
                    }
                }
                R.id.et_password -> {
                    if (!hasFocus) {
                        validationPassword()
                    } else {
                        binding.layoutEtPassword.error = null // Clear error when EditText gains focus
                    }
                }
            }
        }
    }

    override fun onKey(view: View?, event: Int, keyEvent: KeyEvent?): Boolean {
        return false
    }

    private fun validationEmail() : Boolean {
        var errorMessage: String? = null
        val value = binding.etEmail.text.toString()

        if (value.isEmpty()) {
            errorMessage = "Email diperlukan"
        } else if (
            !Patterns.EMAIL_ADDRESS.matcher(value).matches()
        ) {
            errorMessage = "Email tidak valid"
        }

        if (errorMessage != null)  {
            binding.layoutEtEmail.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        } else {
            binding.layoutEtEmail.error = null // Clear error when email is valid
        }

        return errorMessage == null
    }

    private fun validationPassword() : Boolean {
        var errorMessage: String? = null
        val value = binding.etPassword.text.toString()

        if (value.isEmpty()) {
            errorMessage = "Password diperlukan"
        } else if (
            value.length < 8
        ) {
            errorMessage = "Password harus sepanjang 8 karakter"
        }

        if (errorMessage != null) {
            binding.layoutEtPassword.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        } else {
            binding.layoutEtPassword.error = null // Clear error when password is valid
        }

        return errorMessage == null
    }

    private fun saveUserCredentials(username: String?, token: String?) {
        // Simpan informasi pengguna dan token di SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("token", token)
        editor.apply()
    }
}

