package com.kftc.openbankingsample2.biz.center_auth.api;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.EditTextPreference;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthConst;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthHomeFragment;
import com.kftc.openbankingsample2.biz.center_auth.api.user_me.CenterAuthAPIUserMeResultFragment;
import com.kftc.openbankingsample2.biz.center_auth.fragment_center_auth_create_qrcode;
import com.kftc.openbankingsample2.biz.center_auth.http.CenterAuthApiRetrofitAdapter;
import com.kftc.openbankingsample2.biz.center_auth.util.CenterAuthUtils;
import com.kftc.openbankingsample2.biz.main.MainActivity;
import com.kftc.openbankingsample2.common.Scope;
import com.kftc.openbankingsample2.common.application.AppData;
import com.kftc.openbankingsample2.common.data.ApiCallTransferDepositResponse;
import com.kftc.openbankingsample2.common.data.ResMsg;
import com.kftc.openbankingsample2.common.data.Transfer;
import com.kftc.openbankingsample2.common.util.TwoString;
import com.kftc.openbankingsample2.common.util.Utils;
import com.kftc.openbankingsample2.common.util.view.KmUtilMoneyEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 메뉴판
 */
public class my_menu extends AbstractCenterAuthMainFragment implements View.OnClickListener {

    // context
    private Context context;

    // view
    private View view;

    // data
    private Bundle args;


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

        //FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);
        initView();
        return view;
    }

    void initView() {

        EditText e1 = view.findViewById(R.id.editText1);
        EditText e2 = view.findViewById(R.id.editText2);
        EditText e3 = view.findViewById(R.id.editText3);
        EditText e4 = view.findViewById(R.id.editText4);
        EditText e5 = view.findViewById(R.id.editText5);
        EditText e6 = view.findViewById(R.id.editText6);

        // int num1 = Integer.parseInt(e1.getText().toString());

        Button btn1 = (Button) view.findViewById(R.id.btnPay);
        btn1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num1 = Integer.parseInt(e1.getText().toString());
                int num2 = Integer.parseInt(e2.getText().toString());
                int num3 = Integer.parseInt(e3.getText().toString());
                int num4 = Integer.parseInt(e4.getText().toString());
                int num5 = Integer.parseInt(e5.getText().toString());
                int num6 = Integer.parseInt(e6.getText().toString());

                int tot_pri = num1 * price1 + num2 * price2 + num3 * price3 + num4 * price4 + num5 * price5 + num6 * price6;
                String str_tot_pri = Integer.toString(tot_pri);
                show(str_tot_pri);
            }

            void show(String str_tot_pri) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("결제 금액 확인");
                builder.setMessage("총 금액은 " + str_tot_pri + "입니다. 계속 진행 하시겠습니까? ");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Toast.makeText(getActivity().getApplicationContext(), "예를 선택했습니다.", Toast.LENGTH_LONG).show();
                                startFragment(fragment_center_auth_create_qrcode.class, args, R.string.fragment_create_qrcode_to_withdraw);
                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(getActivity().getApplicationContext(), "아니오를 선택했습니다.", Toast.LENGTH_LONG).show();
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