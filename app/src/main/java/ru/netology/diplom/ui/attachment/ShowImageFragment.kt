package ru.netology.diplom.ui.attachment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntegerRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.databinding.FragmentShowImageBinding
import ru.netology.diplom.util.loadImage

@AndroidEntryPoint
class ShowImageFragment : Fragment(R.layout.fragment_show_image) {

    private var _binding: FragmentShowImageBinding? = null
    private val binding get() = _binding!!
    private val navArgs by navArgs<ShowImageFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setColorAppBar(R.color.black)
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            _binding = FragmentShowImageBinding.bind(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navArgs.showImage?.let { attachment ->
            binding.image.loadImage(attachment.url, "media")
        }
    }

    override fun onDestroyView() {
        setColorAppBar(R.color.purple_500)
        _binding = null
        super.onDestroyView()
    }

    private fun setColorAppBar(@IntegerRes colorResource: Int) {
        val colorAppBar = ContextCompat.getColor(requireActivity(), colorResource)
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(ColorDrawable(colorAppBar))
    }
}