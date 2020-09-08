package com.digitalsolution.waterdispenser.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digitalsolution.waterdispenser.activity.data.UserConsumptionDetails
import com.digitalsolution.waterdispenser.data.UserDetailsConstant
import com.google.firebase.database.*

class UserConsumptionViewModel : ViewModel() {
    private val dbUserDetails = UserDetailsConstant.FIREBASE_NODE_CHILD?.let { FirebaseDatabase.getInstance().getReference(UserDetailsConstant.FIREBASE_NODE).child(it) }

    private val _authors = MutableLiveData<List<UserConsumptionDetails>>()
    val authors : LiveData<List<UserConsumptionDetails>>
        get() = _authors

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result

    fun addUserConsumptionDetails(userData: UserConsumptionDetails) {
        userData.ID = dbUserDetails!!.push().key
        dbUserDetails!!.child(userData.ID!!).setValue(userData).addOnCompleteListener {
            if (it.isSuccessful) {
                _result.value = null
            } else {
                _result.value = it.exception
            }
        }
    }

    fun fetchUserConsumptionDetails() {
        dbUserDetails?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                // Logger.Level.INFO(""+p0)
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val authorSList = mutableListOf<UserConsumptionDetails>()
                    for(authorSnapshot in p0.children){
                        val authorList  = authorSnapshot.getValue(UserConsumptionDetails::class.java)
                        authorList?.ID = authorSnapshot.key
                        authorList?.let { authorSList.add(it) }
                    }
                    _authors.value = authorSList
                }
            }
        })
    }
}

