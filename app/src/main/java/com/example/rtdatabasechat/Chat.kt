package com.example.rtdatabasechat

class Chat {
    var message: String? = null
    var id: String? = null

    constructor() {}
    constructor(message: String?, id: String?) {
        this.message = message
        this.id = id
    }
}