package app.com.knowledge.power.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import app.com.knowledge.power.MessageTypes;
import app.com.knowledge.power.R;
import app.com.knowledge.power.models.Message;
import app.com.knowledge.power.utils.SharePrefData;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private static final int MESSAGE_LEFT = 1;
    private static final int MESSAGE_RIGHT = 2;

    ArrayList<Message> messages;
    Context context;

    public MessagesAdapter(ArrayList<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == MESSAGE_RIGHT) {
            View listItem = layoutInflater.inflate(R.layout.message_right, parent, false);
            return new ViewHolder(listItem);
        } else {
            View listItem = layoutInflater.inflate(R.layout.message_left, parent, false);
            return new ViewHolder(listItem);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (message.type == MessageTypes.TEXT) {
            holder.tvMessage.setText(message.message);
        } else {
            holder.tvMessage.setVisibility(View.GONE);
            holder.layoutImg.setVisibility(View.VISIBLE);
            Glide
                    .with(context)
                    .load(message.imgPath)
                    .centerCrop()
                    .placeholder(R.drawable.image_load)
                    .into(holder.ivPicture);
            if (message.message != null && !message.message.isEmpty()) {
                holder.tvCaption.setText(message.message);
            } else {
                holder.tvCaption.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        /*Message message = messages.get(position);

        if (SharePrefData.getInstance().getPrefString(context, "userID").equals(message.sender)) {
            return MESSAGE_RIGHT;
        } else {
            return MESSAGE_LEFT;
        }*/

        return MESSAGE_LEFT;
       /* if (position % 2 == 0) {
            return MESSAGE_RIGHT;
        } else {
            return MESSAGE_LEFT;
        }*/
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvCaption;
        LinearLayout layoutImg;
        ImageView ivPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            layoutImg = itemView.findViewById(R.id.layoutImage);
            ivPicture = itemView.findViewById(R.id.imgPath);
        }
    }
}
