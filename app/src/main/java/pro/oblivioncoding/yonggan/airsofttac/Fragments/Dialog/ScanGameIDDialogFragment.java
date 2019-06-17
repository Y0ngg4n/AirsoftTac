package pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog;

import android.app.Activity;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import me.dm7.barcodescanner.core.DisplayUtils;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.QR.helpers.CameraFace;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.QR.qrscanner.ZBarScannerView;

//https://github.com/MasterKale/barcodescanner-for-dialogs
public class ScanGameIDDialogFragment extends DialogFragment implements ZBarScannerView.ResultHandler {

    static final String BUNDLE_CAMERA_FACE = "cameraFace";
    private static EditText gameIDEditText;
    private static Activity activity;
    private static CameraFace cameraFace = CameraFace.BACK;
    ZBarScannerView mScannerView;
    // Maintain a 4:3 ratio
    int mWindowWidth = 800;
    int mWindowHeight = 600;
    int mViewFinderPadding = 50;
    private ScanGameIDDialogFragment.OnQRCodeScanListener onQRCodeScanListener;

    public ScanGameIDDialogFragment() {
    }

    public static ScanGameIDDialogFragment newInstance(String title, EditText pGameIDEditText, CameraFace pCameraFace, Activity pActivity) {
        gameIDEditText = pGameIDEditText;
        ScanGameIDDialogFragment dialogScanner = new ScanGameIDDialogFragment();
        activity = pActivity;
        return new ScanGameIDDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mScannerView = new ZBarScannerView(getContext(), cameraFace);
        mScannerView.setResultHandler(this);
        mScannerView.setAutoFocus(false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return mScannerView;
    }

    @Override
    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
        if (result.getBarcodeFormat() == me.dm7.barcodescanner.zbar.BarcodeFormat.QRCODE) {
            playSound(RingtoneManager.TYPE_NOTIFICATION);
            onQRCodeScanListener.onQRCodeScan(result.getContents());
            getDialog().dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DisplayUtils.getScreenOrientation(getActivity()) == Configuration.ORIENTATION_PORTRAIT && mWindowWidth > mWindowHeight) {
            getDialog().getWindow().setLayout(mWindowHeight, mWindowWidth);
        } else {
            getDialog().getWindow().setLayout(mWindowWidth, mWindowHeight);
        }
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

    private void playSound(int notificationType) {
        Uri notification = RingtoneManager.getDefaultUri(notificationType);
        Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
        r.play();
    }

    public interface OnQRCodeScanListener {
        void onQRCodeScan(String contents);
    }
}
