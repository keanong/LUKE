package com.xdel.luke.Vrp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;

import com.xdel.luke.R;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.ArrayList;
import java.util.List;

public class VRPSwipeAndDragHelper extends ItemTouchHelper.Callback {

    boolean viewBeingCleared;
    int finaldragflags;

    private ActionCompletionContract contract;

    public VRPSwipeAndDragHelper(ActionCompletionContract contract) {
        this.contract = contract;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        if (viewHolder instanceof JobQueSectionHeaderViewHolder) {
            return 0;
        }
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

        ////return makeMovementFlags(dragFlags, swipeFlags);
        ////2 == HEADER TYPE from AddressListRouting2_RecycleViewAdapter
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
        boolean asd = false;
        boolean qwe = false;
        boolean zxc = false;
        try {
            int currViewHolderIDX = current.getLayoutPosition();
            int targetViewHolderIDX = target.getLayoutPosition();
            if (targetViewHolderIDX >= 0 && currViewHolderIDX >= 0) {
                final int fromPos = current.getAdapterPosition();
                final int toPos = target.getAdapterPosition();
                finaldragflags = fromPos < toPos ? ItemTouchHelper.DOWN : ItemTouchHelper.UP;
                List<JobQueue> al = new ArrayList<JobQueue>();
                al = VrpList_RecycleViewAdapter.rtnCalculatedList(currViewHolderIDX, targetViewHolderIDX,finaldragflags);
                if (al != null && al.size() > 0) {
                    JobQueue jq = al.get(targetViewHolderIDX);
                    asd = current.getItemViewType() == target.getItemViewType() && ((JobQueViewHolder) current).getType().equals(((JobQueViewHolder) target).getType())
                            && ((JobQueViewHolder) target).getEnableMove() && jq.getSDownCount() < 2;
                    qwe = asd;
                }
            }

            if (!asd) {
                ((JobQueViewHolder) current).cl.setBackgroundResource(R.color.menu_red);
                ((JobQueViewHolder) current).lblVrpAddress.setTextColor(Color.WHITE);
                ((JobQueViewHolder) current).lblVrpPostal.setTextColor(Color.WHITE);
                ((JobQueViewHolder) current).lblpNum_dNum.setTextColor(Color.WHITE);
                ((JobQueViewHolder) current).lblCount.setTextColor(Color.WHITE);
            } else {
                ((JobQueViewHolder) current).cl.setBackgroundResource(R.drawable.gradient_bg_hover);
                ((JobQueViewHolder) current).lblVrpAddress.setTextColor(Color.BLACK);
                ((JobQueViewHolder) current).lblVrpPostal.setTextColor(Color.BLACK);
                ((JobQueViewHolder) current).lblpNum_dNum.setTextColor(Color.BLACK);
                ((JobQueViewHolder) current).lblCount.setTextColor(Color.BLACK);
            }
        } catch (Exception e) {
            asd = false;
        } finally {
            return asd;
        }

        /*return current.getItemViewType() == target.getItemViewType() && ((JobQueViewHolder) current).getType().equals(((JobQueViewHolder) target).getType())
                && ((JobQueViewHolder) target).getEnableMove() && ((JobQueViewHolder) target).getSDownCount() < 2;*/
    }

    //when the user interaction with an element is over and it also completed its animation.
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        try {
            VrpList_RecycleViewAdapter.amendJQListAfterMoved(recyclerView, viewHolder);
            ViewCompat.setElevation(viewHolder.itemView, 0);
            viewBeingCleared = true;

        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at VRPSwipeAndDragHelper clearView. Exception: " + s, "", "", "", "");
        }
        //((JobQueViewHolder) viewHolder).cl.setBackgroundResource(R.color.brightgreen);

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
    public boolean isItemViewSwipeEnabled() {
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

        try {
            final View view = viewHolder.itemView;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setNullOutlineProvider(view);
            }


            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                float alpha = 1 - (Math.abs(dX) / recyclerView.getWidth());
                viewHolder.itemView.setAlpha(alpha);
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            if (viewBeingCleared) {
                viewBeingCleared = false;
            } else {
                ViewCompat.setElevation(viewHolder.itemView, 3);
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
    }

    private void setNullOutlineProvider(View view) {
        view.setOutlineProvider(null);
    }

    public interface ActionCompletionContract {
        void onViewMoved(int oldPosition, int newPosition);

        void onViewSwiped(int position);
    }

}
