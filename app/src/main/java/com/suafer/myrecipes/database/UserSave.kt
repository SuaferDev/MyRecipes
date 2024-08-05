package com.suafer.myrecipes.database

class UserSave(
    private var login : String,
    private var password : String,
) {
    fun login(login: String) { this.login = login}
    fun login() : String { return login }

    fun password(password: String) { this.password = password}
    fun password() : String { return password }
}