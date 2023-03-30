package com.dev.nycschools.screens.schoolDetails


import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dev.nycschools.R
import com.dev.nycschools.databinding.FragmentDetailsActivityBinding
import com.dev.nycschools.models.school.SchoolSatScore
import com.dev.nycschools.platform.BaseFragment
import com.dev.nycschools.platform.BaseViewModelFactory
import com.dev.nycschools.platform.LiveDataWrapper
import com.dev.nycschools.screens.school.SchoolActivity
import com.dev.nycschools.screens.school.SchoolActivityViewModel
import com.dev.nycschools.screens.school.SchoolUseCase
import com.dev.nycschools.screens.schoolDetails.SchoolDetailsFragmentArgs.Companion.fromBundle
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.android.inject

/**
 * School Fragment.
 * Handles UI.
 * Fires rest api initiation.
 */
class SchoolDetailsFragment : BaseFragment() {

    private lateinit var _binding: FragmentDetailsActivityBinding

    //---------------Class variables---------------//

    private val mSchoolUseCase: SchoolUseCase by inject()
    private val mBaseViewModelFactory: BaseViewModelFactory =
        BaseViewModelFactory(Dispatchers.Main, Dispatchers.IO, mSchoolUseCase)
    private val tag = SchoolDetailsFragment::class.java.simpleName

    private val mViewModel: SchoolActivityViewModel by lazy {
        ViewModelProvider(this, mBaseViewModelFactory)[SchoolActivityViewModel::class.java]
    }

    //---------------Life Cycle---------------//

    private val selectedSchool by lazy {
        fromBundle(requireArguments()).selectedSchool
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDetailsActivityBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showUpButton()

//        //Start observing the targets
        this.mViewModel.mSchoolSatScoreResponse.observe(
            viewLifecycleOwner, this.mSchoolSatScoreResponse
        )
        this.mViewModel.mLoadingLiveData.observe(viewLifecycleOwner, this.loadingObserver)

        selectedSchool.dbn?.let { this.mViewModel.requestSchoolSatScoreData(it) }
    }

    //---------------Observers---------------//
    private val mSchoolSatScoreResponse =
        Observer<LiveDataWrapper<ArrayList<SchoolSatScore>>> { result ->
            when (result?.responseStatus) {
                LiveDataWrapper.RESPONSESTATUS.LOADING -> {
                    // Loading data
                }
                LiveDataWrapper.RESPONSESTATUS.ERROR -> {
                    // Error for http request
                    logD(tag, "LiveDataResult.Status.ERROR = ${result.response}")
                    binding.errorHolder.visibility = View.VISIBLE
                    showToast("Error in getting data")

                }
                LiveDataWrapper.RESPONSESTATUS.SUCCESS -> {
                    // Data from API
                    logD(tag, "LiveDataResult.Status.SUCCESS = ${result.response}")
                    val listItems = result.response as ArrayList<SchoolSatScore>
                    processData(listItems)
                }
                else -> {}
            }
        }

    override fun getLayoutId(): Int = R.layout.fragment_details_activity

    /**
     * Handle success data
     */
    @SuppressLint("SetTextI18n")
    private fun processData(listItems: ArrayList<SchoolSatScore>) {
        logD(tag, "processData called with SchoolSatScore ${listItems.size}")
        logD(tag, "processData SchoolSatScore =  $listItems")

        val refresh = Handler(Looper.getMainLooper())
        refresh.post {
            binding.schoolName.text = selectedSchool.schoolName
            binding.desc.text =
                getString(R.string.desc) + selectedSchool.overviewParagraph + "\n\n" + selectedSchool.academicopportunities1 + "\n\n" + selectedSchool.academicopportunities2
            if (listItems.size == 1) {
                val schoolDetails = listItems[0]
                binding.satTestTaken.text =
                    getString(R.string.sat_test_taken) + schoolDetails.numOfSatTestTakers
                binding.readingAvg.text =
                    getString(R.string.reading_avg) + schoolDetails.satCriticalReadingAvgScore
                binding.mathAvg.text = getString(R.string.math_avg) + schoolDetails.satMathAvgScore
                binding.writingAvg.text =
                    getString(R.string.writing_avg) + schoolDetails.satWritingAvgScore
            } else {
                binding.satTestTaken.text = getString(R.string.not_available)
                binding.readingAvg.visibility = View.GONE
                binding.mathAvg.visibility = View.GONE
                binding.writingAvg.visibility = View.GONE
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

    private fun showUpButton() {
        (activity as SchoolActivity).showUpButton()
    }

}
