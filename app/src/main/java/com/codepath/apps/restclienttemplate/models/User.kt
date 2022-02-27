package com.codepath.apps.restclienttemplate.models

import org.json.JSONObject

//We will work on the User model before we work on the Tweet model because User is intertwined with Tweet
class User {

    //These are var instead of val because we dont know the value yet and it would be changed later
    var name: String = ""
    var screenName: String = ""
    var publicImageUrl: String = ""

    //Something that we can reference without creating new instances of the user object
    companion object{
        //What we need is a method that takes in a Json object and convert it into a user object
        fun fromJson(jsonObject: JSONObject): User{
            val user= User()
            //"name" is the string in the json file that contains the username
            //setting the name of the object of User to be "name" from json file
            user.name = jsonObject.getString("name")

            user.screenName=jsonObject.getString("screen_name")
            user.publicImageUrl = jsonObject.getString("profile_image_url_https")

            return user
        }
    }



}