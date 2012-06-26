package com.emerginggames.snappers;

import android.os.Bundle;

/**
 * Created by IntelliJ IDEA.
 * User: babay
 * Date: 31.03.12
 * Time: 15:01
 */
public class PaginatedSelectorActivity extends BaseActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_selector);

        findViewById(R.id.indicator).setPadding(defPadding, defPadding, defPadding, 0);

        setupElements();
    }
}