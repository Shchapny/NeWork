package ru.netology.diplom.ui

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.IdRes
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
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_auth, menu)

                menu.let {
                    it.setGroupVisible(R.id.unauthenticated, !authViewModel.authenticated)
                    it.setGroupVisible(R.id.authenticated, authViewModel.authenticated)
                }
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
                                findNavController(R.id.nav_host_fragment_container).navigate(R.id.postFeedFragment)
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

        val bottomNavigation = binding.navigation.apply {
            itemIconTintList = null
            background = null
            menu.getItem(2).isEnabled = false
        }

        val bottomGroup = binding.groupBottomAppBar

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.postFeedFragment, R.id.eventFeedFragment, R.id.jobFeedFragment -> bottomGroup.visibility =
                    View.VISIBLE
                else -> bottomGroup.visibility = View.GONE
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

        binding.fab.setOnClickListener {
            bottomNavigation.menu.apply {
                if (!authViewModel.authenticated) {
                    findNavController(R.id.nav_host_fragment_container).navigate(R.id.authenticationFragment)
                } else {
                    when {
                        getItem(0).isChecked -> fabButtonNavigation(R.id.postNewOrEditFragment)
                        getItem(1).isChecked -> fabButtonNavigation(R.id.eventNewOrEditFragment)
                        getItem(3).isChecked -> fabButtonNavigation(R.id.jobNewOrEditFragment)
                    }
                }
            }
        }

        authViewModel.dataAuth.observe(this) {
            invalidateOptionsMenu()
            authViewModel.loadUser(it?.id ?: 0L)
        }

        authViewModel.user.observe(this) { user ->
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

    private fun fabButtonNavigation(@IdRes id: Int) {
        findNavController(R.id.nav_host_fragment_container).navigate(id)
    }
}