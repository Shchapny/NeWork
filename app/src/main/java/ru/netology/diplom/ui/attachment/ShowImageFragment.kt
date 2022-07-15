package ru.netology.diplom.ui.attachment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.BuildConfig
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
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            _binding = FragmentShowImageBinding.bind(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navArgs.showImage?.let { attachment ->
            binding.image.loadImage(BuildConfig.BASE_URL, "media", attachment.url)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}