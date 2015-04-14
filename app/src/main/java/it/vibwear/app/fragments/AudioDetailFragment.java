package it.vibwear.app.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.lampwireless.vibwear.app.R;

/**
 * Created by biospank on 13/04/15.
 */
public class AudioDetailFragment extends Fragment {

    private Fragment vibSliderFragment;
    private OnAudioChangeListener mListener;

    public static AudioDetailFragment newInstance(String boardName) {
        AudioDetailFragment fragment = new AudioDetailFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public AudioDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAudioChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSettingsListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_detail, container, false);

        if(savedInstanceState != null) {
            vibSliderFragment = getChildFragmentManager().getFragment(savedInstanceState, "VibFrag");
        } else {
            vibSliderFragment = new VibSliderFragment();
            Bundle preference = new Bundle();
            preference.putString("preference", "AudioPreference");
            vibSliderFragment.setArguments(preference);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.rootDetailLayout, vibSliderFragment);
            transaction.commit();
        }

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getChildFragmentManager().putFragment(outState, "VibFrag", vibSliderFragment);
    }

    public interface OnAudioChangeListener {
        public void onVibSliderChange(String boardName);
    }


}
