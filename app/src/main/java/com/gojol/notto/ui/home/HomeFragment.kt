package com.gojol.notto.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.gojol.notto.R
import com.gojol.notto.common.LabelEditType
import com.gojol.notto.common.NOTIFICATION_CHECK_WORKER_UNIQUE_ID
import com.gojol.notto.databinding.FragmentHomeBinding
import com.gojol.notto.model.data.LabelWithCheck
import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.ui.home.calendar.adapter.CalendarAdapter
import com.gojol.notto.ui.home.adapter.LabelAdapter
import com.gojol.notto.ui.home.adapter.LabelWrapperAdapter
import com.gojol.notto.ui.home.adapter.TodoAdapter
import com.gojol.notto.ui.home.util.DayClickListener
import com.gojol.notto.ui.home.util.MonthSwipeListener
import com.gojol.notto.ui.home.util.TodayClickListener
import com.gojol.notto.ui.home.util.todo.TodoItemTouchCallback
import com.gojol.notto.ui.home.util.TodoSwipeListener
import com.gojol.notto.ui.label.EditLabelActivity
import com.gojol.notto.ui.todo.TodoEditActivity
import com.gojol.notto.util.EditLabelDialogBuilder
import com.gojol.notto.util.SuccessButtonWorker
import com.gojol.notto.util.SuccessButtonWorker_AssistedFactory
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class HomeFragment : Fragment(), DayClickListener, MonthSwipeListener {

    private lateinit var binding: FragmentHomeBinding

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var labelAdapter: LabelAdapter
    private lateinit var labelWrapperAdapter: LabelWrapperAdapter
    private lateinit var todoAdapter: TodoAdapter

    private lateinit var todayClickListener: TodayClickListener
    private lateinit var todoSwipeListener: TodoSwipeListener

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

    override fun onResume() {
        super.onResume()

        initData()
    }

    override fun onClick(selectedDate: LocalDate) {
        val year = selectedDate.year
        val month = selectedDate.monthValue
        val date = selectedDate.dayOfMonth

        homeViewModel.updateDate(year, month, date)
    }

    override fun onSwipe() {
        updateViewPager()
    }

    fun setCalendarListener(calendarFragment: Fragment) {
        todayClickListener = calendarFragment as TodayClickListener
        todoSwipeListener = calendarFragment as TodoSwipeListener
    }

    private fun initRecyclerView() {
        calendarAdapter = CalendarAdapter(
            ::todayClickCallback,
            childFragmentManager,
            viewLifecycleOwner.lifecycle
        )
        labelAdapter = LabelAdapter(::labelTouchCallback)
        labelWrapperAdapter = LabelWrapperAdapter(labelAdapter, ::onClickLabelMenu)
        todoAdapter = TodoAdapter(::todoTouchCallback, ::todoEditButtonCallback)

        val concatAdapter: ConcatAdapter by lazy {
            val config = ConcatAdapter.Config.Builder().apply {
                setIsolateViewTypes(false)
            }.build()
            ConcatAdapter(config, calendarAdapter, labelWrapperAdapter, todoAdapter)
        }

        binding.rvHome.apply {
            adapter = concatAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    private fun onClickLabelMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.home_label_popup_menu, popup.menu)

        popup.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_create_label -> {
                    val fragmentManager = requireActivity().supportFragmentManager

                    val dialog = EditLabelDialogBuilder.builder(LabelEditType.CREATE, null).apply {
                        show(fragmentManager, "EditLabelDialogFragment")
                    }

                    fragmentManager.executePendingTransactions()

                    dialog.dialog?.apply {
                        setOnDismissListener {
                            homeViewModel.setDummyData()
                            dialog.dismiss()
                        }
                    }

                    true
                }
                R.id.menu_edit_label -> {
                    startActivity(Intent(context, EditLabelActivity::class.java))

                    true
                }
                else -> false
            }
        }

        popup.show()
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
            homeViewModel.setDummyData()
            calendarAdapter.setDate(it)
            updateViewPager()
        })

        homeViewModel.todoCreateButtonClicked.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { date ->
                startTodoCreateEditActivity(date)
            }
        })

        homeViewModel.todoEditButtonClicked.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { pair ->
                startTodoEditActivity(pair.first, pair.second)
            }
        })

        WorkManager.getInstance(requireContext())
            .getWorkInfosForUniqueWorkLiveData(NOTIFICATION_CHECK_WORKER_UNIQUE_ID)
            .observe(viewLifecycleOwner, { works ->
                works.forEach {
                    if(it.state == WorkInfo.State.SUCCEEDED) {
                        homeViewModel.updateTodoList()
                    }
                }
            })
    }

    private fun updateViewPager() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            calendarAdapter.notifyItemChanged(0)
        }
        handler.post(runnable)
    }

    private fun initData() {
        homeViewModel.setDummyData()
    }

    private fun initTodoListItemTouchListener() {
        val itemTouchHelper = ItemTouchHelper(TodoItemTouchCallback(todoAdapter))
        itemTouchHelper.attachToRecyclerView(binding.rvHome)
    }

    private fun todayClickCallback() {
        todayClickListener.onClick()
    }

    private fun todoTouchCallback(dailyTodo: DailyTodo) {
        homeViewModel.updateDailyTodo(dailyTodo)
        todoSwipeListener.onSwipe()
    }

    private fun todoEditButtonCallback(todo: Todo) {
        homeViewModel.updateNavigateToTodoEdit(todo)
    }

    private fun labelTouchCallback(labelWithCheck: LabelWithCheck) {
        homeViewModel.setLabelClickListener(labelWithCheck)
    }

    private fun startTodoCreateEditActivity(date: LocalDate) {
        val intent = Intent(activity, TodoEditActivity::class.java)
        intent.putExtra("date", date)
        startActivity(intent)
    }

    private fun startTodoEditActivity(todo: Todo, date: LocalDate) {
        val intent = Intent(activity, TodoEditActivity::class.java)
        intent.putExtra("todo", todo)
        intent.putExtra("date", date)
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        labelWrapperAdapter.onSaveState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            labelWrapperAdapter.onRestoreState(savedInstanceState)
        }
    }
}
