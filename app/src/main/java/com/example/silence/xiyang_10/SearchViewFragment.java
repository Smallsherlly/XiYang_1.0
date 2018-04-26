package com.example.silence.xiyang_10;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.silence.xiyang_10.db.DbHelper;
import com.example.silence.xiyang_10.models.HandEdit;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Silence on 2018/3/28.
 */

public class SearchViewFragment extends android.support.v4.app.Fragment {

    ViewGroup mRoot;
    CardView hand_card;
    ImageView search;
    RecyclerView mRecyclerView;
    FloatingActionButton open_hand;
    Adapter adapter;
    Adapter search_adapter;
    EditText editText;
    private searchCallBack callBack;
    List<SearchViewFragment.Book> mylist;
    List<SearchViewFragment.Book> searchlist;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.searchview,container,false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    HandEdit hand = data.getExtras().getParcelable("HandEdit");
                    adapter.addItem(adapter.getItemCount(),new Book(hand.getTitle(),hand.getAuthor(),hand.getCover_path(),hand.getCreation()));
                    adapter.notifyDataSetChanged();
                    Log.d("result","comback");
            }
        }
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView02);
        editText = (EditText) view.findViewById(R.id.editText);
        search = (ImageView) view.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BaseActivity activity = (BaseActivity) getActivity();
                searchlist = findData(editText.getText().toString());
                search_adapter = new SearchViewFragment.Adapter(getContext(), activity.getBottomNavigation().getNavigationHeight(), false,searchlist);
                mRecyclerView.setAdapter(search_adapter);
            }
        });
        open_hand = (FloatingActionButton) view.findViewById(R.id.open_handedit);
        open_hand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tent = getActivity().getIntent();
                String username = tent.getStringExtra("username");
                Intent intent = new Intent(getContext(),HandEditActivity.class);
                intent.putExtra("username",username);
                startActivityForResult(intent,1);
            }
        });

        final BaseActivity activity = (BaseActivity) getActivity();
        createAdater(activity.getBottomNavigation().getNavigationHeight());

        ImageButton delete = (ImageButton) view.findViewById(R.id.cancel);
        EditText title = (EditText) view.findViewById(R.id.editText);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setText("");
            }
        });
    }// @Nullable  意味着可以传入null值

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }
    private void createAdater(int height) {
        mylist = createData();
        adapter = new SearchViewFragment.Adapter(getContext(), height, false,mylist);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter);
    }

    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);// 滑动后在显示页面顶部
    }



    static class TwoLinesViewHolder extends RecyclerView.ViewHolder {// 列表项的类
        final TextView title;
        final TextView description;
        final ImageView imageView;
        final Button button1;
        final Button button2;
        final int marginBottom;
        final TextView creation;

        public TwoLinesViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(android.R.id.title);
            description = (TextView) itemView.findViewById(android.R.id.text1);
            imageView = (ImageView) itemView.findViewById(android.R.id.icon);
            creation = (TextView) itemView.findViewById(R.id.creation);
            marginBottom = ((ViewGroup.MarginLayoutParams) itemView.getLayoutParams()).bottomMargin;
            button1 = (Button) itemView.findViewById(android.R.id.button1);
            button2 = (Button) itemView.findViewById(android.R.id.button2);
        }
    }
    private class Adapter extends RecyclerView.Adapter<SearchViewFragment.TwoLinesViewHolder> {
        private final Picasso picasso;
        private final int navigationHeight;
        private List<SearchViewFragment.Book> data;

        public Adapter(final Context context, final int navigationHeight, final boolean hasAppBarLayout, final List<SearchViewFragment.Book> data) {
            this.navigationHeight = navigationHeight;
            this.data = data;
            this.picasso = Picasso.with(context);
        }
        public void addItem(int positon,SearchViewFragment.Book hand){
            data.add(positon,hand);
            notifyItemInserted(positon);
        }

        public void removeItem(int position){
            data.remove(position);
            notifyItemRemoved(position);
        }
        @Override
        public SearchViewFragment.TwoLinesViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.handedit_card, parent, false);
            final SearchViewFragment.TwoLinesViewHolder holder = new SearchViewFragment.TwoLinesViewHolder(view);


            holder.button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Intent tent = getActivity().getIntent();
                    String username = tent.getStringExtra("username");
                    Intent intent = new Intent(getContext(),HandEditActivity.class);
                    intent.putExtra("username",username);
                    intent.putExtra("Creation",holder.creation.getTag().toString());
                    startActivity(intent);


                }
            });
            holder.button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    HandEdit hand = DbHelper.getInstance().getHandEdit(Long.valueOf(holder.creation.getTag().toString()));
                    hand.setTrashed(1);
                    DbHelper.getInstance().updateHandEdit(hand,true);
                    removeItem(holder.getAdapterPosition());
                }
            });

            return holder;
        }
        @Override
        public void onBindViewHolder(final SearchViewFragment.TwoLinesViewHolder holder, final int position) {
            ((ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams()).topMargin = 0;
            if (position == getItemCount() - 1) {
                ((ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams()).bottomMargin = holder.marginBottom + navigationHeight;
            }else {
                ((ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams()).bottomMargin = holder.marginBottom;
            }
            //data = createData();
            final SearchViewFragment.Book item = data.get(position);
            holder.title.setText(item.title);
            holder.description.setText(item.author);
            holder.imageView.setImageBitmap(null);
            Date date = new Date(item.creation);
            java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
            holder.creation.setText(format.format(date));
            holder.creation.setTag(item.creation);

            picasso.cancelRequest(holder.imageView);
            Log.i("imageUrl",item.imageUrl+"result");
            picasso
                    .load(item.imageUrl)
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
        Intent tent = getActivity().getIntent();
        String username = tent.getStringExtra("username");
        List<HandEdit> handEdits = DbHelper.getInstance().getAllHandEdits(username,false);
        List<SearchViewFragment.Book> book = new ArrayList<>();
        for(HandEdit hand:handEdits){
            book.add(new Book(hand.getTitle(),hand.getAuthor(),hand.getCover_path(),hand.getCreation()));
            Log.d("result",hand.getTitle()+":"+hand.getJson_path());
        }


        return book;
    }

    private List<SearchViewFragment.Book> findData(String pattern) {
        Intent tent = getActivity().getIntent();
        String username = tent.getStringExtra("username");
        List<HandEdit> handEdits = DbHelper.getInstance().getHandEditsByPattern(username,pattern);
        List<SearchViewFragment.Book> book = new ArrayList<>();
        for(HandEdit hand:handEdits){
            book.add(new Book(hand.getTitle(),hand.getAuthor(),hand.getCover_path(),hand.getCreation()));
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

    public interface searchCallBack{
        void sendHandEdit(Long creation);
    }
    public void setOnSearchCallBackListener(searchCallBack listener){
        this.callBack = listener;
    }
}
