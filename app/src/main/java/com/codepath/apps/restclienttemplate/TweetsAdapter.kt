package com.codepath.apps.restclienttemplate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codepath.apps.restclienttemplate.models.Tweet
//The following imports are for the TimeStamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


//It needs to know what it is displaying (aka a list of tweets)
//For each adapter, we need a viewholder to reference the actual views
//changed list to array list so that we can use the clear and add all for pull to refresh
class TweetsAdapter (val tweets: ArrayList<Tweet>): RecyclerView.Adapter<TweetsAdapter.ViewHolder>(){
    //-----------------------------------------------------------------------
    /**
     * Given a date String of the format given by the Twitter API, returns a display-formatted
     * String representing the relative time difference, e.g. "2m", "6d", "23 May", "1 Jan 14"
     * depending on how great the time difference between now and the given date is.
     * This, as of 2016-06-29, matches the behavior of the official Twitter app.
     */
    object TimeFormatter {
        fun getTimeDifference(rawJsonDate: String?): String {
            var time = ""
            val twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy"
            val format = SimpleDateFormat(twitterFormat, Locale.ENGLISH)
            format.isLenient = true
            try {
                val diff = (System.currentTimeMillis() - format.parse(rawJsonDate).time) / 1000
                if (diff < 5) time = "Just now" else if (diff < 60) time =
                    String.format(Locale.ENGLISH, "%ds", diff) else if (diff < 60 * 60) time =
                    String.format(
                        Locale.ENGLISH, "%dm", diff / 60
                    ) else if (diff < 60 * 60 * 24) time = String.format(
                    Locale.ENGLISH, "%dh", diff / (60 * 60)
                ) else if (diff < 60 * 60 * 24 * 30) time = String.format(
                    Locale.ENGLISH, "%dd", diff / (60 * 60 * 24)
                ) else {
                    val now = Calendar.getInstance()
                    val then = Calendar.getInstance()
                    then.time = format.parse(rawJsonDate)
                    time = if (now[Calendar.YEAR] == then[Calendar.YEAR]) {
                        (then[Calendar.DAY_OF_MONTH].toString() + " "
                                + then.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US))
                    } else {
                        (then[Calendar.DAY_OF_MONTH].toString() + " "
                                + then.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)
                                + " " + (then[Calendar.YEAR] - 2000).toString())
                    }
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return time
        }

        /**
         * Given a date String of the format given by the Twitter API, returns a display-formatted
         * String of the absolute date of the form "30 Jun 16".
         * This, as of 2016-06-30, matches the behavior of the official Twitter app.
         */
        fun getTimeStamp(rawJsonDate: String?): String {
            var time = ""
            val twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy"
            val format = SimpleDateFormat(twitterFormat, Locale.ENGLISH)
            format.isLenient = true
            try {
                val then = Calendar.getInstance()
                then.time = format.parse(rawJsonDate)
                val date = then.time
                val format1 = SimpleDateFormat("h:mm a \u00b7 dd MMM yy")
                time = format1.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return time
        }
    }
    //-----------------------------------------------------------------------
    //Inflating the layout that we want to use for each of the items we want to display on the recyclerview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetsAdapter.ViewHolder {

        val context = parent.context
        val inflater = LayoutInflater.from(context)

        //inflate our item layout
        val view = inflater.inflate(R.layout.item_tweet, parent, false)

        //return the viewholder with the view that we just inflated
        return ViewHolder(view)
    }

    //In charge of actually populating data into the item through the view holder
    override fun onBindViewHolder(holder: TweetsAdapter.ViewHolder, position: Int) {
        //Get the tweet that this item needs to inflate
        val tweet: Tweet = tweets.get(position)

        //set item views based on views and data model

        //user might be null if the json was not parsed successfully.
        holder.tvUserName.text = tweet.user?.name
        //Setting the body of the tweet. The "body" is from Tweet.kt.
        holder.tvTweetBody.text = tweet.body
        //Setting the time stamp for the tweet
        holder.tvTimeStamp.setText(tweet.time_stamp)

        //The last thing we need for here is the profile image.
        //We will be using the Glide library which would makes image loading very easy for us.
        //We need to put a context into it, the easiest way in this scenario is with the viewHolder (aka holder)
        //Load the user's public image url in it:
            //take the tweet, get the user object from it, load the image url into the profile image view
        Glide.with(holder.itemView).load(tweet.user?.publicImageUrl).into(holder.ivProfileImage)

    }

    //Tells our adapter how many views are going to be in our recyclerView
    override fun getItemCount(): Int {
        //return the size of our list of tweets
        return tweets.size
    }

    //For pull-to-refresh
    // Clean all elements of the recycler
    fun clear() {
        tweets.clear()
        notifyDataSetChanged()
    }

    // Add a list of items -- change to type used
    fun addAll(tweetList: List<Tweet>) {
        tweets.addAll(tweetList)
        notifyDataSetChanged()
    }

    //creating a ViewHolder class.
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        //Has to reference each of the views in the item_tweet.xml (aka item layout)
        val ivProfileImage = itemView.findViewById<ImageView>(R.id.ivProfileImage)
        val tvUserName = itemView.findViewById<TextView>(R.id.tvUsername)
        val tvTweetBody = itemView.findViewById<TextView>(R.id.tvTweetBody)
        //For TimeStamp
        val tvTimeStamp = itemView.findViewById<TextView>(R.id.tvTimeStamp)


    }


}