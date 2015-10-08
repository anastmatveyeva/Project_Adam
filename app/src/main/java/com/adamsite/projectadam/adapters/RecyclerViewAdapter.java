package com.adamsite.projectadam.adapters;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adamsite.projectadam.Const;
import com.adamsite.projectadam.R;
import com.adamsite.projectadam.model.VKAudio;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.AudioViewHolder> {

    private List<VKAudio> audioList = new ArrayList<>();
    private MediaPlayer audioPlayer;

    public RecyclerViewAdapter(List<VKAudio> data) {
        audioList = data;
    }

    @Override
    public RecyclerViewAdapter.AudioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AudioViewHolder holder, final int position) {
        final VKAudio newAudio = audioList.get(position);
        holder.tvAudioTitle.setText(String.valueOf(newAudio.getAudioTitle()));
        holder.tvAudioArtist.setText(newAudio.getAudioArtist());

        if (newAudio.getAudioStatus() == VKAudio.Status.OFF) {
            holder.ivPlayPause.setVisibility(View.GONE);
        } else if (newAudio.getAudioStatus() == VKAudio.Status.PLAYING) {
            holder.ivPlayPause.setImageResource(R.drawable.ic_pause);
            holder.ivPlayPause.setVisibility(View.VISIBLE);
        } else if (newAudio.getAudioStatus() == VKAudio.Status.PAUSED) {
            holder.ivPlayPause.setImageResource(R.drawable.ic_play);
            holder.ivPlayPause.setVisibility(View.VISIBLE);
        }

        holder.rlRecyclerRoot.setTag(audioList.get(position));

        holder.rlRecyclerRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RelativeLayout relativeLayout = (RelativeLayout) v;
                VKAudio localAudio = (VKAudio) relativeLayout.getTag();
                playAudio(localAudio, newAudio, position, holder);
            }
        });
    }

    private void playAudio(VKAudio localAudio, VKAudio newAudio, int newPos, AudioViewHolder holder) {
        if (newAudio.getAudioStatus() == VKAudio.Status.OFF) {
            for (VKAudio audio : audioList) {
                audio.setAudioStatus(VKAudio.Status.OFF);
            }
            for (AudioViewHolder viewHolder : AudioViewHolder.getHolderList()) {
                viewHolder.ivPlayPause.setVisibility(View.GONE);
            }

            newAudio.setAudioStatus(VKAudio.Status.PLAYING);
            holder.ivPlayPause.setImageResource(R.drawable.ic_pause);
            holder.ivPlayPause.setVisibility(View.VISIBLE);
        } else if (newAudio.getAudioStatus() == VKAudio.Status.PAUSED) {
            newAudio.setAudioStatus(VKAudio.Status.PLAYING);
            holder.ivPlayPause.setImageResource(R.drawable.ic_pause);
            holder.ivPlayPause.setVisibility(View.VISIBLE);
        } else {
            newAudio.setAudioStatus(VKAudio.Status.PAUSED);
            holder.ivPlayPause.setImageResource(R.drawable.ic_play);
            holder.ivPlayPause.setVisibility(View.VISIBLE);
        }

        localAudio.setAudioStatus(newAudio.getAudioStatus());
        try {
            audioList.get(newPos).setAudioStatus(localAudio.getAudioStatus());
        } catch (IndexOutOfBoundsException e) {
            Log.e(Const.LOG_TAG, e.getMessage(), e);
            Toast.makeText(holder.rlRecyclerRoot.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

//
//        audioPlayer = new MediaPlayer();
//
//        Uri audioURI = Uri.parse(curAudio.getAudioURL());
//        try {
//            audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            audioPlayer.setDataSource(context, audioURI);
//            audioPlayer.prepareAsync();
//            audioPlayer.start();
//        } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
//            Toast.makeText(context, R.string.error, Toast.LENGTH_LONG).show();
//            Log.e(Const.LOG_TAG, e.getMessage(), e);
//        }
//
//            holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                }
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {
//                    if (audioPlayer != null && audioPlayer.isPlaying()) {
//                        holder.seekBar.setMax(audioPlayer.getDuration() / 100);
//                        audioPlayer.seekTo((audioPlayer.getDuration() / 100) * seekBar.getProgress());
//                    }
//                }
//            });
//
//            audioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                @Override
//                public void onPrepared(MediaPlayer mp) {
//                    mp.start();
//                }
//            });
//            audioPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
//                @Override
//                public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                    holder.seekBar.setSecondaryProgress(percent);
//                }
//            });
//        }
    }

    public List<VKAudio> getAudioList() {
        return audioList;
    }

    public void setAudioList(ArrayList<VKAudio> audioList) {
        this.audioList = new ArrayList<>(audioList);
        notifyDataSetChanged();
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("savedList", (ArrayList<VKAudio>) audioList);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        audioList = savedInstanceState.getParcelableArrayList("savedList");
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public void animateTo(List<VKAudio> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<VKAudio> newModels) {
        for (int i = audioList.size() - 1; i >= 0; i--) {
            final VKAudio model = audioList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<VKAudio> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final VKAudio model = newModels.get(i);
            if (!audioList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<VKAudio> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final VKAudio model = newModels.get(toPosition);
            final int fromPosition = audioList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public VKAudio removeItem(int position) {
        final VKAudio model = audioList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, VKAudio model) {
        audioList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final VKAudio model = audioList.remove(fromPosition);
        audioList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public static class AudioViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlRecyclerRoot;
        private ImageView ivPlayPause;
        private TextView tvAudioTitle;
        private TextView tvAudioArtist;
        private SeekBar seekBar;
        private static ArrayList<AudioViewHolder> holderList = new ArrayList<>();

        public AudioViewHolder(View itemView) {
            super(itemView);
            rlRecyclerRoot = (RelativeLayout) itemView.findViewById(R.id.rl_recycler_root);
            ivPlayPause = (ImageView) itemView.findViewById(R.id.iv_play_pause);
            tvAudioTitle = (TextView) itemView.findViewById(R.id.tv_audio_title);
            tvAudioArtist = (TextView) itemView.findViewById(R.id.tv_audio_artist);
            seekBar = (SeekBar) itemView.findViewById(R.id.seekbar);
            holderList.add(this);
        }

        private static ArrayList<AudioViewHolder> getHolderList() {
            return holderList;
        }
    }

    public static class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
        private final int[] ATTRS = new int[]{android.R.attr.listDivider};
        private Drawable divider;

        public RecyclerViewItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            divider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            for (int i = 0; i < parent.getChildCount() - 1; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + divider.getIntrinsicHeight();

                divider.setBounds(left, top, right, bottom);
                divider.draw(c);
            }
        }
    }
}