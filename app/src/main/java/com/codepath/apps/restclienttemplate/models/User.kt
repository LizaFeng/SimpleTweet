package com.codepath.apps.restclienttemplate.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.json.JSONObject

//Making this Parcelable since we need this in Tweet and Tweet is Parcelable
//Do the same as Tweet with the variables
@Parcelize
//We will work on the User model before we work on the Tweet model because User is intertwined with Tweet
class User (var name: String = "", var screenName: String = "",
            var publicImageUrl: String = ""): Parcelable {
//Variable explanations
    //These are var instead of val because we dont know the value yet and it would be changed later


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