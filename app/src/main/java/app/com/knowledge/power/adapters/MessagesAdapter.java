package app.com.knowledge.power.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Objects;

import app.com.knowledge.power.MessageTypes;
import app.com.knowledge.power.R;
import app.com.knowledge.power.models.Message;
import app.com.knowledge.power.utils.Commons;
import app.com.knowledge.power.views.activities.ViewImageActivity;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private static final int MESSAGE_LEFT = 1;
    private static final int MESSAGE_RIGHT = 2;

    ArrayList<Message> messages;
    Context context;
    LongPressCallback callback;
    boolean isAdmin;

    public MessagesAdapter(ArrayList<Message> messages, Context context, LongPressCallback callback, boolean isAdmin) {
        this.messages = messages;
        this.context = context;
        this.callback = callback;
        this.isAdmin = isAdmin;
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

        String userId = FirebaseAuth.getInstance().getUid();

        holder.tvTime.setText(Commons.INSTANCE.formattedTime(message.datetime));
        holder.tvUsername.setText(message.sender.name);
        Glide.with(context)
                .load(message.sender.profilePicUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_user)
                .into(holder.ivProfilePicture);

        if (message.type == MessageTypes.TEXT) {
            holder.tvMessage.setText(message.message);

            holder.itemView.setOnLongClickListener(v -> {
                if (isAdmin || userId.equals(message.sender.id)) {
                    String[] colors = {"Edit", "Delete"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Options");
                    builder.setItems(colors, (dialog, which) -> {
                        switch (which) {
                            case 0: {
                                callback.onEditClicked(message);
                                break;
                            }
                            case 1: {
                                callback.onDeleteClicked(message);
                                break;
                            }
                        }
                    });
                    builder.show();
                }
                return false;
            });
        } else {
            holder.tvMessage.setVisibility(View.GONE);
            holder.layoutImg.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(message.imgPath)
                    .centerCrop()
                    .placeholder(R.drawable.image_load)
                    .into(holder.ivPicture);
            if (message.message != null && !message.message.isEmpty()) {
                holder.tvCaption.setText(message.message);
            } else {
                holder.tvCaption.setVisibility(View.GONE);
            }

            holder.layoutImg.setOnClickListener(v -> {
                context.startActivity(new Intent(context, ViewImageActivity.class).putExtra("image", message.imgPath));
            });

            holder.itemView.setOnLongClickListener(v -> {
                if (isAdmin || userId.equals(message.sender.id)) {
                    String[] colors = {"Delete"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Options");
                    builder.setItems(colors, (dialog, which) -> {
                        if (which == 0) {
                            callback.onDeleteClicked(message);
                        }
                    });
                    builder.show();
                }
                return false;
            });

            holder.ivPicture.setOnLongClickListener(v -> {
                if (isAdmin || userId.equals(message.sender.id)) {
                    String[] colors = {"Delete"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Options");
                    builder.setItems(colors, (dialog, which) -> {
                        if (which == 0) {
                            callback.onDeleteClicked(message);
                        }
                    });
                    builder.show();
                }
                return false;
            });
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
        Message message = messages.get(position);

        if (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(message.sender.id)) {
            return MESSAGE_RIGHT;
        } else {
            return MESSAGE_LEFT;
        }
    }

    public interface LongPressCallback {
        void onEditClicked(Message message);

        void onDeleteClicked(Message message);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvCaption, tvUsername, tvTime;
        LinearLayout layoutImg;
        ImageView ivPicture;
        RoundedImageView ivProfilePicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            layoutImg = itemView.findViewById(R.id.layoutImage);
            ivPicture = itemView.findViewById(R.id.imgPath);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePic);
        }
    }
}
