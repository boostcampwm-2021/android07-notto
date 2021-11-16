package com.gojol.notto.ui.home

import android.content.Intent
import java.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.R
import com.gojol.notto.common.AdapterViewType
import com.gojol.notto.databinding.FragmentHomeBinding
import com.gojol.notto.model.data.LabelWithCheck
import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.ui.home.adapter.CalendarAdapter
import com.gojol.notto.ui.home.adapter.LabelAdapter
import com.gojol.notto.ui.home.adapter.LabelWrapperAdapter
import com.gojol.notto.ui.home.adapter.TodoAdapter
import com.gojol.notto.ui.home.util.TodoItemTouchCallback
import com.gojol.notto.ui.todo.TodoEditActivity
import com.gojol.notto.util.getDate
import com.gojol.notto.util.getMonth
import com.gojol.notto.util.getYear
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

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = homeViewModel

        initRecyclerView()
        initObserver()
        initTodoListItemTouchListener()
        initCalendar()
    }

    private fun initCalendar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val today = Calendar.getInstance()
            CalendarFragment.selectedYear = today.getYear()
            CalendarFragment.selectedMonth = today.getMonth()
            CalendarFragment.selectedDate = today.getDate()
        }

        // TODO *인터페이스, sharedviewmodel, eventbus
        CalendarFragment.callback = ::updateSelectedDate
    }

    private fun updateSelectedDate(year: Int, month: Int, date: Int) {
        homeViewModel.updateDate(year, month, date)
    }

    override fun onResume() {
        super.onResume()

        initData()
        homeViewModel.updateDate()
    }

    private fun initRecyclerView() {
        calendarAdapter = CalendarAdapter(requireActivity())
        labelAdapter = LabelAdapter(::labelTouchCallback)
        labelWrapperAdapter = LabelWrapperAdapter(labelAdapter)
        todoAdapter = TodoAdapter(::todoTouchCallback, ::todoEditButtonCallback)

        val concatAdapter: ConcatAdapter by lazy {
            val config = ConcatAdapter.Config.Builder().apply {
                setIsolateViewTypes(false)
            }.build()
            ConcatAdapter(config, calendarAdapter, labelWrapperAdapter, todoAdapter)
        }

        binding.rvHome.apply {
            adapter = concatAdapter
            layoutManager = getLayoutManager(concatAdapter)
            itemAnimator?.removeDuration = 0
            itemAnimator?.addDuration = 0
            itemAnimator?.changeDuration = 0
        }
    }

    private fun initObserver() {
        homeViewModel.todoList.observe(viewLifecycleOwner, {
            todoAdapter.submitList(it)
        })

        homeViewModel.labelList.observe(viewLifecycleOwner, {
            homeViewModel.updateTodoList(it)
            labelAdapter.submitList(it)
        })

        homeViewModel.date.observe(viewLifecycleOwner, {
            calendarAdapter.setDate(it)
            homeViewModel.setDummyData()
        })

        homeViewModel.isTodoCreateButtonClicked.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let {
                startTodoEditActivity()
            }
        })

        homeViewModel.todoEditButtonClicked.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { todo ->
                startTodoCreateActivity(todo)
            }
        })
    }

    private fun initData() {
        homeViewModel.setDummyData()
    }

    private fun initTodoListItemTouchListener() {
        val itemTouchHelper = ItemTouchHelper(TodoItemTouchCallback(todoAdapter))
        itemTouchHelper.attachToRecyclerView(binding.rvHome)
    }

    private fun getLayoutManager(adapter: ConcatAdapter): RecyclerView.LayoutManager {
        val layoutManager = GridLayoutManager(context, 7)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    AdapterViewType.CALENDAR.viewType -> 7
                    AdapterViewType.LABEL.viewType -> 1
                    AdapterViewType.TODO.viewType -> 7
                    else -> 7
                }
            }
        }

        return layoutManager
    }

    private fun todoTouchCallback(dailyTodo: DailyTodo) {
        homeViewModel.updateDailyTodo(dailyTodo)
        calendarAdapter.refreshAdapter()
    }

    private fun todoEditButtonCallback(todo: Todo) {
        homeViewModel.updateIsTodoEditButtonClicked(todo)
    }

    private fun labelTouchCallback(labelWithCheck: LabelWithCheck) {
        homeViewModel.setLabelClickListener(labelWithCheck)
    }

    private fun startTodoEditActivity() {
        val intent = Intent(activity, TodoEditActivity::class.java)
        startActivity(intent)
    }

    private fun startTodoCreateActivity(todo: Todo) {
        val intent = Intent(activity, TodoEditActivity::class.java)
        intent.putExtra("todo", todo)
        startActivity(intent)
    }
}
