package com.example.login.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.login.R;


public class GenerateShouzhang_Fragment extends Fragment {

    private ShareActionProvider mShareActionProvider;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_generate_shouzhang,container,false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.sharemenu,menu);
//        MenuItem shareItem = menu.findItem(R.id.shouzhang_share);
//        if(shareItem!=null)
//        {
//            mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
//            mShareActionProvider.setShareIntent(getDefaultIntent());
//        }
//        else
//            Log.e("Generate Shouzhang Fragment","shareItem 空指针");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.shouzhang_share:
                Toast.makeText(getActivity(), "前往分享手帐", Toast.LENGTH_SHORT).show();

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent getDefaultIntent()
    {
        Intent defaultintent = new Intent(Intent.ACTION_SEND);
        defaultintent.setType("image/*");
        return defaultintent;
    }

}
