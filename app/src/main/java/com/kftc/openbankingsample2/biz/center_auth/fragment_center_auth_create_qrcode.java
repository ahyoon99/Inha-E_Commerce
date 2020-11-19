package com.kftc.openbankingsample2.biz.center_auth;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.main.HomeFragment;

import java.util.Locale;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class fragment_center_auth_create_qrcode extends AbstractCenterAuthMainFragment {
    // context
    private Context context;

    // view
    private View view;

    // data
    private Bundle args;

    // timer
    private static final long START_TIME_IN_MILLIS = 30000;
    private TextView mTextViewCountDown;
    private Button mButtonComplete;
    private Button mButtonReset;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    // QR code generator
    EditText qrvalue;
    Button generateBtn,scanBtn;
    ImageView qrImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        args = getArguments();
        if (args == null) args = new Bundle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_center_auth_create_qrcode, container, false);
        initView();
        return view;
    }

    void initView() {

        mTextViewCountDown = (TextView) view.findViewById(R.id.TimerCountDownText);
        mButtonComplete = view.findViewById(R.id.btnCompleteQRCreation);
        mButtonReset = view.findViewById(R.id.btnExtendQRTime);

        mButtonComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseTimer();
                showAlertDialog();
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() { // 시간 연장하기 버튼
            @Override
            public void onClick(View view) {
                resetTimer();
            }
        });

        startTimer();
        updateCountDownText();

        qrImage = view.findViewById(R.id.QRimageView);

        makeQR();

    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                showAlertDialog();
            }
        }.start();

        mTimerRunning = true;
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
        pauseTimer();
        startTimer();
    }

    private void updateCountDownText() {

        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d", seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    void showAlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("QR코드 결제 완료");
        builder.setMessage("확인 버튼을 누르면 메인화면으로 이동합니다.");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context,"판매자 메인화면으로 이동합니다.",Toast.LENGTH_LONG).show();
                        startFragment(SellerHomeFragment.class, args, R.string.fragment_id_seller);
                    }
                });

        builder.show();
    }

    public void makeQR() {
        String data = "1000 어플이름-입금 김오픈 097 232000067812";
        /*
        *금액 1000
        *입금계좌인자내역 어플이름-입금
        *최종수취고객성명 김오픈
        *최종수취고객계좌표준코드 197
        *최종수취고객계좌번호 232000067812
        * */

        if(data.isEmpty()){
            Toast.makeText(context, "value required",Toast.LENGTH_LONG);
        }else {

            QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, 500);
            Bitmap qrBits = qrgEncoder.getBitmap();
            qrImage.setImageBitmap(qrBits);

        }
    }
}