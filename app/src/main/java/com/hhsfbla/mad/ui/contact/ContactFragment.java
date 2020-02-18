package com.hhsfbla.mad.ui.contact;

import androidx.annotation.ColorRes;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hhsfbla.mad.R;

public class ContactFragment extends Fragment {

    private ContactViewModel mViewModel;
    TextView fblaLink;
    ImageButton facebook, insta, twitter, linkedin;



    public static ContactFragment newInstance() {
        return new ContactFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_contact, container, false);

        fblaLink = root.findViewById(R.id.fblaWebLinkTxtView);
        fblaLink.setText(Html.fromHtml("<a href='https://www.fbla-pbl.org'>FBLA Website Link</androidx.constraintlayout.widget.ConstraintLayout<a>"));
        fblaLink.setMovementMethod(LinkMovementMethod.getInstance());
        fblaLink.setTextColor(Color.BLACK);

        facebook = root.findViewById(R.id.facebookButton);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/FutureBusinessLeaders")));
            }
        });

        insta = root.findViewById(R.id.instaButton);
        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/fbla_pbl/")));
            }
        });

        twitter = root.findViewById(R.id.twitterButton);
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/FBLA_National")));
            }
        });


        linkedin = root.findViewById(R.id.linkedinButton);
        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/company/future-business-leaders-america-phi-beta-lambda/")));
            }
        });

        return root;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ContactViewModel.class);
        // TODO: Use the ViewModel
    }

}
