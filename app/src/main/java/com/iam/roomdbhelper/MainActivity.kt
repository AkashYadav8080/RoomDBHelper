package com.iam.roomdbhelper

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.iam.roomdbhelper.data.User
import com.iam.roomdbhelper.repository.UserRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = UserRepository(applicationContext)

        lifecycleScope.launch {
            repository.insertUser(User(1, "Boss"))
            val users = repository.getUsers()
            Log.d("ROOM", "Users: $users")
        }
    }
}
