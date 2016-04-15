package cn.kejin.exrecyclerview

import android.content.Context
import android.graphics.Canvas
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import java.util.*

import kotlin.collections.mutableListOf

/**
 * Author: Kejin ( Liang Ke Jin )
 * Date: 2016/3/22
 */
class ExRecyclerView: RecyclerView {
    companion object {
        val TAG = "ExRecyclerView"
    }

    /**
     * define in xml layout
     */
    private var xmlHeader = 0

    protected val headerViews: ArrayList<View> = ArrayList()

    private var xmlFooter = 0

    protected val footerViews: ArrayList<View> = ArrayList()

    protected val wrapper = AdapterWrapper()

    protected var wrappedAdapter: Adapter<ViewHolder>? = null;

    constructor(context: Context?) : this(context, null, 0)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {

        super.setAdapter(wrapper)
        if (context != null && attrs != null) {
            val attr = context.obtainStyledAttributes(attrs, R.styleable.ExRecyclerView, defStyle, 0)
            val headerId = attr.getResourceId(R.styleable.ExRecyclerView_header, 0)
            if (headerId != 0) {
                val header = View.inflate(context, headerId, null)
                if (header != null) {
                    xmlHeader = addHeader(header)
                }
            }

            val footerId = attr.getResourceId(R.styleable.ExRecyclerView_footer, 0)
            if (footerId != 0) {
                val footer = View.inflate(context, footerId, null)
                if (footer != null) {
                    xmlFooter = addFooter(footer)
                }
            }

            attr.recycle()

            this.itemAnimator
        }

        itemTouchHelper.attachToRecyclerView(this)
    }

    /**
     * get the header view that define in xml layout
     * @return view or null
     */
    fun getHeader() : View?
            = if (xmlHeader > 0) { getHeader(xmlHeader) } else {null}

    /**
     * remove the header view that define in xml layout
     */
    fun removeHeader() {
        if (xmlHeader > 0) {
            removeHeader(xmlHeader)
        }
    }

    /**
     * get header size
     * @return header views count
     */
    fun getHeaderSize() = headerViews.size

    /**
     * Whether has this header view
     * @param view View
     * @return Boolean
     */
    fun hasHeader(view: View) : Boolean
            = headerViews.contains(view)

    /**
     * find header view by hashcode
     * @param code view's hashcode
     * @return View or null
     */
    fun getHeader(code: Int) : View? {
        return headerViews.firstOrNull { it.hashCode() == code }
    }

    /**
     * add a header view,
     * @param view header view
     * @return Int the view hashcode, can use this code find the header view
     */
    fun addHeader(view: View) : Int {
        if (!headerViews.contains(view)) {
            headerViews.add(view)
            setFullSpan(view)

            wrapper.notifyItemInserted(getHeaderSize()-1)
        }

        return view.hashCode()
    }

    /**
     * remove a header view by hashcode
     * @param code view's hashcode
     */
    fun removeHeader(code: Int) {
        val index = headerViews.indexOfFirst { it.hashCode() == code }
        if (index in 0..headerViews.size-1) {
            if (code == xmlHeader) {
                xmlHeader = 0
            }

            val view = headerViews.removeAt(index)
            removeFromParent(view)
            wrapper.notifyItemRemoved(index)
        }
    }

    /**
     * remove a header view
     * @param view header view
     */
    fun removeHeader(view: View) {
        val index = headerViews.indexOf(view)
        if (index in 0..headerViews.size-1) {
            if (view.hashCode() == xmlHeader) {
                xmlHeader = 0
            }

            headerViews.removeAt(index)
            removeFromParent(view)
            wrapper.notifyItemRemoved(index)
        }
    }

    ///////////////////////////////////Footers///////////////////////////////////////
    /**
     * get footer view that defined in xml layout
     * @return view or null
     */
    fun getFooter() : View?
            = if (xmlFooter > 0) { getFooter(xmlFooter) } else {null}

    /**
     * remove footer view that defined in xml layout
     */
    fun removeFooter() {
        if (xmlFooter != 0) {
            removeFooter(xmlFooter)
        }
    }

    /**
     * @return Int footer views count
     */
    fun getFooterSize() = footerViews.size

    /**
     * whether has a footer view
     * @param view
     * @return Boolean
     */
    fun hasFooter(view : View) : Boolean
            = footerViews.contains(view)

    /**
     * find the footer view by hashcode
     * @param code view's hashcode
     * @return view or null (not find)
     */
    fun getFooter(code: Int) : View? {
        return footerViews.firstOrNull { it.hashCode() == code }
    }

    /**
     * add a footer view
     * @param view footer view
     * @return Int view's hashcode
     */
    fun addFooter(view: View) : Int {
        if (!footerViews.contains(view)) {
            footerViews.add(view)
            setFullSpan(view)

            wrapper.notifyItemInserted(wrapper.itemCount)
        }

        return view.hashCode()
    }

    /**
     * remove a footer view
     * @param code view's hashcode
     */
    fun removeFooter(code: Int) {
        val index = footerViews.indexOfFirst { it.hashCode() == code }
        if (index in 0..footerViews.size-1) {
            if (code == xmlFooter) {
                xmlFooter = 0
            }

            val view = footerViews.removeAt(index)
            removeFromParent(view)

            wrapper.notifyItemRemoved(index+getHeaderSize()+getWrapItemCount())
        }
    }

    /**
     * remove a footer view
     * @param view footer view
     */
    fun removeFooter(view: View) {
        val index = footerViews.indexOf(view)
        if (index in 0..footerViews.size-1) {
            if (view.hashCode() == xmlFooter) {
                xmlFooter = 0
            }

            footerViews.removeAt(index)
            removeFromParent(view)
            wrapper.notifyItemRemoved(getHeaderSize() + getWrapItemCount() + index)
        }
    }

    /**
     * get all item count, include headers and footers
     */
    fun getItemCount(): Int {
        return getHeaderSize() + getFooterSize() + getWrapItemCount()
    }

    private fun getWrapItemCount()
            = wrappedAdapter?.itemCount?:0

    override fun getAdapter(): Adapter<*>? {
        return wrappedAdapter
    }

    @Suppress("UNCHECKED_CAST")
    override fun setAdapter(wrapAdapter: Adapter<*>?) {
        wrappedAdapter?.unregisterAdapterDataObserver(adapterDataObserver)
        if (wrapAdapter != null) {
            wrappedAdapter = wrapAdapter as Adapter<ViewHolder>
            wrappedAdapter?.registerAdapterDataObserver(adapterDataObserver)

            wrapAdapter.notifyDataSetChanged()
        }
        else {
            wrappedAdapter = null
        }

        adapterDataObserver.onChanged()
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(layout)

        if (layout != null && layout is GridLayoutManager) {
            configGridLayoutManager(layout)
        }

        headerViews.forEach { setFullSpan(it) }
        footerViews.forEach { setFullSpan(it) }
    }

    private fun configGridLayoutManager(layoutManager: GridLayoutManager) {
        val oldLookup = layoutManager.spanSizeLookup;
        val spanCount = layoutManager.spanCount;
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (isHeaderOrFooterPos(position)) {
                    return spanCount
                }
                return oldLookup.getSpanSize(position - getHeaderSize())
            }
        }
    }

    /**
     * 如果 view 是不可回收的, 则在从RecyclerView移除之后要主动将他从他的 parentView中移除掉
     * 不然会出现移除之后, 还粘在 RecyclerView 中的情况
     */
    private fun removeFromParent(view: View) {
        val pView = view.parent
        if (pView != null && pView is ViewGroup) {
            pView.removeView(view)
        }
    }

    /**
     * 设置 LayoutParams, 如果不设置, 在 LinearLayoutManager 时有时会出现不能填满宽度的情况
     */
    private fun setFullSpan(view: View?) {
        if (view == null || layoutManager == null) {
            return
        }

        if (layoutManager is StaggeredGridLayoutManager) {
            val layoutParams = StaggeredGridLayoutManager.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.isFullSpan = true
            view.layoutParams = layoutParams
        }
        else {
            val layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            view.layoutParams = layoutParams
        }
    }

    private fun isHeaderOrFooterPos(pos: Int): Boolean {
        return (pos < getHeaderSize() || pos >= getHeaderSize() + (wrappedAdapter?.itemCount ?: 0))
    }

    protected inner class AdapterWrapper : Adapter<ViewHolder>(){

        override fun getItemViewType(position: Int): Int {
            if (isHeaderOrFooterPos(position)) {
                return -position - 1;
            }

            val pos = position - getHeaderSize()
            return wrappedAdapter?.getItemViewType(pos) ?: 0;
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            if (!isHeaderOrFooterPos(position)) {
                wrappedAdapter?.onBindViewHolder(holder, position-getHeaderSize())
            }
        }

        override fun getItemCount(): Int {
            return getHeaderSize() + getFooterSize() + getWrapItemCount()
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
            if (viewType < 0) {
                var pos = -(viewType + 1)
                if (pos >= 0 && pos < getHeaderSize()) {
                    val header = headerViews[pos]
                    val pView = header.parent
                    if (pView == parent) {
                        pView?.removeView(header)
                    }
                    return WrapperViewHolder(header)
                }

                pos -= getHeaderSize() + getWrapItemCount()
                if (pos >= 0 && pos < getFooterSize()) {
                    val footer = footerViews[pos]
                    val fView = footer.parent
                    if (fView == parent) {
                        fView?.removeView(footer)
                    }

                    return WrapperViewHolder(footer)
                }
                return null;
            }

            return wrappedAdapter?.onCreateViewHolder(parent, viewType)
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
            super.onDetachedFromRecyclerView(recyclerView)
        }

        override fun onFailedToRecycleView(holder: ViewHolder?): Boolean {
            if (holder != null) {
                removeFromParent(holder.itemView)
            }
            return super.onFailedToRecycleView(holder)
        }

        inner class WrapperViewHolder(itemView: View?) : ViewHolder(itemView) {
            init {
                setIsRecyclable(false)
            }
        }
    }

    /**
     * Data observer
     */
    var lastWrappedAdapterSize = getWrapItemCount()
    private fun onWrappedDataSizeChanged() {
        lastWrappedAdapterSize = getWrapItemCount()
    }

    private val adapterDataObserver = object : AdapterDataObserver() {
        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            wrapper.notifyItemMoved(getHeaderSize() + fromPosition, getHeaderSize() + toPosition)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            onWrappedDataSizeChanged()

            wrapper.notifyItemRangeInserted(getHeaderSize() + positionStart, itemCount)
//            wrapper.notifyItemRangeChanged(positionStart + getHeaderSize() + itemCount,
//                    wrapper.itemCount - (getHeaderSize() + positionStart + itemCount) - getFooterSize())
        }

        override fun onChanged() {
            val curItemCount = getWrapItemCount()
            var deltaCount = lastWrappedAdapterSize-curItemCount
            if (deltaCount > 0) {
                wrapper.notifyItemRangeRemoved(getHeaderSize()+curItemCount, deltaCount)
            }
            else if (deltaCount < 0) {
                deltaCount = -deltaCount
                wrapper.notifyItemRangeInserted(getHeaderSize()+lastWrappedAdapterSize, deltaCount)
            }

            wrapper.notifyItemRangeChanged(getHeaderSize(), curItemCount)
            lastWrappedAdapterSize = curItemCount
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            onWrappedDataSizeChanged()
            wrapper.notifyItemRangeRemoved(getHeaderSize() + positionStart, itemCount)
//            wrapper.notifyItemRangeChanged(getHeaderSize() + positionStart,
//                    wrapper.itemCount - getHeaderSize() - positionStart - getFooterSize())
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            wrapper.notifyItemRangeChanged(getHeaderSize() + positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            wrapper.notifyItemRangeChanged(getHeaderSize() + positionStart, itemCount, payload)
        }
    }

    /******************************************** Load More Function ******************************/

    var isLoadingMore = false
        private set

    var loadMoreListener : OnLoadMoreListener? = null

    fun setOnLoadMoreListener(listener: OnLoadMoreListener?) {
        loadMoreListener = listener
    }

    fun setOnLoadMoreListener(r : ()->Boolean) {
        loadMoreListener = object : OnLoadMoreListener {
            override fun onLoadMore() : Boolean {
                return r()
            }
        }
    }

    fun endLoadMore() {
        isLoadingMore = false
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        val wrapperSize = wrappedAdapter?.itemCount?:0
        if (!isLoadingMore && loadMoreListener != null) {
            val visPos = getVisiblePos()
            if (visPos.second+1 >= getHeaderSize()+wrapperSize) {
                isLoadingMore = loadMoreListener?.onLoadMore()?:false
            }
        }
    }

    /**
     * get first and last visible item position
     */
    private fun getVisiblePos() : Pair<Int, Int> {
        if (layoutManager == null) {
            return Pair(0, 0)
        }

        var first = 0
        var last = 0
        when (layoutManager) {
            is GridLayoutManager -> {
                first = (layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
                last = (layoutManager as GridLayoutManager).findLastVisibleItemPosition()
            }

            is LinearLayoutManager -> {
                first = (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition();
                last = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }

            is StaggeredGridLayoutManager -> {
                var firstPos : IntArray = IntArray(2, {0});
                (layoutManager as StaggeredGridLayoutManager).findFirstVisibleItemPositions(firstPos)

                var lastPos : IntArray = IntArray(2, {0});
                (layoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(lastPos)

                first = firstPos.min()?:0
                last = lastPos.max()?:0
            }
        }

        return Pair(first, last)
    }

    interface OnLoadMoreListener {
        fun onLoadMore() : Boolean
    }


    /***************************************** Drag & Swipe Dismiss Function *********************/

    val itemTouchHelper = ItemTouchHelper(ItemTouchCallbackDocker())

    /**
     * 自定义的ItemTouchCallback
     * 注意: itemTouchCallback 里面的 viewHolder的adapterPosition 都是包含了 headerSize 的position
     * 所以需要在使用的时候, 减去 exRecycler.getHeaderSize()
     */
    var itemTouchCallback : ItemTouchHelper.Callback? = null;

    /**
     * 默认的监听, 监听主要的事件
     */
    var itemActionListener : ItemActionListener? = null

    /**
     * 封装 Callback 保证 header 和 footer 不会被移动
     */
    protected inner class ItemTouchCallbackDocker : ItemTouchHelper.Callback() {

        override fun isItemViewSwipeEnabled(): Boolean {
            return itemTouchCallback?.isItemViewSwipeEnabled
                    ?:(itemActionListener?.isItemViewSwipeEnabled() ?: super.isItemViewSwipeEnabled())
        }

        override fun isLongPressDragEnabled(): Boolean {
            return itemTouchCallback?.isLongPressDragEnabled
                    ?:(itemActionListener?.isLongPressDragEnabled()) ?: super.isLongPressDragEnabled()
        }

        override fun onChildDraw(
                c: Canvas?,
                recyclerView: RecyclerView?,
                viewHolder: ViewHolder?,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean) {
            val callback = itemTouchCallback?.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            if (callback == null) {
                val handled = itemActionListener?.onChildDraw(c, this@ExRecyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (handled == null || !handled) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }
        }


        override fun clearView(recyclerView: RecyclerView?, viewHolder: ViewHolder?) {

            val callback = itemTouchCallback?.clearView(recyclerView, viewHolder);
            if (callback == null) {
                val handled = itemActionListener?.clearView(this@ExRecyclerView, viewHolder)
                if (handled == null || !handled) {
                    super.clearView(recyclerView, viewHolder)
                }
            }
        }


        override fun onSelectedChanged(viewHolder: ViewHolder?, actionState: Int) {

            val callback = itemTouchCallback?.onSelectedChanged(viewHolder, actionState);
            if (callback == null) {
                val handled = itemActionListener?.onSelectedChanged(this@ExRecyclerView, viewHolder, actionState)
                if (handled == null || !handled) {
                    super.onSelectedChanged(viewHolder, actionState)
                }
            }
        }


        override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: ViewHolder?): Int {
            /**
             * 禁止移动header 或者 footer
             */
            if (viewHolder == null || isHeaderOrFooterPos(viewHolder.adapterPosition)) {
                return makeMovementFlags(0, 0)
            }

            return itemTouchCallback?.getMovementFlags(recyclerView, viewHolder)
                    ?:itemActionListener?.getMovementFlags(this@ExRecyclerView, viewHolder)
                    ?:makeMovementFlags(0, 0)
        }

        override fun onMove(recyclerView: RecyclerView?, viewHolder: ViewHolder?, target: ViewHolder?): Boolean {
            return itemTouchCallback?.onMove(recyclerView, viewHolder, target)
                    ?:itemActionListener?.onMove(this@ExRecyclerView, viewHolder, target)
                    ?:false
        }

        override fun onSwiped(viewHolder: ViewHolder?, direction: Int) {
            itemTouchCallback?.onSwiped(viewHolder, direction)
                    ?:itemActionListener?.onSwiped(this@ExRecyclerView, viewHolder, direction)
        }


        override fun onMoved(
                recyclerView: RecyclerView?,
                viewHolder: ViewHolder?,
                fromPos: Int,
                target: ViewHolder?,
                toPos: Int,
                x: Int,
                y: Int) {
            itemTouchCallback?.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
                    ?:super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
        }

        override fun getMoveThreshold(viewHolder: ViewHolder?): Float {
            return itemTouchCallback?.getMoveThreshold(viewHolder)
                    ?:super.getMoveThreshold(viewHolder)
        }

        override fun canDropOver(recyclerView: RecyclerView?, current: ViewHolder?, target: ViewHolder?): Boolean {
            return itemTouchCallback?.canDropOver(recyclerView, current, target)
                    ?:super.canDropOver(recyclerView, current, target)
        }

        override fun chooseDropTarget(selected: ViewHolder?, dropTargets: MutableList<ViewHolder>?, curX: Int, curY: Int): ViewHolder? {
            return itemTouchCallback?.chooseDropTarget(selected, dropTargets, curX, curY)?:super.chooseDropTarget(selected, dropTargets, curX, curY)
        }

        override fun getAnimationDuration(recyclerView: RecyclerView?, animationType: Int, animateDx: Float, animateDy: Float): Long {
            return itemTouchCallback?.getAnimationDuration(recyclerView, animationType, animateDx, animateDy)
                    ?:super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy)
        }

        override fun getBoundingBoxMargin(): Int {
            return itemTouchCallback?.boundingBoxMargin
                    ?:super.getBoundingBoxMargin()
        }

        override fun onChildDrawOver(c: Canvas?, recyclerView: RecyclerView?, viewHolder: ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
            itemTouchCallback?.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    ?:super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

        override fun interpolateOutOfBoundsScroll(recyclerView: RecyclerView?, viewSize: Int, viewSizeOutOfBounds: Int, totalSize: Int, msSinceStartScroll: Long): Int {
            return itemTouchCallback?.interpolateOutOfBoundsScroll(recyclerView, viewSize, viewSizeOutOfBounds, totalSize, msSinceStartScroll)
                    ?:super.interpolateOutOfBoundsScroll(recyclerView, viewSize, viewSizeOutOfBounds, totalSize, msSinceStartScroll)
        }

        override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
            return itemTouchCallback?.getSwipeEscapeVelocity(defaultValue)
                    ?:super.getSwipeEscapeVelocity(defaultValue)
        }

        override fun getSwipeThreshold(viewHolder: ViewHolder?): Float {
            return itemTouchCallback?.getSwipeThreshold(viewHolder)
                    ?:super.getSwipeThreshold(viewHolder)
        }

        override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
            return itemTouchCallback?.convertToAbsoluteDirection(flags, layoutDirection)
                    ?:super.convertToAbsoluteDirection(flags, layoutDirection)
        }

        override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
            return itemTouchCallback?.getSwipeVelocityThreshold(defaultValue)
                    ?:super.getSwipeVelocityThreshold(defaultValue)
        }
    }
}