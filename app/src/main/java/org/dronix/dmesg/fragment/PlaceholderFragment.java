package org.dronix.dmesg.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import org.dronix.dmesg.R;
import org.dronix.dmesg.task.DmesgTask;

public  class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<String>{
    private TextView mDmesgTv;
    private ScrollView mScrollView;

    public PlaceholderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDmesgTv = (TextView) view.findViewById(R.id.frag_main_dmesg_tv);
        mScrollView = (ScrollView) view.findViewById(R.id.fgrag_main_dmesg_scrollview);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_dmesg, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.start_and_stop) {
            Loader dmesgLoader = getLoaderManager().getLoader(0);
            if (dmesgLoader != null && dmesgLoader.isStarted()){
                getLoaderManager().destroyLoader(0);
                item.setIcon(android.R.drawable.ic_media_play);
            }
            else{
                getLoaderManager().initLoader(0, null, this);
                item.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
                mDmesgTv.setText("");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    private Handler updateMessageHandler = new Handler( new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            String value =(String) msg.obj;
            mDmesgTv.setText(value);
            mScrollView.fullScroll(View.FOCUS_DOWN);
            return true;
        }
    });


    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new DmesgTask(getActivity(), updateMessageHandler);

    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}