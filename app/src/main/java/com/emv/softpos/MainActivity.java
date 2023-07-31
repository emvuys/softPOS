package com.emv.softpos;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextWatcher;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText;
    private final Button[] buttons = new Button[10];
    private static final int MSG_NFC_CARD_FOUND = 1;
    private static final int MSG_NFC_CARD_NOT_FOUND = 2;
    private NfcAdapter mNfcAdapter;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取EditText和按钮
        editText = findViewById(R.id.edit_text);
        Button buttonClear = findViewById(R.id.button_clear);
        Button buttonDelete = findViewById(R.id.button_delete);
        Button buttonPay = findViewById(R.id.button_pay);
        buttons[0] = findViewById(R.id.button_0);
        buttons[1] = findViewById(R.id.button_1);
        buttons[2] = findViewById(R.id.button_2);
        buttons[3] = findViewById(R.id.button_3);
        buttons[4] = findViewById(R.id.button_4);
        buttons[5] = findViewById(R.id.button_5);
        buttons[6] = findViewById(R.id.button_6);
        buttons[7] = findViewById(R.id.button_7);
        buttons[8] = findViewById(R.id.button_8);
        buttons[9] = findViewById(R.id.button_9);

        // 为按钮添加点击事件侦听器
        buttonClear.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
        buttonPay.setOnClickListener(this);
        for (Button button : buttons) {
            button.setOnClickListener(this);
        }
        // 添加TextWatcher以侦听EditText的文本更改
        editText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable s) {
                String amountString = s.toString();
                if (!amountString.isEmpty()) {
                    double amount = Double.parseDouble(amountString);
                    amount /= 100.0;
                    DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                    String formattedAmount = decimalFormat.format(amount);
                    editText.removeTextChangedListener(this);
                    editText.setText(formattedAmount);
                    editText.setSelection(formattedAmount.length());
                    editText.addTextChangedListener(this);
                }
            }
        });
        // 获取NfcAdapter实例
        NfcManager nfcManager = (NfcManager) getSystemService(NFC_SERVICE);
        mNfcAdapter = nfcManager.getDefaultAdapter();
        // 创建Handler处理消息
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_NFC_CARD_FOUND:
                        // 执行支付操作
                        executePayment();
                        break;
                    case MSG_NFC_CARD_NOT_FOUND:
                        // 显示寻卡超时提醒
                        showTimeoutReminder();
                        break;
                }
                return true;
            }
        });

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_clear:
                // 点击“Clear”按钮时，将EditText的文本设置为0.00
                editText.setText("0.00");
                break;
            case R.id.button_delete:
                // 点击“Delete”按钮时，删除EditText中的最后一个字符
                String currentText = editText.getText().toString();
                if (currentText.length() > 0) {
                    String newText = currentText.substring(0, currentText.length() - 1);
                    if (newText.isEmpty()) {
                        newText = "0.00";
                    }
                    editText.setText(newText);
                }
                break;
            case R.id.button_pay:
                // 点击“Pay”按钮时，执行支付操作（此处略）
                if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) {
                    Toast.makeText(MainActivity.this, "请打开手机NFC功能", Toast.LENGTH_SHORT).show();
                    return;
                }
                // 开始寻卡
                startNfcCardDetection();
                // 设置超时处理
                break;
            default:
                // 点击数字按钮时，在EditText中添加相应的数字
                Button button = (Button) v;
                String digit = button.getText().toString();
                editText.setText(digit);
                break;
        }
    }

    private void startNfcCardDetection() {
        // 在后台线程中执行开始寻卡的操作
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 执行开始寻卡的逻辑
                // ...

                // 模拟寻卡过程，这里使用了休眠5秒钟来模拟寻卡耗时
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 寻卡完成后，可以通过Handler发送消息通知主线程
                mHandler.sendEmptyMessage(MSG_NFC_CARD_FOUND);
            }
        });
        thread.start();

        // 30秒后检测是否找到NFC卡
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 如果30秒内没有找到NFC卡，发送消息通知主线程
                mHandler.sendEmptyMessage(MSG_NFC_CARD_NOT_FOUND);
            }
        }, 30000);
    }

    private void executePayment() {
        // 执行支付操作
        // ...

        // 显示支付成功提示
        Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show();
    }

    private void showTimeoutReminder() {
        // 显示寻卡超时提醒
        Toast.makeText(this, "NFC card detection timeout", Toast.LENGTH_SHORT).show();
    }

}