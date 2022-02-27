package com.codepath.apps.restclienttemplate.models

import org.json.JSONArray
import org.json.JSONObject

class Tweet {

    //We want to display the tweet body
    var body: String= ""
    //We want to display when the tweet was born into the world
    var createdAt: String = ""
    //We want to display who created the tweet.
    var user: User? = null

    //Similar to User.kt, we want to have a companion object where we would have a function that actually allows us
        //to pass in a json object and turn that into a tweet object we can use
    companion object{
        //Creating a tweet object from a json object (parses one json object into a tweet)
        fun fromJson(jsonObject: JSONObject): Tweet {
            val tweet = Tweet()
            //We will put the tag in the name: ""
            tweet.body = jsonObject.getString("text")
            tweet.createdAt = jsonObject.getString("created_at")
            //Get the user class, utilize it from json method with the json object we will pass in
                //The user is not just a string, its an actual User
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"))
            return tweet


        }

        //Method that takes a whole json array and convert that into a list of tweets
        fun fromJsonArray(jsonArray: JSONArray): List<Tweet>{
            val tweets = ArrayList<Tweet>()
            //We used .. before which meant inclusive (we went all the way to the end of the length of the
                //json array but the length of the json array would be out of bounds (no index)
                //until means exclusive
            for(i in 0 until jsonArray.length()){
                tweets.add(fromJson(jsonArray.getJSONObject(i)))
            }
            return tweets


        }

    }


}