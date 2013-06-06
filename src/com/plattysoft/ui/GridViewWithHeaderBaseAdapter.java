package com.plattysoft.ui;

import android.content.Context;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

/**
 * Created by shalafi on 6/6/13.
 */
public abstract class GridViewWithHeaderBaseAdapter extends BaseAdapter {

    public interface GridItemClickListener {
    	
		void onGridItemClicked(View v, int position, long itemId);
		
	}

	private class ListItemClickListener implements OnClickListener {

		private int mPosition;

		public ListItemClickListener(int currentPos) {
			mPosition = currentPos;
		}

		@Override
		public void onClick(View v) {
			onGridItemClicked (v, mPosition);
		}

	}

	private int mNumColumns;
	private Context mContext;
	private GridItemClickListener mGridItemClickListener;

    public GridViewWithHeaderBaseAdapter(Context context) {
    	mContext = context;
        mNumColumns = 1;
    }
    
	public final void setOnGridClickListener(GridItemClickListener listener) {
		mGridItemClickListener = listener;
	}
    
    private final void onGridItemClicked(View v, int position) {
    	if (mGridItemClickListener != null) {
    		mGridItemClickListener.onGridItemClicked(v, position, getItemId(position));
    	}
	}

	@Override
    public final int getCount() {
        return (int) Math.ceil(getItemCount()*1f / getNumColumns());
    }

    public abstract int getItemCount();

    protected abstract View getItemView(int position, View view, ViewGroup parent);

    @Override
    public final View getView(int position, View view, ViewGroup viewGroup) {
    	LinearLayout layout;
    	int columnWidth = 0;
    	if (viewGroup !=  null) {
    		columnWidth = viewGroup.getWidth()/mNumColumns;    		
    	}
        // Make it be rows of the number of columns
        if (view == null) {
            // This is items view
        	layout = new LinearLayout(mContext);
        	layout.setOrientation(LinearLayout.HORIZONTAL);
        	// Here the view is not measured, get the width of the parent if not null
            // Now add the sub views to it
            for (int i = 0; i < mNumColumns; i++) {
            	int currentPos = position * mNumColumns + i;
            	// Get the new View
            	View insideView;
            	if (currentPos < getItemCount()) {            		
            		insideView = getItemView(currentPos, null, viewGroup);            	
            	}
            	else {
            		insideView = new View(mContext);
        			layout.addView(insideView);
            	}            	
            	layout.addView(insideView);
            	// Set the width of this column
        		LayoutParams params = insideView.getLayoutParams();
        		params.width = columnWidth;
        		insideView.setLayoutParams(params);
            }            
        }
        else {
        	layout = (LinearLayout) view;
        	if (columnWidth == 0) {
        		columnWidth = layout.getWidth()/mNumColumns;
        	}
        	for (int i = 0; i < mNumColumns; i++) {
        		int currentPos = position * mNumColumns + i;
        		View insideView = layout.getChildAt(i);
        		// If there are less views than objects. add a view here
        		if (insideView == null) {
        			insideView = new View(mContext);
        			layout.addView(insideView);
        		}
        		// Set the width of this column
        		LayoutParams params = insideView.getLayoutParams();
        		params.width = columnWidth;
        		insideView.setLayoutParams(params);
        		
        		if (currentPos < getItemCount()) {
        			insideView.setVisibility(View.VISIBLE);
        			// Populate the view
        			View theView = getItemView(currentPos, insideView, viewGroup);
        			theView.setOnClickListener(new ListItemClickListener (currentPos));
        			if (!theView.equals(insideView)) {
        				// DO NOT CHANGE THE VIEWS
        			}
        		}
        		else {
        			insideView.setVisibility(View.INVISIBLE);
        		}
        	}
        }
        
        return layout;
    }

    public final int getNumColumns() {
        return mNumColumns;
    }

    public final void setNumColumns(int numColumns) {
        mNumColumns = numColumns;
        notifyDataSetChanged();
    }
}
