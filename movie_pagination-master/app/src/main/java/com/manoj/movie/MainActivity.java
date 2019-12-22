package com.manoj.movie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.manoj.movie.adapter.Datalist;
import com.manoj.movie.adapter.PaginationListener;
import com.manoj.movie.adapter.onClickRecycleView;
import com.manoj.movie.adapter.viewDataAdapter;
import com.manoj.movie.api.api;
import com.manoj.movie.api.pojo.movielist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.manoj.movie.adapter.PaginationListener.PAGE_START;
import static com.manoj.movie.api.api.imageurl;

public class MainActivity extends AppCompatActivity implements onClickRecycleView {
    ListView movielistview;
    viewDataAdapter adapter;
    ImageView list, grid;
    RecyclerView recyclerView;
    RadioGroup rg;
    List<Datalist> alldata = new ArrayList<>();
    ProgressDialog progressbar;
    int pagenumber;
    String movieCategory;
    SwipeRefreshLayout swipeRefresh;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    Snackbar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.my_recycler_view);
        movieCategory = "Now Playing";


        adapter = new viewDataAdapter(alldata, MainActivity.this);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        pagenumber = 1;

        movielistList(pagenumber);

        recyclerView.addOnScrollListener(new PaginationListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                movielistList(currentPage);
                Toast.makeText(MainActivity.this, "Loading page " + currentPage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });


    }


    public void clear() {
        alldata.clear();
    }

    private void movielistList(int pagenumber) {
        progressbar = ProgressDialog.show(MainActivity.this, "",
                "Loading  Please wait...", false);
        Retrofit retro = new Retrofit.Builder()
                .baseUrl(api.baseurl)
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();
        api retrfit = retro.create(api.class);
        Call<movielist> call;
        call = retrfit.getmovies(
                "5a9e972c916d99006f4d6ec3c46829ce",
                pagenumber);

        call.enqueue(new Callback<movielist>() {
            @Override
            public void onResponse(Call<movielist> call, retrofit2.Response<movielist> response) {

                if (response.isSuccessful()) {
                    for (int i = 0; i < response.body().getResults().size(); i++) {
                        Toast.makeText(MainActivity.this, "SuccessFully ...", Toast.LENGTH_SHORT).show();
                        Log.d("results", response.body().getResults().get(i).getOriginalTitle());

                        Datalist datalist = new Datalist(
                                response.body().getResults().get(i).getTitle(),
                                response.body().getResults().get(i).getPosterPath(),
                                response.body().getResults().get(i).getVoteAverage(),
                                response.body().getResults().get(i).getId()
                        );
                        alldata.add(datalist);
                    }
                    progressbar.dismiss();
                    isLoading = false;
                    adapter.notifyDataSetChanged();

                } else {
                    progressbar.dismiss();
                    isLoading = false;
                    Toast.makeText(MainActivity.this, "Failed ", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<movielist> call, Throwable t) {
                progressbar.dismiss();
                isLoading = false;
                Log.d("errorin:", t.getMessage());
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, alldata.get(position).getTitle(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, MovieDetails.class);
        intent.putExtra("movieid", alldata.get(position).getId());
        startActivity(intent);

    }


}
