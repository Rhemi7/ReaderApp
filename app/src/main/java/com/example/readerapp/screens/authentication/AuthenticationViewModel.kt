package com.example.readerapp.screens.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AuthenticationViewModel: ViewModel() {
//    val loadingState = MutableStateFlow(LoadingState.IDLE)

    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    fun createUserWithEmailAndPassword(email: String, password: String, home: () -> Unit) {
        viewModelScope.launch {
            if(_loading.value == false) {
                    _loading.value = true
                    try {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val displayName = task.result?.user?.email?.split("@")?.get(0)
                                createUser(displayName)
                                home()
                            } else {
                                android.util.Log.d("SIE", "createUserWithEmailAndPassword: ${task.result} ")
                            }
                                _loading.value = false
                        }
                    } catch (e: java.lang.Exception) {
                        android.util.Log.d("SIE", "createUserWithEmailAndPassword: ")
                    }
                }
        }
    }

    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid
        val user = mutableMapOf<String, Any>()
        user["user_id"] = userId.toString()
        user["display_name"] = displayName.toString()

        FirebaseFirestore.getInstance().collection("users").add(user)

    }

    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit)
    = viewModelScope.launch {
        try {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    task -> if (task.isSuccessful) {
                        //Take Users to the home screen
                        home()
            } else {
                Log.d("LGE", "signInWithEmailAndPassword: Logged in ${task.result} ")
            }
            }
        } catch (e: java.lang.Exception) {
            Log.d("LGE", "signInWithEmailAndPassword: $e")
        }
    }
}