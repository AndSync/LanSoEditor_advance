package com.example.advanceDemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.example.advanceDemo.layerDemo.PhotoAlbumLayerDemoActivity;
import com.example.advanceDemo.utils.ConvertToEditModeDialog;
import com.example.advanceDemo.utils.CopyDefaultVideoAsyncTask;
import com.example.advanceDemo.utils.DemoProgressDialog;
import com.example.advanceDemo.utils.DemoUtil;
import com.example.advanceDemo.utils.FileExplorerActivity;
import com.example.advanceDemo.utils.GlideEngine;
import com.lansoeditor.advanceDemo.R;
import com.lansosdk.box.LSOVideoOption;
import com.lansosdk.box.OnLanSongSDKCompletedListener;
import com.lansosdk.box.OnLanSongSDKErrorListener;
import com.lansosdk.box.OnLanSongSDKProgressListener;
import com.lansosdk.box.VideoFrameLayer;
import com.lansosdk.videoeditor.DrawPadAllExecute2;
import com.lansosdk.videoeditor.EditModeVideo;
import com.lansosdk.videoeditor.LanSoEditor;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lansosdk.videoeditor.MediaInfo;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.List;


public class ListMainActivity extends Activity implements OnClickListener {

    private static final String TAG = "ListMainActivity";
    private static final boolean VERBOSE = false;
    private final static int SELECT_FILE_REQUEST_CODE = 10;
    int permissionCnt = 0;
    private TextView tvVideoPath;
    private boolean isPermissionOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 初始化SDK˙
         */
        LanSoEditor.initSDK(getApplicationContext(),null);
        LanSongFileUtil.deleteDefaultDir();
        /**
         * 检查权限
         */
        testPermission();

        initView();
        //显示版本提示
        DemoUtil.showVersionDialog(ListMainActivity.this);
        testFile();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LanSoEditor.unInitSDK();
        LanSongFileUtil.deleteDefaultDir();
    }

    @Override
    public void onClick(View v) {

        if (!isPermissionOk) {
            testPermission();
        }

        if (isPermissionOk && checkPath()) {
                switch (v.getId()) {
                    case R.id.id_mainlist_camerarecord:
                        startDemoActivity(ListCameraRecordActivity.class);
                        break;
                    case R.id.id_mainlist_somelayer:
                        startDemoActivity(ListLayerDemoActivity.class);
                        break;
                    case R.id.id_mainlist_changjing:
                        startDemoActivity(ListSceneDemoActivity.class);
                        break;
                    case R.id.id_mainlist_douyin:
                        startDemoActivity(DouYinDemoActivity.class);
                        break;
                    case R.id.id_mainlist_weishang:
                        startDemoActivity(ListAEActivity.class);
                        break;
                    case R.id.id_mainlist_videoonedo:
                        startDemoActivity(VideoOneDO2Activity.class);
                        break;
                    case R.id.id_mainlist_bitmaps:
                        startDemoActivity(ListBitmapAudioActivity.class);
                        break;
                    case R.id.id_main_list_lso_layer:
                        startDemoActivity(ConcatCompositionActivity.class);
                        break;
                    case R.id.id_mainlist_videoplay:
                        startDemoActivity(VideoPlayerActivity.class);
                        break;
                    default:
                        break;
                }
        }
    }
    // -----------------------------
    private void initView() {
        tvVideoPath = (TextView) findViewById(R.id.id_main_tvvideo);
        findViewById(R.id.id_mainlist_camerarecord).setOnClickListener(this);
        findViewById(R.id.id_mainlist_somelayer).setOnClickListener(this);
        findViewById(R.id.id_mainlist_changjing).setOnClickListener(this);
        findViewById(R.id.id_mainlist_douyin).setOnClickListener(this);
        findViewById(R.id.id_mainlist_weishang).setOnClickListener(this);
        findViewById(R.id.id_mainlist_videoonedo).setOnClickListener(this);
        findViewById(R.id.id_mainlist_bitmaps).setOnClickListener(this);
        findViewById(R.id.id_mainlist_videoplay).setOnClickListener(this);

        findViewById(R.id.id_main_list_lso_layer).setOnClickListener(this);

        //---------------------
        findViewById(R.id.id_main_select_video).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(ListMainActivity.this, FileExplorerActivity.class);
//                startActivityForResult(i, SELECT_FILE_REQUEST_CODE);
                PictureSelector.create(ListMainActivity.this)
                        .openGallery(PictureMimeType.ofVideo())
                        .loadImageEngine(GlideEngine.createGlideEngine())
                        .isWithVideoImage(false)
                        .maxVideoSelectNum(1)
                        .imageSpanCount(4)
                        .isCamera(false)
                        .selectionMode(PictureConfig.SINGLE)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
            }
        });

        findViewById(R.id.id_main_use_default_videobtn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new CopyDefaultVideoAsyncTask(ListMainActivity.this, tvVideoPath, "dy_xialu2.mp4").execute();
            }
        });
    }

    private boolean checkPath() {
        if (tvVideoPath.getText() != null && tvVideoPath.getText().toString().isEmpty()) {
            Toast.makeText(ListMainActivity.this, "请输入视频地址", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            String path = tvVideoPath.getText().toString();
            if (!(new File(path)).exists()) {
                Toast.makeText(ListMainActivity.this, "文件不存在", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                MediaInfo info = new MediaInfo(path);
                return info.prepare();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == SELECT_FILE_REQUEST_CODE) {
                    Bundle b = data.getExtras();
                    String selected = b.getString("SELECT_VIDEO");
                    checkConvertDialog(selected);
                }
                if (requestCode == PictureConfig.CHOOSE_REQUEST){
                    //图片视频回掉结果
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if (selectList != null && selectList.size() > 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            checkConvertDialog( selectList.get(0).getAndroidQToPath());
                        } else {
                            checkConvertDialog( selectList.get(0).getPath());
                        }
                    }

                }
                break;
            default:
                break;
        }
    }

    private void checkConvertDialog(final String file) {
        if (!EditModeVideo.checkEditModeVideo(file)) {
            new AlertDialog.Builder(ListMainActivity.this)
                    .setTitle("提示")
                    .setMessage("是否转换为 编辑模式!")
                    .setPositiveButton("转换", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //转换为编辑模式对话框.
                            ConvertToEditModeDialog editMode = new ConvertToEditModeDialog(ListMainActivity.this, file, new ConvertToEditModeDialog.onConvertToEditModeDialogListener() {
                                @Override
                                public void onConvertCompleted(String video) {
                                    if (tvVideoPath != null) {
                                        tvVideoPath.setText(video);
                                    }
                                }
                            });
                            editMode.start();
                        }
                    })
                    .setNegativeButton("不转", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (tvVideoPath != null) {
                                tvVideoPath.setText(file);
                            }
                        }
                    })
                    .show();
        }
    }

    private void startDemoActivity(Class<?> cls) {
        String path = tvVideoPath.getText().toString();
        Intent intent = new Intent(ListMainActivity.this, cls);
        intent.putExtra("videopath", path);
        DemoApplication.getInstance().currentEditVideo = path;

        startActivity(intent);
    }

    private void testPermission() {
        if (permissionCnt > 2) {
            DemoUtil.showDialog(ListMainActivity.this, "Demo没有读写权限,请关闭后重新打开demo,并在弹出框中选中[允许]");
            return;
        }
        permissionCnt++;
        // PermissionsManager采用github上开源库,不属于sdk的一部分.
        // 下载地址是:https://github.com/anthonycr/Grant,您也可以使用别的方式来检查app所需权限.
        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this,
                new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        isPermissionOk = true;
                    }
                    @Override
                    public void onDenied(String permission) {
                        isPermissionOk = false;
                    }
                });
    }

    private void testFile() {



    }


}
