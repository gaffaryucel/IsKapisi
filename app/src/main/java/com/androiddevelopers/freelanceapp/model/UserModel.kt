package com.androiddevelopers.freelanceapp.model

class UserModel {
    var name :String? = null
    var email :String? = null
    constructor()
    constructor(
        name : String? = null
    ){
        this.name = name
    }
}