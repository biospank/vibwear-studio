package it.vibwear.app.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import it.lampwireless.vibwear.app.R;
import it.vibwear.app.utils.AppManager;

public class KillerAppIconFragment extends Fragment {
    private static final String PACKAGE_NAME = "pakageName";

    private AppManager killerApp;

    public KillerAppIconFragment() {
        // Required empty public constructor
    }

    public static KillerAppIconFragment newInstance(String packageName) {
        KillerAppIconFragment fragment = new KillerAppIconFragment();
        Bundle args = new Bundle();
        args.putString(PACKAGE_NAME, packageName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        killerApp = new AppManager(getActivity(), getArguments().getString(PACKAGE_NAME));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_killer_app_icon, container, false);

        ImageView iv = (ImageView)layout.findViewById(R.id.killerAppIcon);

        iv.setImageDrawable(killerApp.getIconApp());

        return layout;
    }

}
