package com.codepath.apps.restclienttemplate

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException

class TimelineActivity : AppCompatActivity() {

    //Making an instance of the twitter client class and tell android we will initialize it later
    lateinit var client: TwitterClient

    //After creating the tweets adapter, we need to create a variable to reference...
        //1. our recyclerView
    lateinit var rvTweets: RecyclerView
        //2. our adapter
    lateinit var adapter: TweetsAdapter
        //3. list of tweets (holds the tweets that we get back after our API call
    val tweets = ArrayList<Tweet>()

    //Creating a variable for the pull to refresh
    lateinit var swipeContainer: SwipeRefreshLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        //Initializing the instance of the twitter client class
        client=TwitterApplication.getRestClient(this)

        //For pull to refresh: initializing variable
        //we know the id is swapContainer from the layout file
        swipeContainer = findViewById(R.id.swipeContainer)

        //Saying whats actually going to happen when we swipe
        swipeContainer.setOnRefreshListener{
            Log.i(TAG, "Refreshing timeline")
            populateHomeTimeline()

        }

        //color schemes for the swipe to refresh
        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light,
        )

        //Populate the variables for the 1.2.3.
        rvTweets = findViewById(R.id.rvTweets)
        adapter=TweetsAdapter(tweets)
        rvTweets.layoutManager = LinearLayoutManager(this)
        rvTweets.adapter=adapter

        //Once TimelineActivity is launched, we automatically call this populateHomeTimeline()
            //and it calls the API endpoint getApiUrl("statuses/home_timeline.json") in TwitterClient
            //based on if the API call was successful, we would be in onSuccess or onFailure

        populateHomeTimeline()
    }

    //TODO
    //Inflating the menu resource file we want to use
    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        //Inflate the right menu resource file
        menuInflater.inflate(R.menu.menu_main,menu)
        //If we return false then the menu will not be shown
        return true
    }
    //TODO
    //Handles what we actually do when clicked on menu item
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //refer back to the icon with the id we give to it
        if(item.itemId == R.id.compose){
            //Navigating to the compose screen
            val intent = Intent(this, ComposeActivity::class.java)
            //Instead of just doing startActivity(intent), we want to use a function
                //that gives back a result. The request code can be initialized to anything we want.
                //Just make sure you use the same code for the activity you want the user to come from.
            startActivityForResult(intent,REQUEST_CODE)

        }
        return super.onOptionsItemSelected(item)
    }

    //This method is called when we come back from ComposeActivity (aka after finish())
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){

        //REQUEST_CODE is defined in the companion object
        //The request code makes sure that we are coming back from ComposeActivity
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE){
            //If we are coming back from the ComposeActivity, then get that tweet
            //Everything is passed back through the data object so we need to get that data object
            //data? because sometimes the intent is empty so pass safe check
            val tweet = data?.getParcelableExtra<Tweet>("tweet") as Tweet
            //Update timeline by adding new tweet to existing list of tweets
            //Modifying the data source of tweets
            tweets.add(0,tweet)
            //update adapter
            adapter.notifyItemInserted(0)
            //Add something so that we dont have to personally scroll to see tweet
            rvTweets.smoothScrollToPosition(0)
        }

        //At the very end, we still want android to handle whatever is at the end of
        //onActivityResult
        super.onActivityResult(requestCode, resultCode, data)
    }

    //Utilize the twitter client to actually populate the timeline
    fun populateHomeTimeline(){
        //Calling the method in TwitterClient.kt
        client.getHomeTimeline(object: JsonHttpResponseHandler(){
            //Take the json and populate the recyclerView. We took away the "?" because if we assume
                //onSuccess means that the json and headers should exist.
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {

                //First we got our jsonArray from our json response.
                val jsonArray = json.jsonArray

                //Add a try catch block because we are doing a lot of json parsing
                try{
                    //Pull to refresh: Clear out whatever is in the adapter. If we dont have this,
                        // when we refresh, we will just have duplicates
                    adapter.clear()
                    //Parse json array and make a list of tweets (implemented in Tweets.kt)
                    val listOfNewTweetsRetrieved = Tweet.fromJsonArray(jsonArray)
                    //Added our list of tweets to our original list of tweets
                    tweets.addAll(listOfNewTweetsRetrieved)
                    //Notify adapter that the data set changed
                    adapter.notifyDataSetChanged()

                    //For pull to refresh: To get rid of the refreshing icon go away when done refreshing
                    swipeContainer.setRefreshing(false)
                    Log.i(TAG, "onSuccess! $json")
                }catch(e: JSONException){
                    Log.e(TAG, "JSON Exception")
                }

            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                //We added a statusCode so that we will get more information on why it failed
                Log.i(TAG, "onFailure $statusCode")
            }



        })
    }

    companion object{
        val TAG="TimelineActivity"
        //for startActivtyForResult
        val REQUEST_CODE=10
    }
}