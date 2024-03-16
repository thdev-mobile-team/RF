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
import java.sql.PreparedStatement
import java.sql.SQLException

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        val editTextUsername = findViewById<EditText>(R.id.username)
        val editTextPassword = findViewById<EditText>(R.id.password)
        val buttonLogin = findViewById<Button>(R.id.loginButton)
        val textViewSignup = findViewById<TextView>(R.id.buttonSignup)

        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            GlobalScope.launch(Dispatchers.IO) {
                if (authenticateUser(username, password)) {
                    runOnUiThread {
                        Toast.makeText(this@Login, "Login successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@Login, Chat::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@Login, "Invalid username or password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        textViewSignup.setOnClickListener {
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)
        }
    }

    private fun authenticateUser(username: String, password: String): Boolean {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(this@Login, "Failed to authenticate user: Database driver not found", Toast.LENGTH_SHORT).show()
            }
            return false
        }

        val url = "jdbc:mysql://localhost:3306/chatbot_rf"
        val dbUsername = "root"
        val dbPassword = ""

        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null

        return try {
            connection = DriverManager.getConnection(url, dbUsername, dbPassword)
            val query = "SELECT * FROM account WHERE username=? AND password=?"
            preparedStatement = connection.prepareStatement(query)
            preparedStatement.setString(1, username)
            preparedStatement.setString(2, password)
            val resultSet = preparedStatement.executeQuery()
            resultSet.next()
        } catch (e: SQLException) {
            e.printStackTrace()
            runOnUiThread {
                Toast.makeText(this@Login, "Failed to authenticate user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            false
        } finally {
            try {
                preparedStatement?.close()
                connection?.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }
    }
}
