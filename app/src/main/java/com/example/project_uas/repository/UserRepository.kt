package com.example.project_uas.repository

import com.example.project_uas.database.UserDao
import com.example.project_uas.model.User

class UserRepository(private val userDao: UserDao) {

    suspend fun getUser(username: String): User? {
        return userDao.getUser(username)
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }
}
