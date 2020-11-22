package com.kftc.openbankingsample2.biz.center_auth;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.api.account_balance.CenterAuthAPIAccountBalanceFragmentSeller;
import com.kftc.openbankingsample2.biz.center_auth.api.account_transaction.CenterAuthAPIAccountTransactionRequestFragment;
import com.kftc.openbankingsample2.biz.center_auth.api.account_transaction.CenterAuthAPIAccountTransactionResultFragment;
import com.kftc.openbankingsample2.biz.center_auth.api.my_menu;
import com.kftc.openbankingsample2.biz.center_auth.api.user_me.CenterAuthAPIUserMeResultFragment;
import com.kftc.openbankingsample2.biz.center_auth.auth.CenterAuthFragment;
import com.kftc.openbankingsample2.biz.center_auth.http.CenterAuthApiRetrofitAdapter;
import com.kftc.openbankingsample2.biz.center_auth.util.CenterAuthUtils;
import com.kftc.openbankingsample2.biz.main.HomeFragment;
import com.kftc.openbankingsample2.common.application.AppData;
import com.kftc.openbankingsample2.common.data.ApiCallAccountTransactionResponse;
import com.kftc.openbankingsample2.common.data.ApiCallUserMeResponse;
import com.kftc.openbankingsample2.common.data.BankAccount;
import com.kftc.openbankingsample2.common.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * 센터인증 메인화면
 */
public class SellerHomeFragment extends AbstractCenterAuthMainFragment {

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
        view = inflater.inflate(R.layout.fragment_seller_home, container, false);
        initView();
        return view;
    }

    private void initView() {

        // 계좌등록
        view.findViewById(R.id.btnAuthToken).setOnClickListener(v -> startFragment(CenterAuthFragment.class, args, R.string.fragment_id_center_auth));

        // 사용자 정보조회
        view.findViewById(R.id.btnInqrUserInfoPage).setOnClickListener(v -> {

            // 설정에서 access token과 user sequence number 가져옴
            String accessToken =  CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_ACCESS_TOKEN);
            String userSeqNo = CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_USER_SEQ_NUM);

            // 요청 전문
            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("user_seq_no", userSeqNo);

            showProgress();
            CenterAuthApiRetrofitAdapter.getInstance()
                    .userMe("Bearer " + accessToken, paramMap)
                    .enqueue(super.handleResponse("res_cnt", "등록계좌수", responseJson -> {

                                // 성공하면 결과화면으로 이동
                                ApiCallUserMeResponse result = new Gson().fromJson(responseJson, ApiCallUserMeResponse.class);
                                args.putParcelable("result", result);

                                // CenterAuthAPIUserMeResultFragment로 이동
                                startFragment(CenterAuthAPIUserMeResultFragment.class, args, R.string.fragment_id_api_call_userme);
                            })
                    );
        });

        // 잔액조회
        view.findViewById(R.id.btnInqrBlncPage).setOnClickListener(v ->startFragment(CenterAuthAPIAccountBalanceFragmentSeller.class, args, R.string.fragment_id_api_call_balance));

        // 거래내역조회
        view.findViewById(R.id.btnInqrTranRecPage).setOnClickListener(v -> {

            // 사용자의 계좌 목록을 불러온 후 선택창 띄우기
            ArrayAdapter<BankAccount> bankAccountAdapter = new ArrayAdapter<>(context, R.layout.simple_list_item_divider, R.id.text1, AppData.centerAuthBankAccountList);
            showAlertAccount(bankAccountAdapter, (parent, view, position, id) -> {

                // 선택창에서 선택한 계좌 정보 가져오기
                BankAccount bankAccount = bankAccountAdapter.getItem(position);
                String fintechUseNum = bankAccount.getFintech_use_num();

                // API 호출에 필요한 parameter값들 초기화(설정)
                String accessToken =  CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_ACCESS_TOKEN);
                String BankTranId = setRandomBankTranIdCustom();
                String inquiryType = "A";
                String inquiryBase = "D";
                String fromData = "20180101";
                String fromTime = "";
                String toDate = Utils.getDateString8(0);
                String toTime = "";
                String sortOrder = "D";
                String tranDtime = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(new Date());
                String beforeInquiryTraceInfo = "123";

                // 요청전문
                HashMap<String, String> paramMap = new HashMap<>();
                paramMap.put("bank_tran_id", BankTranId);
                paramMap.put("fintech_use_num", fintechUseNum);
                paramMap.put("inquiry_type", inquiryType);
                paramMap.put("inquiry_base", inquiryBase);
                paramMap.put("from_date", fromData);
                paramMap.put("from_time", fromTime);
                paramMap.put("to_date", toDate);
                paramMap.put("to_time", toTime);
                paramMap.put("sort_order", sortOrder);
                paramMap.put("tran_dtime", tranDtime);
                paramMap.put("befor_inquiry_trace_info", beforeInquiryTraceInfo);

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

                                      // CenterAuthAPIAccountTransactionResultFragment로 이동
                                    startFragment(CenterAuthAPIAccountTransactionResultFragment.class, args, R.string.fragment_id_api_call_transaction);
                                })
                        );

            });
        });

        // QR코드 생성
        view.findViewById(R.id.btnCreateQRPage).setOnClickListener(v -> startFragment(my_menu.class, args, R.string.fragment_id_menu));

    }

    @Override
    public void onBackPressed() {
        startFragment(HomeFragment.class, args, R.string.fragment_id_home);
    }

}
