package com.hhsfbla.mad.ui.contact;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hhsfbla.mad.R;
import com.hhsfbla.mad.activities.HomeActivity;
import com.hhsfbla.mad.data.User;
import com.hhsfbla.mad.data.UserType;

/**
 * Represents a fragment consisting of various links to contact FBLA and report issues
 */
public class ContactFragment extends Fragment {

    private TextView fblaLink, formLink;
    private ImageButton facebook, insta, twitter, linkedin;

    /**
     * Creates and inflates a new ContactFragment with the following parameters
     *
     * @param inflater           to inflate the fragment
     * @param container          ViewGroup into which the fragment is inflated
     * @param savedInstanceState used to save activity regarding this fragment
     * @return the inflated fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_contact, container, false);
        getActivity().setTitle("Contact Us");

        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.toObject(User.class).getUserType() == UserType.ADVISOR)
                    ((HomeActivity) getContext()).hideMyCompsItem();
                else
                    ((HomeActivity) getContext()).showMyCompsItem();

            }
        });

        fblaLink = root.findViewById(R.id.fblaWebLinkTxtView);
        facebook = root.findViewById(R.id.facebookButton);
        insta = root.findViewById(R.id.instaButton);
        twitter = root.findViewById(R.id.twitterButton);
        linkedin = root.findViewById(R.id.linkedinButton);
        formLink = root.findViewById(R.id.qaForm);

        fblaLink.setText(Html.fromHtml("<a href='https://www.fbla-pbl.org'>FBLA Website</androidx.constraintlayout.widget.ConstraintLayout</a>"));
        fblaLink.setMovementMethod(LinkMovementMethod.getInstance());
        fblaLink.setTextColor(Color.BLACK);

        formLink.setText(Html.fromHtml("<a href='https://forms.gle/LMNjUUEDPGALoLpaA'>Q&A Form</androidx.constraintlayout.widget.ConstraintLayout</a>"));
        formLink.setMovementMethod(LinkMovementMethod.getInstance());
        formLink.setTextColor(Color.BLACK);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/FutureBusinessLeaders")));
            }
        });

        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/fbla_pbl/")));
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/FBLA_National")));
            }
        });


        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/company/future-business-leaders-america-phi-beta-lambda/")));
            }
        });

        return root;

    }

    /**
     * Handles actions upon successful creation of the host activity
     *
     * @param savedInstanceState used to save activity regarding this fragment
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
