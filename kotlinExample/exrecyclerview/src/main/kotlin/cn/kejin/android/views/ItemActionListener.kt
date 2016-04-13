package cn.kejin.android.views

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * Author: Kejin ( Liang Ke Jin )
 * Date: 2016/4/4
 */

/**
 * 将 ItemTouchHelper.Callback 的主要部分抽离出来
 */
interface ItemActionListener {

    fun isItemViewSwipeEnabled() : Boolean

    fun isLongPressDragEnabled() : Boolean

    /**
     * 当滑动或者拖动时回调, 一般在此改变这个view的外观
     * @return 返回 false 表示没有处理完毕, 需要调用 super.onChildDraw()
     */
    fun onChildDraw(
            c: Canvas?,
            recyclerView: ExRecyclerView,
            viewHolder: RecyclerView.ViewHolder?,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean):Boolean

    /**
     * 当
     * @return 返回 false 表示没有处理完毕, 需要调用 super.clearView()
     */
    fun clearView(
            recyclerView: ExRecyclerView,
            viewHolder: RecyclerView.ViewHolder?):Boolean

    /**
     * @return 返回 false 表示没有处理完毕, 需要调用 super.onSelectedChanged()
     */
    fun onSelectedChanged(
            recyclerView: ExRecyclerView,
            viewHolder: RecyclerView.ViewHolder?,
            actionState: Int):Boolean

    /**
     * 返回移动的标志位, 定制拖动或者滑动的方向, 如果不想让一个位置被拖动, 返回
     * makeMovementFlags(0, 0)
     */
    fun getMovementFlags(
            recyclerView: ExRecyclerView,
            viewHolder: RecyclerView.ViewHolder?): Int

    /**
     * 当拖动的viewHolder 可以移动到一个新位置时回调
     * @return Boolean 返回true 则表示 viewHolder 和 target 之间已经进行了交换
     */
    fun onMove(
            recyclerView: ExRecyclerView,
            viewHolder: RecyclerView.ViewHolder?,
            target: RecyclerView.ViewHolder?): Boolean

    /**
     * 当滑动成功时回调
     * @param direction ItemTouchHelper.START
     *                  ItemTouchHelper.END
     */
    fun onSwiped(
            recyclerView: ExRecyclerView,
            viewHolder: RecyclerView.ViewHolder?,
            direction: Int)

    fun makeMovementFlags(dragFlags: Int, swipeFlags: Int):Int {
        return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
    }
}