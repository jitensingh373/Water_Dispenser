package com.digitalsolution.waterdispenser.activity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digitalsolution.waterdispenser.activity.data.UserConsumptionDetails
import com.digitalsolution.waterdispenser.activity.data.UserDetailsConstant
import com.google.firebase.database.FirebaseDatabase

class UserConsumptionViewModel : ViewModel() {
    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result

        fun addUserConsumptionDetails(userData: UserConsumptionDetails) {
           val dbUserDetails = FirebaseDatabase.getInstance().getReference(UserDetailsConstant.FIREBASE_NODE).child(UserDetailsConstant.FIREBASE_NODE_CHILD)
            userData.uniqueKey = dbUserDetails!!.push().key
            dbUserDetails!!.child(userData.uniqueKey!!).setValue(userData).addOnCompleteListener {
                if(it.isSuccessful){
                    _result.value = null
                }
                else {
                    _result.value = it.exception
                }
            }
    }
}

