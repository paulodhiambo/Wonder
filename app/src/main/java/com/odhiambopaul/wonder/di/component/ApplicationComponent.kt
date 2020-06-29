package com.odhiambopaul.wonder.di.component

import com.odhiambopaul.wonder.di.module.ApiModule
import com.odhiambopaul.wonder.di.module.DataBaseModule
import com.odhiambopaul.wonder.ui.home.HomeActivity
import com.odhiambopaul.wonder.ui.users.UserListActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ApiModule::class, DataBaseModule::class]
)
interface ApplicationComponent {

    fun inject(homeActivity: HomeActivity)

    fun inject(userListActivity: UserListActivity)
//    @Component.Factory
//    interface Factory {
//        fun create(@BindsInstance app: App): ApplicationComponent
//    }
}