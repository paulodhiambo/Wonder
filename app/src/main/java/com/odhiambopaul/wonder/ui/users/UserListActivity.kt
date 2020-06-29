package com.odhiambopaul.wonder.ui.users

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.odhiambopaul.wonder.App
import com.odhiambopaul.wonder.R
import com.odhiambopaul.wonder.databinding.ActivityUserListBinding
import com.odhiambopaul.wonder.di.factory.UserListViewModelFactory
import javax.inject.Inject

class UserListActivity : AppCompatActivity() {
    @Inject
    lateinit var userListViewModelFactory: UserListViewModelFactory

    private lateinit var userListViewModel: UserListViewModel

    private lateinit var binding: ActivityUserListBinding
    private val userAdapter by lazy {
        UsersAdapter()
    }

    private fun injectDagger() {
        App.instance.applicationComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectDagger()
        userListViewModel =
            ViewModelProvider(this, userListViewModelFactory).get(UserListViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_list)
        userListViewModel.getUsers()
        userListViewModel.users.observe(this, Observer { users ->
            //observe
            binding.userRecycler.apply {
                setHasFixedSize(true)
                adapter = userAdapter
                layoutManager = LinearLayoutManager(this@UserListActivity)
                userAdapter.addItems(users)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_all_users -> {
                deleteAllUsers()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteAllUsers() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete all Users ")
            .setMessage("You are about to delete all users")
            .setPositiveButton("Delete All") { _, _ ->
                userListViewModel.deleteAllUsers()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
        builder.create()
    }
}
