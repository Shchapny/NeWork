package ru.netology.diplom.util

import android.content.Intent
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import ru.netology.diplom.R

fun loadFromGallery(fragment: Fragment, maxSize: Int, intent: (Intent) -> Unit) {
    ImagePicker.with(fragment)
        .crop()
        .compress(maxSize)
        .provider(ImageProvider.GALLERY)
        .galleryMimeTypes(arrayOf("image/png", "image/jpeg"))
        .createIntent(intent)
}

fun loadFromCamera(fragment: Fragment, maxSize: Int, intent: (Intent) -> Unit) {
    ImagePicker.with(fragment)
        .crop()
        .compress(maxSize)
        .provider(ImageProvider.CAMERA)
        .createIntent(intent)
}

fun ImageView.loadImage(url: String, argument: String) {
    when (argument) {
        "avatars" -> Glide.with(this)
            .load(url)
            .placeholder(R.drawable.ic_avatar)
            .circleCrop()
            .timeout(10_000)
            .into(this)
        "media" -> Glide.with(this)
            .load(url)
            .timeout(10_000)
            .into(this)
    }
}

