package com.example.criminalintent;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import Utils.PictureUtils;
import database.CrimeDbSchema;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;

    //模型向控制器传递参数
    private Crime mCrime;

    private EditText mTitleField;
    private Button mDataButton;
    private CheckBox mSolvedCheckBox;

    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;

    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    private File mPhotoFile;

    //编写newInstance(UUID)方法，从CrimePagerActivity获取crimeId
    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);

        CrimeFragment fragment= new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //从Argument获取crimeId
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

        //获取照片文件位置
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

        //显示菜单项
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime,container,false);

        //实例化组件
        mTitleField = v.findViewById(R.id.crime_title);
        //使用Crime数据更新视图
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            /**
             * CharSequence代表用户输入
             */
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDataButton = v.findViewById(R.id.crime_data);
        updateDate();
        mDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                //DatePickerFragment dialog = new DatePickerFragment();
                /**
                 * 添加newIntance
                 */
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getTitleData());
                //向DatePickerFragment传递
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                dialog.show(fm,"dialog_date");
            }
        });

        mReportButton = v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //创建隐式Intent用来发送消息
                Intent intent = new Intent(Intent.ACTION_SEND);
                //指定发送信息的类型
                intent.setType("text/plain");
                //邮件内容
                intent.putExtra(Intent.EXTRA_TEXT,getCrimeReport());
                //邮件主题
                intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject));
                /*

                //通过ShareCompat创建发送信息
                Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .getIntent();

                 */
                //使用选择器
                // 目的：用户可以自定义选择打开的应用，不会默认
                intent = Intent.createChooser(intent,getString(R.string.send_report));
                startActivity(intent);
            }
        });


        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect()!=null){
            mSuspectButton.setText(mCrime.getSuspect());
        }

        //检查是否存在联系人应用
        PackageManager packageManager = getActivity().getPackageManager();
        //如果没有联系人应用，按钮不可选
        if (packageManager.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY)==null){
            mSuspectButton.setEnabled(false);
        }

        mCallButton = v.findViewById(R.id.crime_call);
        mCallButton.setEnabled(false);



        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        //设置监听器
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mPhotoView = v.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotoFile == null || !mPhotoFile.exists()){
                    mPhotoView.setImageDrawable(null);
                }else {

                    FragmentManager fm = getFragmentManager();
                    PictureDialogFragment dialogFragment = PictureDialogFragment.newInstance(mPhotoFile);
                    //向PictureDialogFragment中传递数据
                    dialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_PHOTO);
                    dialogFragment.show(fm, "dialog_photo");
                }
            }
        });


        mPhotoButton = v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile!=null && captureImage.resolveActivity(packageManager)!=null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //把本地文件路径转换为uri形式
                Uri uri = FileProvider.getUriForFile(getActivity(),"com.example.criminalintent.fileprovider",mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);

                List<ResolveInfo>cameraActivities = getActivity().getPackageManager()
                        .queryIntentActivities(captureImage,PackageManager.MATCH_DEFAULT_ONLY);
                for(ResolveInfo activity:cameraActivities){
                    //允许相机应用在uri指定的位置写文件
                    getActivity().grantUriPermission(activity.activityInfo.packageName,uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });

        //在相框中显示位图
        //updatePhotoView();

        //优化缩略图加载
        final ViewTreeObserver mPhotoObserver = mPhotoView.getViewTreeObserver();
        mPhotoObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updatePhotoView(mPhotoView.getWidth(),mPhotoView.getHeight());
            }
        });


        return  v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime,menu);
    }

    /**
     * 选择菜单项响应事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case R.id.delete:
               //删除Crime
               CrimeLab.get(getActivity()).deleteCrime(mCrime);
               getActivity().finish();
               return true;
               default:
                   return super.onOptionsItemSelected(item);
       }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setTitleData(date);
            updateDate();
        }

        else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            String contactId = null;

            //查询语句，返回全部联系人的名字,得到目标联系人的ID
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts._ID};

            //cursor只包含一条记录
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            try {
                if (c.getCount() == 0) {
                    return;
                }

                c.moveToFirst();
                //获取cursor的字符串形式
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);

            } finally {
                c.close();
            }
        }

        else if(requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),"com.example.criminalintent.fileprovider",mPhotoFile);

            //关闭文件访问
            getActivity().revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            //updatePhotoView();;
            updatePhotoView(mPhotoView.getWidth(),mPhotoView.getHeight());
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    /**
     * 格式化按钮日期
     */
    private void updateDate() {
        mDataButton.setText(formateDate(mCrime.getTitleData()));
    }

    //发送内容
    private String getCrimeReport(){
        String solvedString = null;
        if (mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }else{
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dataString = DateFormat.format(dateFormat,mCrime.getTitleData()).toString();

        String suspect = mCrime.getSuspect();

        String report = getString(R.string.crime_report,mCrime.getTitle(),dataString,solvedString,"The suspect is "+suspect);
        return report;
    }

    //格式化日期
    private String formateDate(Date date){
        SimpleDateFormat df = new SimpleDateFormat("EE MM/dd/yyyy", Locale.CHINA);
        return df.format(date);
    }

    /**
     * 更新ImageView
     */
    private void updatePhotoView(int width, int height){
        if (mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else{
            //确定位图尺寸大小
            //Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());

            //优化缩略图加载
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),width,height);

            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
