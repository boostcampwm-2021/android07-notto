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
import androidx.recyclerview.widget.SimpleItemAnimator
import com.gojol.notto.R
import com.gojol.notto.common.AdapterViewType
import com.gojol.notto.common.LabelEditType
import com.gojol.notto.databinding.FragmentHomeBinding
import com.gojol.notto.model.data.LabelWithCheck
import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.ui.home.CalendarFragment.Companion.DATE_CLICK_BUNDLE_KEY
import com.gojol.notto.ui.home.CalendarFragment.Companion.DATE_CLICK_KEY
import com.gojol.notto.ui.home.CalendarFragment.Companion.UPDATE_HEIGHT_KEY
import com.gojol.notto.ui.home.adapter.CalendarAdapter
import com.gojol.notto.ui.home.adapter.LabelAdapter
import com.gojol.notto.ui.home.adapter.LabelWrapperAdapter
import com.gojol.notto.ui.home.adapter.TodoAdapter
import com.gojol.notto.ui.home.util.TodoItemTouchCallback
import com.gojol.notto.ui.label.EditLabelActivity
import com.gojol.notto.ui.todo.TodoEditActivity
import com.gojol.notto.util.EditLabelDialogBuilder
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

        setFragmentResultListener(DATE_CLICK_KEY) { _, bundle ->
            val selectedDate = bundle.get(DATE_CLICK_BUNDLE_KEY) as LocalDate
            val year = selectedDate.year
            val month = selectedDate.monthValue
            val date = selectedDate.dayOfMonth

            homeViewModel.updateDate(year, month, date)
        }

        setFragmentResultListener(UPDATE_HEIGHT_KEY) { _, _ ->
            updateViewPager()
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

    override fun onResume() {
        super.onResume()

        initData()
    }

    private fun initRecyclerView() {
        calendarAdapter = CalendarAdapter(parentFragmentManager, lifecycle)
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
            layoutManager = getLayoutManager(concatAdapter)
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

    companion object {
        const val TODO_SWIPE_KEY = "todo_swipe"
    }
}
