package com.cancer.yaqeen.presentation.util.binding_adapters

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.cancer.yaqeen.R


@BindingAdapter("imageUrl")
fun bindImage(imageView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        if (it.isNotEmpty()) {
            try {
                imageView.clipToOutline = true
                Glide.with(imageView.context)
                    .load(it)
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.background_view_gray)
                    )
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView)
            } catch (e: Exception) {
            }
        }
        else imageView.setImageResource(R.color.silver_medal)
    }
}

@BindingAdapter("imageURI")
fun bindImageURI(imageView: ImageView, uri: Uri?){
    imageView.setImageURI(uri)
}

@BindingAdapter("resourceId")
fun bindResourceImage(imageView: ImageView, resourceId: Int){
    Glide.with(imageView.context)
        .load(resourceId)
        .into(imageView)
}
