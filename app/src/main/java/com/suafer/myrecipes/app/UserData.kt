package com.suafer.myrecipes.app


class UserData private constructor() {

    var id: Int? = null
        private set

    companion object {
        val instance: UserData by lazy { UserData() }
    }

    fun setId(id: Int) {
        this.id = id
    }
}