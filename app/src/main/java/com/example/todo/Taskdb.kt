package com.example.todo

data class Taskdb(
    val id: String = "",
    val title: String = "",
    val tenggat: String = "",
    val lists: Map<String, ListItem>? = null
)
