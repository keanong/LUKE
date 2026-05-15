package com.xdel.luke;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class NoPasteEditText extends androidx.appcompat.widget.AppCompatEditText {
    private boolean isTyping = false;
    private String lastText = "";
    private int lastCursorPosition = 0;
    private boolean isResetting = false;

    public NoPasteEditText(Context context) {
        super(context);
        init();
    }

    public NoPasteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoPasteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Disable long click and text selection
        setLongClickable(false);
        setTextIsSelectable(false);

        // Add text watcher to detect pasting
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isResetting) {
                    // If user is typing one character at a time, this is normal input
                    isTyping = (after == 1 && count == 0);
                    lastText = s.toString();
                    lastCursorPosition = getSelectionStart();
                    // Make sure cursor position is valid
                    if (lastCursorPosition < 0) {
                        lastCursorPosition = 0;
                    }
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No implementation needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Skip if we're in the middle of resetting the text
                if (isResetting) {
                    return;
                }

                // If multiple characters were added at once and we weren't typing,
                // it's likely a paste operation
                if (!isTyping && s.length() > lastText.length() + 1) {
                    isResetting = true;
                    try {
                        // Revert to previous text
                        setText(lastText);

                        // Ensure the cursor position is valid
                        int safePosition = Math.min(lastCursorPosition, lastText.length());
                        if (safePosition >= 0 && !lastText.isEmpty()) {
                            setSelection(safePosition);
                        }

                        // Optionally show a message to the user
                        Toast.makeText(getContext(), "Pasting is not allowed", Toast.LENGTH_SHORT).show();
                    } finally {
                        isResetting = false;
                    }
                }
            }
        });

        // Disable clipboard operations
        setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Remove paste and other clipboard options
                if (menu != null) {
                    menu.removeItem(android.R.id.paste);
                    menu.removeItem(android.R.id.cut);
                    menu.removeItem(android.R.id.copy);
                }
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // No implementation needed
            }
        });
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        // Prevent paste, cut, and copy operations
        if (id == android.R.id.paste || id == android.R.id.cut || id == android.R.id.copy) {
            Toast.makeText(getContext(), "This operation is not allowed", Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.onTextContextMenuItem(id);
    }

    @Override
    public boolean isSuggestionsEnabled() {
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Detect Ctrl+V key combination
        if (keyCode == KeyEvent.KEYCODE_V && (event.getMetaState() & KeyEvent.META_CTRL_ON) != 0) {
            Toast.makeText(getContext(), "Pasting is not allowed", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
