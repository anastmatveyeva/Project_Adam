package com.adamsite.projectadam.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adamsite.projectadam.R;
import com.adamsite.projectadam.model.VKAudio;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private RelativeLayout rlRecyclerRoot;
    private ImageView ivPlayPause;
    private TextView tvAudioTitle;
    private TextView tvAudioArtist;

    private int position;
    private ActionListener actionListener;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        rlRecyclerRoot = (RelativeLayout) itemView.findViewById(R.id.rl_recycler_root);
        rlRecyclerRoot.setOnClickListener(onCLick);

        ivPlayPause = (ImageView) itemView.findViewById(R.id.iv_play_pause);
        tvAudioTitle = (TextView) itemView.findViewById(R.id.tv_audio_title);
        tvAudioArtist = (TextView) itemView.findViewById(R.id.tv_audio_artist);
    }

    public void bindView(VKAudio audio, int position, ActionListener actionListener) {
        this.actionListener = actionListener;
        this.position = position;

        tvAudioTitle.setText(audio.getAudioTitle());
        tvAudioArtist.setText(audio.getAudioArtist());

        if (audio.getAudioStatus().equals(VKAudio.Status.PLAYING)) {
            ivPlayPause.setVisibility(View.VISIBLE);
            ivPlayPause.setImageResource(R.drawable.ic_pause_black_24dp);
        } else if (audio.getAudioStatus().equals(VKAudio.Status.PAUSED)) {
            ivPlayPause.setVisibility(View.VISIBLE);
            ivPlayPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        } else {
            ivPlayPause.setVisibility(View.GONE);
        }
    }

    View.OnClickListener onCLick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (actionListener != null) {
                actionListener.onClick(position);
            }
        }
    };

    public interface ActionListener {
        void onClick(int position);
    }
}
