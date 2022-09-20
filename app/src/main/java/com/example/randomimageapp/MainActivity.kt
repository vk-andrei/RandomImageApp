package com.example.randomimageapp

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.randomimageapp.databinding.ActivityMainBinding
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private var useKeyword: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivTest.layoutParams.width = resources.getDimensionPixelSize(R.dimen.image_width)
        binding.ivTest.layoutParams.height = resources.getDimensionPixelSize(R.dimen.image_height)


        binding.btnGetRandomImage.setOnClickListener {
            //onGetRandomImagePressed()
            onGetRandomImagePressedWithProgressBar()
        }
        /**
        Обработчик на кнопку в клавиатуре (в данныом случае SEARCH)
        android:imeOptions="actionSearch"
         **/
        binding.etKeyword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                return@setOnEditorActionListener onGetRandomImagePressedWithProgressBar()
            }
            return@setOnEditorActionListener false
        }
        /**********************************************************/

        binding.checkBoxUseKeyword.setOnClickListener {
            this.useKeyword = binding.checkBoxUseKeyword.isChecked
            Log.d("TAG", "useKeyword = $useKeyword")
            updateUi()
        }
        binding.checkBoxUseKeyword.setOnCheckedChangeListener { _, isChecked ->
            // TODO: этот способ отличается от другого тем, что этот реагирует и на ПРОГРАММНОЕ изменение чекбокса
        }

        updateUi()

    }

    private fun updateUi() = with(binding) {
        checkBoxUseKeyword.isChecked = useKeyword
        if (useKeyword) {
            etKeyword.visibility = View.VISIBLE
        } else {
            etKeyword.visibility = View.GONE
        }
    }

    private fun onGetRandomImagePressed(): Boolean {
        val keyword = binding.etKeyword.text.toString()
        if (useKeyword && keyword.isBlank()) {
            binding.etKeyword.error = "Keyword is empty"
            return true  // НЕ ЗАКРЫВАЕТСЯ КЛАВИАТУРА
        }

        val encodeKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8.name())
        Glide.with(this)
            //.load("https://source.unsplash.com/random/800x600?$encodeKeyword")
            .load("https://source.unsplash.com/random/800x600?${if (useKeyword) "?$encodeKeyword" else ""}")
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(R.drawable.ic_baseline_cloud_download_24) // во время обращения к серверу отображается
            .into(binding.ivTest)
        return false // ЗАКРЫВАЕТСЯ КЛАВИАТУРА
    }

    private fun onGetRandomImagePressedWithProgressBar(): Boolean {
        val keyword = binding.etKeyword.text.toString()
        if (useKeyword && keyword.isBlank()) {
            binding.etKeyword.error = "Keyword is empty"
            return true  // НЕ ЗАКРЫВАЕТСЯ КЛАВИАТУРА
        }

        binding.progressBar.visibility = View.VISIBLE
        val encodeKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8.name())
        Glide.with(this)
            //.load("https://source.unsplash.com/random/800x600?$encodeKeyword")
            .load("https://source.unsplash.com/random/800x600?${if (useKeyword) "?$encodeKeyword" else ""}")
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)

            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    return false
                }
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    return false
                }
            })

            .into(binding.ivTest)
        return false // ЗАКРЫВАЕТСЯ КЛАВИАТУРА
    }

}
