package com.teixeira.vcspace.editor;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.PopupMenu;

import com.teixeira.vcspace.editor.databinding.LayoutSearcherBinding;
import com.teixeira.vcspace.resources.R;

import io.github.rosemoe.sora.widget.EditorSearcher;
import io.github.rosemoe.sora.widget.EditorSearcher.SearchOptions;

public class SearcherLayout extends LinearLayout implements View.OnClickListener {

    private final LayoutSearcherBinding binding;
    private final PopupMenu optionsMenu;

    private SearchOptions searchOptions = new SearchOptions(true, false);
    private EditorSearcher searcher;
    private boolean isSearching = false;

    public SearcherLayout(Context context) {
        this(context, null);
    }

    public SearcherLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearcherLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SearcherLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        binding = LayoutSearcherBinding.inflate(LayoutInflater.from(context));

        binding.searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                search(editable != null ? editable.toString() : "");
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
        });

        optionsMenu = new PopupMenu(context, binding.searchOptions);
        optionsMenu.getMenu().add(0, 0, 0, R.string.editor_search_option_ignore_case)
            .setCheckable(true)
            .setChecked(true);
        optionsMenu.getMenu().add(0, 1, 0, R.string.editor_search_option_use_regex)
            .setCheckable(true)
            .setChecked(false);

        optionsMenu.setOnMenuItemClickListener(item -> {
            item.setChecked(!item.isChecked());

            boolean ignoreCase = searchOptions.caseInsensitive;
            boolean useRegex = searchOptions.type == SearchOptions.TYPE_REGULAR_EXPRESSION;
            if (item.getItemId() == 0) {
                ignoreCase = item.isChecked();
            } else if (item.getItemId() == 1) {
                useRegex = item.isChecked();
            }

            searchOptions = new SearchOptions(ignoreCase, useRegex);
            search(binding.searchText.getText().toString());
            return true;
        });

        binding.searchOptions.setOnClickListener(this);
        binding.gotoLast.setOnClickListener(this);
        binding.gotoNext.setOnClickListener(this);
        binding.replace.setOnClickListener(this);
        binding.replaceAll.setOnClickListener(this);
        binding.close.setOnClickListener(this);

        addView(binding.getRoot(), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        binding.getRoot().setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == binding.searchOptions.getId()) {
            optionsMenu.show();
        } else if (id == binding.gotoLast.getId()) {
            gotoLast();
        } else if (id == binding.gotoNext.getId()) {
            gotoNext();
        } else if (id == binding.replace.getId()) {
            replace();
        } else if (id == binding.replaceAll.getId()) {
            replaceAll();
        } else if (id == binding.close.getId()) {
            if (isSearching) {
                binding.getRoot().setVisibility(View.GONE);
                isSearching = false;
                if (searcher != null) {
                    searcher.stopSearch();
                }
            }
        }
    }

    public void beginSearchMode() {
        if (!isSearching) {
            binding.getRoot().setVisibility(View.VISIBLE);
            isSearching = true;
            search(binding.searchText.getText().toString());
        }
    }

    public void bindSearcher(EditorSearcher searcher) {
        this.searcher = searcher;
    }

    private void search(String text) {
        if (searcher == null) {
            return;
        }
        if (text != null && !text.isEmpty()) {
            searcher.search(text, searchOptions);
        } else {
            searcher.stopSearch();
        }
    }

    private void gotoLast() {
        try {
            if (searcher != null) {
                searcher.gotoPrevious();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void gotoNext() {
        try {
            if (searcher != null) {
                searcher.gotoNext();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void replace() {
        try {
            if (searcher != null) {
                searcher.replaceThis(binding.replaceText.getText().toString());
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    private void replaceAll() {
        try {
            if (searcher != null) {
                searcher.replaceAll(binding.replaceText.getText().toString());
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
