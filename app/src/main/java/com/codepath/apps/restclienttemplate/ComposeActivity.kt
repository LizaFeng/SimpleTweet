package com.codepath.apps.restclienttemplate

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    //After setting up the button and text in the xml file of this class, we will grab
        //references to them
    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    //Make a variable for our client
    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        //Grabbing the textview
        etCompose = findViewById(R.id.etTweetCompose)
        //Grabbing the button as well
        btnTweet = findViewById(R.id.btnTweet)

        //Initializing variable client
        client = TwitterApplication.getRestClient(this)

        //For the TextChangedListener
        val etValue = findViewById(R.id.etTweetCompose) as EditText

        val tvCharCounter = findViewById<TextView>(R.id.tvCharCounter)
        etValue.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Fires right as the text is being changed (even supplies the range of text)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Fires right before text is changing
            }

            override fun afterTextChanged(s: Editable) {
                // Fires right after the text has changed
                //tvCharCounter.setText(s.count())
                tvCharCounter.setText((280-(s.toString().length)).toString())
            }
        })a,

        //We need to do something when the user clicks on the tweet button
            //so we need to set an onClickListener
        btnTweet.setOnClickListener{

            //Grab the content of edittext (etCompose): we want to do this because we need to know
                //what the user want to tweet.
            val tweetContent = etCompose.text.toString()

            //1. Make sure the tweet isn't empty
            if(tweetContent.isEmpty()){
                Toast.makeText(this, "Empty tweets not allowed", Toast.LENGTH_SHORT).show()
                //Look into displaying SnackBar message (snackbar will show the message to the user for a few seconds)
            }
            //2. Make sure the tweet is under character count
            if (tweetContent.length > 280){
                Toast.makeText(this, "Tweet is too long! Limit is 140 characters", Toast.LENGTH_SHORT)
                    .show()
            }else{
                //Make an api call to Twitter to publish tweet
               client.publishTweet(tweetContent, object: JsonHttpResponseHandler(){

                   override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                       Log.i(TAG, "Successfully published tweet!")
                       //Grabbing the tweet and converting it to a tweet object
                       val tweet = Tweet.fromJson(json.jsonObject)
                       //Pass tweet back to timeline activity
                        //creating an intent to pass to TimelineActivity
                       intent.putExtra("tweet",tweet )
                       //Set the result and day whether things are ok or not, when setting result, also
                        //pass intent
                       setResult(RESULT_OK, intent)
                       //close out compose activity so that we are back to TimelineActivity
                       finish()
                    //TODO: send the tweet back to TimelineActivity to show
                   }

                   override fun onFailure(
                       statusCode: Int,
                       headers: Headers?,
                       response: String?,
                       throwable: Throwable?
                   ) {
                       Log.e(TAG, "Failed to publish tweet", throwable)
                   }



               })
            }






        }
    }
}