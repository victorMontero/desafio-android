package com.picpay.desafio.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.picpay.desafio.android.R
import com.picpay.desafio.db.ContactDatabase
import com.picpay.desafio.repository.ContactRepository
import com.picpay.desafio.ui.viewmodel.ContactViewModel
import com.picpay.desafio.ui.viewmodel.ContactViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_contacts.*

class ContactListActivity : AppCompatActivity() {

    lateinit var viewModel: ContactViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        val repository = ContactRepository(ContactDatabase(this))
        val viewModelProviderFactory = ContactViewModelProviderFactory(application, repository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(ContactViewModel::class.java)

        bottom_navigation_view.setupWithNavController(users_nav_host_fragment.findNavController())
    }
}
