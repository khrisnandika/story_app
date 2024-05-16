package com.example.storyapps.view

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapps.R
import com.example.storyapps.data.model.RegisterResponse
import com.example.storyapps.data.network.ApiConfig
import com.example.storyapps.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener, View.OnKeyListener {

    private lateinit var binding: ActivityRegisterBinding // Deklarasi view binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title1 = binding.txtTitleRegister1
        val title2 = binding.txtTitleRegister2
        val title3 = binding.txtTitleRegister3

        val txt_username = binding.layoutEtUsername
        val txt_email = binding.layoutEtEmail
        val txt_password = binding.layoutEtPassword
        val txt_confirm = binding.layoutEtConfirmPassword

        val btn_register = binding.btnRegister
        val txt_tittle = binding.txtTitle
        val txt_regis = binding.txtLogin

        val title1Animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top)
        title1.startAnimation(title1Animation)
        val title2Animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top)
        title2.startAnimation(title2Animation)
        val title3Animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)
        title3.startAnimation(title3Animation)

        // animasi edit text
        val usernameAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top)
        txt_username.startAnimation(usernameAnimation)
        val emailAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top)
        txt_email.startAnimation(emailAnimation)
        val passwordAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top)
        txt_password.startAnimation(passwordAnimation)
        val confirmPasswordAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top)
        txt_confirm.startAnimation(confirmPasswordAnimation)

        // animasi button
        val buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)
        btn_register.startAnimation(buttonAnimation)

        //animati bawah button
        val tittleAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)
        txt_tittle.startAnimation(tittleAnimation)
        val regisAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)
        txt_regis.startAnimation(regisAnimation)


        // Set visibility of views to VISIBLE
        txt_username.visibility = View.VISIBLE
        txt_email.visibility = View.VISIBLE
        txt_password.visibility = View.VISIBLE
        txt_confirm.visibility = View.VISIBLE


        btn_register.visibility = View.VISIBLE
        txt_tittle.visibility = View.VISIBLE
        txt_regis.visibility = View.VISIBLE
        title1.visibility = View.VISIBLE
        title2.visibility = View.VISIBLE
        title3.visibility = View.VISIBLE

        // Inisialisasi event listener
        binding.btnRegister.setOnClickListener(this)
        binding.txtLogin.setOnClickListener(this)
        binding.etUsername.onFocusChangeListener = this
        binding.etEmail.onFocusChangeListener = this
        binding.etPassword.onFocusChangeListener = this
        binding.etConfirmpassword.onFocusChangeListener = this
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_register -> {
                val name = binding.etUsername.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
                val confirmPassword = binding.etConfirmpassword.text.toString().trim()

                if (validateForm()) {
                    registerUser(name, email, password)
                } else {
                    Toast.makeText(this, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.txt_login -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (view != null) {
            when(view.id) {
                R.id.et_username -> {
                    if (!hasFocus) {
                        validationUsername()
                    }
                }
                R.id.et_email -> {
                    if (!hasFocus) {
                        validationEmail()
                    }
                }
                R.id.et_password -> {
                    if (!hasFocus) {
                        validationPassword()
                    }
                }
                R.id.et_confirmpassword -> {
                    if (!hasFocus) {
                        validationConfirmPassword()
                    }
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        if (!validationUsername()) isValid = false
        if (!validationEmail()) isValid = false
        if (!validationPassword()) isValid = false
        if (!validationConfirmPassword()) isValid = false

        return isValid
    }

    private fun validationUsername() : Boolean {
        val value: String = binding.etUsername.text.toString()

        if (value.isEmpty()) {
            binding.etUsername.error = "Username is required"
            return false
        }

        return true
    }

    private fun validationEmail() : Boolean {
        val value = binding.etEmail.text.toString()

        if (value.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return false
        } else if (!isValidEmail(value)) {
            binding.etEmail.error = "Invalid email address"
            return false
        }

        return true
    }

    private fun validationPassword() : Boolean {
        val value = binding.etPassword.text.toString()

        if (value.isEmpty()) {
            binding.etPassword.error = "Password is required"
            return false
        } else if (value.length < 8) {
            binding.etPassword.error = "Password must be at least 8 characters long"
            return false
        }

        return true
    }

    private fun validationConfirmPassword() : Boolean {
        val value = binding.etConfirmpassword.text.toString()

        if (value.isEmpty()) {
            binding.etConfirmpassword.error = "Confirm Password is required"
            return false
        } else if (value != binding.etPassword.text.toString()) {
            binding.etConfirmpassword.error = "Passwords do not match"
            return false
        }

        return true
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
        return pattern.matcher(email).matches()
    }

    private fun registerUser(name: String, email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiConfig.getApiService().register(name, email, password)
                withContext(Dispatchers.Main) {
                    handleRegisterResponse(response)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Email is already taken", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleRegisterResponse(response: RegisterResponse) {
        if (!response.error) {
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onKey(view: View?, event: Int, keyEvent: KeyEvent?): Boolean {
        return false
    }
}
