package com.example.login.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.login.JSESSIONID;
import com.example.login.NetUtils;
import com.example.login.R;
import com.example.login.websocket_Manager;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileWithBitmapCallback;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UpdateHeadPic_Activity extends AppCompatActivity implements View.OnClickListener {

    //调取系统摄像头的请求码
    private static final int MY_ADD_CASE_CALL_PHONE = 6;
    //打开相册的请求码
    private static final int MY_ADD_CASE_CALL_PHONE2 = 7;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private LayoutInflater inflater;
    private ImageView headpic_preview;
    private View layout;
    private Button reset;
    private TextView takePhotoTV;
    private TextView choosePhotoTV;
    private TextView cancelTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_head_pic_);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.hide();
        }

        headpic_preview = findViewById(R.id.headpic_preview);
        String file= readpic();
        Bitmap pic = BitmapFactory.decodeFile(file);
        if(pic!=null&&headpic_preview!=null)
        {
            headpic_preview.setImageBitmap(pic);
        }
        reset = findViewById(R.id.reset_headpic);
        reset.setOnClickListener(this);
    }

    /**
     * 初始化控件方法
    **/
    public void viewInit() {
        builder = new AlertDialog.Builder(this);//创建对话框
        inflater = getLayoutInflater();
        layout = inflater.inflate(R.layout.dialog_select_photo, null);//获取自定义布局
        builder.setView(layout);//设置对话框的布局
        dialog = builder.create();//生成最终的对话框
        dialog.show();//显示对话框

        takePhotoTV = layout.findViewById(R.id.select_from_camera);
        choosePhotoTV = layout.findViewById(R.id.select_from_album);
        cancelTV = layout.findViewById(R.id.cancel_reset);
        //设置监听
        takePhotoTV.setOnClickListener(this);
        choosePhotoTV.setOnClickListener(this);
        cancelTV.setOnClickListener(this);
    }

    /**
     * 修改头像按钮执行方法
     * @param view
     */
    public void UpdatePhoto(View view) {
        viewInit();
    }

    private void takePhoto() throws IOException {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        // 获取文件
        File file = createFileIfNeed("UserIcon.png");
        //拍照后原图回存入此路径下
        Uri uri;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            uri = Uri.fromFile(file);
        } else {
            /**
             * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
             * 并且这样可以解决MIUI系统上拍照返回size为0的情况
             */
            uri = FileProvider.getUriForFile(this, "com.example.login.UpdateHeadPic_Activity", file);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, 1);
    }

    // 在sd卡中创建一保存图片（原图和缩略图共用的）文件夹
    private File createFileIfNeed(String fileName) throws IOException {
        String fileA = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/com.example.login/files/Pictures/";
        File fileJA = new File(fileA);
        if (!fileJA.exists()) {
            fileJA.mkdirs();
        }
        File file = new File(fileA, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 打开相册
     */
    private void choosePhoto() {
        //这是打开系统默认的相册(就是你系统怎么分类,就怎么显示,首先展示分类列表)
        Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(picture, 2);
    }

    /**
     * 申请权限回调方法
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_ADD_CASE_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    takePhoto();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this,"拒绝了你的请求",Toast.LENGTH_SHORT).show();
                //"权限拒绝");

            }
        }


        if (requestCode == MY_ADD_CASE_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto();
            } else {
                //"权限拒绝");

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * startActivityForResult执行后的回调方法，接收返回的图片
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode != Activity.RESULT_CANCELED) {

            String state = Environment.getExternalStorageState();
            if (!state.equals(Environment.MEDIA_MOUNTED)) return;
            // 把原图显示到界面上
            Tiny.FileCompressOptions options;
            options = new Tiny.FileCompressOptions();
            Tiny.getInstance().source(readpic()).asFile().withOptions(options).compress(new FileWithBitmapCallback() {
                @Override
                public void callback(boolean isSuccess, Bitmap bitmap, String outfile, Throwable t) {
                    // 自动裁剪bitmap为1：1比例
                    Bitmap bit_cropped = cropBitmap(bitmap);
                    saveImageToServer(bit_cropped, outfile);//显示图片到imgView上
                }
            });
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK
                && null != data) {
            try {
                Uri selectedImage = data.getData();//获取路径
                headpic_preview.setImageURI(selectedImage);
                Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                // 自动裁剪bitmap为1：1比例
                Bitmap bit_cropped = cropBitmap(bit);
                saveImageToServer(bit_cropped,Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.example.login/files/Pictures/" + "UserIcon.png");
//                Uri selectedImage = data.getData();//获取路径
//                Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
//                Tiny.getInstance().source(selectedImage).asFile().withOptions(options).compress(new FileWithBitmapCallback() {
//                    @Override
//                    public void callback(boolean isSuccess, Bitmap bitmap, String outfile, Throwable t) {
//                        saveImageToServer(bitmap, outfile);
//                    }
//                });
            } catch (Exception e) {
                //"上传失败");
            }
        }
    }

    /**
     * 从保存原图的地址读取图片
     */
    private String readpic() {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.example.login/files/Pictures/" + "UserIcon.png";
        return filePath;
    }

    private void saveImageToServer(final Bitmap bitmap, String outfile) {
        final File file = new File(outfile);
        if(file.exists())
        {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            out.close();
            Log.d("UpdateHeadPic_Activity", "已经保存headpic");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(this)
                .load(file)
                .apply(requestOptions)
                .into(headpic_preview);

        Thread uploadheadpic = new Thread() {
            @Override
            public void run() {
                UploadHeadPictoServer(file);
            }
        };
        uploadheadpic.start();
        try {
            uploadheadpic.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //headpic_preview.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.reset_headpic:
                viewInit();
                break;
            case R.id.select_from_camera:
                //"点击了照相";
                //  6.0之后动态申请权限 摄像头调取权限,SD卡写入权限
                //判断是否拥有权限，true则动态申请
                if (ContextCompat.checkSelfPermission(UpdateHeadPic_Activity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(UpdateHeadPic_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdateHeadPic_Activity.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_ADD_CASE_CALL_PHONE);
                } else {
                    try {
                        //有权限,去打开摄像头
                        takePhoto();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
                break;
            case R.id.select_from_album:
                //"点击了相册";
                //  6.0之后动态申请权限 SD卡写入权限
                if (ContextCompat.checkSelfPermission(UpdateHeadPic_Activity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UpdateHeadPic_Activity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_ADD_CASE_CALL_PHONE2);

                } else {
                    //打开相册
                    choosePhoto();
                }
                dialog.dismiss();
                break;
            case R.id.cancel_reset:
                dialog.dismiss();//关闭对话框
                break;
            default:break;
        }
    }

    private void UploadHeadPictoServer(File headpic)
    {
        int statusID = 1;

        FileInputStream fis = null;
        StringBuffer sb = null;

        String urlPath="http://www.lovecurry.club:8080/TravelApp/account/DoHeadPic";
        //Log.d("UpdateHeadPic_Activity",content);
        URL url;
        // 这里用sortWay变量 这样即使下拉刷新也能保持用户希望的排序方式
        try {
            url=new URL(urlPath);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection(); //开启连接
            conn.setConnectTimeout(5000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("ser-Agent", "Fiddler");

            conn.setRequestProperty("Cookie", JSESSIONID.getJSESSIONIDNAME());
            System.out.println("JSESSIONID.getJSESSIONIDNAME():"+JSESSIONID.getJSESSIONIDNAME());

            OutputStream os=conn.getOutputStream();
            int n = 0;
            try {
                fis = new FileInputStream(headpic);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, len); //写入图片数据
                }
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            os.flush();
            os.close();

            int code=conn.getResponseCode();
            System.out.println(code);
            if(code==200){   //与后台交互成功返回 200
                Log.d("UpdateHeadPic_Activity","服务器返回200");
//
//                //读取返回的json数据
//                InputStream inputStream=conn.getInputStream();
//                // 调用自己写的NetUtils() 将流转成string类型
//                String json= NetUtils.readString(inputStream);
//                //System.out.println(json);
//                System.out.println("Main_Activity json:"+json);
//
//                String status = conn.getHeaderField("status");
//                int dakastatus = Integer.parseInt(status);
//                Log.e("dakastatus: ","String Status = "+status+" int dakastatus = "+dakastatus);
//
//                if(dakastatus==200)
//                {
//                    statusID  = 1;
//                    return statusID;       //打卡成功
//                }
//                else if(dakastatus==-1)
//                {
//                    statusID  = 0;
//                    return statusID;       //打卡失败
//                }
            }
            else
            {
                Log.d("UpdateHeadPic_Activity","服务器没返回200");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        statusID  = -1;
//        return statusID;                  //数据提交失败
    }

    private Bitmap cropBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int cropWidth = w >= h ? h : w;// 裁切后所取的正方形区域边长
        int cropHeight = cropWidth;
        return Bitmap.createBitmap(bitmap, w/2-cropHeight/2, h/2-cropWidth/2, cropWidth, cropHeight);
    }
}
