package com.vfs.somecoolname

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

data class UserProfile(
    val name: String = "",
    val email: String = ""
)

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db by lazy { FirebaseDatabase.getInstance().reference }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        auth = FirebaseAuth.getInstance()

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                toast("Completa email y password")
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener {
                    goToGroups()
                }
                .addOnFailureListener { e ->
                    toast("Login falló: ${e.message}")
                }
        }

        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val pass = etPassword.text.toString()

            if (name.isEmpty() || email.isEmpty() || pass.length < 6) {
                toast("Nombre, email y password (mín 6 chars)")
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener
                    val profile = UserProfile(name = name, email = email)

                    // Guardar perfil en Realtime DB
                    db.child("users").child(uid).setValue(profile)
                        .addOnSuccessListener { goToGroups() }
                        .addOnFailureListener { e -> toast("No se guardó user: ${e.message}") }
                }
                .addOnFailureListener { e ->
                    toast("Registro falló: ${e.message}")
                }
        }
    }

    override fun onStart() {
        super.onStart()
        // Si ya está logueada, salta login
        if (FirebaseAuth.getInstance().currentUser != null) {
            goToGroups()
        }
    }

    private fun goToGroups() {
        startActivity(Intent(this, GroupsActivity::class.java))
        finish()
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}