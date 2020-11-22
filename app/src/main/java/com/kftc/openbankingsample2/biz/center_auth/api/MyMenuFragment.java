package com.kftc.openbankingsample2.biz.center_auth.api;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthCreateQRCodeFragment;

/**
 * 메뉴판
 */
public class MyMenuFragment extends AbstractCenterAuthMainFragment implements View.OnClickListener {

    // context
    private Context context;

    // view
    private View view;

    // data
    private Bundle args;

    private static final String total_price_arg ="0";

    // 각 메뉴의 가격
    private int price1 = 4000;
    private int price2 = 5000;
    private int price3 = 5000;
    private int price4 = 6000;
    private int price5 = 1000;
    private int price6 = 1000;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        args = getArguments();
        if (args == null) args = new Bundle();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);
        initView();
        return view;
    }

    void initView() {

        // 각 메뉴의 수량을 입력할 EditText
        EditText e1 = view.findViewById(R.id.editText1);
        EditText e2 = view.findViewById(R.id.editText2);
        EditText e3 = view.findViewById(R.id.editText3);
        EditText e4 = view.findViewById(R.id.editText4);
        EditText e5 = view.findViewById(R.id.editText5);
        EditText e6 = view.findViewById(R.id.editText6);

        // QR코드 만들기 버튼
        Button btn1 = (Button) view.findViewById(R.id.btnPay);
        btn1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 각 메뉴의 수량을 integer타입의 변수에 넣어준다.
                int num1 = Integer.parseInt(e1.getText().toString());
                int num2 = Integer.parseInt(e2.getText().toString());
                int num3 = Integer.parseInt(e3.getText().toString());
                int num4 = Integer.parseInt(e4.getText().toString());
                int num5 = Integer.parseInt(e5.getText().toString());
                int num6 = Integer.parseInt(e6.getText().toString());

                // 메뉴 수량 * 메뉴 가격을 모두 더해 총 가격을 계산해준다.
                int tot_pri = num1 * price1 + num2 * price2 + num3 * price3 + num4 * price4 + num5 * price5 + num6 * price6;
                // 총 가격을 String 타입으로 형변환 해준다.
                String str_tot_pri = Integer.toString(tot_pri);

                show(str_tot_pri);
            }

            // 총 가격을 받아서 AlertDialog를 해준다.
            void show(String str_tot_pri) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("결제 금액 확인");
                builder.setMessage("총 금액은 " + str_tot_pri + "입니다. 계속 진행 하시겠습니까? ");
                // AlertDialog에서 "예" 버튼을 클릭 할 경우 QR코드 화면으로 이동한다.
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                args.putString("total_price_arg", str_tot_pri);
                                setArguments(args);
                                startFragment(CenterAuthCreateQRCodeFragment.class, args, R.string.fragment_create_qrcode_to_withdraw);
                            }
                        });
                // AlertDialog에서 "아니오" 버튼을 클릭 할 경우 아무일도 일어나지 않는다.
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                }
                        });
                builder.show();
            }
        });
    }

    @Override
    public void onClick(View view) {
    }
}