package com.digitalsolution.waterdispenser.activity.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.digitalsolution.waterdispenser.R
import com.digitalsolution.waterdispenser.activity.data.LoginRepository
import com.digitalsolution.waterdispenser.activity.data.Result
import com.digitalsolution.waterdispenser.activity.data.model.LoggedInUser

class LoginViewModel internal constructor(private val loginRepository: LoginRepository) : ViewModel() {
    private val loginFormState = MutableLiveData<LoginFormState?>()
    private val loginResult = MutableLiveData<LoginResult?>()
    fun getLoginFormState(): LiveData<LoginFormState?> {
        return loginFormState
    }

    fun getLoginResult(): LiveData<LoginResult?> {
        return loginResult
    }

    fun login(username: String?, password: String?) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)
        if (result is Result.Success<*>) {
            val data = (result as Result.Success<LoggedInUser>).data
            loginResult.setValue(LoginResult(LoggedInUserView(data.displayName)))
        } else {
            loginResult.setValue(LoginResult(R.string.login_failed))
        }
    }

    fun loginDataChanged(username: String?, password: String?) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(LoginFormState(R.string.invalid_username, null))
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(LoginFormState(null, R.string.invalid_password))
        } else {
            loginFormState.setValue(LoginFormState(true))
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String?): Boolean {
        return if (username == null) {
            false
        } else !username.trim { it <= ' ' }.isEmpty()
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String?): Boolean {
        return password != null && password.trim { it <= ' ' }.length > 5
    }

}