package com.xdel.luke.RecycleViewsRelated;

import android.graphics.Canvas;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;

import com.xdel.luke.R;
import com.xdel.luke.RecycleViewsRelated.test.SectionHeaderViewHolder;
import com.xdel.luke.RecycleViewsRelated.test.UserViewHolder;

public class SwipeAndDragHelper extends ItemTouchHelper.Callback {

    private ActionCompletionContract contract;

    public SwipeAndDragHelper(ActionCompletionContract contract) {
        this.contract = contract;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        if (viewHolder instanceof SectionHeaderViewHolder) {
            return 0;
        }
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        //return makeMovementFlags(dragFlags, swipeFlags);
        //2 == HEADER TYPE from AddressListRouting2_RecycleViewAdapter
        return viewHolder.getItemViewType() == 2 ? 0 : makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        contract.onViewMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    //Return true if the current ViewHolder can be dropped over the the target ViewHolder.
    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
        /*boolean asd = false;

        int targetpos = ((UserViewHolder) target).getOldPosition();
        if (targetpos > -1) {
            asd = AddressListRouting2_RecycleViewAdapter.itemEnableMoveArray.get(targetpos, false);
        } else {
            asd = true;
        }


        if (((UserViewHolder) target).Type.toUpperCase().equals("OPS")) {
            asd =  false;
        } else if (((UserViewHolder) target).Type.toUpperCase().equals("GC") && ((UserViewHolder) current).Type.toUpperCase().equals("VRP")) {
            asd = true;
        } else if (((UserViewHolder) target).Type.toUpperCase().equals("VRP") && ((UserViewHolder) current).Type.toUpperCase().equals("GC")) {
            asd = false;
        } else {
            asd = false;
        }*/

        int currentItemViewType = current.getItemViewType();
        int targetItemViewType = target.getItemViewType();
        String currenttype = ((UserViewHolder) current).getType();
        String targettype = ((UserViewHolder) target).getType();
        boolean asd = currentItemViewType == targetItemViewType;


        return current.getItemViewType() == target.getItemViewType() && ((UserViewHolder) current).getType().equals(((UserViewHolder) target).getType());
    }

    //when the user interaction with an element is over and it also completed its animation.
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        ((UserViewHolder) viewHolder).cl.setBackgroundResource(R.drawable.list_selector_white);

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        contract.onViewSwiped(viewHolder.getAdapterPosition());
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled(){
        return false;
    }

    @Override
    public void onChildDraw(Canvas c,
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX,
                            float dY,
                            int actionState,
                            boolean isCurrentlyActive) {

        final View view = viewHolder.itemView;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setNullOutlineProvider(view);
        }


        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            float alpha = 1 - (Math.abs(dX) / recyclerView.getWidth());
            viewHolder.itemView.setAlpha(alpha);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void setNullOutlineProvider(View view) {
        view.setOutlineProvider(null);
    }

    public interface ActionCompletionContract {
        void onViewMoved(int oldPosition, int newPosition);

        void onViewSwiped(int position);
    }

}
