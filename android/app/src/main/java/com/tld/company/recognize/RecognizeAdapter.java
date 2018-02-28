package com.tld.company.recognize;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tld.company.R;
import com.tld.company.bean.HblFlowerInfo;

import org.apache.http.util.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

class RecognizeAdapter extends RecyclerView.Adapter<RecognizeAdapter.RecognizeVH> {

    List<HblFlowerInfo> mData;

    RecognizeAdapter() {
        mData = new ArrayList<>();
    }

    @Override
    public RecognizeVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recognize_info, parent, false);
        return new RecognizeVH(view);
    }

    @Override
    public void onBindViewHolder(RecognizeVH holder, int position) {
        holder.setNormalProperties(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    void replaceData(List<HblFlowerInfo> data) {
        if (mData != null) {
            mData.clear();
            mData.addAll(data);
        } else {
            mData = new ArrayList<>(data);
        }
        notifyDataSetChanged();
    }

    static class RecognizeVH extends RecyclerView.ViewHolder {

        @BindView(R.id.idi_nameTextView)
        TextView nameView;
        @BindView(R.id.idi_detailTextView)
        TextView detailView;
        @BindView(R.id.idi_confidentTextView)
        TextView confidentView;
        @BindView(R.id.idi_lineView)
        View lineView;

        RecognizeVH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setNormalProperties(HblFlowerInfo info) {
            nameView.setText(info.getName());
            if (TextUtils.isEmpty(info.getAliasName())) {
                detailView.setVisibility(View.GONE);
                lineView.setVisibility(View.GONE);
            } else {
                detailView.setVisibility(View.VISIBLE);
                lineView.setVisibility(View.VISIBLE);
                detailView.setText(info.getAliasName());
            }
            confidentView.setText(String.format(Locale.CHINA, "可信度：%.0f", info.getScore()) + "%");
        }
    }
}
