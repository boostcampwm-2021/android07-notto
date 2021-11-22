package com.gojol.notto.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
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
import com.gojol.notto.ui.home.CalendarFragment.Companion.DATE_CLICK_KEY
import com.gojol.notto.ui.home.adapter.CalendarAdapter
import com.gojol.notto.ui.home.adapter.LabelAdapter
import com.gojol.notto.ui.home.adapter.LabelWrapperAdapter
import com.gojol.notto.ui.home.adapter.TodoAdapter
import com.gojol.notto.ui.home.util.TodoItemTouchCallback
import com.gojol.notto.ui.todo.TodoEditActivity
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var labelAdapter: LabelAdapter
    private lateinit var labelWrapperAdapter: LabelWrapperAdapter
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(DATE_CLICK_KEY) { _, _ ->
            val year = CalendarFragment.selectedYear
            val month = CalendarFragment.selectedMonth
            val date = CalendarFragment.selectedDate

            updateSelectedDate(year, month, date)
        }
    }

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
        calendarAdapter = CalendarAdapter(parentFragmentManager, lifecycle)
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
            itemAnimator = null
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
                homeViewModel.date.value?.let { date ->
                    startTodoCreateActivity(todo, date)
                }
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
        setFragmentResult(TODO_SWIPE_KEY, bundleOf())
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

    private fun startTodoCreateActivity(todo: Todo, date: LocalDate) {
        val intent = Intent(activity, TodoEditActivity::class.java)
        intent.putExtra("todo", todo)
        intent.putExtra("date", date)
        startActivity(intent)
    }

    companion object {
        const val TODO_SWIPE_KEY = "todo_swipe"
    }
}
