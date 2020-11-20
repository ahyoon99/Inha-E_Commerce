package com.kftc.openbankingsample2.biz.center_auth.api;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.kftc.openbankingsample2.biz.center_auth.api.transfer_withdraw.CenterAuthAPITransferWithdrawActivity;
import com.kftc.openbankingsample2.biz.center_auth.api.transfer_withdraw.CenterAuthAPITransferWithdrawCheckFragment;
import com.kftc.openbankingsample2.biz.center_auth.api.user_me.CenterAuthAPIUserMeRequestFragment;

/**
 * API 호출 메뉴
 */
public class CenterAuthAPIFragment extends AbstractCenterAuthMainFragment {

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
        view.findViewById(R.id.btnTrnsWDPage).setOnClickListener(v -> withdrawOnClick());

        // 등록계좌조회
        view.findViewById(R.id.btnAccountList).setOnClickListener(v -> startFragment(CenterAuthAPIAccountListRequestFragment.class, args, R.string.fragment_id_api_call_account));

        // 이체결과조회
        view.findViewById(R.id.btnTransferResult).setOnClickListener(v -> startFragment(CenterAuthAPITransferResultFragment.class, args, R.string.fragment_id_api_call_transfer_result));
    }

    @Override
    public void onBackPressed() {
        startFragment(CenterAuthHomeFragment.class, null, R.string.fragment_id_center);
    }

    public void withdrawOnClick() {
        Intent intent = new Intent(getActivity(), CenterAuthAPITransferWithdrawActivity.class);
        startActivity(intent);
    }
}
