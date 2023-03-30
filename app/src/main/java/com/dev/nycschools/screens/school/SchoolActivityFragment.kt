package com.dev.nycschools.screens.school


import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.nycschools.R
import com.dev.nycschools.databinding.FragmentMainActivityBinding
import com.dev.nycschools.models.school.School
import com.dev.nycschools.platform.BaseFragment
import com.dev.nycschools.platform.BaseViewModelFactory
import com.dev.nycschools.platform.LiveDataWrapper
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.android.inject

/**
 * School Fragment.
 * Handles UI.
 * Fires rest api initiation.
 */
class SchoolActivityFragment : BaseFragment(), ItemClickListener {

    private lateinit var _binding: FragmentMainActivityBinding
    var isLoading = false

    //---------------Class variables---------------//

    private val mSchoolUseCase: SchoolUseCase by inject()
    private val mBaseViewModelFactory: BaseViewModelFactory =
        BaseViewModelFactory(Dispatchers.Main, Dispatchers.IO, mSchoolUseCase)
    private val mTag = SchoolActivityFragment::class.java.simpleName
    private lateinit var mRecyclerViewAdapter: SchoolRecyclerViewAdapter

    private val mViewModel: SchoolActivityViewModel by lazy {
        ViewModelProvider(this, mBaseViewModelFactory)[SchoolActivityViewModel::class.java]
    }

    //---------------Life Cycle---------------//


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding

    override fun onSaveInstanceState(outState: Bundle) {
        mViewModel.recyclerViewState =
            binding.landingListRecyclerView.layoutManager?.onSaveInstanceState()
        super.onSaveInstanceState(outState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainActivityBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideUpButton()
        //Start observing the targets
        this.mViewModel.mSchoolResponse.observe(viewLifecycleOwner, this.mDataObserver)
        this.mViewModel.mLoadingLiveData.observe(viewLifecycleOwner, this.loadingObserver)
        this.mViewModel.resetLoadMoreData()
        mRecyclerViewAdapter = SchoolRecyclerViewAdapter(requireActivity(), arrayListOf(), this)
        binding.landingListRecyclerView.adapter = mRecyclerViewAdapter
        binding.landingListRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        binding.landingListRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val manager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition: Int = manager.findLastVisibleItemPosition()
                val totalItemCount = recyclerView.layoutManager?.itemCount?.minus(1)
                super.onScrolled(recyclerView, dx, dy)
                logD(
                    mTag,
                    "Scrolling lastVisibleItemPosition: $lastVisibleItemPosition - totalItemCount = $totalItemCount"
                )
                if (lastVisibleItemPosition == totalItemCount) {
                    if (!isLoading) {
                        isLoading = true
                        mRecyclerViewAdapter.startLoadMoreProgress()
                        mViewModel.requestLoadMoreData()
                    }
                }
            }
        })
        this.mViewModel.requestLoginActivityData()


    }

    //---------------Observers---------------//
    private val mDataObserver = Observer<LiveDataWrapper<ArrayList<School>>> { result ->
        when (result?.responseStatus) {
            LiveDataWrapper.RESPONSESTATUS.LOADING -> {
                // Loading data
            }
            LiveDataWrapper.RESPONSESTATUS.ERROR -> {
                // Error for http request
                logD(mTag, "LiveDataResult.Status.ERROR = ${result.response}")
                binding.errorHolder.visibility = View.VISIBLE
                showToast("Error in getting data")

            }
            LiveDataWrapper.RESPONSESTATUS.SUCCESS -> {
                // Data from API
                logD(mTag, "LiveDataResult.Status.SUCCESS = ${result.response}")
                val listItems = result.response as ArrayList<School?>
                if (!mViewModel.isLoadMoreData) {
                    processData(listItems)
                } else {
                    processLoadMoreData(listItems, mViewModel.newDataCount)
                }

            }
            else -> {}
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_main_activity

    /**
     * Handle success data
     * Handling Recycler View Retain Scroll state
     */
    private fun processData(listItems: ArrayList<School?>) {
        logD(mTag, "processData called with data ${listItems.size}")
        logD(mTag, "processData listItems =  $listItems")

        val refresh = Handler(Looper.getMainLooper())
        refresh.post {
            mRecyclerViewAdapter.updateListItems(
                listItems
            )
            // Handling Recycler View Retain Scroll state
            mViewModel.recyclerViewState?.let {
                binding.landingListRecyclerView.layoutManager?.onRestoreInstanceState(it)
                mViewModel.recyclerViewState = null // to prevent state restoration on list updates
            }
        }


    }

    /**
     * Handle success data
     */
    private fun processLoadMoreData(listItems: ArrayList<School?>, newDataCount: Int) {
        if (isLoading) {
            val refresh = Handler(Looper.getMainLooper())
            refresh.post {
                mRecyclerViewAdapter.stopLoadMoreProgress(newDataCount)
                mRecyclerViewAdapter.updateMoreListItems(listItems, newDataCount)
                isLoading = false
            }

        }
    }

    /**
     * Hide / show loader
     */
    private val loadingObserver = Observer<Boolean> { visible ->
        // Show hide progress bar
        if (visible) {
            binding.progressCircular.visibility = View.VISIBLE
        } else {
            binding.progressCircular.visibility = View.INVISIBLE
        }
    }

    private fun hideUpButton() {
        (activity as SchoolActivity).hideUpButton()
    }

    override fun onItemClick(view: View, school: School) {
        val bundle = bundleOf(
            "selectedSchool" to school
        )
        Navigation.findNavController(view)
            .navigate(R.id.action_nav_home_to_nav_school_details, bundle)

        mViewModel.recyclerViewState =
            binding.landingListRecyclerView.layoutManager?.onSaveInstanceState()
    }


}

interface ItemClickListener {
    fun onItemClick(view: View, school: School)
}
