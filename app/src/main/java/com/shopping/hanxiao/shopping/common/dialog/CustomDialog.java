package com.shopping.hanxiao.shopping.common.dialog;

/**
 * Created by wenzhi on 17/9/18.
 */

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shopping.hanxiao.shopping.R;
import com.shopping.hanxiao.shopping.utils.ScreenInfoUtil;

public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
        }
        // 锁定dialog宽度
        getWindow().setLayout(ScreenInfoUtil.getScreenWidth() - ScreenInfoUtil.dpToPx(80), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public static class DialogBuilder {
        private int customTheme;
        private String title;
        private String msg;
        private int msgGravity;
        private int msgLeftMargin;
        private int contentLayoutId;
        private String okBtnText;
        private int okBtnTextColor;
        private String cancelBtnText;
        private int cancelBtnTextColor;
        private int okBtnBg;
        private int cancelBtnBg;

        private boolean cancelableWhenTouchOutside = true;
        private View.OnClickListener okBtnClickListener;
        private View.OnClickListener cancelBtnClickListener;

        protected CustomDialog dialog;
        protected Context context;
        protected LayoutInflater inflater;

        public DialogBuilder(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        public DialogBuilder setCustomTheme(int theme) {
            customTheme = theme;
            return this;
        }

        public DialogBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public DialogBuilder setTitle(int resId) {
            if (resId != 0) {
                title = context.getString(resId);
            }
            return this;
        }

        public DialogBuilder setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public DialogBuilder setMsg(int resId) {
            if (resId != 0) {
                msg = context.getString(resId);
            }
            return this;
        }

        public DialogBuilder setMsgGravity(int gravity) {
            msgGravity = gravity;
            return this;
        }

        public DialogBuilder setMsgLeftMargin(int leftMargin) {
            msgLeftMargin = leftMargin;
            return this;
        }

        public DialogBuilder setCustomContentLayout(int contentLayoutId) {
            this.contentLayoutId = contentLayoutId;
            return this;
        }

        public DialogBuilder setOkBtn(String okBtnText, View.OnClickListener okBtnClickListener) {
            this.okBtnText = okBtnText;
            this.okBtnClickListener = okBtnClickListener;
            return this;
        }

        public DialogBuilder setOkBtn(int resId, View.OnClickListener okBtnClickListener) {
            if (resId != 0) {
                okBtnText = context.getString(resId);
            }
            this.okBtnClickListener = okBtnClickListener;
            return this;
        }

        public DialogBuilder setOkBtnTextColor(int color) {
            okBtnTextColor = color;
            return this;
        }

        public DialogBuilder setOkBtnBg(int bgId) {
            if (bgId != 0) {
                okBtnBg = bgId;
            }
            return this;
        }

        public DialogBuilder setCancelBtn(String cancelBtnText, View.OnClickListener cancelBtnClickListener) {
            this.cancelBtnText = cancelBtnText;
            this.cancelBtnClickListener = cancelBtnClickListener;
            return this;
        }

        public DialogBuilder setCancelBtn(int resId, View.OnClickListener cancelBtnClickListener) {
            if (resId != 0) {
                cancelBtnText = context.getString(resId);
            }
            this.cancelBtnClickListener = cancelBtnClickListener;
            return this;
        }

        public DialogBuilder setCancelableWhenTouchOutside(boolean cancelable) {
            this.cancelableWhenTouchOutside = cancelable;
            return this;
        }

        public DialogBuilder setCancelBtnBg(int bgId) {
            if (bgId != 0) {
                cancelBtnBg = bgId;
            }
            return this;
        }

        public DialogBuilder setCancelBtnTextColor(int color) {
            cancelBtnTextColor = color;
            return this;
        }

        public CustomDialog build() {
            dialog = new CustomDialog(context, customTheme != 0 ? customTheme : R.style.CustomDialog);
            setupDialog();
            return dialog;
        }

        protected void setupDialog() {
            View dialogLayoutRoot = inflater.inflate(R.layout.cosutom_base_dialog, null);

            if (!TextUtils.isEmpty(title)) {
                TextView titleTv = (TextView) dialogLayoutRoot.findViewById(R.id.pf_dialog_title);
                titleTv.setText(title);
                titleTv.setVisibility(View.VISIBLE);
            }

            if (!TextUtils.isEmpty(msg)) {
                TextView msgTv = (TextView) dialogLayoutRoot.findViewById(R.id.pf_dialog_msg);
                msgTv.setText(msg);
                msgTv.setVisibility(View.VISIBLE);

                if (msgGravity != 0) {
                    msgTv.setGravity(msgGravity);
                }

                if (msgLeftMargin != 0) {
                    LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) msgTv.getLayoutParams();
                    lp.leftMargin = msgLeftMargin;
                    msgTv.setLayoutParams(lp);
                }
            }

            FrameLayout contentContainer = (FrameLayout) dialogLayoutRoot.findViewById(R.id.pf_dialog_content_container);
            if (contentLayoutId != 0) {
                inflater.inflate(contentLayoutId, contentContainer);
            } else {
                contentContainer.setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(okBtnText) && TextUtils.isEmpty(cancelBtnText)) {
                dialogLayoutRoot.findViewById(R.id.pf_dialog_btn_container).setVisibility(View.GONE);
            } else {
                Button okBtn = (Button) dialogLayoutRoot.findViewById(R.id.pf_dialog_okBtn);
                if (!TextUtils.isEmpty(okBtnText)) {
                    okBtn.setText(okBtnText);
                    okBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (okBtnClickListener != null) {
                                okBtnClickListener.onClick(v);
                            }
                            dialog.dismiss();
                        }
                    });
                    if (okBtnBg != 0) {
                        okBtn.setBackgroundResource(okBtnBg);
                    }
                    if (okBtnTextColor != 0) {
                        okBtn.setTextColor(okBtnTextColor);
                    }

                } else {
                    okBtn.setVisibility(View.GONE);
                }
                Button cancelBtn = (Button) dialogLayoutRoot.findViewById(R.id.pf_dialog_cancelBtn);
                if (!TextUtils.isEmpty(cancelBtnText)) {
                    cancelBtn.setText(cancelBtnText);
                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (cancelBtnClickListener != null) {
                                cancelBtnClickListener.onClick(v);
                            }
                            dialog.dismiss();
                        }
                    });
                    if (cancelBtnBg != 0) {
                        cancelBtn.setBackgroundResource(cancelBtnBg);
                    }
                    if (cancelBtnTextColor != 0) {
                        cancelBtn.setTextColor(cancelBtnTextColor);
                    }
                } else {
                    cancelBtn.setVisibility(View.GONE);
                }
            }

            dialog.setContentView(dialogLayoutRoot);
            dialog.setCanceledOnTouchOutside(cancelableWhenTouchOutside);
            // 同样通过这个变量来控制back key关闭dialog的行为
            dialog.setCancelable(cancelableWhenTouchOutside);
        }
    }
}


