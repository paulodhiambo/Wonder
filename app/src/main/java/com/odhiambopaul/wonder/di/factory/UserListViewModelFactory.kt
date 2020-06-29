package com.odhiambopaul.wonder.di.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.odhiambopaul.wonder.repository.DatabaseRepository
import com.odhiambopaul.wonder.repository.NetworkRepository
import com.odhiambopaul.wonder.ui.users.UserListViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class UserListViewModelFactory @Inject constructor(
    private var networkRepository: NetworkRepository,
    private var databaseRepository: DatabaseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserListViewModel(networkRepository, databaseRepository) as T
    }

}