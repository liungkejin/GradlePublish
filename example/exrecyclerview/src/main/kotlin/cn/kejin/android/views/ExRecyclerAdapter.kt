package cn.kejin.android.views

import android.app.Activity
import android.graphics.Canvas
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import android.view.ViewGroup
import java.util.*

/**
 * Author: Kejin ( Liang Ke Jin )
 * Date: 2016/3/16
 */

/**
 * ExRecyclerAdapter 内置类 List<Model> 的数据类型, 并实现对数据操作时的notify
 */
abstract class ExRecyclerAdapter<Model, Holder: RecyclerView.ViewHolder>(val activity : Activity)
                            : RecyclerView.Adapter<Holder>(), ItemActionListener
{
    protected var data : MutableList<Model> = mutableListOf()
        private set

    fun inflateView(id : Int, parent: ViewGroup? = null)
            = activity.layoutInflater.inflate(id, parent, false)

    override fun getItemCount() = data.size

    private fun isValidPosition(pos: Int) = pos in 0..data.size-1

    fun get(pos: Int): Model? {
        return if (isValidPosition(pos)) data[pos] else null
    }

    /**
     * 设置单个数据
     * @param pos 位置
     * @param model 数据
     * @return Boolean 设置结果
     */
    fun set(pos: Int, model: Model, notify: Boolean=true) : Boolean {
        if (isValidPosition(pos)) {
            data[pos] = model
            if (notify) {
                notifyItemChanged(pos)
            }
            return true;
        }

        return false
    }

    /**
     * 重新设置所有的数据
     * @param list
     */
    fun set(list : Collection<Model>, notify: Boolean=true) {
        data.clear()
        data.addAll(list)
        if (notify) {
            notifyDataSetChanged()
        }
    }

    /**
     * 移动一个数据
     * @param from
     * @param to
     * @return Boolean
     */
    fun move(from: Int, to: Int, notify: Boolean=true): Boolean {
        if (from != to && isValidPosition(from) && isValidPosition(to)) {

            if (from > to) {
                for (i in from downTo to+1) {
                    Collections.swap(data, i, i-1)
                    if (notify) {
                        notifyItemMoved(i, i-1)
                    }
                }
            }
            else if (from < to) {
                for (i in from..to-1) {
                    Collections.swap(data, i, i+1)
                    if (notify) {
                        notifyItemMoved(i, i+1)
                    }
                }
            }

            return true;
        }
        return false
    }

    /**
     * 在 index 位置加入一个数据, 即 insert
     */
    fun add(index : Int, model: Model, notify: Boolean=true) {
        data.add(index, model)
        if (notify) {
            notifyItemInserted(index)
        }
    }

    /**
     * 追加一个数据
     */
    fun add(model: Model, notify: Boolean=true) {
        val index = data.size
        data.add(index, model)
        if (notify) {
            notifyItemInserted(index)
        }
    }

    /**
     * 追加所有的数据
     */
    fun addAll(list: Collection<Model>, notify: Boolean=true) {
        val insertPos = data.size
        data.addAll(list)
        if (notify) {
            notifyItemRangeInserted(insertPos, list.size)
        }
    }

    /**
     * 移除指定位置的数据
     */
    fun removeAt(index: Int, notify: Boolean=true): Boolean {
        if (isValidPosition(index)) {
            data.removeAt(index)
            if (notify) {
                notifyItemRemoved(index)
            }
            return true
        }
        return false
    }

    /**
     * 移除指定数据, 注意如果有多个相同的数据，则只会移除第一个
     */
    fun remove(model:Model, notify: Boolean=true): Boolean {
        val index = data.indexOf(model)
        if (isValidPosition(index)) {
            data.removeAt(index)
            if (notify) {
                notifyItemRemoved(index)
            }

            return true
        }
        return false
    }

    /**
     * 移除所有的指定数据
     */
    fun removeAll(model: Model, notify: Boolean=true): Boolean {
        var flag = false
        var index = data.indexOf(model)
        while (isValidPosition(index)) {
            flag = true
            data.removeAt(index)
            if (notify) {
                notifyItemRemoved(index)
            }
            index = data.indexOf(model)
        }

        return flag
    }

    /**
     * 清除所有的数据
     */
    fun clear(notify: Boolean=true) {
        data.clear()
        if (notify) {
            notifyDataSetChanged()
        }
    }

    /********************************** Item Action Listener ***********************************/

    var longPressDragEnable = false
    var itemViewSwipeEnable = false

    fun enableDragAndSwipe() {
        longPressDragEnable = true
        itemViewSwipeEnable = true
    }

    fun disableDragAndSwipe() {
        longPressDragEnable = false
        itemViewSwipeEnable = false
    }

    override fun isItemViewSwipeEnabled(): Boolean
            = itemViewSwipeEnable

    override fun isLongPressDragEnabled(): Boolean
            = longPressDragEnable

    override fun onChildDraw(
            c: Canvas?,
            recyclerView: ExRecyclerView,
            viewHolder: RecyclerView.ViewHolder?,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean): Boolean {
        if (viewHolder != null) {
            when (actionState) {
                ItemTouchHelper.ACTION_STATE_SWIPE -> {
                    val alpha: Float = 1.0f - Math.abs(dX) / viewHolder.itemView.width;
                    viewHolder.itemView.alpha = alpha;
                    viewHolder.itemView.translationX = dX;
                }
            }
        }

        return false;
    }

    override fun clearView(
            recyclerView: ExRecyclerView,
            viewHolder: RecyclerView.ViewHolder?): Boolean {
        if (viewHolder != null) {
            viewHolder.itemView.alpha = 1.0f
//            这里itemView如果是一个CardView, 在 v19 的机器上会出 ClassCastException
//            需要使用 setCardBackgroundColor()
//            if (viewHolder.itemView is CardView) {
//                (viewHolder.itemView as CardView).setCardBackgroundColor(Color.WHITE)
//            }
//            CardView(activity).setCardBackgroundColor(Color.WHITE)
//            viewHolder.itemView.setBackgroundColor(Color.WHITE)
        }
        return false
    }

    override fun onSelectedChanged(
            recyclerView: ExRecyclerView,
            viewHolder: RecyclerView.ViewHolder?,
            actionState: Int): Boolean {
        if (viewHolder != null &&
                actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
//            if (viewHolder.itemView is CardView) {
//                (viewHolder.itemView as CardView).setCardBackgroundColor(Color.LTGRAY)
//            }
//            viewHolder.itemView.setBackgroundColor(Color.LTGRAY)
        }
        return false
    }

    override fun getMovementFlags(
            recyclerView: ExRecyclerView,
            viewHolder: RecyclerView.ViewHolder?): Int {
        var dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        var swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END

        when (recyclerView.layoutManager) {
            is StaggeredGridLayoutManager,
            is GridLayoutManager -> {
                dragFlags = dragFlags or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                swipeFlags = 0
            }
        }

        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
            recyclerView: ExRecyclerView,
            viewHolder: RecyclerView.ViewHolder?,
            target: RecyclerView.ViewHolder?): Boolean {
        if (viewHolder != null && target != null) {
            val from = viewHolder.adapterPosition-recyclerView.getHeaderSize()
            val to = target.adapterPosition-recyclerView.getHeaderSize()

            return move(from, to)
        }
        return false;
    }

    override fun onSwiped(
            recyclerView: ExRecyclerView,
            viewHolder: RecyclerView.ViewHolder?,
            direction: Int) {
        if (viewHolder != null) {
            removeAt(viewHolder.adapterPosition - recyclerView.getHeaderSize())
        }
    }

    abstract class ExViewHolder<Model>(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        fun findView(id : Int) = itemView.findViewById(id)

        abstract fun bindView(model : Model, pos: Int)
    }
}