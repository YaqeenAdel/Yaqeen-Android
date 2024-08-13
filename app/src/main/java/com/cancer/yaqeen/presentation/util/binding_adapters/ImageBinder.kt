package com.cancer.yaqeen.presentation.util.binding_adapters

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.cancer.yaqeen.R


fun bindImage(imageView: ImageView, imgUrl: String?, placeHolderIsAppIcon: Boolean = true) {
    val placeHolderId = if (placeHolderIsAppIcon) R.drawable.logo_launcher else R.color.white_gray
    if (imgUrl.isNullOrEmpty()) {
        imageView.setImageResource(placeHolderId)
    } else {
        try {
            imageView.clipToOutline = true
            Glide.with(imageView.context)
                .load(imgUrl)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.background_view_gray)
                )
                .error(placeHolderId)
                .placeholder(placeHolderId)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
        } catch (e: Exception) {
        }
    }
}

@BindingAdapter("imageURI")
fun bindImageURI(imageView: ImageView, uri: Uri?) {
    imageView.setImageURI(uri)
}

@BindingAdapter("resourceId")
fun bindResourceImage(imageView: ImageView, resourceId: Int) {
    Glide.with(imageView.context)
        .load(resourceId)
        .into(imageView)
}
