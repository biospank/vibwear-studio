package it.vibwear.app.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import it.lampwireless.vibwear.app.R;

public class SettingsDetailFragment extends Fragment {
    private static final String ARG_BOARD_NAME = "boardName";

    private View layout;
    private String mBoardName;
    private EditText etBoardName;
    private Button btChange;

    private OnSettingsChangeListener mListener;

    public static SettingsDetailFragment newInstance(String boardName) {
        SettingsDetailFragment fragment = new SettingsDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOARD_NAME, boardName);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnSettingsChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSettingsListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBoardName = getArguments().getString(ARG_BOARD_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_settings_detail, container, false);

        etBoardName = (EditText) layout.findViewById(R.id.et_board_name);
        etBoardName.setText(mBoardName);

        btChange = (Button) layout.findViewById(R.id.bt_change);

        btChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed();
            }
        });

        return layout;

    }

    public void onButtonPressed() {
        if (mListener != null) {
            String name = etBoardName.getText().toString();
            if(name.length() > 0) {
                mListener.onBoardNameChange(etBoardName.getText().toString());
                getFragmentManager().popBackStackImmediate();
            }
        }
    }

    public interface OnSettingsChangeListener {
        public void onBoardNameChange(String boardName);
    }


}
