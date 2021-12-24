package com.example.projectn1.profile.videos;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.projectn1.R;
import com.example.projectn1.dto.SearchVideos;
import com.example.projectn1.dto.Video;
import com.example.projectn1.profile.fullPages.FullVideoFragment;
import com.example.projectn1.profile.fullPages.OnClickFullExhibitor;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileVideosFragment extends Fragment implements OnClickFullExhibitor{
    View view;
    ProfileVideoAdapter adapter = new ProfileVideoAdapter();
    SwipeRefreshLayout swipeRL;
    public RecyclerView recyclerView;
    AppCompatTextView oops, noPosts;
    AppCompatImageView noFile;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        view = inflater.inflate(R.layout.profile_layout, container, false);
        swipeRL = view.findViewById(R.id.swipeRefresh);

        oops = view.findViewById(R.id.oops);
        noPosts = view.findViewById(R.id.no_posts);
        noFile = view.findViewById(R.id.no_file);

        recyclerView = view.findViewById(R.id.recViewProfile);


        videosProfilePage();
        checkInternet();

        swipeRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRL.setRefreshing(false);
                videosProfilePage();
            }
        });
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        com.example.projectn1.dto.Videos videosS = com.example.projectn1.dto.Videos.create();
        Call<SearchVideos> camera = videosS.searchVideo("videos");

        camera.enqueue(new Callback<SearchVideos>() {
            @Override
            public void onResponse(Call<SearchVideos> call, Response<SearchVideos> response) {
                SearchVideos body = response.body();
                System.out.println(body);

                if (body != null) {
                    List<Video> videos = body.getVideos();
                    System.out.println(videos);

                    ArrayList<Videos> profileVideo = new ArrayList<>();
                    if (videos != null) {

                        for (Video video : videos) {
                            profileVideo.add(new Videos(video.getImage(),
                                    video.getVideoFiles().get(0).getLink()));
                        }
                        System.out.println(profileVideo);
                        adapter.setVideos(profileVideo);
                    }
                    else {
                        System.out.println("No videos available");
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchVideos> call, Throwable t) {

            }
        });

    }

    private void videosProfilePage() {

        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                getContext(),
                3,
                RecyclerView.VERTICAL,
                false
        );

        recyclerView.setLayoutManager(gridLayoutManager);

        adapter.setOnClickExhibitor(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onShowFull(String videoUrl) {
//        System.out.println(videoUrl);

        Bundle bundle = new Bundle();
        bundle.putString("videoUrl", videoUrl);
        FullVideoFragment fullVideoFragment = new FullVideoFragment();
        fullVideoFragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentHomePage, fullVideoFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }


    private void checkInternet() {
        if (getActivity() != null) {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                    .getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager
                            .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                            .getState() == NetworkInfo.State.CONNECTED) {

                oops.setVisibility(View.GONE);
                noPosts.setVisibility(View.GONE);
                noFile.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

            } else {

                oops.setVisibility(View.VISIBLE);
                noPosts.setVisibility(View.VISIBLE);
                noFile.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        }
    }
}
