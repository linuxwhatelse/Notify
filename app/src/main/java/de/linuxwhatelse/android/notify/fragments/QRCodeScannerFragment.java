package de.linuxwhatelse.android.notify.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by tadly on 12/21/16.
 */

public class QRCodeScannerFragment extends Fragment implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;

    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.activity = getActivity();

        mScannerView = new ZBarScannerView(getActivity());
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        Intent intent = new Intent();
        intent.putExtra("content", result.getContents());
        intent.putExtra("format", result.getBarcodeFormat().getName());

        this.activity.setResult(ClientsFragment.QR_SCAN_RESULT, intent);

        mScannerView.stopCamera();

        this.activity.finish();
    }
}
