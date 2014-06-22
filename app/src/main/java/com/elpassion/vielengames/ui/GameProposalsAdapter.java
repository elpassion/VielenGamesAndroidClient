package com.elpassion.vielengames.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.elpassion.vielengames.R;
import com.elpassion.vielengames.api.VielenGamesClient;
import com.elpassion.vielengames.data.GameProposal;
import com.elpassion.vielengames.data.Player;
import com.elpassion.vielengames.utils.ViewUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public final class GameProposalsAdapter extends BaseAdapter {

    public static final String TAG = GameProposalsAdapter.class.getSimpleName();
    private Context context;
    private VielenGamesClient client;
    private Player me;
    private final LayoutInflater inflater;
    private final List<GameProposal> proposals;

    public GameProposalsAdapter(Context context, List<GameProposal> proposals, Player me, VielenGamesClient client) {
        this.context = context;
        this.client = client;
        this.inflater = LayoutInflater.from(context);
        this.proposals = new ArrayList<GameProposal>(proposals);
        this.me = me;
    }

    public void remove(GameProposal proposal) {
        proposals.remove(proposal);
        notifyDataSetChanged();
    }

    public void add(GameProposal proposal) {
        proposals.add(0, proposal);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return proposals.size();
    }

    @Override
    public GameProposal getItem(int position) {
        return proposals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.game_proposal_item, parent, false);
        }
        final GameProposal item = getItem(position);

        Player player = item.getAwaitingPlayers().get(0);
        boolean isMyGame = player.getId().equals(me.getId());

        View.OnClickListener buttonListener = isMyGame ? getDeleteGameButtonListener(item) : getJoinGameButtonListener(item);
        int buttonBackGroundColor = isMyGame ? android.R.color.holo_red_dark : R.color.green_normal;
        String buttonLabelText = isMyGame ? context.getString(R.string.button_delete_label) : context.getString(R.string.button_join_label);

        ViewUtils.setOnClickListener(convertView, R.id.game_proposal_action_button, buttonListener);
        ViewUtils.setBackgroundColor(context.getResources().getColor(buttonBackGroundColor), convertView, R.id.game_proposal_action_button);
        ViewUtils.setText(buttonLabelText, convertView, R.id.game_proposal_action_button);

        ViewUtils.setText(player.getName(), convertView, R.id.game_proposal_name);
        ImageView profileIcon = ViewUtils.findView(convertView, R.id.game_proposal_profile_icon);
        Picasso.with(context).load(player.getAvatarUrl()).into(profileIcon);
        ViewUtils.setText(formatAge(item.getAgeInSeconds()), convertView, R.id.game_proposal_age);
        return convertView;
    }

    private CharSequence formatAge(int ageInSeconds) {
        if (ageInSeconds < 60) {
            return "Just now!";
        } else if (ageInSeconds < 60 * 60) {
            int ageInMinutes = ageInSeconds / 60;
            if (ageInMinutes == 1) {
                return "One minute ago.";
            } else {
                if (ageInMinutes > 30) {
                    ageInMinutes -= ageInMinutes % 10;
                } else if (ageInMinutes > 10) {
                    ageInMinutes -= ageInMinutes % 5;
                }
                return ageInMinutes + " minutes ago.";
            }
        } else if (ageInSeconds < 24 * 60 * 60) {
            int ageInHours = ageInSeconds / (60 * 60);
            if (ageInHours == 1) {
                return "One hour ago.";
            } else {
                return ageInHours + " hours ago.";
            }
        } else {
            int ageInDays = ageInSeconds / (24 * 60 * 60);
            if (ageInDays == 1) {
                return "Yesterday.";
            } else if (ageInDays < 100) {
                return ageInDays + " days ago.";
            } else {
                return "Long, long ago.";
            }
        }
    }

    private View.OnClickListener getJoinGameButtonListener(final GameProposal item) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.joinGameProposal(item);
            }
        };
    }

    private View.OnClickListener getDeleteGameButtonListener(final GameProposal item) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.leaveGameProposal(item);
            }
        };
    }
}
