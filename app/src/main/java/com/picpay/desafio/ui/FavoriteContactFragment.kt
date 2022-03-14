package com.picpay.desafio.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.picpay.desafio.adapters.ContactAdapter
import com.picpay.desafio.android.R
import kotlinx.android.synthetic.main.fragment_favorite_contact.*

class FavoriteContactFragment : Fragment(R.layout.fragment_favorite_contact) {

    lateinit var viewModel: ContactViewModel
    lateinit var contactAdapter: ContactAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as ContactListActivity).viewModel

        setupRecyclerView()

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val contact = contactAdapter.differ.currentList[position]
                viewModel.deleteContact(contact)
                Snackbar.make(view, "${contact.username} ${getString(R.string.snackbar_favorites_remove)}", Snackbar.LENGTH_LONG).apply {
                    setAction(getString(R.string.undo)) {
                        viewModel.saveContact(contact)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(recycler_view_favorite_contact_fragment)
        }

        viewModel.getFavoriteContact()
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { contacts ->
                contactAdapter.differ.submitList(contacts)
            })
    }

    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter()
        recycler_view_favorite_contact_fragment.apply {
            adapter = contactAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}