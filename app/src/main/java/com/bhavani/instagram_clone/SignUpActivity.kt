package com.bhavani.instagram_clone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bhavani.instagram_clone.databinding.ActivitySignUpBinding
import com.bhavani.instagram_clone.models.User
import com.bhavani.instagram_clone.utils.USER_NODE
import com.bhavani.instagram_clone.utils.USER_PROFILE_FOLDER
import com.bhavani.instagram_clone.utils.uploadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    private lateinit var user: User

    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()){
        uri->
        uri?.let {
            uploadImage(uri, USER_PROFILE_FOLDER){
                user.image=it
                binding.profileImage.setImageURI(uri)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        user = User()

        binding.signUpBtn.setOnClickListener {
            if ((binding.name.editText?.text.toString() == "") or
                (binding.email.editText?.text.toString() == "") or
                (binding.password.editText?.text.toString() == "")
            ){
                Toast.makeText(this,"Please fill all the information",Toast.LENGTH_SHORT).show()
            }else{

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.email.editText?.text.toString(),
                    binding.password.editText?.text.toString()
                ).addOnCompleteListener {result ->
                    if (result.isSuccessful){
                        user.name = binding.name.editText?.text.toString()
                        user.email = binding.email.editText?.text.toString()
                        user.password = binding.password.editText?.text.toString()

                        Firebase.firestore.collection(USER_NODE).document(Firebase.auth.currentUser!!.uid).set(user)
                            .addOnSuccessListener {
                                startActivity(Intent(this@SignUpActivity,HomeActivity::class.java))
                            }

                    }else{
                        Toast.makeText(this, result.exception?.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.addImage.setOnClickListener {
            launcher.launch("image/*")
        }
    }
}