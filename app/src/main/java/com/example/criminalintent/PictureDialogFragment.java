package com.example.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.File;

import Utils.PictureUtils;

public class PictureDialogFragment extends DialogFragment {
    private static final String ARG_PiCTURE = "files";

    private ImageView mImageView;

    //创建newInstance
    public static PictureDialogFragment newInstance(File file){
        Bundle args = new Bundle();
        args.putSerializable(ARG_PiCTURE,file);

        PictureDialogFragment fragment = new PictureDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        File file =(File) getArguments().getSerializable(ARG_PiCTURE);

        //显示视图
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo,null);

        //初始化imageView对象
        mImageView = v.findViewById(R.id.crime_photo_details);
        //设置位图
        Bitmap bitmap = PictureUtils.getScaledBitmap(file.getPath(),getActivity());
        mImageView.setImageBitmap(bitmap);

        //返回一个AlertDialog
        return new AlertDialog.Builder(getActivity()).setView(v)
                .setPositiveButton(android.R.string.ok,null)
                .create();

    }
}
