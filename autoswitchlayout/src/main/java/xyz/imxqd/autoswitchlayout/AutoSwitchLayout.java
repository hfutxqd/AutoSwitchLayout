package xyz.imxqd.autoswitchlayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;


public class AutoSwitchLayout extends LinearLayout implements AdapterView.OnItemSelectedListener, RadioGroup.OnCheckedChangeListener, OnItemSelectedListener {

    public static final String VERSION = "1.0.0";
    private static final String TAG = "AutoSwitchLayout";

    AdapterView.OnItemSelectedListener listener = null;

    int targetId = 0;

    public AutoSwitchLayout(Context context) {
        super(context);
        init(context, null);
    }

    public AutoSwitchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutoSwitchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressLint("CustomViewStyleable")
    private void init(Context context, AttributeSet attrs) {

        if (attrs == null) {
            Log.d(TAG, "init with no attrs");
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoSwitchLayout);
        targetId = a.getResourceId(R.styleable.AutoSwitchLayout_attach_view, 0);
        a.recycle();
        Log.d(TAG, "init with attrs");

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        View view = getRootView().findViewById(targetId);
        if (view != null) {
            attachTo(view);
        }

    }

    public void attachTo(View targetView) {
        if (targetView instanceof AdapterView) {
            ((AdapterView) targetView).setOnItemSelectedListener(this);
            Log.d(TAG, "attached to AdapterView");
        } else if (targetView instanceof RadioGroup) {
            ((RadioGroup) targetView).setOnCheckedChangeListener(this);
            Log.d(TAG, "attached to RadioGroup");
        } else {
            throw new RuntimeException("Can not attach to " + targetView.getClass().getName() + "!");
        }
    }

    public void attachTo(SelectedItemObservable observable) {
        observable.setOnItemSelectedListener(this);
    }

    @SuppressLint("CustomViewStyleable")
    public void onItemSelected(int id, int pos) {
        Log.d(TAG, String.format("onItemSelected:%d %d", id, pos));
        int count = getChildCount();
        for (int i = 0;i < count; i++) {
            View view = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            if (layoutParams.showOnIdSelected == id || layoutParams.showOnPositionSelected == pos) {
                view.setVisibility(VISIBLE);
            } else {
                view.setVisibility(GONE);
            }
        }

    }

    @Override
    public void onNothingSelected() {
        onItemSelected(R.id.autoswitch_selected_none, -1);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (this.listener != null) {
            this.listener.onItemSelected(parent, view, position, id);
        }
        onItemSelected(view.getId(), position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (this.listener != null) {
            this.listener.onNothingSelected(parent);
        }
        onNothingSelected();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        View v = group.findViewById(checkedId);
        onItemSelected(checkedId, group.indexOfChild(v));
    }

    public interface SelectedItemObservable {
        void setOnItemSelectedListener(OnItemSelectedListener listener);
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {

        private int showOnPositionSelected;
        private int showOnIdSelected;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.AutoSwitchLayout_Layout);
            this.showOnPositionSelected = a.getInt(R.styleable.AutoSwitchLayout_Layout_layout_show_on_position_selected, -1);
            this.showOnIdSelected = a.getResourceId(R.styleable.AutoSwitchLayout_Layout_layout_show_on_id_selected, 0);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.showOnPositionSelected = -1;
            this.showOnIdSelected = 0;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            if (source instanceof LayoutParams) {
                this.showOnPositionSelected = ((LayoutParams) source).showOnPositionSelected;
                this.showOnIdSelected = ((LayoutParams) source).showOnIdSelected;
            } else {
                this.showOnPositionSelected = -1;
                this.showOnIdSelected = 0;
            }
        }

    }

}
