package com.xdel.luke.objClasses;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.xdel.luke.JobsHandler;
import com.xdel.luke.R;
import com.xdel.luke.RecycleViewsRelated.RecyclerViewItemClickListener;
import com.xdel.luke.RecycleViewsRelated.SwipeAndDragHelper;
import com.xdel.luke.RecycleViewsRelated.test.SectionHeaderViewHolder;
import com.xdel.luke.RecycleViewsRelated.test.User;
import com.xdel.luke.RecycleViewsRelated.test.UserViewHolder;

import java.util.List;

public class AddressListRouting2_RecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        SwipeAndDragHelper.ActionCompletionContract {

    private static final int USER_TYPE = 1;
    private static final int HEADER_TYPE = 2;
    public static List<User> usersList;
    private ItemTouchHelper touchHelper;
    private RecyclerViewItemClickListener recyclerViewItemClickListener;
    public static SparseBooleanArray itemEnableMoveArray;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case USER_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_user_list_item, parent, false);
                return new UserViewHolder(view);
            case HEADER_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_user_list_section_header, parent, false);
                return new SectionHeaderViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_user_list_item, parent, false);
                return new UserViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        final int pos = position;
        final User data = usersList.get(pos);
        final int itempos = getItemPosition(data);

        try {
            if (itemViewType == USER_TYPE) {
                ((UserViewHolder) holder).username.setOnClickListener(null);
                ((UserViewHolder) holder).reorderView.setOnTouchListener(null);
                ((UserViewHolder) holder).reorderView.setVisibility(itemEnableMoveArray.get(itempos, false) ? View.VISIBLE : View.GONE);

                ((UserViewHolder) holder).setType(data.getType());
                ((UserViewHolder) holder).username.setText(data.getName());
                ((UserViewHolder) holder).username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        objGlobalMethods.showDialog(myApp.currentActivity, "OK", usersList.get(getItemPosition(data)).getName()).show();
                    }
                });

                if (itemEnableMoveArray.get(getItemPosition(data), false)) {
                    ((UserViewHolder) holder).reorderView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                                ((UserViewHolder) holder).cl.setBackgroundResource(R.drawable.gradient_bg_hover);
                                touchHelper.startDrag(holder);
                            }
                            return false;
                        }
                    });
                }
            } else {
                final SectionHeaderViewHolder headerViewHolder = (SectionHeaderViewHolder) holder;
                headerViewHolder.sectionTitle.setText(usersList.get(itempos).getType());
                if (data.getType().toUpperCase().equals("OPS")) {
                    hideShowUpdateCtrls(headerViewHolder, false, true);
                } else {
                    hideShowUpdateCtrls(headerViewHolder, data.getIsEditing(), false);
                    headerViewHolder.lblCancel.setOnClickListener(null);
                    headerViewHolder.lblEdit.setOnClickListener(null);
                    headerViewHolder.lblUpdate.setOnClickListener(null);

                    headerViewHolder.lblCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int i = getItemPosition(data);
                            User u = usersList.get(i);
                            u.setIsEditing(false);
                            usersList.set(i, u);
                            setitemEnableMoveArray(false, i);
                            hideShowUpdateCtrls(headerViewHolder, false, false);
                            notifyDataSetChanged();
                        }
                    });

                    headerViewHolder.lblEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int i = getItemPosition(data);
                            User u = usersList.get(i);
                            u.setIsEditing(true);
                            usersList.set(i, u);
                            setitemEnableMoveArray(true, i);
                            hideShowUpdateCtrls(headerViewHolder, true, false);
                            notifyDataSetChanged();
                        }
                    });

                    headerViewHolder.lblUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int i = getItemPosition(data);
                            User u = usersList.get(i);
                            u.setIsEditing(false);
                            usersList.set(i, u);
                            setitemEnableMoveArray(false, i);
                            hideShowUpdateCtrls(headerViewHolder, false, false);
                            notifyDataSetChanged();
                        }
                    });
                }
            }
        } catch (Exception e) {
            String s = JobsHandler.getStackTraceString(e);
            objGlobalMethods.writeToFile("Exception at AddressListRouting2_RecycleViewAdapter onBindViewHolder. Exception: " + e.getMessage().toString() + "\nStack: " + s,"","","","");
        }
    }

    private void hideShowUpdateCtrls(SectionHeaderViewHolder headerViewHolder, boolean IsEditing, boolean hideall){
        if (hideall) {
            headerViewHolder.lblCancel.setVisibility(View.GONE);
            headerViewHolder.lblEdit.setVisibility(View.GONE);
            headerViewHolder.lblUpdate.setVisibility(View.GONE);
        } else if (IsEditing) {
            headerViewHolder.lblCancel.setVisibility(View.VISIBLE);
            headerViewHolder.lblEdit.setVisibility(View.GONE);
            headerViewHolder.lblUpdate.setVisibility(View.VISIBLE);
        } else {
            headerViewHolder.lblCancel.setVisibility(View.GONE);
            headerViewHolder.lblEdit.setVisibility(View.VISIBLE);
            headerViewHolder.lblUpdate.setVisibility(View.GONE);
        }
    }

    private void setitemEnableMoveArray(boolean enabled, int Ppos) {
        if (usersList != null && usersList.size() > 0){
            for (int i = Ppos; i <= usersList.size() -1; i++) {
                if (!usersList.get(i).getType().equals("OPS")) {
                    itemEnableMoveArray.put(i, enabled);
                }
            }
        }
    }

    @Override //to prevent messed up animation when use notifyDatasetChanged()
    public long getItemId(int position) {
        return usersList.get(position).hashCode();
    }


    private int getItemPosition(User user){ // (<-- replace with your item)
        int i = 0;
        // (replace with your items and methods here)
        for (User currentuser : usersList) {
            if (currentuser.getId() == user.getId()) break;
            i++;
        }
        return i;
    }

    @Override
    public int getItemCount() {
        return usersList == null ? 0 : usersList.size();
    }

    @Override
    public int getItemViewType(int position) {
        /*if (TextUtils.isEmpty(usersList.get(position).getName())) {
            return HEADER_TYPE;
        } else {
            return USER_TYPE;
        }*/

        int v = USER_TYPE;

        try {
            Boolean isheader = usersList.get(position).getIsHeader().equals("") || usersList.get(position).getIsHeader().equals("t");
            if (isheader) {
                v = HEADER_TYPE;
            }
        } catch (Exception e) {
            String s = e.getMessage().toString();
        }
        return v;
        //return usersList.get(position).getIsHeader().equals("") || usersList.get(position).getIsHeader().equals("t") ? HEADER_TYPE : USER_TYPE;
    }

    public void setUserList(List<User> usersList) {
        this.usersList = usersList;
        notifyDataSetChanged();
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {
        try {
            User targetUser = usersList.get(oldPosition);
            User user = new User(targetUser);
            usersList.remove(oldPosition);
            usersList.add(newPosition, user);
            notifyItemMoved(oldPosition, newPosition);
        } catch (Exception e) {
            String x = JobsHandler.getStackTraceString(e);
        }
    }

    @Override
    public void onViewSwiped(int position) {
        usersList.remove(position);
        notifyItemRemoved(position);
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.touchHelper = touchHelper;
    }

}
