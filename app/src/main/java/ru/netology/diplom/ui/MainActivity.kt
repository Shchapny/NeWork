package ru.netology.diplom.ui

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.authorization.AppAuth
import ru.netology.diplom.databinding.ActivityMainBinding
import ru.netology.diplom.viewmodel.auth.AuthViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var appAuth: AppAuth

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_auth, menu)
                Log.d("menu", "onCreateMenu 1")

                menu.let {
                    it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
                    Log.d("menu", "${!viewModel.authenticated} 1")
                    it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
                    Log.d("menu", "${viewModel.authenticated} 2")
                }
                Log.d("menu", "onCreateMenu 2")
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.signin -> {
                        findNavController(R.id.nav_host_fragment_container).navigate(R.id.authenticationFragment)
                        true
                    }
                    R.id.signup -> {
                        findNavController(R.id.nav_host_fragment_container).navigate(R.id.registrationFragment)
                        true
                    }
                    R.id.signout -> {
                        MaterialAlertDialogBuilder(this@MainActivity)
                            .setMessage(R.string.refine_signout)
                            .setPositiveButton(R.string.sign_out) { _, _ ->
                                appAuth.removeAuth()
                                return@setPositiveButton
                            }
                            .setNegativeButton(R.string.cancel) { _, _ ->
                                return@setNegativeButton
                            }.show()
                        true
                    }
                    else -> false
                }
            }
        })

        val bottomNavigation = binding.navigation
        bottomNavigation.itemIconTintList = null
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.postFeedFragment, R.id.eventFeedFragment, R.id.jobFeedFragment -> bottomNavigation.visibility =
                    View.VISIBLE
                else -> bottomNavigation.visibility = View.GONE
            }
        }
        val appBar = AppBarConfiguration(
            setOf(
                R.id.postFeedFragment, R.id.eventFeedFragment, R.id.jobFeedFragment
            )
        )
        val itemIconUser = bottomNavigation.menu.findItem(R.id.user)
        setupActionBarWithNavController(navController, appBar)
        bottomNavigation.setupWithNavController(navController)

        viewModel.dataAuth.observe(this) {
            invalidateOptionsMenu()
            Log.d("menu", "${invalidateOptionsMenu()} invalidate")
            viewModel.loadUser(it?.id ?: 0L)
        }

        viewModel.user.observe(this) { user ->
            if (user?.avatar?.isNotBlank() == true) {
                Glide.with(this@MainActivity)
                    .asBitmap()
                    .load("${user.avatar}")
                    .transform(CircleCrop())
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            itemIconUser.icon = BitmapDrawable(resources, resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            } else {
                itemIconUser.setIcon(R.drawable.ic_person)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_container)
        return navController.navigateUp()
    }
}