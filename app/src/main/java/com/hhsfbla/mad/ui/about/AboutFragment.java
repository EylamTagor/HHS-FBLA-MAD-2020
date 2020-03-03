package com.hhsfbla.mad.ui.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hhsfbla.mad.R;

/**
 * Represents the About FBLA page, including a mission statement and core values
 */
public class AboutFragment extends Fragment {

    /**
     * Creates and inflates a new AboutFragment with the following parameters
     *
     * @param inflater           to inflate the fragment
     * @param container          ViewGroup into which the fragment is inflated
     * @param savedInstanceState used to save activity regarding this fragment
     * @return the inflated fragment
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("About FBLA");
        return inflater.inflate(R.layout.fragment_about, container, false);
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
