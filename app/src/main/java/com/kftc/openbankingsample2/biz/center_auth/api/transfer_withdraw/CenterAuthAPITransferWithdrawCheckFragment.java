package com.kftc.openbankingsample2.biz.center_auth.api.transfer_withdraw;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthConst;
import com.kftc.openbankingsample2.biz.center_auth.http.CenterAuthApiRetrofitAdapter;
import com.kftc.openbankingsample2.common.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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

        // 출금이체 요청
        view.findViewById(R.id.btnNext).setOnClickListener(v -> {

            // 직전내용 저장
            String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiIxMTAwNzYyNDM2Iiwic2NvcGUiOlsiaW5xdWlyeSIsImxvZ2luIiwidHJhbnNmZXIiXSwiaXNzIjoiaHR0cHM6Ly93d3cub3BlbmJhbmtpbmcub3Iua3IiLCJleHAiOjE2MTMyMTU2NjAsImp0aSI6IjI1Nzg4MDEwLWZkZTItNDU2ZS1iYzVhLThkZGZiZGY2MjMzMiJ9.uk4NE6y9kHZ6sHAHIcl26STcWRp2up7HCl2plJ5eVw0".trim();
            Utils.saveData(CenterAuthConst.CENTER_AUTH_ACCESS_TOKEN, accessToken);
            String cntrAccountNum = "8487279403";
            Utils.saveData(CenterAuthConst.CENTER_AUTH_CNTR_ACCOUNT_NUM, cntrAccountNum);
            String fintechUseNum = "199163628057884692187614";
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
                    .enqueue(super.handleResponse("tran_amt", "이체완료!! 이체금액"));
        });

        // 취소
        view.findViewById(R.id.btnRescan).setOnClickListener(v -> onBackPressed());
    }
}
