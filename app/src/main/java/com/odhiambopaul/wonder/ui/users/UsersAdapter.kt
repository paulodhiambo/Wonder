package com.odhiambopaul.wonder.ui.users

import com.odhiambopaul.wonder.R
import com.odhiambopaul.wonder.data.entity.User
import com.odhiambopaul.wonder.databinding.UserItemBinding
import com.odhiambopaul.wonder.di.ui.BaseRecyclerViewAdapter

class UsersAdapter : BaseRecyclerViewAdapter<User, UserItemBinding>() {
    override fun getLayout(): Int {
        return R.layout.user_item
    }

    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<UserItemBinding>,
        position: Int
    ) {
        holder.binding.user = items[position]
        //onclick event
        holder.binding.root.setOnClickListener {
            listener?.invoke(it, items[position], position)
        }
    }
}