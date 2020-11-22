package com.kftc.openbankingsample2.biz.center_auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.api.account_balance.CenterAuthAPIAccountBalanceFragmentBuyer;
import com.kftc.openbankingsample2.biz.center_auth.api.account_transaction.CenterAuthAPIAccountTransactionResultFragment;
import com.kftc.openbankingsample2.biz.center_auth.api.transfer_withdraw.CenterAuthAPITransferWithdrawCheckFragment;
import com.kftc.openbankingsample2.biz.center_auth.api.user_me.CenterAuthAPIUserMeResultFragment;
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
 * 소비자  메인화면
 */
public class BuyerHomeFragment extends AbstractCenterAuthMainFragment {

    // context
    private Context context;

    // view
    private View view;

    // data
    private Bundle args;
    private Bundle sendingArgs;

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
        view = inflater.inflate(R.layout.fragment_buyer_home, container, false);
        initView();
        return view;
    }

    private void initView() {


        view.findViewById(R.id.btnScanQRPage).setOnClickListener(v -> withdrawOnClick());

        // 사용자 정보조회
        view.findViewById(R.id.btnInqrUserInfoPage).setOnClickListener(v -> {

            String accessToken =  CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_ACCESS_TOKEN);
            String userSeqNo = CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_USER_SEQ_NUM);

            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("user_seq_no", userSeqNo);

            showProgress();

            // Retrofit 이용하여 사용자 정보 조회 API 호출
            CenterAuthApiRetrofitAdapter.getInstance()
                    .userMe("Bearer " + accessToken, paramMap)
                    .enqueue(super.handleResponse("res_cnt", "등록계좌수", responseJson -> {

                                // 성공하면 결과화면으로 이동
                                ApiCallUserMeResponse result = new Gson().fromJson(responseJson, ApiCallUserMeResponse.class);
                                args.putParcelable("result", result);

                                // 사용자 정보 조회 창으로 이동
                                startFragment(CenterAuthAPIUserMeResultFragment.class, args, R.string.fragment_id_api_call_userme);
                            })
                    );
        });

        // 잔액조회
        view.findViewById(R.id.btnInqrBlncPage).setOnClickListener(v -> startFragment(CenterAuthAPIAccountBalanceFragmentBuyer.class, args, R.string.fragment_id_api_call_balance));

        // 거래내역조회
        view.findViewById(R.id.btnInqrTranRecPage).setOnClickListener(v -> {
            ArrayAdapter<BankAccount> bankAccountAdapter = new ArrayAdapter<>(context, R.layout.simple_list_item_divider, R.id.text1, AppData.centerAuthBankAccountList);
            showAlertAccount(bankAccountAdapter, (parent, view, position, id) -> {

                // 선택되면 해당 EditText 에 값을 입력.
                BankAccount bankAccount = bankAccountAdapter.getItem(position);
                String fintechUseNum = bankAccount.getFintech_use_num();

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

                // 거래 내역 조회 API 호출
                CenterAuthApiRetrofitAdapter.getInstance()
                        .accountTrasactionListFinNum("Bearer " + accessToken, paramMap)
                        .enqueue(super.handleResponse("page_record_cnt", "현재페이지 조회건수", responseJson -> {

                                    // 성공하면 결과화면으로 이동
                                    ApiCallAccountTransactionResponse result =
                                            new Gson().fromJson(responseJson, ApiCallAccountTransactionResponse.class);
                                    args.putParcelable("result", result);
                                    args.putSerializable("request", paramMap);
                                    args.putString(CenterAuthConst.BUNDLE_KEY_ACCESS_TOKEN, accessToken);

                                    // 거래 내역 조회 창으로 이동
                                    startFragment(CenterAuthAPIAccountTransactionResultFragment.class, args, R.string.fragment_id_api_call_transaction);
                                })
                        );

            });
        });
    }

    public void withdrawOnClick() {

        // QR코드 인식
        IntentIntegrator intentIntegrator = new IntentIntegrator(this.activity);
        intentIntegrator.setBeepEnabled(false);
        intentIntegrator.forSupportFragment(BuyerHomeFragment.this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // QR코드 인식 결과
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            String resultContents = result.getContents();

            if (result.getContents() != null) {
                /*
                QRInfo[0]: 거래 금액, QRInfo[1]: 계좌 인자 내역, QRInfo[2]: 수취인 이름, QRInfo[3]: 수취인 코드, QRInfo[4]: 수취인 계좌번호
                 */
                String QRinfo[] = resultContents.split(" ");

                // 결제 정보 확인 창에 보낼 데이터
                sendingArgs = new Bundle();
                sendingArgs.putStringArray("key", QRinfo);

                // 결제 정보 확인 창으로 이동
               startFragment(CenterAuthAPITransferWithdrawCheckFragment.class, sendingArgs, R.string.fragment_id_api_call_withdraw);
            }

            else {

            }
        }

        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        startFragment(HomeFragment.class, args, R.string.fragment_id_home);
    }
}
