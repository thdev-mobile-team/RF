package com.example.rf

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        val editTextNewUsername = findViewById<EditText>(R.id.username)
        val editTextNewPassword = findViewById<EditText>(R.id.password)
        val editTextConfirmPassword = findViewById<EditText>(R.id.confirm_password)
        val buttonRegister = findViewById<Button>(R.id.registerButton)
        val textViewLogin = findViewById<TextView>(R.id.textLogin)

        buttonRegister.setOnClickListener {
            val newUsername = editTextNewUsername.text.toString().trim()
            val newPassword = editTextNewPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()

            if (newUsername.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this@Register, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this@Register, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            GlobalScope.launch(Dispatchers.IO) {
                if (registerUser(newUsername, newPassword)) {
                    runOnUiThread {
                        Toast.makeText(this@Register, "User registered successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@Register, Login::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@Register, "Failed to register user", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        textViewLogin.setOnClickListener {
            val intent = Intent(this@Register, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(username: String, password: String): Boolean {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(this@Register, "Failed to register user: Database driver not found", Toast.LENGTH_SHORT).show()
            }
            return false
        }

        val url = "jdbc:mysql://localhost:3306/chatbot_rf"
        val dbUsername = "root"
        val dbPassword = ""

        var connection: Connection? = null
        var statement: Statement? = null

        return try {
            connection = DriverManager.getConnection(url, dbUsername, dbPassword)
            val query = "INSERT INTO account (username, password) VALUES ('$username', '$password')"
            statement = connection.createStatement()
            statement.executeUpdate(query)
            true
        } catch (e: SQLException) {
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(this@Register, "Failed to register user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            false
        } finally {
            try {
                statement?.close()
                connection?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }
}
