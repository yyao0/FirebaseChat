package com.example.firebasechat

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.gsm.SmsMessage.MessageClass
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebasechat.databinding.ActivityMainBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private val messageList = mutableListOf<Message>()
    private val adapter = MessageAdapter(messageList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("chats")

        binding.rvChat.layoutManager = LinearLayoutManager(this)
        binding.rvChat.adapter = adapter

        binding.btnSend.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val messageText = binding.etMessage.text.toString()
            val message = Message(username = username, text = messageText)
            sendMessage(message)
        }

        binding.btnUpload.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messageList.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val message = postSnapshot.getValue(Message::class.java)
                    message?.let { messageList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("tag", databaseError.message)
            }
        })
    }

    private fun sendMessage(message: Message) {
        val messageId = database.push().key
        messageId?.let {
            database.child(it).setValue(message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            uploadImageToFirebase(imageUri)
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri?) {
        val storageReference = FirebaseStorage.getInstance().getReference("images/${UUID.randomUUID()}")
        imageUri?.let {
            storageReference.putFile(it).addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { imgUri ->
                    val message = Message(username = binding.etUsername.text.toString(), imageUrl = imgUri.toString())
                    sendMessage(message)
                }
            }
        }
    }

    companion object {
        const val IMAGE_PICK_CODE = 101
    }
}