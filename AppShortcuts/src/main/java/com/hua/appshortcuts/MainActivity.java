package com.hua.appshortcuts;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = "MainActivity";

    private static final String ID_ADD_WEBSITE = "add_website";

    private static final String ACTION_ADD_WEBSITE = "com.hua.appshortcuts.ADD_WEBSITE";

    private WebsiteAdapter mAdapter;

    private ShortcutHelper mHelper;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.website_list);

//        mHelper = new ShortcutHelper(this);
//        mHelper.maybeRestoreAllDynamicShortcuts();
//        mHelper.refreshShortcuts(false);
//        if(ACTION_ADD_WEBSITE.equals(getIntent().getAction())) {
//            addWebsite();
//        }

//        mAdapter = new WebsiteAdapter(this);
//        mListView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
//        refreshList();
    }

    private void addWebsite() {
        Log.i(TAG, "addWebsite");
        mHelper.reportShortcutUsed(ID_ADD_WEBSITE);

        final EditText editText = new EditText(this);
        editText.setHint("http://www.baidu.com/");
        editText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_URI);

        new AlertDialog.Builder(this)
                .setTitle("Add new WebSite")
                .setMessage("Type URL of a website")
                .setView(editText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String url = editText.getText().toString().trim();
                        if(url.length() > 0) {
                            addUriAsync(url);
                        }
                    }
                })
                .show();

    }

    private void addUriAsync(String url) {
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                mHelper.addWebSiteShortcut(url);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                refreshList();
            }
        }.execute();
    }

    private void refreshList() {
        mAdapter.setShortcuts(mHelper.getShortcuts());
    }

    public void onAddPressed(View view) {
        addWebsite();
    }

    private String getType(ShortcutInfo shortcut) {
        final StringBuilder sb = new StringBuilder();
        String sep = "";
        if (shortcut.isDynamic()) {
            sb.append(sep);
            sb.append("Dynamic");
            sep = ", ";
        }
        if (shortcut.isPinned()) {
            sb.append(sep);
            sb.append("Pinned");
            sep = ", ";
        }
        if (!shortcut.isEnabled()) {
            sb.append(sep);
            sb.append("Disabled");
            sep = ", ";
        }
        return sb.toString();
    }

    private static final List<ShortcutInfo> EMPTY_LIST = new ArrayList<>();

    @Override
    public void onClick(View view) {
        final ShortcutInfo shortcut = (ShortcutInfo) ((View)view.getParent()).getTag();

        switch (view.getId()) {
            case R.id.disable:
                if (shortcut.isEnabled()) {
                    mHelper.disableShortcut(shortcut);
                } else {
                    mHelper.enableShortcut(shortcut);
                }
                refreshList();
                break;
            case R.id.remove:
                mHelper.removeShortcut(shortcut);
                refreshList();
                break;
        }
    }

    public void onToNext(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }

    private class WebsiteAdapter extends BaseAdapter {

        private final Context mContext;
        private final LayoutInflater mLayoutInflater;
        private List<ShortcutInfo> mList = EMPTY_LIST;

        public WebsiteAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }

        public void setShortcuts(List<ShortcutInfo> list){
            mList = list;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = mLayoutInflater.inflate(R.layout.list_item, null);
            }

            bindView(view, position, mList.get(position));

            return view;
        }

        public void bindView(View view, int position, ShortcutInfo shortcut) {
            view.setTag(shortcut);

            final TextView line1 = (TextView) view.findViewById(R.id.line1);
            final TextView line2 = (TextView) view.findViewById(R.id.line2);

            line1.setText(shortcut.getLongLabel());

            line2.setText(getType(shortcut));

            final Button remove = (Button) view.findViewById(R.id.remove);
            final Button disable = (Button) view.findViewById(R.id.disable);

            disable.setText(shortcut.isEnabled() ? R.string.disable_shortcut : R.string.enable_shortcut);

            remove.setOnClickListener(MainActivity.this);
            disable.setOnClickListener(MainActivity.this);
        }
    }
}
