package yunpaysdk.aten.com.sdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aten.yunpaysdk.SsoHandler;

import static android.app.Activity.RESULT_OK;

/**
 * project:SDKDemo
 * package:yunpaysdk.aten.com.sdkdemo
 * Created by Zhang JinCheng on 2017/9/9.
 * e-mail : 774222004@qq.com
 */


public class SdkFragment extends android.app.Fragment {

    View view;
    private TextView info;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.sdk_fragment, null);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        info = (TextView)view.findViewById(R.id.tv_info);


        view.findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SsoHandler(SdkFragment.this).authRegister();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == SsoHandler.REQUEST_CODE){ //授权成功
                info.setText("回调结果："+data.getStringExtra("data"));
            }else if (requestCode == SsoHandler.CANCLE_CODE){ //授权失败
                Toast.makeText(getContext(), "取消授权", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
