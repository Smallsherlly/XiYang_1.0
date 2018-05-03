package com.example.silence.xiyang_10;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.example.silence.xiyang_10.RichEditor.DragScaleView;
import com.example.silence.xiyang_10.RichEditor.EditorBean;
import com.example.silence.xiyang_10.db.DbHelper;
import com.example.silence.xiyang_10.models.HandEdit;
import com.example.silence.xiyang_10.myhttputils.FailedMsgUtils;
import com.example.silence.xiyang_10.myhttputils.MyHttpUtils;
import com.example.silence.xiyang_10.myhttputils.bean.CommCallback;
import com.example.silence.xiyang_10.myhttputils.bean.HttpBody;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;


/**
 * Created by Silence on 2018/3/14.
 * 这是主页的碎片类
 */

public class MainPageFragment extends Fragment implements ViewPager.OnPageChangeListener,OnItemClickListener {
    RecyclerView mRecyclerView;
    CoordinatorLayout mCoordinatorLayout;
    private ConvenientBanner convenientBanner;
    private ArrayList<Integer> localImages = new ArrayList<Integer>();
    ViewGroup mRoot;
    Handler mHandler;
    String username;
    SwipeRefreshLayout mSwipeLayout;
    List<SearchViewFragment.Book> searchlist;
    MainPageFragment.Adapter search_adapter;
    private ProgressDialog mProgressDialog;
    public MainPageFragment(){

    }
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);// 将布局加载到碎片实例中

        return view;
    }
    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Intent intent = getActivity().getIntent();
        username = intent.getStringExtra("username");
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_ly);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("正从云端下载文件，请稍候");

        mHandler = new Handler()
        {
            public void handleMessage(android.os.Message msg)
            {
                switch (msg.what)
                {
                    case 0:

                        final BaseActivity activity = (BaseActivity) getActivity();
                        searchlist = createData();
                        search_adapter = new MainPageFragment.Adapter(getContext(), activity.getBottomNavigation().getNavigationHeight(), false,searchlist);
                        mRecyclerView.setAdapter(search_adapter);
                        if(mSwipeLayout.isRefreshing()){
                            //关闭刷新动画
                            mSwipeLayout.setRefreshing(false);
                        }
                        break;

                }
            };
        };
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新adapter
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        final String state= NetUilts.getDatabase("admin");

                        getActivity().runOnUiThread(new Runnable() {//执行任务在主线程中
                            @Override
                            public void run() {//就是在主线程中操作
                                // Toast.makeText(MainActivity.this, state, Toast.LENGTH_SHORT).show();
                                Log.i("data",state);
                                if(!state.equals("无此用户数据"))
                                    dataStringManage(state);
                            }
                        });
                    }
                }).start();
                //这里可以做一下下拉刷新的操作
                //例如下面代码，在方法中发送一个handler模拟延时操作
                mHandler.sendEmptyMessageDelayed(0, 3000);
            }
        });
        mRecyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView01);
        initViews();
        init();
    }// @Nullable  意味着可以传入null值
    public void dataStringManage(String data){
        String[] parts = data.split(";");
        for(int i=0; i<parts.length; i++) {
            String[] fields = parts[i].split(",");
            HandEdit handEdit = new HandEdit();
            handEdit.setCreation(fields[0]);
            handEdit.setLastModification(fields[1]);
            handEdit.setZan_number(Long.valueOf(fields[2]));
            handEdit.setAuthor(fields[3]);
            handEdit.setJson_path(fields[4]);
            handEdit.setCover_path(fields[5]);
            handEdit.setTitle(fields[6]);
            handEdit.setContent(fields[7]);
            handEdit.setArchived(Integer.valueOf(fields[8]));
            handEdit.setTrashed(Integer.valueOf(fields[9]));
            File imgsrc = new File(fields[4]);
            if (!imgsrc.exists()) {
                int index = fields[4].indexOf("2018");
                if (index != -1) {
                    String filename = fields[4].substring(index);
                    onDownload(filename, fields[3]);
                }
            }
                imgsrc = new File(fields[5]);
                if (!imgsrc.exists()) {
                    int index2 = fields[5].indexOf("2018");
                    if (index2 != -1) {
                        String filename = fields[5].substring(index2);
                        onDownload(filename, fields[3]);
                    }

                }
                DbHelper.getInstance().updateHandEdit(handEdit, false);
            }


    }

    public void onDownload(String filename,String loadname) {
        HttpBody body = new HttpBody();
        //mProgressDialog.show();
        body.setUrl("http://119.23.206.213:80/Login/upload/userdata/"+loadname+"/"+filename)
                .setConnTimeOut(6000)
                .setFileSaveDir(getActivity().getExternalFilesDir(null)+"/userdata/"+loadname)
                .setReadTimeOut(5 * 60 * 1000);

        MyHttpUtils.build()
                .setHttpBody(body)
                .onExecuteDwonload(new CommCallback() {

                    @Override
                    public void onSucceed(Object o) {
                        // view.setImageURI(Uri.parse(bean.getContent()));
                        // ToastUtils.showToast(HandEditActivity.this, "下载完成");
                        //mProgressDialog.dismiss();

                        //为了保险起见可以先判断当前是否在刷新中（旋转的小圈圈在旋转）....
//                        if(mSwipeLayout.isRefreshing()){
//                            //关闭刷新动画
//                            mSwipeLayout.setRefreshing(false);
//                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        ToastUtils.showToast(getActivity(), FailedMsgUtils.getErrMsgStr(throwable));
                    }

                    @Override
                    public void onDownloading(long total, long current) {
                        System.out.println(total + "-------" + current);
                        //tvProgress.setText(new DecimalFormat("######0.00").format(((double) current / total) * 100) + "%");//保留两位小数
                    }
                });
    }
    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final BaseActivity activity = (BaseActivity) getActivity();
        mRoot = (ViewGroup) activity.findViewById(R.id.CoordinatorLayout01);

        if (mRoot instanceof CoordinatorLayout) {// 判断mRoot是否属于Coordinatorlayout实例
            mCoordinatorLayout = (CoordinatorLayout) mRoot;
        }

        final int navigationHeight=0;

        final BottomNavigation navigation = activity.getBottomNavigation();
        if (null != navigation) {
            navigation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    navigation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int totalHeight = navigation.getNavigationHeight();
                    createAdater(totalHeight);
                }
            });
        } else {
            createAdater(navigationHeight);
        }


    }

    private void init() {
//        initImageLoader();
        loadTestDatas();
//        本地图片例子
        convenientBanner.setPages(
                new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, localImages)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused})
                //设置指示器的方向
//                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)

                .setOnItemClickListener(this);
        //网络加载例子
//        networkImages= Arrays.asList(images);
//        convenientBanner.setPages(new CBViewHolderCreator<NetworkImageHolderView>() {
//            @Override
//            public NetworkImageHolderView createHolder() {
//                return new NetworkImageHolderView();
//            }
//        },networkImages);
    }

    //初始化网络图片缓存库
    private void initImageLoader(){
        //网络图片例子,结合常用的图片缓存库UIL,你可以根据自己需求自己换其他网络图片库
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().
                showImageForEmptyUri(R.drawable.ic_default_adimage)
                .cacheInMemory(true).cacheOnDisk(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity()).defaultDisplayImageOptions(defaultOptions)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }
    /*
    加载图片
    * */
    private void loadTestDatas() {
        //本地图片集合
        for(int position=0; position<3; position++) {
            localImages.add(getResId("ic_test_" + position, R.mipmap.class));
        }
    }


    @Override
    public void onDestroyView(){
        super.onDestroyView();
        localImages.clear();// 防止图片重复加载
    }
    /**
     * 通过文件名获取资源id 例子：getResId("icon", R.drawable.class);
     *
     * @param variableName
     * @param c
     * @return
     */
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void initViews() {
        convenientBanner = (ConvenientBanner) getView().findViewById(R.id.convenientBanner);


        try {
            Class cls = Class.forName("com.ToxicBakery.viewpager.transforms.RotateDownTransformer");//设置翻页效果
            ABaseTransformer transforemer = (ABaseTransformer) cls.newInstance();
            convenientBanner.getViewPager().setPageTransformer(true, transforemer);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private void createAdater(int height) {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new Adapter(getContext(), height, false, createData()));
    }

    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);// 滑动后在显示页面顶部
    }



    static class TwoLinesViewHolder extends RecyclerView.ViewHolder {// 列表项的类
        final TextView title;
        final TextView description;
        final ImageView imageView;
        final Button button1;
        final int marginBottom;
        final TextView creationx;

        public TwoLinesViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.titlex);
            description = (TextView) itemView.findViewById(R.id.textx);
            imageView = (ImageView) itemView.findViewById(R.id.iconx);
            creationx = (TextView) itemView.findViewById(R.id.creationx);
            marginBottom = ((ViewGroup.MarginLayoutParams) itemView.getLayoutParams()).bottomMargin;
            button1 = (Button) itemView.findViewById(R.id.buttonx);

        }
    }
    private class Adapter extends RecyclerView.Adapter<TwoLinesViewHolder> {
        private final Picasso picasso;
        private final int navigationHeight;
        private List<SearchViewFragment.Book> data;

        public Adapter(final Context context, final int navigationHeight, final boolean hasAppBarLayout, final List<SearchViewFragment.Book> data) {
            this.navigationHeight = navigationHeight;
            this.data = data;
            this.picasso = Picasso.with(context);
        }
        @Override
        public TwoLinesViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.simple_card_item, parent, false);
            final TwoLinesViewHolder holder = new TwoLinesViewHolder(view);

            holder.button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Intent intent = new Intent(getContext(),HandEditActivity.class);
                    Intent userdata = getActivity().getIntent();
                    String username = userdata.getStringExtra("username");
                    intent.putExtra("username",username);
                    intent.putExtra("fromModel",true);
                    intent.putExtra("Creation", holder.creationx.getTag().toString());
                    startActivity(intent);
                }
            });

            return holder;
        }
        @Override
        public void onBindViewHolder(final TwoLinesViewHolder holder, final int position) {
            ((ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams()).topMargin = 0;
            if (position == getItemCount() - 1) {
                ((ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams()).bottomMargin = holder.marginBottom + navigationHeight;
            }else {
                ((ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams()).bottomMargin = holder.marginBottom;
            }

            final SearchViewFragment.Book item = data.get(position);
            holder.title.setText(item.title);
            holder.description.setText(item.author);
            holder.imageView.setImageBitmap(null);
            Date date = new Date(item.creation);
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
            holder.creationx.setText(format.format(date));
            holder.creationx.setTag(item.creation);

            picasso.cancelRequest(holder.imageView);
            String path = "file://"+item.imageUrl;
            picasso
                    .load(path)
                    .noPlaceholder()
                    .resizeDimen(R.dimen.simple_card_image_width, R.dimen.simple_card_image_height)
                    .centerCrop()
                    .into(holder.imageView);
        }
        @Override
        public int getItemCount() {
            return data.size();
        }
    }
    private List<SearchViewFragment.Book> createData() {
        List<HandEdit> handEdits = DbHelper.getInstance().getAllHandEdits("admin",false);
        List<SearchViewFragment.Book> book = new ArrayList<>();
        for(HandEdit hand:handEdits){
            book.add(new SearchViewFragment.Book(hand.getTitle(),hand.getAuthor(),hand.getCover_path(),hand.getCreation()));
            Log.d("result",hand.getTitle()+":"+hand.getJson_path());
        }


        return book;
    }

    private List<SearchViewFragment.Book> findData(String pattern) {

        List<HandEdit> handEdits = DbHelper.getInstance().getHandEditsByPattern("admin",pattern);
        List<SearchViewFragment.Book> book = new ArrayList<>();
        for(HandEdit hand:handEdits){
            book.add(new SearchViewFragment.Book(hand.getTitle(),hand.getAuthor(),hand.getCover_path(),hand.getCreation()));
            Log.d("result",hand.getTitle()+":"+hand.getJson_path());
        }


        return book;
    }
    static class Book {
        final String title;
        final String author;
        final String imageUrl;
        final Long creation;

        Book(final String title, final String author, final String imageUrl,final Long creation) {
            this.title = title;
            this.author = author;
            this.imageUrl = imageUrl;
            this.creation = creation;
        }
    }
    // 开始自动翻页
    @Override
    public void onResume() {
        super.onResume();
        //开始自动翻页
        convenientBanner.startTurning(5000);
    }

    // 停止自动翻页
    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onItemClick(int position) {

    }
}

