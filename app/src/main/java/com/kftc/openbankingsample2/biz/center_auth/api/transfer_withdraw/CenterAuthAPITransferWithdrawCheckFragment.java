package com.kftc.openbankingsample2.biz.center_auth.api.transfer_withdraw;

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
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthConst;
import com.kftc.openbankingsample2.biz.center_auth.api.account_transaction.CenterAuthAPIAccountTransactionResultFragment_Buyer;
import com.kftc.openbankingsample2.biz.center_auth.http.CenterAuthApiRetrofitAdapter;
import com.kftc.openbankingsample2.biz.center_auth.util.CenterAuthUtils;
import com.kftc.openbankingsample2.common.application.AppData;
import com.kftc.openbankingsample2.common.data.ApiCallAccountTransactionResponse;
import com.kftc.openbankingsample2.common.data.BankAccount;
import com.kftc.openbankingsample2.common.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

public class CenterAuthAPITransferWithdrawCheckFragment extends AbstractCenterAuthMainFragment {
    // context
    private Context context;

    // view
    private View view;

    // data
    private Bundle args;
    private String[] QRInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        args = getArguments();
        if (args == null) args = new Bundle();

        QRInfo = args.getStringArray("key");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_center_auth_api_transfer_withdraw_check, container, false);
        initView();
        return view;
    }

    void initView() {
        // QR코드 정보 저장
        String tran_amt = QRInfo[0];
        String dps_print_content = QRInfo[1];
        String recv_client_name = QRInfo[2];
        String recv_client_code = QRInfo[3];
        String recv_client_account_num = QRInfo[4];

        EditText etReqClntName = view.findViewById(R.id.req_client_name);
        etReqClntName.setText("유영훈");

        EditText etRecvClntName = view.findViewById(R.id.recv_client_name);
        etRecvClntName.setText(recv_client_name);

        EditText etTranAmt = view.findViewById(R.id.trans_amt);
        etTranAmt.setText(tran_amt);

        EditText etRecvClientNum = view.findViewById(R.id.recv_client_num);
        etRecvClientNum.setText(recv_client_account_num);

        EditText etreqClientNum = view.findViewById(R.id.req_client_num);
        AtomicReference<String> etfintechUseNum = new AtomicReference<>("");

        // 출금 계좌 선택 버튼을 누르면 계좌 선택 창이 뜨고(showAccountDialogCustom), 계좌를 선택하면 해당 fintech 번호와 해당 계좌번호가 etfintechUseNum과 etreqClientMum에 설정된다
        View.OnClickListener onClickListener = v -> showAccountDialogCustom(etfintechUseNum, null, etreqClientNum);
        view.findViewById(R.id.btnSelectFintechUseNum).setOnClickListener(onClickListener);


        // 출금이체 요청
        view.findViewById(R.id.btnNext).setOnClickListener(v -> {

            // API 호출에 필요한 파라미터들 설정
            String accessToken = CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_ACCESS_TOKEN);
            Utils.saveData(CenterAuthConst.CENTER_AUTH_ACCESS_TOKEN, accessToken);
            String cntrAccountNum = "8487279403";
            Utils.saveData(CenterAuthConst.CENTER_AUTH_CNTR_ACCOUNT_NUM, cntrAccountNum);
            String fintechUseNum = etfintechUseNum.toString();
            Utils.saveData(CenterAuthConst.CENTER_AUTH_FINTECH_USE_NUM, fintechUseNum);
            String reqClientName = "유영훈";
            Utils.saveData(CenterAuthConst.CENTER_AUTH_REQ_CLIENT_NAME, reqClientName);
            String reqClientBankCode = "097".trim();
            Utils.saveData(CenterAuthConst.CENTER_AUTH_REQ_CLIENT_BANK_CODE, reqClientBankCode);
            String reqClientAccountNum = "9498279403".trim();
            Utils.saveData(CenterAuthConst.CENTER_AUTH_REQ_CLIENT_ACCOUNT_NUM, reqClientAccountNum);

            // 은행거래고유번호(20자리)
            // 하루동안 유일성이 보장되어야함. 이용기관번호(10자리) + 생성주체구분코드(1자리, U:이용기관, O:오픈뱅킹) + 이용기관 부여번호(9자리)
            String clientUseCode = "T991636280";
            String randomUnique9String = Utils.getCurrentTime();    // 이용기관 부여번호를 임시로 시간데이터 사용
            String bankTranId = String.format("%sU%s", clientUseCode, randomUnique9String);

            // 요청전문
            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("bank_tran_id", bankTranId);
            paramMap.put("cntr_account_type", "N");
            paramMap.put("cntr_account_num", cntrAccountNum);
            paramMap.put("dps_print_content", dps_print_content);
            paramMap.put("fintech_use_num", fintechUseNum);
            paramMap.put("tran_amt", tran_amt);
            paramMap.put("tran_dtime", new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(new Date()));
            paramMap.put("req_client_name", reqClientName);
            paramMap.put("req_client_fintech_use_num", fintechUseNum);
            paramMap.put("req_client_num", "HONGGILDONG1234");
            paramMap.put("transfer_purpose", "TR");
            paramMap.put("recv_client_name", recv_client_name);
            paramMap.put("recv_client_bank_code", recv_client_code);
            paramMap.put("recv_client_account_num", recv_client_account_num);

            showProgress();
            CenterAuthApiRetrofitAdapter.getInstance()
                    .transferWithdrawFinNum("Bearer " + accessToken, paramMap)
                    .enqueue(super.handleResponse("tran_amt", "이체완료!! 이체금액", responseJson -> {

                                // API 호출을 위해 파라미터 변수 초기화(설정)
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
                                HashMap<String, String> paramMap2 = new HashMap<>();
                                paramMap2.put("bank_tran_id", BankTranId);
                                paramMap2.put("fintech_use_num", fintechUseNum);
                                paramMap2.put("inquiry_type", inquiryType);
                                paramMap2.put("inquiry_base", inquiryBase);
                                paramMap2.put("from_date", fromData);
                                paramMap2.put("from_time", fromTime);
                                paramMap2.put("to_date", toDate);
                                paramMap2.put("to_time", toTime);
                                paramMap2.put("sort_order", sortOrder);
                                paramMap2.put("tran_dtime", tranDtime);
                                paramMap2.put("befor_inquiry_trace_info", beforeInquiryTraceInfo);

                                showProgress();
                                CenterAuthApiRetrofitAdapter.getInstance()
                                        .accountTrasactionListFinNum("Bearer " + accessToken, paramMap2)
                                        .enqueue(super.handleResponse("page_record_cnt", "현재페이지 조회건수", responseJson2 -> {

                                            // 성공하면 결과화면으로 이동
                                                    ApiCallAccountTransactionResponse result =
                                                            new Gson().fromJson(responseJson2, ApiCallAccountTransactionResponse.class);
                                                    args.putParcelable("result", result);
                                                    args.putSerializable("request", paramMap2);
                                                    args.putString(CenterAuthConst.BUNDLE_KEY_ACCESS_TOKEN, accessToken);

                                                    startFragment(CenterAuthAPIAccountTransactionResultFragment_Buyer.class, args, R.string.fragment_id_api_call_transaction);
                                        })
                                );
                            })
                    );

        });

        // 취소
        view.findViewById(R.id.btnRescan).setOnClickListener(v -> onBackPressed());
    }
}
