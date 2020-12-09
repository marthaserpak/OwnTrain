package com.example.owntrain.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.owntrain.R
import com.example.owntrain.activities.HomeActivity
import com.example.owntrain.coordinateBtnAndInputs
import com.example.owntrain.models.User
import com.example.owntrain.showToast
import com.example.owntrain.validate
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.email_fragment.*
import kotlinx.android.synthetic.main.register_fragment.*

class RegisterActivity : AppCompatActivity(), EmailFragment.Listener, RegisterFragment.Listener {
    /*KeyboardVisibilityEventListener*/

    private var mEmail: String? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private val TAG = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        if (savedInstanceState == null) {
            /* Этот код, добавляющий фрагмент на экран должен выполнятся один раз иначе выйдет
            * дубликат фрагмента. Для этого мы проверим на null savedInstanceState,  и если она
            * null это будет означать что, Активити только создалось и можно в него поместить фрагмент */
            supportFragmentManager.beginTransaction().add(R.id.register_container, EmailFragment())
                .commit()
        }

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference

    }

    override fun onNext(email: String) {
        if (email.isNotEmpty()) {
            mEmail = email
            mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result?.signInMethods?.isEmpty() != false) {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.register_container, RegisterFragment())
                            .addToBackStack(null)
                            .commit()
                    } else {
                        showToast("This email is already exists.")
                    }
                } else {
                    Log.e(TAG, "Error: ${it.exception.toString()}")
                    showToast("${it.exception.toString()}")
                }
            }
        } else {
            showToast("Please, enter an email.")
        }
    }

    override fun onRegister(fullName: String, password: String) {
        if (validate(fullName, password)) {
            val email = mEmail
            if (email != null) {
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { it ->
                        if (it.isSuccessful) {
                            val user = mkUser(fullName, email)
                            val reference = mDatabase.child("users").child(it.result!!.user!!.uid)
                            reference.setValue(user).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    startActivity(Intent(this, HomeActivity::class.java))
                                    finish()
                                } else {
                                    unKnownRegisterError(it)
                                }
                            }
                        } else {
                            unKnownRegisterError(it)
                        }
                    }
            } else {
                showToast("Enter an email, please")
                supportFragmentManager.popBackStack()
            }
        } else {
            showToast("Please, enter full name and password.")
        }
    }

    private fun mkUsername(fullName: String) =
        fullName.toLowerCase().replace(" ", ".")

    private fun mkUser(fullName: String, email: String): User {
        val username = mkUsername(fullName)
        return User(fullName, username = username, email = email)
    }

    private fun unKnownRegisterError(it: Task<*>) {
        Log.e(TAG, "Failed to create user, ${it.exception.toString()}")
        showToast("Something wrong happen, try again.")
    }
}


// email input, next btn
class EmailFragment : Fragment() {

    private lateinit var mListener: Listener


    //ВСЕГДА ИМПЛЕМЕНТИРУЮТСЯ!!!!!!!!!
    interface Listener {
        fun onNext(email: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.email_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        coordinateBtnAndInputs(register_nxt_btn, register_email)
        register_nxt_btn.setOnClickListener {
            val email = register_email.text.toString()
            mListener.onNext(email)
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        /* Мы нашего listener ставим на роль контекста, который передает данные Активити */
        mListener = context as Listener
    }

}

// full name, register btn
class RegisterFragment : Fragment() {

    private lateinit var mListener: Listener

    interface Listener {
        fun onRegister(fullName: String, password: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.register_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        coordinateBtnAndInputs(btn_register, fullName_register, password_register)
        btn_register.setOnClickListener {
            val fullName = fullName_register.text.toString()
            val password = password_register.text.toString()
            mListener.onRegister(fullName, password)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = context as Listener
    }


}