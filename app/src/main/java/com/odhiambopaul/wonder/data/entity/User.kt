package com.odhiambopaul.wonder.data.entity

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bumptech.glide.Glide
import com.odhiambopaul.wonder.R


@Entity
data class User(
    @PrimaryKey(autoGenerate = false)
    val name: String,
    val gender: String,
    val latitude: String,
    val longitude: String,
    val image: String

) {
    companion object {
        @JvmStatic
        @BindingAdapter("android:imageUri")
        fun bindImage(view: View, imageUri: String) {
            val image = view.findViewById<ImageView>(R.id.imageView)
            Glide.with(view)
                .load(imageUri)
                .thumbnail(Glide.with(view).load(R.drawable.ic_launcher_foreground))
                .into(image)
        }
    }

    fun getLocation(): String {
        return "$latitude $longitude"
    }
}