package com.picpay.desafio.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "contacts"
)
data class Contact(
    @PrimaryKey
    val id: String,
    val img: String,
    val name: String,
    val username: String
) : Serializable