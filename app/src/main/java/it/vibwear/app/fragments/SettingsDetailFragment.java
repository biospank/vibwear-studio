package it.vibwear.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import it.lampwireless.vibwear.app.R;

public class SettingsDetailFragment extends Fragment {
    private static final String ARG_BOARD_NAME = "boardName";
    public static final String LOW_BATTERY_PREFS_NAME = "LOW_BATTERY_DETAILS";
    public static final String NOTIFY_ME_KEY = "notify_me";

    private View layout;
    private String mBoardName;
    private EditText etBoardName;
    private Button btChange;
    private CheckBox cbNotifyMe;
    private Button btCheckFirmware;

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

        cbNotifyMe = (CheckBox) layout.findViewById(R.id.cb_notify_me);

        if(mBoardName.equalsIgnoreCase(getResources().getString(R.string.factory_device_name)))
            etBoardName.setText(getResources().getString(R.string.default_device_name));
        else
            etBoardName.setText(mBoardName);

        btChange = (Button) layout.findViewById(R.id.bt_change);

        btChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onChangeNameButtonPressed();
            }
        });

        cbNotifyMe.setChecked(getOnLowBatteryPref());

        cbNotifyMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setOnLowBatteryPref(isChecked);
            }
        });

        btCheckFirmware = (Button) layout.findViewById(R.id.bt_check_for_update);

        btCheckFirmware.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckFirwareButtonPressed();
            }
        });

        return layout;

    }

    public boolean getOnLowBatteryPref() {
        SharedPreferences settings = getActivity().getSharedPreferences(LOW_BATTERY_PREFS_NAME,
                Context.MODE_PRIVATE);
        return settings.getBoolean(NOTIFY_ME_KEY, false);

    }

    public void setOnLowBatteryPref(boolean newValue) {
        SharedPreferences.Editor editor;
        SharedPreferences settings = getActivity().getSharedPreferences(LOW_BATTERY_PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.putBoolean(NOTIFY_ME_KEY, newValue);

        editor.commit();

    }


    public void onChangeNameButtonPressed() {
        if (mListener != null) {
            String name = etBoardName.getText().toString();
            if(name.length() > 0) {
                mListener.onBoardNameChange(etBoardName.getText().toString());
                getFragmentManager().popBackStackImmediate();
            }
        }
    }

    public void onCheckFirwareButtonPressed() {
        mListener.onFirmwareUpdate();
    }

    public interface OnSettingsChangeListener {
        public void onBoardNameChange(String boardName);
        public void onFirmwareUpdate();
    }


}
