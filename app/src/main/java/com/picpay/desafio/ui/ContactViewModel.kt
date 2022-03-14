package com.picpay.desafio.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.picpay.desafio.ContactApplication
import com.picpay.desafio.android.R
import com.picpay.desafio.models.Contact
import com.picpay.desafio.models.ContactListResponse
import com.picpay.desafio.repository.ContactRepository
import com.picpay.desafio.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class ContactViewModel(
    app: Application,
    val contactRepository: ContactRepository
) : AndroidViewModel(app) {

    val contacts: MutableLiveData<Resource<ContactListResponse>> = MutableLiveData()

    init {
        getContactList()
    }

    fun getContactList() = viewModelScope.launch {
        safeContactListCall()
    }

    private fun handleContactListResponse(response: Response<ContactListResponse>) : Resource<ContactListResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveContact(contact: Contact) = viewModelScope.launch {
        contactRepository.insert(contact)
    }

    fun getFavoriteContact() = contactRepository.getFavoriteContacts()

    fun deleteContact(contact: Contact) = viewModelScope.launch {
        contactRepository.deleteContact(contact)
    }

    private suspend fun safeContactListCall(){
        contacts.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()){
                val response = contactRepository.getContacts()
                contacts.postValue(handleContactListResponse(response))
            } else {
                contacts.postValue(Resource.Error("${R.string.error_internet}"))
            }
        } catch (t: Throwable){
            when(t){
                is IOException -> contacts.postValue(Resource.Error("${R.string.error_network}"))
                else -> contacts.postValue(Resource.Error("${R.string.error_conversion}"))
            }
        }
    }

    private fun hasInternetConnection() : Boolean {
        val connectivityManager = getApplication<ContactApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}