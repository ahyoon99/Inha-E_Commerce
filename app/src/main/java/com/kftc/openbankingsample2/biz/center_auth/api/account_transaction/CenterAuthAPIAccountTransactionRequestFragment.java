package com.kftc.openbankingsample2.biz.center_auth.api.account_transaction;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.gson.Gson;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthConst;
import com.kftc.openbankingsample2.biz.center_auth.http.CenterAuthApiRetrofitAdapter;
import com.kftc.openbankingsample2.biz.center_auth.util.CenterAuthUtils;
import com.kftc.openbankingsample2.common.Scope;
import com.kftc.openbankingsample2.common.application.AppData;
import com.kftc.openbankingsample2.common.data.ApiCallAccountTransactionResponse;
import com.kftc.openbankingsample2.common.util.TwoString;
import com.kftc.openbankingsample2.common.util.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * 거래내역조회 요청
 */
public class CenterAuthAPIAccountTransactionRequestFragment extends AbstractCenterAuthMainFragment {

    // context
    private Context context;

    // view
    private View view;

    // data
    private Bundle args;
    private String inquiryType;     // 조회구분코드
    private String inquiryBase;     // 조회기준코드
    private String sortOrder;       // 정렬순서

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
        view = inflater.inflate(R.layout.fragment_center_auth_api_account_transaction_request, container, false);
        initView();
        return view;
    }

    void initView() {

        // 거래내역조회 요청
        view.findViewById(R.id.btnNext).setOnClickListener(v -> {

            // 직전내용 저장
            String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiIxMTAwNzYyNDM2Iiwic2NvcGUiOlsiaW5xdWlyeSIsImxvZ2luIiwidHJhbnNmZXIiXSwiaXNzIjoiaHR0cHM6Ly93d3cub3BlbmJhbmtpbmcub3Iua3IiLCJleHAiOjE2MTMyMTU2NjAsImp0aSI6IjI1Nzg4MDEwLWZkZTItNDU2ZS1iYzVhLThkZGZiZGY2MjMzMiJ9.uk4NE6y9kHZ6sHAHIcl26STcWRp2up7HCl2plJ5eVw0".trim();
            Utils.saveData(CenterAuthConst.CENTER_AUTH_ACCESS_TOKEN, accessToken);
            String fintechUseNum = "199163628057884692187614";
            Utils.saveData(CenterAuthConst.CENTER_AUTH_FINTECH_USE_NUM, fintechUseNum);

            // 은행거래고유번호(20자리)
            // 하루동안 유일성이 보장되어야함. 이용기관번호(10자리) + 생성주체구분코드(1자리, U:이용기관, O:오픈뱅킹) + 이용기관 부여번호(9자리)
            String clientUseCode = "T991636280";
            String randomUnique9String = Utils.getCurrentTime();    // 이용기관 부여번호를 임시로 시간데이터 사용
            String bankTranResult = String.format("%sU%s", clientUseCode, randomUnique9String);

            // 요청전문
            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("bank_tran_id", bankTranResult);
            paramMap.put("fintech_use_num", fintechUseNum);
            paramMap.put("inquiry_type", "A");
            paramMap.put("inquiry_base", "D");
            paramMap.put("from_date", Utils.getDateString8(-30).trim());
            paramMap.put("to_date", Utils.getDateString8(0).trim());
            paramMap.put("sort_order", "D");
            paramMap.put("tran_dtime", new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format((new Date())));
            paramMap.put("befor_inquiry_trace_info", "123");

            showProgress();
            CenterAuthApiRetrofitAdapter.getInstance()
                    .accountTrasactionListFinNum("Bearer " + accessToken, paramMap)
                    .enqueue(super.handleResponse("page_record_cnt", "현재페이지 조회건수", responseJson -> {

                        // 성공하면 결과화면으로 이동
                        ApiCallAccountTransactionResponse result =
                                new Gson().fromJson(responseJson, ApiCallAccountTransactionResponse.class);
                        args.putParcelable("result", result);
                        args.putSerializable("request", paramMap);
                        args.putString(CenterAuthConst.BUNDLE_KEY_ACCESS_TOKEN, accessToken);
                        goNext();
                    })
            );
        });

        // 취소
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> onBackPressed());
    }

    void goNext() {
        startFragment(CenterAuthAPIAccountTransactionResultFragment.class, args, R.string.fragment_id_api_call_transaction);
    }

}
