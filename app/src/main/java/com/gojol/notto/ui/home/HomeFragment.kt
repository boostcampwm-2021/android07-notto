package com.gojol.notto.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.R
import com.gojol.notto.databinding.FragmentHomeBinding
import com.gojol.notto.model.database.label.Label
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var labelAdapter: LabelAdapter
    private lateinit var labelWrapperAdapter: LabelWrapperAdapter
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = homeViewModel

        homeViewModel.todoList.observe(viewLifecycleOwner, {
            todoAdapter.submitList(it)
        })

        CoroutineScope(Dispatchers.IO).launch {
            context?.let {
                val labelList = Dummy(it).getLabel()
                    .map { label -> LabelWithCheck(label, false) }
                    .toMutableList()
                labelList.add(0, LabelWithCheck(Label(0, "전체"), true))
                labelAdapter.submitList(labelList)
            }
        }
    }

    private fun initRecyclerView() {
        calendarAdapter = CalendarAdapter(homeViewModel)
        labelAdapter = LabelAdapter()
        labelWrapperAdapter = LabelWrapperAdapter(labelAdapter)
        todoAdapter = TodoAdapter()

        val concatAdapter: ConcatAdapter by lazy {
            val config = ConcatAdapter.Config.Builder().apply {
                setIsolateViewTypes(false)
            }.build()
            ConcatAdapter(config, calendarAdapter, labelWrapperAdapter, todoAdapter)
        }

        binding.rvHome.apply {
            adapter = concatAdapter
            layoutManager = getLayoutManager(concatAdapter)
        }
    }

    private fun getLayoutManager(adapter: ConcatAdapter) : RecyclerView.LayoutManager{
        val layoutManager = GridLayoutManager(context, 7)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    CalendarAdapter.VIEW_TYPE -> 7
                    LabelAdapter.VIEW_TYPE -> 1
                    TodoAdapter.VIEW_TYPE -> 7
                    else -> 7
                }
            }
        }

        return layoutManager
    }
}
