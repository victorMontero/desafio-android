package com.picpay.desafio.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.picpay.desafio.models.Contact
import com.picpay.desafio.models.ContactListResponse
import com.picpay.desafio.repository.ContactRepository
import com.picpay.desafio.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class ContactViewModel(
    app: Application,
    val contactRepository: ContactRepository
) : AndroidViewModel(app) {

    val contacts: MutableLiveData<Resource<ContactListResponse>> = MutableLiveData()

    init {
        getContactList()
    }

    fun getContactList() = viewModelScope.launch {
            contacts.postValue(Resource.Loading())
        val response = contactRepository.getContacts()
        contacts.postValue(handleContactListResponse(response))
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
}