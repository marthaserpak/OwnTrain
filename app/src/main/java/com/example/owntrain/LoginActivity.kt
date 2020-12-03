package com.example.owntrain

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
// 576869ae2d1f5663f6fbd2a395fc39ab1cfa6517
class LoginActivity : AppCompatActivity(), View.OnClickListener, KeyboardVisibilityEventListener {
    private val TAG = "LoginActivity"

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d("TAG", "onCreate")

        mAuth = FirebaseAuth.getInstance()
        KeyboardVisibilityEvent.setEventListener(this, this)

        login_btn.setOnClickListener(this)
        signUp_text.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.login_btn -> {
                val email = login_email.text.toString()
                val password = login_password.text.toString()
                if (validate(email, password)) {
                    mAuth.signInWithEmailAndPassword(email, password)
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {
                    Toast.makeText(this, "Please, enter correct \nemail and password.",
                            Toast.LENGTH_SHORT).show()
                }
            }
            R.id.signUp_text -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }

    override fun onVisibilityChanged(isOpen: Boolean) {
        if (isOpen) {
            signUp_text.visibility = View.GONE
        } else {
            signUp_text.visibility = View.VISIBLE
        }
    }


}