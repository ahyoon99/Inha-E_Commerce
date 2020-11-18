package com.kftc.openbankingsample2.biz.center_auth.api;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthConst;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthHomeFragment;
import com.kftc.openbankingsample2.biz.center_auth.api.account_balance.CenterAuthAPIAccountBalanceFragment;
import com.kftc.openbankingsample2.biz.center_auth.api.account_list.CenterAuthAPIAccountListRequestFragment;
import com.kftc.openbankingsample2.biz.center_auth.api.account_transaction.CenterAuthAPIAccountTransactionRequestFragment;
import com.kftc.openbankingsample2.biz.center_auth.api.transfer_result.CenterAuthAPITransferResultFragment;
import com.kftc.openbankingsample2.biz.center_auth.api.transfer_withdraw.CenterAuthAPITransferWithdrawCheckFragment;
import com.kftc.openbankingsample2.biz.center_auth.api.user_me.CenterAuthAPIUserMeRequestFragment;
import com.kftc.openbankingsample2.biz.center_auth.http.CenterAuthApiRetrofitAdapter;
import com.kftc.openbankingsample2.biz.center_auth.util.CenterAuthUtils;
import com.kftc.openbankingsample2.common.data.ApiCallAccountTransactionResponse;
import com.kftc.openbankingsample2.common.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * API 호출 메뉴
 */
public class CenterAuthAPIFragment extends AbstractCenterAuthMainFragment implements View.OnClickListener {

    // context
    private Context context;

    // view
    private View view;

    // data
    private Bundle args;

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
        view = inflater.inflate(R.layout.fragment_center_auth_api, container, false);
        initView();
        return view;
    }

    void initView() {
        // 사용자 정보조회
        view.findViewById(R.id.btnInqrUserInfoPage).setOnClickListener(v -> startFragment(CenterAuthAPIUserMeRequestFragment.class, args, R.string.fragment_id_api_call_userme));

        // 잔액조회
        view.findViewById(R.id.btnInqrBlncPage).setOnClickListener(v -> startFragment(CenterAuthAPIAccountBalanceFragment.class, args, R.string.fragment_id_api_call_balance));

        // 거래내역조회
        view.findViewById(R.id.btnInqrTranRecPage).setOnClickListener(v -> startFragment(CenterAuthAPIAccountTransactionRequestFragment.class, args, R.string.fragment_id_api_call_transaction));

        // 출금이체
        view.findViewById(R.id.btnTrnsWDPage).setOnClickListener(this::onClick);

        // 등록계좌조회
        view.findViewById(R.id.btnAccountList).setOnClickListener(v -> startFragment(CenterAuthAPIAccountListRequestFragment.class, args, R.string.fragment_id_api_call_account));

        // 이체결과조회
        view.findViewById(R.id.btnTransferResult).setOnClickListener(v -> startFragment(CenterAuthAPITransferResultFragment.class, args, R.string.fragment_id_api_call_transfer_result));
    }

    @Override
    public void onBackPressed() {
        startFragment(CenterAuthHomeFragment.class, null, R.string.fragment_id_center);
    }

    @Override
    public void onClick(View v) {
        IntentIntegrator intentIntegrator = new IntentIntegrator(CenterAuthAPIFragment.this.activity);
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiIxMTAwNzYyNDM2Iiwic2NvcGUiOlsiaW5xdWlyeSIsImxvZ2luIiwidHJhbnNmZXIiXSwiaXNzIjoiaHR0cHM6Ly93d3cub3BlbmJhbmtpbmcub3Iua3IiLCJleHAiOjE2MTMyMTU2NjAsImp0aSI6IjI1Nzg4MDEwLWZkZTItNDU2ZS1iYzVhLThkZGZiZGY2MjMzMiJ9.uk4NE6y9kHZ6sHAHIcl26STcWRp2up7HCl2plJ5eVw0".trim();
        Utils.saveData(CenterAuthConst.CENTER_AUTH_ACCESS_TOKEN, accessToken);
        String fintechUseNum = "199163628057884692187614";
        Utils.saveData(CenterAuthConst.CENTER_AUTH_FINTECH_USE_NUM, fintechUseNum);

        // 은행거래고유번호(20자리)
        // 하루동안 유일성이 보장되어야함. 이용기관번호(10자리) + 생성주체구분코드(1자리, U:이용기관, O:오픈뱅킹) + 이용기관 부여번호(9자리)
        String clientUseCode = CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_USE_CODE);
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
        paramMap.put("tran_dtime", new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format((new Date())).toString());
        paramMap.put("befor_inquiry_trace_info", "123");

        showProgress();
        CenterAuthApiRetrofitAdapter.getInstance()
                .accountTrasactionListFinNum("Bearer " + accessToken, paramMap)
                .enqueue(super.handleResponse("page_record_cnt", "현재페이지 조회건수", responseJson -> {

                            // 성공하면 결과화면으로 이동
                            ApiCallAccountTransactionResponse apiCallAccountTransactionResponse =
                                    new Gson().fromJson(responseJson, ApiCallAccountTransactionResponse.class);
                            args.putParcelable("result", apiCallAccountTransactionResponse);
                            args.putSerializable("request", paramMap);
                            args.putString(CenterAuthConst.BUNDLE_KEY_ACCESS_TOKEN, accessToken);
                            goNext();
                        })
                );
    }

    void goNext() {
        startFragment(CenterAuthAPITransferWithdrawCheckFragment.class, args, R.string.fragment_id_api_call_transaction);
    }
}
