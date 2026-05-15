package com.xdel.luke.Vrp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;

import com.xdel.luke.DrawerMain2Activity;
import com.xdel.luke.R;
import com.xdel.luke.objClasses.myApp;
import com.xdel.luke.objClasses.objGlobalMethods;

import java.util.List;

public class VRPSwipeAndDragHelper2 extends ItemTouchHelper.Callback {
    boolean viewBeingCleared;
    int finaldragflags;

    private VRPSwipeAndDragHelper2.ActionCompletionContract contract;

    public VRPSwipeAndDragHelper2(VRPSwipeAndDragHelper2.ActionCompletionContract contract) {
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
        try {
            DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
            if (act != null) {
                act.enableDisableEditRouteButtons(false);
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
        contract.onViewMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    //Return true if the current ViewHolder can be dropped over the the target ViewHolder.
    @Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
        boolean asd = false;
        boolean qwe = false;
        boolean prevQueIsHeader = false;
        boolean prevQueIsMoved = false;
        boolean targetQueIsMoved = false;
        boolean wasUpdated = false;
        JobQueue cq = null;
        JobQueue tq = null;
        JobQueue eq = null;
        JobQueue nq = null;
        JobQueue jq = null;
        VrpList_RecycleViewAdapter2 adapter2;
        JobQueViewHolder2 currViewHolder = null;
        List<JobQueue> al = null;
        try {
            currViewHolder = (JobQueViewHolder2) current;
            adapter2 = (VrpList_RecycleViewAdapter2) recyclerView.getAdapter();
            wasUpdated = adapter2.wasUpdated();
            int currViewHolderIDX = current.getLayoutPosition();
            int targetViewHolderIDX = target.getLayoutPosition();
            boolean currNtargetSameType = current.getItemViewType() == target.getItemViewType() && ((JobQueViewHolder2) current).getType().equals(((JobQueViewHolder2) target).getType());

            if (adapter2 != null && wasUpdated) {
                al = VrpList_RecycleViewAdapter2.rtnCalculatedList(currViewHolderIDX, targetViewHolderIDX, finaldragflags);
            } else {
                al = adapter2.getJQlist();
            }

            if (targetViewHolderIDX == 0) {
                asd = false;
            } else if (targetViewHolderIDX > 0 && currViewHolderIDX >= 0) {
                final int fromPos = current.getAdapterPosition();
                final int toPos = target.getAdapterPosition();
                finaldragflags = fromPos < toPos ? ItemTouchHelper.DOWN : ItemTouchHelper.UP;
                if (al != null && al.size() > 0 && finaldragflags == ItemTouchHelper.UP) {
                    eq = targetViewHolderIDX > 0 ? al.get(targetViewHolderIDX - 1) : null;
                    tq = al.get(targetViewHolderIDX);
                    asd = eq != null && eq.IsHeader && tq != null && !tq.IsHeader && tq.getType().equals("vrp");
                }
            }
            currViewHolder.cl.setBackgroundResource((!asd && prevQueIsHeader) || (asd) ? R.drawable.gradient_bg_hover : R.color.menu_red);
            currViewHolder.lblVrpAddress.setTextColor((!asd && prevQueIsHeader) || (asd) ? Color.BLACK : Color.WHITE);
            currViewHolder.lblVrpPostal.setTextColor((!asd && prevQueIsHeader) || (asd) ? Color.BLACK : Color.WHITE);
            currViewHolder.lblpNum_dNum.setTextColor((!asd && prevQueIsHeader) || (asd) ? Color.BLACK : Color.WHITE);
            currViewHolder.lblCount.setTextColor((!asd && prevQueIsHeader) || (asd) ? Color.BLACK : Color.WHITE);
        } catch (Exception e) {
            asd = false;
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 canDropOver. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }
    /*@Override
    public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
        boolean asd = false;
        boolean qwe = false;
        boolean prevQueIsHeader = false;
        boolean prevQueIsMoved = false;
        boolean targetQueIsMoved = false;
        boolean wasUpdated = false;
        JobQueue cq = null;
        JobQueue tq = null;
        JobQueue eq = null;
        JobQueue nq = null;
        JobQueue jq = null;
        VrpList_RecycleViewAdapter2 adapter2;
        JobQueViewHolder2 currViewHolder = null;
        List<JobQueue> al = null;
        try {
            currViewHolder = (JobQueViewHolder2) current;
            adapter2 = (VrpList_RecycleViewAdapter2) recyclerView.getAdapter();
            wasUpdated = adapter2.wasUpdated();
            int currViewHolderIDX = current.getLayoutPosition();
            int targetViewHolderIDX = target.getLayoutPosition();
            boolean currNtargetSameType = current.getItemViewType() == target.getItemViewType() && ((JobQueViewHolder2) current).getType().equals(((JobQueViewHolder2) target).getType());

            if (adapter2 != null && wasUpdated) {
                al = VrpList_RecycleViewAdapter2.rtnCalculatedList(currViewHolderIDX, targetViewHolderIDX, finaldragflags);
            } else {
                al = adapter2.getJQlist();
            }


            if (targetViewHolderIDX == 0) {
                asd = false;
            } else if (targetViewHolderIDX > 0 && currViewHolderIDX >= 0) {
                final int fromPos = current.getAdapterPosition();
                final int toPos = target.getAdapterPosition();
                finaldragflags = fromPos < toPos ? ItemTouchHelper.DOWN : ItemTouchHelper.UP;
                if (al != null && al.size() > 0) {
                    cq = al.get(currViewHolderIDX);
                    tq = al.get(targetViewHolderIDX);
                    targetQueIsMoved = tq.getisMoved();

                    if (wasUpdated) {
                        if (finaldragflags == ItemTouchHelper.UP) {
                            eq = targetViewHolderIDX != 0 && targetViewHolderIDX > 1 ? al.get(targetViewHolderIDX - 1) : null;
                            if (eq != null && !eq.IsHeader && eq.isMoved && tq != null && !tq.IsHeader && !tq.isMoved && !tq.isUpdated) {
                                asd = true;
                            } else if (eq != null && !eq.IsHeader && eq.isMoved && tq != null && !tq.IsHeader && tq.isMoved && !tq.isUpdated) {
                                asd = true;
                            } else if (!tq.isUpdated && !tq.isMoved && targetViewHolderIDX == 1) {
                                asd = true;
                            } else if (!tq.isUpdated && tq.isMoved && targetViewHolderIDX == 1) {
                                asd = true;
                            } else if (!tq.isUpdated && !tq.isMoved) {
                                asd = false;
                            }
                            *//*if (eq != null && !eq.IsHeader && eq.isMoved && tq != null && !tq.IsHeader && !tq.isMoved && !tq.isUpdated) {
                                asd = true;
                            } else if (!tq.isUpdated && !tq.isMoved) {
                                asd = false;
                            } else {
                                asd = current.getItemViewType() == target.getItemViewType() && ((JobQueViewHolder2) current).getType().equals(((JobQueViewHolder2) target).getType())
                                        && ((JobQueViewHolder2) target).getEnableMove() && tq.getSDownCount() < 2;
                                qwe = asd;
                            }*//*
                        } else {
                            if (!cq.isUpdated && !cq.isMoved) {
                                if (targetViewHolderIDX == cq.StartID) {
                                    asd = true;
                                } else if (tq.isUpdated && tq.isMoved) {
                                    asd = true;
                                } else if (!tq.isUpdated && tq.isMoved) {
                                    asd = true;
                                } else {
                                    asd = false;
                                }
                            } else if (!cq.isUpdated && cq.isMoved) {
                                if (targetViewHolderIDX == cq.StartID) {
                                    asd = true;
                                } else if (!tq.isMoved) {
                                    asd = false;
                                } else {
                                    asd = true;
                                }
                            } else if (!tq.isUpdated && !tq.isMoved) {
                                asd = false;
                            } else if (cq.isUpdated) {
                                asd = false;
                            }
                            *//*if (!cq.isUpdated && !cq.isMoved) {
                                if (targetViewHolderIDX == cq.StartID) {
                                    asd = true;
                                } else if (tq.isUpdated && tq.isMoved) {
                                    asd = true;
                                } else if (!tq.isUpdated && tq.isMoved) {
                                    asd = true;
                                } else {
                                    asd = false;
                                }
                            } else if (!cq.isUpdated && cq.isMoved) {
                                if (targetViewHolderIDX == cq.StartID) {
                                    asd = true;
                                } else if (!tq.isMoved) {
                                    asd = false;
                                } else {
                                    asd = true;
                                }
                            } else if (!tq.isUpdated && !tq.isMoved) {
                                asd = false;
                            } else {
                                asd = current.getItemViewType() == target.getItemViewType() && ((JobQueViewHolder2) current).getType().equals(((JobQueViewHolder2) target).getType())
                                        && ((JobQueViewHolder2) target).getEnableMove() && tq.getSDownCount() < 2;
                                qwe = asd;
                            }*//*
                        }
                    } else {
                        if (finaldragflags == ItemTouchHelper.UP) {
                            if (targetViewHolderIDX > 0) {
                                eq = al.get(targetViewHolderIDX - 1);
                            } else if (targetViewHolderIDX == 0) {
                                eq = null;
                                prevQueIsHeader = true;
                            }
                            if (eq != null) {
                                prevQueIsHeader = eq.getIsHeader();
                                prevQueIsMoved = prevQueIsHeader ? false : eq.getisMoved();
                            }

                            if (currNtargetSameType && prevQueIsHeader) {
                                asd = true;
                            } else if (currNtargetSameType && !prevQueIsHeader) {
                                asd = prevQueIsMoved;
                            }
                        } else {
                            if ((targetViewHolderIDX == cq.getStartID())) {
                                asd = true;
                            } else if (targetQueIsMoved) {
                                asd = true;
                            } else if (!targetQueIsMoved && tq.StartID < cq.StartID && cq.StartID - 1 == tq.StartID) {
                                asd = true;
                            }
                        }
                    }
                }
            }
            currViewHolder.cl.setBackgroundResource((!asd && prevQueIsHeader) || (asd) ? R.drawable.gradient_bg_hover : R.color.menu_red);
            currViewHolder.lblVrpAddress.setTextColor((!asd && prevQueIsHeader) || (asd) ? Color.BLACK : Color.WHITE);
            currViewHolder.lblVrpPostal.setTextColor((!asd && prevQueIsHeader) || (asd) ? Color.BLACK : Color.WHITE);
            currViewHolder.lblpNum_dNum.setTextColor((!asd && prevQueIsHeader) || (asd) ? Color.BLACK : Color.WHITE);
            currViewHolder.lblCount.setTextColor((!asd && prevQueIsHeader) || (asd) ? Color.BLACK : Color.WHITE);
        } catch (Exception e) {
            asd = false;
            String s = e.getMessage();
            objGlobalMethods.writeToFile("Exception at VrpList_RecycleViewAdapter2 canDropOver. Exception: " + s, "", "", "", "");
        } finally {
            return asd;
        }
    }*/

    //when the user interaction with an element is over and it also completed its animation.
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        try {
            VrpList_RecycleViewAdapter2 adapter2 = (VrpList_RecycleViewAdapter2) recyclerView.getAdapter();
            //adapter2.amendJQListAfterMoved4(recyclerView, viewHolder, finaldragflags);
            adapter2.amendJQListAfterMoved5(recyclerView, viewHolder);
            ViewCompat.setElevation(viewHolder.itemView, 0);
            viewBeingCleared = true;
            try {
                DrawerMain2Activity act = (DrawerMain2Activity) myApp.currentActivity;
                if (act != null) {
                    act.enableDisableEditRouteButtons(true);
                }
            } catch (Exception e) {
                String s = e.getMessage().toString();
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
            objGlobalMethods.writeToFile("Exception at VRPSwipeAndDragHelper2 clearView. Exception: " + s, "", "", "", "");
        }
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
