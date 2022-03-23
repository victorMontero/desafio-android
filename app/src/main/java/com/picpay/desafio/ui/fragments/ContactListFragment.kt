package com.picpay.desafio.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.picpay.desafio.adapters.ContactAdapter
import com.picpay.desafio.android.R
import com.picpay.desafio.ui.ContactListActivity
import com.picpay.desafio.ui.viewmodel.ContactViewModel
import com.picpay.desafio.util.Resource
import kotlinx.android.synthetic.main.fragment_contact_list.*

class ContactListFragment : Fragment(R.layout.fragment_contact_list) {

    lateinit var viewModel: ContactViewModel
    lateinit var contactAdapter: ContactAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as ContactListActivity).viewModel
        setupRecyclerView()

        contactAdapter.setOnItemClickListener { contact ->
            viewModel.saveContact(contact)
            Snackbar.make(
                view,
                "${contact.username} ${getString(R.string.snackbar_favorites_add)}",
                Snackbar.LENGTH_SHORT
            ).apply {
                setAction(getString(R.string.undo)) {
                    viewModel.deleteContact(contact)
                }
            }.show()
        }

        viewModel.contacts.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { contactListResponse ->
                        contactAdapter.differ.submitList(contactListResponse)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(
                            activity,
                            "${getString(R.string.error)} ",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        progress_bar_contact_list_fragment.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        progress_bar_contact_list_fragment.visibility = View.VISIBLE
    }

    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter()
        recycler_view_contact_list_fragment.apply {
            adapter = contactAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}