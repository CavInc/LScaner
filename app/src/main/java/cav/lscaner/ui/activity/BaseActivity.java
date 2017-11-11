package cav.lscaner.ui.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import cav.lscaner.R;

public class BaseActivity extends AppCompatActivity {
    protected ProgressDialog mProgressDialog;

    public void showProgress(){
        if (mProgressDialog==null) {
            mProgressDialog = new ProgressDialog(this,R.style.custom_dialog);
            mProgressDialog.setCancelable(false);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.progress_splash);
        }else{
            mProgressDialog.show();
            mProgressDialog.setContentView(R.layout.progress_splash);
        }

    }

    public void hideProgress(){
        if (mProgressDialog!=null){
            if (mProgressDialog.isShowing()){
                mProgressDialog.hide();
            }
        }
    }

    // показ toast сообщения
    public void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    // показ сообщения о ошибке и сброс его в лог
    public void showError(String message,Exception error){
        showToast(message);
    }

}