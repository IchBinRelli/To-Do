package com.example.todo

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ListRepo(private val context: Context) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference
    private val userId: String?

    init {
        userId = firebaseAuth.currentUser?.uid
        database = FirebaseDatabase.getInstance().getReference("lists/$userId")
    }

    // Tambah List
    fun tambah(
        title: String,
//        tenggat: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val listId = database.push().key ?: return
        val daftar = Daftar(listId, title)

        database.child(listId).setValue(daftar)
            .addOnSuccessListener {
                Toast.makeText(context, "Daftar berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Gagal menambahkan daftar")
            }
    }

    // Hapus List
//    fun hapus(
//        listId: String,
//        onSuccess: () -> Unit,
//        onFailure: (String) -> Unit
//    ) {
//        database.child(listId).removeValue()
//            .addOnSuccessListener {
//                Toast.makeText(context, "Daftar berhasil dihapus", Toast.LENGTH_SHORT).show()
//                onSuccess()
//            }
//            .addOnFailureListener { e ->
//                onFailure(e.message ?: "Gagal menghapus daftar")
//            }
//    }
    fun hapus(listId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val listRef = database.child(listId)
        val taskRef = FirebaseDatabase.getInstance().getReference("tasks").child(listId)

        // Hapus task duluan karena rule butuh list-nya masih ada
        taskRef.removeValue()
            .addOnSuccessListener {
                listRef.removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Daftar & task berhasil dihapus", Toast.LENGTH_SHORT).show()
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onFailure("Task kehapus, tapi gagal hapus list: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                onFailure("Gagal hapus task: ${e.message}")
            }
    }

    // Edit List
    fun edit(
        listId: String,
        newTitle: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val updates = mapOf(
            "title" to newTitle,
//            "tenggat" to newDate
        )

        database.child(listId).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(context, "Daftar berhasil diperbarui", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Gagal memperbarui daftar")
            }
    }

    // Fetch Lists
    fun fetchLists(
        onListsFetched: (List<Daftar>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val daftarList = mutableListOf<Daftar>()

                for (itemSnapshot in snapshot.children) {
                    val daftar = itemSnapshot.getValue(Daftar::class.java)
                    if (daftar != null) daftarList.add(daftar)
                }

                onListsFetched(daftarList)
            }

            override fun onCancelled(error: DatabaseError) {
//                onFailure("Gagal memuat daftar: ${error.message}")
            }
        })
    }
}
