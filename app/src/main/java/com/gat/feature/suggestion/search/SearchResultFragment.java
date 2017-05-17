package com.gat.feature.suggestion.search;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.gat.R;
import com.gat.common.adapter.Item;
import com.gat.common.adapter.impl.OnItemLoadMoreClickListener;
import com.gat.common.listener.IRecyclerViewItemClickListener;
import com.gat.common.listener.LoadMoreScrollListener;
import com.gat.common.util.MZDebug;
import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.Keyword;
import com.gat.feature.book_detail.BookDetailActivity;
import com.gat.feature.book_detail.BookDetailScreen;
import com.gat.feature.personaluser.PersonalUserActivity;
import com.gat.feature.personaluser.PersonalUserScreen;
import com.gat.feature.suggestion.search.item.SearchBookResultItem;
import com.gat.feature.suggestion.search.item.SearchBuilder;
import com.gat.feature.suggestion.search.item.SearchHistoryItem;
import com.gat.feature.suggestion.search.item.SearchUserResultItem;
import com.gat.feature.suggestion.search.listener.OnFragmentRequestLoadMore;
import com.gat.feature.suggestion.search.listener.OnSearchCanLoadMore;
import com.gat.feature.suggestion.search.listener.OnUserTapOnKeyword;
import com.gat.feature.suggestion.search.listener.OnLoadHistorySuccess;
import com.gat.feature.suggestion.search.listener.OnSearchBookResult;
import com.gat.feature.suggestion.search.listener.OnSearchUserResult;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mryit on 4/9/2017.
 */

public class SearchResultFragment extends Fragment
        implements OnSearchBookResult, OnLoadHistorySuccess, OnSearchUserResult,
        IRecyclerViewItemClickListener, OnItemLoadMoreClickListener, OnSearchCanLoadMore {

    @BindView(R.id.text_view_title)
    TextView textViewTitle;

    @BindView(R.id.recycler_view_search)
    RecyclerView recyclerView;

    @BindView(R.id.image_view_loading)
    ImageView imageLoading;

    private OnUserTapOnKeyword onUserTapOnKeyword;
    private OnFragmentRequestLoadMore requestLoadMore;
    private int mTabType = 0;
    
    private LoadMoreScrollListener loadMoreScrollListener;
    private SearchBookAdapter searchResultAdapter;

    public SearchResultFragment() {}

    @SuppressLint("ValidFragment")
    public SearchResultFragment(int tab_position, OnFragmentRequestLoadMore requestLoadMore, OnUserTapOnKeyword onUserTapOnKeyword) {
        this.mTabType = tab_position;
        this.requestLoadMore = requestLoadMore;
        this.onUserTapOnKeyword = onUserTapOnKeyword;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);
        ButterKnife.bind(this, view);

        this.searchResultAdapter = new SearchBookAdapter(mTabType);
        this.searchResultAdapter.setOnItemClickListener(this);
        this.searchResultAdapter.setOnLoadMoreClickListener(this);

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        this.recyclerView.setNestedScrollingEnabled(false);
        this.recyclerView.setAdapter(this.searchResultAdapter);

        loadMoreScrollListener = new LoadMoreScrollListener(3, true, () -> {
            // request load more
            MZDebug.e("____________________________________________________ REQUEST LOAD MORE");
        });
        recyclerView.addOnScrollListener(loadMoreScrollListener);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageLoading);
        Glide.with(getActivity()).load(R.raw.ic_loading_book).into(imageViewTarget);
    }

    @Override
    public void onLoadHistoryResult(List<Keyword> list) {
        MZDebug.w("onLoadHistoryResult đã nhận được history || TAB = " + mTabType + ", list size = " + list.size());

        hideProgress();
        List<Item> listItems = SearchBuilder.transformListHistory(list);
        this.searchResultAdapter.setItem(listItems);
    }


    @Override
    public void onSearchBookResult(DataResultListResponse<BookResponse> list) {

        hideProgress();
        if (null == list || list.getResultInfo() == null) {
            MZDebug.w("WARNING: onSearchBookResult : list null _________________________________W");
            return;
        }

        String totalString = String.format(getString(R.string.show_count_search_result), list.getTotalResult());
        this.textViewTitle.setText(totalString);

        List<Item> listItems = SearchBuilder.transformListBook(list.getResultInfo());
        this.searchResultAdapter.setItems(listItems);
    }

    @Override
    public void onSearchUserResult(DataResultListResponse<UserResponse> list) {

        hideProgress();
        if (null == list || list.getResultInfo() == null) {
            MZDebug.w("WARNING: onSearchUserResult : list null _________________________________W");
            return;
        }

        // set title
        String totalString = String.format(getString(R.string.show_count_search_result), list.getTotalResult());
        this.textViewTitle.setText(totalString);

        // set new list items result
        MZDebug.w(" onSearchUserResult set new list items");
        List<Item> listItems = SearchBuilder.transformListUser(list.getResultInfo());
        this.searchResultAdapter.setItems(listItems);
    }


    @Override
    public void onItemClickListener(View v, int position) {
        Item item = searchResultAdapter.getItemAt(position);

        if (item instanceof SearchHistoryItem) {

            SearchHistoryItem historyItem = (SearchHistoryItem) item;
            onUserTapOnKeyword.onUserTapOnHistoryKeyword(historyItem.keyword().getKeyword());
        } else if (item instanceof SearchBookResultItem) {

            SearchBookResultItem bookItem = (SearchBookResultItem) item;
            SuggestSearchActivity.start(getActivity().getApplicationContext(), BookDetailActivity.class,
                    BookDetailScreen.instance( (int) bookItem.bookResponse().getEditionId()));
        } else if (item instanceof SearchUserResultItem) {

            SearchUserResultItem userItem = (SearchUserResultItem) item;
            SuggestSearchActivity.start(getActivity().getApplicationContext(), PersonalUserActivity.class,
                    PersonalUserScreen.instance(userItem.userResponse().getUserId()));

        }
    }

    public void showProgress() {
        imageLoading.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        imageLoading.setVisibility(View.GONE);
    }

    @Override
    public void onLoadMoreClick() {
        this.requestLoadMore.requestLoadMoreSearchResult();
    }

    @Override
    public void onCanLoadMoreItem(int tab_position) {
        MZDebug.w("SearchResultFragment : can load more");
        this.searchResultAdapter.addItemLoadMore();
    }

    @Override
    public void onLoadMoreBookWithTitleSuccess(DataResultListResponse<BookResponse> list) {

        String totalString = String.format(getString(R.string.show_count_search_result), list.getTotalResult());
        this.textViewTitle.setText(totalString);

        this.searchResultAdapter.setMoreBookItems(list.getResultInfo());
    }

    @Override
    public void onLoadMoreBookWithAuthorSuccess(DataResultListResponse<BookResponse> list) {

        String totalString = String.format(getString(R.string.show_count_search_result), list.getTotalResult());
        this.textViewTitle.setText(totalString);

        this.searchResultAdapter.setMoreBookItems(list.getResultInfo());
    }

    @Override
    public void onLoadMoreUserSuccess(DataResultListResponse<UserResponse> list) {

        String totalString = String.format(getString(R.string.show_count_search_result), list.getTotalResult());
        this.textViewTitle.setText(totalString);

        this.searchResultAdapter.setMoreUserItems(list.getResultInfo());
    }
}
