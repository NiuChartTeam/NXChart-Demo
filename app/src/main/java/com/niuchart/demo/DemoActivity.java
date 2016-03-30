/**
 * 2015 Hangzhou LongKui software Inc. All rights reserved.
 */
package com.niuchart.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.niuchart.*;
import com.niuchart.core.define.NXConstants;
import com.niuchart.core.drawing.NXColor;
import com.niuchart.core.drawing.NXLineStyle;
import com.niuchart.core.utils.NXResourceUtil;
import com.niuchart.dashboard.paper.NXReportActivity;
import com.niuchart.json.JSONArray;
import com.niuchart.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends ActionBarActivity {
    private static final String TAG = DemoActivity.class.getSimpleName();
    private static final String FIRST_INSTALL = "first_install";
    private static final String PREFERENCES_KEY = "preference_key";
    private static final String OPEN_INDEX = "open_index";
    private static final String REPORT_FOLDER_NAME_IN_ASSET = "report";
    //调试模式下json与db会每次启动都会zcopy到外存,如需要更改json配置文件后看效果，可将其设为true,启动时会另起线程copy file.
    private static final boolean IS_DEBUG = false;
    //recyclerView的数据
    List<RowItem> mItems;
    private RecyclerView mList;
    private Paint mPaint = new Paint();

    private void initItems() {
        try {
            final String baseDir = getFilesDir() + File.separator + REPORT_FOLDER_NAME_IN_ASSET;
            File itemsFile = new File(baseDir, "items.json");
            if (!itemsFile.exists()) {
                return;
            }
            InputStream inputStream = new FileInputStream(itemsFile);

            String itemsJson = new String(IOUtility.readBytes(inputStream), "UTF-8");
            JSONArray jsonArray = JSONObject.parseArray(itemsJson);
            mItems = new ArrayList<>();
            for (int i = 0; jsonArray != null && i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("itemName");
                String id = jsonObject.getString("itemId");
                String desc = jsonObject.getString("description");
                String reportName = jsonObject.getString("reportName");
                RowItem item = new RowItem(name, desc, id, reportName);
                mItems.add(item);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

    private void initList() {
        class ListViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;
            public TextView mSubTextView;

            public ListViewHolder(View view) {
                super(view);
                mTextView = (TextView) view.findViewById(R.id.itemText);
                mSubTextView = (TextView) view.findViewById(R.id.subItemText);
            }
        }
        mList = (RecyclerView) findViewById(R.id.backgroundView);

        final NXLineStyle frameBorder = new NXLineStyle(this);
        frameBorder.setLineWidth(NXResourceUtil.dip2px(this, 1));
        frameBorder.setLineColor(NXColor.parseColor(NXConstants.TEXT_COLOR_A));

        mList.addItemDecoration(new RecyclerView.ItemDecoration() {

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View itemView = parent.getChildAt(i);
                    int bottom = itemView.getBottom();
                    float[] line = new float[]{itemView.getLeft(), bottom, itemView.getRight(), bottom};
                    frameBorder.drawLine(line, c, mPaint);
                }
            }
        });
        mList.setAdapter(new RecyclerView.Adapter<ListViewHolder>() {

            @Override
            public ListViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false);
                ListViewHolder vh = new ListViewHolder(view);
                return vh;
            }

            @Override
            public void onBindViewHolder(ListViewHolder holder, int position) {
                RowItem item = mItems.get(position);
                holder.mTextView.setText(item.getName());
                holder.mSubTextView.setText(item.getDesc());
            }

            @Override
            public int getItemCount() {
                return mItems == null ? 0 : mItems.size();
            }

        });
        mList.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        openByIdName(mItems.get(position));
                        setOpenIndex(position);
                    }
                })
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        //自定义actionbar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        mPaint.setAntiAlias(true);

        setContentView(R.layout.demo_activity);
        initList();
        //首次运行时copy asset下的report到interal Storage
        if (isFirstInstall() || IS_DEBUG) {
            AsyncTask<String, Integer, Boolean> task = new ReportCopyTask();
            task.execute();
        } else {
            initItems();
        }
    }

    private void openByIdName(RowItem item) {
        final String baseDir = getFilesDir() + File.separator + REPORT_FOLDER_NAME_IN_ASSET + File.separator + item.getId();
        Intent intent = new Intent(DemoActivity.this, NXReportActivity.class);
        intent.putExtra(NXReportActivity.NX_BASE_URL, baseDir);
        intent.putExtra(NXReportActivity.NX_JSON_CONFIG_FILE_NAME, item.getReportName());
        //"crmAESEncrypt.json是加密的

        //经aes加密的json配置文件请加上下面二行
        //intent.putExtra(NXReportActivity.NX_DECODE_KEY, "password");
        //intent.putExtra(NXReportActivity.NX_IV_KEY, "16位aes向量");

        startActivity(intent);
    }

    public boolean isFirstInstall() {
        return getApplication().getSharedPreferences(PREFERENCES_KEY, Activity.MODE_PRIVATE).getInt(FIRST_INSTALL, -1) != BuildConfig.VERSION_CODE;
    }

    public void setFirstInstall(int firstInstall) {
        SharedPreferences.Editor editor = getApplication().getSharedPreferences(PREFERENCES_KEY, Activity.MODE_PRIVATE).edit();
        editor.putInt(FIRST_INSTALL, BuildConfig.VERSION_CODE);
        editor.commit();
    }

    private int getOpenIndex() {
        return getApplication().getSharedPreferences(PREFERENCES_KEY, Activity.MODE_PRIVATE).getInt(OPEN_INDEX, 0);
    }

    private void setOpenIndex(int index) {
        SharedPreferences.Editor editor = getApplication().getSharedPreferences(PREFERENCES_KEY, Activity.MODE_PRIVATE).edit();
        editor.putInt(OPEN_INDEX, index);
        editor.commit();
    }

    private class ReportCopyTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            TextView actionTitle = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.action_bar_title);
            actionTitle.setText("Copying Test Reports..");
            for (int i = 0; i < mList.getChildCount(); i++) {
                View button = mList.getChildAt(i);
                button.setVisibility(View.GONE);
            }
        }

        @Override
        protected Boolean doInBackground(String... input) {
            try {
                String reportFolderName = REPORT_FOLDER_NAME_IN_ASSET;//与asset下的名字对应
                String baseDir = getFilesDir().getAbsolutePath();
                File rootFile = new File(baseDir + File.separator + reportFolderName);
                if (rootFile.exists()) {
                    IOUtility.deleteFolderOrFile(rootFile);
                }
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    IOUtility.copyFileOrDir(DemoActivity.this, reportFolderName, baseDir);
                }
                return true;
            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                setFirstInstall(BuildConfig.VERSION_CODE);
                initItems();
                for (int i = 0; i < mList.getChildCount(); i++) {
                    View row = mList.getChildAt(i);
                    row.setVisibility(View.VISIBLE);
                }
                TextView actionTitle = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.action_bar_title);
                actionTitle.setText(R.string.app_name);
                mList.getAdapter().notifyDataSetChanged();
//                if (IS_DEBUG) {
//                    openByIdName(mItems.get(getOpenIndex()));
//                }
            }
        }
    }

    public List<RowItem> getItems() {
        return mItems;
    }

    public static String getReportFolderNameInAsset() {
        return REPORT_FOLDER_NAME_IN_ASSET;
    }
}
