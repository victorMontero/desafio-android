package com.picpay.desafio.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.picpay.desafio.getOrAwaitValue
import com.picpay.desafio.models.Contact
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ContactDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ContactDatabase
    private lateinit var dao: ContactDao

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ContactDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.getContactDao()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun insertContactItem() = runBlockingTest {
        dao.insert(CONTACT_ITEM)

        val allContactItems = dao.getAllContacts().getOrAwaitValue()

        assertThat(allContactItems).contains(CONTACT_ITEM)
    }

    @Test
    fun deleteContactItem() = runBlockingTest {
        dao.insert(CONTACT_ITEM)
        dao.deleteContact(CONTACT_ITEM)

        val allContactItems = dao.getAllContacts().getOrAwaitValue()

        assertThat(allContactItems).doesNotContain(CONTACT_ITEM)
    }

    companion object {
        private val CONTACT_ITEM = Contact(
            id = "1",
            img = "https://randomuser.me/api/portraits/men/1.jpg",
            name = "Sandrine Spinka",
            username = "Tod86"
        )
    }
}