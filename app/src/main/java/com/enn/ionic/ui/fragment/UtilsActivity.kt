package com.enn.ionic.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseActivity
import com.enn.base.data.getValue
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.base.ktx.startActivity
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.base.util.launchAndCollectIn
import com.enn.base.util.launchWithLoading
import com.enn.base.util.launchWithLoadingAndCollect
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.adapter.OnChildItemChildClickListener
import com.enn.ionic.adapter.UtilsAdapter
import com.enn.ionic.adapter.UtilsHeadAdapter
import com.enn.ionic.bean.HqwModelVO
import com.enn.ionic.bean.UtilsBean
import com.enn.ionic.databinding.FragmentUtilsBinding
import com.enn.ionic.databinding.FragmentUtilsHeaderBinding
import com.enn.ionic.ui.activity.WebActivityNew
import com.enn.ionic.utils.launchNoLoadingNoBase
import com.enn.ionic.vm.UtilsViewModel
import com.enn.network.toast

class UtilsActivity : BaseActivity(R.layout.fragment_utils), OnItemDragListener {

    private val mViewModel by viewModels<UtilsViewModel>()
    private val mBinding: FragmentUtilsBinding by binding()
    private lateinit var mBindingHead: FragmentUtilsHeaderBinding

    //    private val args: UtilsFragmentArgs by navArgs()
    private lateinit var utilsAdapter: UtilsAdapter
    private var isEditable: Boolean = false
    private lateinit var firstUtils: ArrayList<HqwModelVO>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.themeAndColorStatusBar(this)
        initView()
        initUtilsObserver()
        launchWithLoading {
            this.getValue("phone", ConstantObject.PHONE).let {
                mViewModel.requestUtilsData(it)
            }
        }
    }

    private fun initView() {
        firstUtils =
            intent.getParcelableArrayListExtra<HqwModelVO>("firstUtils") as ArrayList<HqwModelVO>
        mBinding.utilsIm.setOnClickListener {
            finish()
        }
        //应用列表
        utilsAdapter = UtilsAdapter(R.layout.item_utils_fragment, null)
        mBindingHead = FragmentUtilsHeaderBinding.inflate(layoutInflater)
        utilsAdapter.addHeaderView(mBindingHead.root)
        mBinding.utilsRecy.adapter = utilsAdapter.apply {

            setOnChildItemChildClickListener(object : OnChildItemChildClickListener<HqwModelVO> {
                override fun onItemChildClick(
                    adapter: BaseQuickAdapter<*, *>,
                    view: View,
                    parentPosition: Int,
                    position: Int,
                    item: HqwModelVO
                ) {
                    when (view.id) {
                        R.id.item_utils_recy_li -> {
//                            编辑状态不能点击
                            if (isEditable) {
//                                addToHead(adapter, position, item)
                                return
                            }
                            val item: HqwModelVO = adapter.getItem(position) as HqwModelVO
                            val linker: String? =
                                when (item.modelId) {
                                    ConstantObject.GAS_PLAN_ID -> ConstantObject.GAS_PLAN_URL
//                            ConstantObject.GAS_CHECK_ID -> ConstantObject.GAS_CHECK_URL
                                    else -> item.linkUrl
                                }
                            when (item.modelId) {
                                ConstantObject.PAY_ID -> {
                                    getSysTimeIsLimit {
                                        toWeb(linker, item)
                                    }
                                }
                                else -> {
                                    toWeb(linker, item)
                                }
                            }


                        }
                        R.id.item_utils_recy_im_right_layer -> {
                            addToHead(adapter, position, item)
                        }
                        else -> {}
                    }

                }
            })
        }
//        头部布局
        mBindingHead.utilsHeaderText2.text = "已收起${firstUtils.size?.minus(1)}个常用功能"
        mBindingHead.utilsHeaderRecy.adapter =
            UtilsHeadAdapter(
                R.layout.item_utils_head,
                mViewModel.getUtilsHeaderLisr(firstUtils)
            ).apply {
                setOnItemClickListener { adapter, view, position ->
                    if (adapter.data.size == 1) {
                        toast("最少保留一项常用功能~")
                        return@setOnItemClickListener
                    }
                    utilsAdapter.toEditOne((adapter.getItem(position) as HqwModelVO).modelId, true)
                    adapter.removeAt(position)
                }


            }
        mBindingHead.utilsHeaderBtn.setOnCheckedChangeListener { button, b ->
//            更新编辑状态
            isEditable = b
            toEdit(b)
            if (b) {
                mBindingHead.utilsHeaderBtn.text = "保存"
                mBindingHead.utilsHeaderText2.text = "拖动可调整功能顺序"
                mBindingHead.utilsHeaderRecy.visibility = View.VISIBLE
            } else {
                mBindingHead.utilsHeaderBtn.text = "管理"
                mBindingHead.utilsHeaderRecy.visibility = View.GONE
                mBindingHead.utilsHeaderText2.text =
                    "已收起${(mBindingHead.utilsHeaderRecy.adapter as UtilsHeadAdapter)?.data?.size}个常用功能"
//                上传数据
                postUtilsOrder()
            }
        }
        (mBindingHead.utilsHeaderRecy.adapter as UtilsHeadAdapter).draggableModule.apply {
            isDragEnabled = true
            itemTouchHelperCallback.setDragMoveFlags(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN)
            setOnItemDragListener(this@UtilsActivity)
        }
    }

    private fun toWeb(linker: String?, item: HqwModelVO) {
        startActivity<WebActivityNew>(Bundle().apply {
            putCharSequence("url", linker)
            putCharSequence("token", getValue("token", ""))
            putCharSequence("title", item.modelName)
        })
    }

    private fun addToHead(adapter: BaseQuickAdapter<*, *>, position: Int, item: HqwModelVO) {
        if ((mBindingHead.utilsHeaderRecy.adapter as UtilsHeadAdapter).data.size > 6) {
            toast("最多增加7个功能")
            return
        }
        (adapter.getItem(position) as HqwModelVO).isEdit = false
        adapter.notifyItemChanged(position)
        //添加进头部局
        (mBindingHead.utilsHeaderRecy.adapter as UtilsHeadAdapter).addData(item)
    }

    //可编辑状态
    private fun toEdit(edit: Boolean) {
        utilsAdapter.toEditAll(
            (mBindingHead.utilsHeaderRecy.adapter as UtilsHeadAdapter).data,
            edit
        )
    }

    private fun initUtilsObserver() {
        mViewModel.uiState.launchAndCollectIn(this, Lifecycle.State.STARTED) {
            onSuccess = { result: List<UtilsBean>? ->

                utilsAdapter.setNewInstance(ArrayList(result))

            }

            onFailed = { _, msg -> toast(msg ?: "") }

            onError = { }
        }
    }

    /**
     *保存移动后的数据
     */
    private fun postUtilsOrder() {
        launchWithLoadingAndCollect({
            mViewModel.changeUtilsOrder(
                (mBindingHead.utilsHeaderRecy.adapter as UtilsHeadAdapter).data,
                this?.getValue("phone", ConstantObject.PHONE) ?: ""
            )
        }) {
            onDataEmpty = {
                toast("修改成功")
                FlowEventBus.post(Event.AfterChangeUtils(0))
                finish()
            }
            onFailed = { _, errorMsg ->
                toast(errorMsg ?: "")
            }
        }
    }

    /**
     *充值缴费判断是否超时
     */
    private fun getSysTimeIsLimit(toDo: () -> Unit) {
        launchNoLoadingNoBase({
            mViewModel.getSysTimeIsLimit()
        }) {
            onSuccess = {
                when (it) {
                    false -> {
                        toDo()
                    }
                    else -> {
                        toast(ConstantObject.PAY_TIME_LIMIT)
                    }
                }
            }

        }
    }


    override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
    }

    override fun onItemDragMoving(
        source: RecyclerView.ViewHolder?,
        from: Int,
        target: RecyclerView.ViewHolder?,
        to: Int
    ) {
    }

    override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
    }

}