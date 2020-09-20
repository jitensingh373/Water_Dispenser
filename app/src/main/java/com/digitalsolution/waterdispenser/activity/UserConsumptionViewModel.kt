package com.digitalsolution.waterdispenser.activity

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digitalsolution.waterdispenser.activity.data.UserConsumptionDetails
import com.digitalsolution.waterdispenser.data.UserDetailsConstant
import com.google.firebase.database.*
import java.sql.Timestamp

class UserConsumptionViewModel : ViewModel() {
    private val dbUserDetails = UserDetailsConstant.FIREBASE_NODE_CHILD?.let { FirebaseDatabase.getInstance().getReference(UserDetailsConstant.FIREBASE_NODE).child(it) }
    val timestamp = Timestamp(System.currentTimeMillis())
    private val _authors = MutableLiveData<List<UserConsumptionDetails>>()
    val authors: LiveData<List<UserConsumptionDetails>>
        get() = _authors

    private val timeStamp = MutableLiveData<String>()
    val timeStampValue: LiveData<String>
        get() = timeStamp


    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result

    fun addUserConsumptionDetails(userData: UserConsumptionDetails) {
        userData.ID = dbUserDetails!!.push().key
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    dbUserDetails!!.child(userData.ID!!).setValue(userData).addOnCompleteListener {
                        if (it.isSuccessful) {
                            _result.setValue(null)
                        } else {
                            _result.value = it.exception
                        }
                    }
                } else {
                    val scoresRef = FirebaseDatabase.getInstance().getReference("scores")
                    scoresRef.keepSynced(true)
                    dbUserDetails!!.child(userData.ID!!).setValue(userData).addOnCompleteListener {
                        if (it.isSuccessful) {
                            _result.setValue(null)
                        } else {
                            _result.value = it.exception
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Listener was cancelled")
            }
        })
    }

    fun lastfillTimeStamp() {
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
        dbUserDetails?.orderByChild("WATERTYPE")?.equalTo("refill")?.limitToLast(1)
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        // Logger.Level.INFO(""+p0)
                    }
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            var timeStampLocal: String? = null
                            for (authorSnapshot in p0.children) {
                                val authorList = authorSnapshot.getValue(UserConsumptionDetails::class.java)
                                authorList?.ID = authorSnapshot.key
                                authorList?.let { timeStampLocal = authorList.TIMESTAMP.toString() }
                            }
                            timeStamp.value = (timeStampLocal)
                        }
                    }
                })
    }

    fun fetchUserConsumptionAllDetails(timeStamp: String?) {
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")
        dbUserDetails?.orderByChild("WATERTYPE")?.endAt(timeStamp,"timestamp")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            val authorSList = mutableListOf<UserConsumptionDetails>()
                            for (authorSnapshot in p0.children) {
                                val authorList = authorSnapshot.getValue(UserConsumptionDetails::class.java)
                                authorList?.ID = authorSnapshot.key
                                authorList?.let { authorSList.add(it) }
                            }
                            _authors.value = authorSList
                        }
                    }
                })
    }

}

