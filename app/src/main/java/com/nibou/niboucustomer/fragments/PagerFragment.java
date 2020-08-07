package com.nibou.niboucustomer.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.nibou.niboucustomer.R;
import com.nibou.niboucustomer.databinding.FragmentPagerBinding;

public class PagerFragment extends Fragment {

    private FragmentPagerBinding binding;

    public static PagerFragment newInstance(int sectionNumber) {
        PagerFragment fragment = new PagerFragment();
        Bundle args = new Bundle();
        args.putInt("index", sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pager, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        int position = getArguments() != null ? getArguments().getInt("index") : 0;
        String[] titles1 = {getString(R.string.welcome_to_nibou), getString(R.string.step_survey), getString(R.string.step_secure), getString(R.string.step_value)};
        int images[] = {R.drawable.welcome_icon, R.drawable.survey_icon, R.drawable.secure_icon, R.drawable.value_icon};
        String[] titles2 = {getString(R.string.welcome_description), getString(R.string.survey_description), getString(R.string.secure_description), getString(R.string.value_description)};
        binding.sectionLabel.setText(titles1[position]);
        binding.logo.setImageResource(images[position]);
        binding.sectionLabel2.setText(titles2[position]);

    }
}