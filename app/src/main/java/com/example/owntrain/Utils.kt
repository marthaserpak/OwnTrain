package com.example.owntrain

import android.content.Context
import android.widget.Toast



fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun validate(login: String, password: String) =
    login.isNotEmpty() && password.isNotEmpty()
