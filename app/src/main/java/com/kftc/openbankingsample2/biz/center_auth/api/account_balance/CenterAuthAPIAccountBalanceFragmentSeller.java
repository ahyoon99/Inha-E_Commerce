package com.kftc.openbankingsample2.biz.center_auth.api.account_balance;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthConst;
import com.kftc.openbankingsample2.biz.center_auth.SellerHomeFragment;
import com.kftc.openbankingsample2.biz.center_auth.api.user_me.CenterAuthAPIUserMeResultFragment;
import com.kftc.openbankingsample2.biz.center_auth.http.CenterAuthApiRetrofitAdapter;
import com.kftc.openbankingsample2.biz.center_auth.util.CenterAuthUtils;
import com.kftc.openbankingsample2.common.Scope;
import com.kftc.openbankingsample2.common.application.AppData;
import com.kftc.openbankingsample2.common.data.ApiCallUserMeResponse;
import com.kftc.openbankingsample2.common.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * 잔액조회
 */
public class CenterAuthAPIAccountBalanceFragmentSeller extends AbstractCenterAuthMainFragment {

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
        view = inflater.inflate(R.layout.fragment_center_auth_api_account_balance, container, false);
        initView();
        return view;
    }

    void initView() {


        // 핀테크이용번호 : 최근 계좌로 기본 설정
        EditText etFintechUseNum = view.findViewById(R.id.etFintechUseNum);
        etFintechUseNum.setText(Utils.getSavedValue(CenterAuthConst.CENTER_AUTH_FINTECH_USE_NUM));

        // 핀테크이용번호 : 기존 계좌에서 선택
        View.OnClickListener onClickListener = v -> showAccountDialog(etFintechUseNum);
        view.findViewById(R.id.btnSelectFintechUseNum).setOnClickListener(onClickListener);

        // 잔액을 확인할 계좌 선택 후 "확인" 버튼을 눌렀을 경우
        view.findViewById(R.id.btnNext).setOnClickListener(v -> {

            // 은행거래고유번호
            String clientUseCode = CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_USE_CODE);
            String randomUnique9String = Utils.getCurrentTime();    // 이용기관 부여번호를 임시로 시간데이터 사용
            String etBankTranId = String.format("%sU%s", clientUseCode, randomUnique9String);

            // 거래 일시
            String etTranDtime = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(new Date());

            // access_token
            String accessToken =  CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_ACCESS_TOKEN);
            Utils.saveData(CenterAuthConst.CENTER_AUTH_ACCESS_TOKEN, accessToken);

            // 핀테크 이용번호 (계좌번호)
            String fintechUseNum = etFintechUseNum.getText().toString();
            Utils.saveData(CenterAuthConst.CENTER_AUTH_FINTECH_USE_NUM, fintechUseNum);
            
            String userSeqNo = CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_USER_SEQ_NUM);

            // 요청전문
            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("bank_tran_id", etBankTranId);
            paramMap.put("fintech_use_num", fintechUseNum);
            paramMap.put("tran_dtime", etTranDtime);

            showProgress();
            CenterAuthApiRetrofitAdapter.getInstance()
                    .accountBalanceFinNum("Bearer " + accessToken, paramMap)
                    .enqueue(super.handleResponse("balance_amt", "잔액", responseJson -> {
                        // 성공하면 결과화면으로 이동
                        ApiCallUserMeResponse result = new Gson().fromJson(responseJson, ApiCallUserMeResponse.class);
                        args.putParcelable("result", result);

                        startFragment(SellerHomeFragment.class, args, R.string.fragment_id_seller);
                    }));
        });

        // "취소" 버튼을 눌렀을 경우
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> onBackPressed());
    }
}
