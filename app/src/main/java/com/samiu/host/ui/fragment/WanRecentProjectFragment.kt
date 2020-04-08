package com.samiu.host.ui.fragment

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.samiu.base.ui.BaseVMFragment
import com.samiu.base.ui.viewBinding
import com.samiu.host.R
import com.samiu.host.databinding.FragmentWanRecentProjectBinding
import com.samiu.host.global.RECENT_PROJECT
import com.samiu.host.global.toBrowser
import com.samiu.host.ui.adapter.WanProjectAdapter
import com.samiu.host.viewmodel.WanProjectViewModel
import kotlinx.android.synthetic.main.fragment_wan_recent_project.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

/**
 * @author Samiu 2020/3/5
 */
class WanRecentProjectFragment :
    BaseVMFragment<WanProjectViewModel>(R.layout.fragment_wan_recent_project) {
    private val binding by viewBinding(FragmentWanRecentProjectBinding::bind)

    private val mViewModelWan: WanProjectViewModel by viewModel()
    private lateinit var adapterWan: WanProjectAdapter
    private var currentPage by Delegates.notNull<Int>()

    override fun initView() {
        initRecyclerView()
        LiveEventBus    //refresh data
            .get(RECENT_PROJECT, Int::class.java)
            .observe(this, Observer { refreshData(it) })
    }

    override fun initData() {
        refreshData(-1)
    }

    private fun refreshData(type: Int) {
        when (type) {
            -1 -> { //onRefresh
                currentPage = 0
                adapterWan.clearAll()
                mViewModelWan.getRecentProjects(currentPage)
            }
            1 -> {  //onLoadMore
                currentPage += 1
                mViewModelWan.getRecentProjects(currentPage)
            }
        }
    }

    override fun startObserve() = mViewModelWan.run {
        mRecentProjects.observe(this@WanRecentProjectFragment, Observer { adapterWan.addAll(it) })
    }

    private fun initRecyclerView() {
        adapterWan = WanProjectAdapter(context)
        recent_project_rv.layoutManager = LinearLayoutManager(context)
        recent_project_rv.adapter = adapterWan
        adapterWan.setOnItemClick { url -> toBrowser(this, url) }
    }
}
