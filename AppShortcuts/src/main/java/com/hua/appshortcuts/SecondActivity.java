package com.hua.appshortcuts;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private ShortcutManager mShortcutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        mShortcutManager = getSystemService(ShortcutManager.class);
    }

    public void onCreateDynamicShortcut(View view) {
        Uri uri = Uri.parse("http://www.youku.com");
        ShortcutInfo.Builder builder = new ShortcutInfo.Builder(this, "youku_id");
        builder.setShortLabel("youku");
        builder.setLongLabel(new String("优酷(www.youku.com)"));
        builder.setIcon(Icon.createWithResource(this, R.drawable.add));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        builder.setIntent(intent);
        mShortcutManager.setDynamicShortcuts(Collections.singletonList(builder.build()));
    }

    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();

    }

    public void onMultiIntent(View view) {
        ShortcutInfo.Builder builder = new ShortcutInfo.Builder(this, "thirdly_activity");
        builder.setShortLabel("thirdly_activity");
        builder.setIcon(Icon.createWithResource(this, R.drawable.add));
        Intent[] intents = new Intent[]{
                new Intent(Intent.ACTION_VIEW, Uri.EMPTY, this, ThirdlyActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK),
//                new Intent(ThirdlyActivity.ACTION)
                new Intent(Intent.ACTION_VIEW, Uri.EMPTY, this, SecondActivity.class)
        };
        builder.setIntents(intents);
        mShortcutManager.setDynamicShortcuts(Collections.singletonList(builder.build()));
    }

    public void onRank(View view) {
        ShortcutInfo.Builder secondBuilder = new ShortcutInfo.Builder(this, "SecondActivity");
        secondBuilder.setShortLabel("SecondActivity");
        secondBuilder.setIntent(new Intent(Intent.ACTION_VIEW, Uri.EMPTY, this, SecondActivity.class));
        secondBuilder.setRank(1);

        ShortcutInfo.Builder thirdlyBuilder = new ShortcutInfo.Builder(this, "ThirdlyActivity");
        thirdlyBuilder.setShortLabel("ThirdlyActivity");
        thirdlyBuilder.setIntent(new Intent(Intent.ACTION_VIEW, Uri.EMPTY, this, ThirdlyActivity.class));
        thirdlyBuilder.setRank(0);
        mShortcutManager.setDynamicShortcuts(Arrays.asList(secondBuilder.build(), thirdlyBuilder.build()));
    }

    public void onUpdateShortcuts(View view) {
        ShortcutInfo.Builder firstBuilder = new ShortcutInfo.Builder(this, "MainActivity");
        firstBuilder.setShortLabel("MainActivity");
        firstBuilder.setIntent(new Intent(Intent.ACTION_VIEW, Uri.EMPTY, this, MainActivity.class));
        firstBuilder.setRank(2);

        ShortcutInfo.Builder secondBuilder = new ShortcutInfo.Builder(this, "SecondActivity");
        secondBuilder.setShortLabel("SecondActivity");
        secondBuilder.setIntent(new Intent(Intent.ACTION_VIEW, Uri.EMPTY, this, SecondActivity.class));
        secondBuilder.setRank(0);

        ShortcutInfo.Builder thirdlyBuilder = new ShortcutInfo.Builder(this, "ThirdlyActivity");
        thirdlyBuilder.setShortLabel("ThirdlyActivity");
        thirdlyBuilder.setIntent(new Intent(Intent.ACTION_VIEW, Uri.EMPTY, this, ThirdlyActivity.class));
        thirdlyBuilder.setRank(1);

        mShortcutManager.updateShortcuts(Arrays.asList(firstBuilder.build(), secondBuilder.build(), thirdlyBuilder.build()));
    }



    public List<ShortcutInfo> getShortcuts() {
        final List<ShortcutInfo> ret = new ArrayList<>();
        final HashSet<String> seenKeys = new HashSet<>();

        for(ShortcutInfo shortcut : mShortcutManager.getDynamicShortcuts()) {
            if(!shortcut.isImmutable()) {
                ret.add(shortcut);
                seenKeys.add(shortcut.getId());
            }
        }

        for(ShortcutInfo shortcut : mShortcutManager.getPinnedShortcuts()) {
            if(!shortcut.isImmutable() && !seenKeys.contains(shortcut.getId())) {
                ret.add(shortcut);
                seenKeys.add(shortcut.getId());
            }
        }
        return ret;
    }

    public void onRemoveAll(View view) {
        mShortcutManager.removeAllDynamicShortcuts();
    }

    public void onDisable(View view) {
        mShortcutManager.disableShortcuts(Collections.singletonList("SecondActivity"), "Can't click");
    }

    public void onEnable(View view) {
        mShortcutManager.enableShortcuts(Collections.singletonList("SecondActivity"));
    }
}
