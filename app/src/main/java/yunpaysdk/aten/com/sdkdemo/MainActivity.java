package yunpaysdk.aten.com.sdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.aten.yunpaysdk.SsoHandler;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        info = (TextView) findViewById(R.id.tv_info);

        //配置授权参数
        SsoHandler.redirect_uri = "url";
        SsoHandler.client_id="lkfjsoerrj_klrlks";
        SsoHandler.client_secret="no";


        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SsoHandler ssoHandler = new SsoHandler(MainActivity.this);
                ssoHandler.authRegister();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(MainActivity.this, SdkActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == SsoHandler.REQUEST_CODE){ //授权成功
                info.setText("回调结果："+data.getStringExtra("data"));
            }else if (requestCode == SsoHandler.CANCLE_CODE){ //授权失败
                Toast.makeText(MainActivity.this, "取消授权", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
