package it.vibwear.app.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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
    private Fragment audioSliderFragment;
//    private OnAudioChangeListener mListener;

    public static AudioDetailFragment newInstance() {
        return new AudioDetailFragment();
    }

    public AudioDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnAudioChangeListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnAudioChangeListener");
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_detail, container, false);

        if(savedInstanceState != null) {
            FragmentManager fm = getChildFragmentManager();
            vibSliderFragment = fm.getFragment(savedInstanceState, "VibFrag");
            audioSliderFragment = fm.getFragment(savedInstanceState, "AudioFrag");
        } else {
            vibSliderFragment = new VibSliderFragment();
            audioSliderFragment = new AudioSliderFragment();
            Bundle preference = new Bundle();
            preference.putString("preference", "AudioPreference");
            vibSliderFragment.setArguments(preference);
            audioSliderFragment.setArguments(preference);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.rootDetailLayout, vibSliderFragment);
            transaction.add(R.id.rootDetailLayout, audioSliderFragment);
            transaction.commit();
        }

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        FragmentManager fm = getChildFragmentManager();

        fm.putFragment(outState, "VibFrag", vibSliderFragment);
        fm.putFragment(outState, "AudioFrag", audioSliderFragment);
    }

//    public interface OnAudioChangeListener {
//        public void onVibSliderChange(String boardName);
//    }


}
