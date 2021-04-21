package com.example.abyss.model.data.dto

 class PostDTO{
     var imageUrl: String = ""
     var text: String = ""
     var userId: String = ""
     constructor(imageUrl: String, text: String)
     {
          this.imageUrl = imageUrl
          this.text = text
     }
}
