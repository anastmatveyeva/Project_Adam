package com.adamsite.projectadam.adapter;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adamsite.projectadam.Const;
import com.adamsite.projectadam.R;
import com.adamsite.projectadam.model.VKAudio;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<VKAudio> audioList = Collections.emptyList();
    private MediaPlayer audioPlayer;

    public RecyclerViewAdapter(Context context, List<VKAudio> data) {
        this.layoutInflater = LayoutInflater.from(context);
        this.audioList = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final VKAudio audio = audioList.get(position);

        holder.tvAudioTitle.setText(String.valueOf(audio.getAudioTitle()));
        holder.tvAudioArtist.setText(audio.getAudioArtist());

        holder.rlRecyclerRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = holder.rlRecyclerRoot.getContext();
                audioPlay(audio, context);
                audio.setPlaying(!audio.isPlaying());
            }
        });
    }

    private void audioPlay(VKAudio audio, Context context) {
        if(audioPlayer != null && audioPlayer.isPlaying()){
            audioPlayer.stop();
        }

        audioPlayer = new MediaPlayer();
        Uri audioURI = Uri.parse(audio.getAudioURL());
        if (audioPlayer != null) {
            try {
                audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                audioPlayer.setDataSource(context, audioURI);
            } catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e) {
                Toast.makeText(context, R.string.error, Toast.LENGTH_LONG).show();
                Log.e(Const.LOG_TAG, e.getMessage(), e);
            }

            try {
                audioPlayer.prepareAsync();
            } catch (IllegalStateException e) {
                Toast.makeText(context, R.string.error, Toast.LENGTH_LONG).show();
                Log.e(Const.LOG_TAG, e.getMessage(), e);
            }

            audioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    mp.release();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public void animateTo(List<VKAudio> audioList) {
        applyAndAnimateRemovals(audioList);
        applyAndAnimateAdditions(audioList);
        applyAndAnimateMovedItems(audioList);
    }

    private void applyAndAnimateRemovals(List<VKAudio> newAudios) {
        for (int i = audioList.size() - 1; i >= 0; i--) {
            final VKAudio model = audioList.get(i);
            if (!newAudios.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<VKAudio> newAudios) {
        for (int i = 0, count = newAudios.size(); i < count; i++) {
            final VKAudio model = newAudios.get(i);
            if (!audioList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<VKAudio> newAudios) {
        for (int toPosition = newAudios.size() - 1; toPosition >= 0; toPosition--) {
            final VKAudio model = newAudios.get(toPosition);
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

    public void addItem(int position, VKAudio audio) {
        audioList.add(position, audio);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final VKAudio audio = audioList.remove(fromPosition);
        audioList.add(toPosition, audio);
        notifyItemMoved(fromPosition, toPosition);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout rlRecyclerRoot;
        private ImageView ivPlay;
        private TextView tvAudioTitle;
        private TextView tvAudioArtist;

        public ViewHolder(View itemView) {
            super(itemView);
            rlRecyclerRoot = (RelativeLayout) itemView.findViewById(R.id.ll_recycler_root);
            ivPlay = (ImageView) itemView.findViewById(R.id.iv_play);
            tvAudioTitle = (TextView) itemView.findViewById(R.id.tv_audio_title);
            tvAudioArtist = (TextView) itemView.findViewById(R.id.tv_audio_artist);
        }
    }

    public static class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {

//        private final int VERTICAL_ITEM_SPACE = 24;

        private final int[] ATTRS = new int[]{android.R.attr.listDivider};
        private Drawable divider;

        public RecyclerViewItemDecoration(Context context) {
            final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
            divider = styledAttributes.getDrawable(0);
            styledAttributes.recycle();
        }

        public RecyclerViewItemDecoration(Context context, int resId) {
            divider = ContextCompat.getDrawable(context, resId);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            if (parent.getChildAdapterPosition(view) == 0)
//                outRect.top = VERTICAL_ITEM_SPACE;
//
//            if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1)
//                outRect.bottom = VERTICAL_ITEM_SPACE;
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