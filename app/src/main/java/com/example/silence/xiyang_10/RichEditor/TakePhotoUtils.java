package com.example.silence.xiyang_10.RichEditor;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 选择照片、拍照的工具类
 * Created by HDL on 2016/9/30.
 */
public class TakePhotoUtils {
    /**
     * App目标sdk在Android6.0以下的系统,使用下面的方式可以拍照
     *
     * @param context
     * @return Uri图片的备用uri
     */
    public static Uri takePhotoAndroid6(Activity context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, filename);
        Uri photoUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        context.startActivityForResult(intent, CamaraRequestCode.CAMARA_TAKE_PHOTO);
        return photoUri;
    }

    /**
     * 获取相册的图片
     *
     * @param mActivity
     * @param flag
     * @return
     */
    public static Uri getGalleryImg(Activity mActivity, int flag) {
        Intent getImgIntent = new Intent();
        getImgIntent.setAction(Intent.ACTION_PICK);
        getImgIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Uri imageUri = null;
        if (getImgIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            String sdcardState = Environment.getExternalStorageState();
            File outputImage = null;
            if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
                try {
                    outputImage = createImageFile(mActivity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("TakePhotoUtils", "memory exception");
            }
            try {
                if (outputImage.exists()) {
                    outputImage.delete();
                }
                outputImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (outputImage != null) {
                imageUri = Uri.fromFile(outputImage);
                getImgIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                mActivity.startActivityForResult(getImgIntent, flag);
            }
        }
        return imageUri;
    }

    /**
     * 创建图片文件
     *
     * @param mActivity
     * @return
     * @throws IOException
     */
    public static File createImageFile(Activity mActivity) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + System.currentTimeMillis();//创建以时间命名的文件名称
        File storageDir = getOwnCacheDirectory(mActivity, "takephoto");//创建保存的路径
        File image = new File(storageDir.getPath(), imageFileName + ".jpg");
        if (!image.exists()) {
            try {
                //在指定的文件夹中创建文件
                image.createNewFile();
            } catch (Exception e) {
            }
        }

        return image;
    }

    /**
     * 根据目录创建文件夹
     *
     * @param context
     * @param cacheDir
     * @return
     */
    public static File getOwnCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        //判断sd卡正常挂载并且拥有权限的时候创建文件
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }
        if (appCacheDir == null || !appCacheDir.exists() && !appCacheDir.mkdirs()) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    /**
     * 备用图片uri
     *
     * @return
     */
    public static Uri backPhotoUri(Context context) {
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, filename);
        Uri photoUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        return photoUri;
    }


    /**
     * 获取压缩图片的options
     *
     * @param path  图片的路径
     * @param grade 压缩的级别---一般为4即可
     * @return
     */
    public static BitmapFactory.Options getOptions(String path, int grade) {
        Log.e("Mylog", path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = grade;      //此项参数可以根据需求进行计算
        options.inJustDecodeBounds = false;
        options.inDither = false;    /*不进行图片抖动处理*/
        options.inPreferredConfig = null;  /*设置让解码器以最佳方式解码*/
        return options;
    }

    /**
     * 获取拍照后图片的uri
     *
     * @param mActivity   当前调用的activity
     * @param requestCode 请求码
     * @return
     * @throws IOException
     */
    public static Uri takePhoto(Activity mActivity, int requestCode) throws IOException {

        // Must be done during an initialization phase like onCreate

        //指定拍照intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri = null;
        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            String sdcardState = Environment.getExternalStorageState();
            File outputImage = null;
            if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
                outputImage = createImageFile(mActivity);
            } else {
                Toast.makeText(mActivity.getApplicationContext(), "内存异常", Toast.LENGTH_SHORT).show();
            }
            try {
                if (outputImage.exists()) {
                    outputImage.delete();
                }
                outputImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (outputImage != null) {
                imageUri = Uri.fromFile(outputImage);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, backPhotoUri(mActivity));
                mActivity.startActivityForResult(takePictureIntent, requestCode);
            }
        }

        return imageUri;
    }





    /**
     * 检查是否有权限
     *
     * @param context
     * @return
     */
    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE");
        return perm == 0;
    }

    /**
     * 通过uri获取图片的真实路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePathByUri(Context context, Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param context
     * @param imageUri
     * @return
     */
    public static String getImageAbsolutePath(Activity context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    /**
     * @param context
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 获取图片的路径
     *
     * @param imgPath
     * @return
     */
    public static String getImageName(String imgPath) {
        return imgPath.substring(imgPath.lastIndexOf("/") + 1);
    }

    /**
     * 压缩图片尺寸
     *
     * @param pathName
     * @param targetWidth
     * @param targetHeight
     * @return
     */
    public static Bitmap compressBySize(String pathName, int targetWidth, int targetHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;// 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);
        // 得到图片的宽度、高度；
        float imgWidth = opts.outWidth;
        float imgHeight = opts.outHeight;
        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于等于该比例的最小整数；
//        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
//        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
        opts.inSampleSize = 1;
        if (widthRatio > 1 || widthRatio > 1) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }
        //设置好缩放比例后，加载图片进内容；
        opts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(pathName, opts);
        return bitmap;
    }

    /**
     * 存储进SD卡
     *
     * @param bm
     * @param fileName
     * @param quality  图片保存的质量
     * @throws Exception
     */
    public static String saveFile(Context mActivity, Bitmap bm, String fileName, int quality) throws Exception {
        File dirFile = new File(fileName);
        String imageName = getImageName(fileName);
        File copyFile = null;
        boolean isTakePhoto = true;
        //如果没有在takephoto目录下 就复制图片到该目录
        if (!fileName.contains("takephoto")) {
            isTakePhoto = false;
            File takephoto = getOwnCacheDirectory(mActivity, "takephoto");
            copyFile = new File(takephoto.getAbsolutePath(), imageName);
            FileInputStream fis = new FileInputStream(dirFile);
            FileOutputStream fos = new FileOutputStream(copyFile);
            int len;
            byte[] buf = new byte[64 * 1024];
            while ((len = fis.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fis.close();
            fos.close();
        }
        File myCaptureFile = null;
        if (isTakePhoto) {
            //检测图片是否存在
            if (dirFile.exists()) {
                dirFile.delete();  //删除原图片
            }
            myCaptureFile = new File(fileName);
        } else {
            //检测图片是否存在
            if (copyFile.exists()) {
                copyFile.delete();  //删除原图片
            }
            myCaptureFile = new File(copyFile.getAbsolutePath());
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        //100表示不进行压缩，70表示压缩率为30%
        bm.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        bos.flush();
        bos.close();

        return isTakePhoto ? fileName : copyFile.getAbsolutePath();
    }
}
