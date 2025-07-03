package com.example.project_uas.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_uas.database.AppDatabase
import com.example.project_uas.model.User
import com.example.project_uas.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    fun register(
        user: User,
        onResult: (success: Boolean, message: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingUser = repository.getUser(user.username)
            if (existingUser != null) {
                onResult(false, "Username sudah digunakan.")
            } else {
                repository.insertUser(user)
                onResult(true, "Registrasi berhasil!")
            }
        }
    }

    fun login(
        username: String,
        password: String,
        onResult: (success: Boolean, message: String, userId: Int) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUser(username)
            if (user != null && user.password == password) {
                onResult(true, "Login berhasil!", user.id)  // ✅ kirim user.id
            } else {
                onResult(false, "Username atau password salah.", -1)
            }
        }
    }
}
